import os
from pathlib import Path
from typing import List

import frappe
import csv
import io
from erpnext.data_configuration.page.data_configuration.csvModels import ImportCSVResponse
from erpnext.data_configuration.page.data_configuration.SupplierModel import SupplierModel
from erpnext.data_configuration.page.data_configuration.MaterialRequestModel import MaterialRequestModel
from erpnext.data_configuration.page.data_configuration.ReferenceModel import ReferenceModel
from erpnext.data_configuration.page.data_configuration.ItemGroupModel import ItemGroupModel
from erpnext.data_configuration.page.data_configuration.ItemModel import ItemModel
from erpnext.data_configuration.page.data_configuration.WarehouseModel import WarehouseModel
from pydantic import ValidationError


#base dir
output_dir = "/mnt/c/Users/TahinaBemax/Desktop/EvaluationS6/Saison_2/bench2.0/sites/itu.erpnext/private/files/"
item_group_filename = "item_groups_extracted.csv"
items_filename = "items_extracted.csv"
suppliers_filename = "suppliers_extracted.csv"
req_mat_filename = "req_mat_extracted.csv"
req_for_quotation_filename = "req_for_quotation.csv"
supplier_quotation_filename = "supplier_quotation.csv"
warehouse_filename = "warehouse_extracted.csv"


@frappe.whitelist()
def delete_all_data():
    #frappe.db.sql("DELETE FROM `tabItem`")

    #Demande de besoin
    table_names = [ "tabItem", "tabItem Group", "tabSupplier", "tabData Import",
                     "tabMaterial Request", "tabMaterial Request Item", "tabRequest for Quotation", 
                #    "tabRequest for Quotation Item", "tabRequest for Quotation Supplier",
                #     "tabSupplier Quotation", "tabSupplier Quotation Item",
                #     "tabPurchase Order", "tabPurchase Order Item", "tabPurchase Invoice",
                #     "tabPurchase Invoice", "tabPurchase Invoice Item", "tabPurchase Invoice Advance"
                    ]
    for table in table_names:
        frappe.db.sql(f"DELETE FROM `{table}`")

    frappe.db.commit()
    return "OK"

@frappe.whitelist()
def import_csv():
    try:
        supplier_file = frappe.request.files.get('supplier_file')
        material_request_file = frappe.request.files.get('material_request_file')
        ref_file = frappe.request.files.get('ref_file')

        delimiter = frappe.form_dict.get('delimiter')

        #if not supplier_file or not material_request_file or not ref_file:
        if not material_request_file or not ref_file:
            return ImportCSVResponse(
                success=False,
                message="Supplier, Material Request and Reference files are required!",
                imported_count=0,
                errors=["File uploaded is not enough!"]
            ).model_dump()


        try:
            suppliers = prepare_supplier(supplier_file, delimiter)
            Materials = prepare_material_request(material_request_file, delimiter)
            references = prepare_reference(ref_file, delimiter)

            item_groups = extract_item_groups(Materials)
            items = extract_items(Materials)
            Warehouses = extract_warehouses(Materials)

            if export_all_to_csv(Warehouses, item_groups, items, suppliers, Materials, references):
                responses = start_import()
                print(f"{responses}")

        except ValidationError as ve:
                print(f"{ve}")

                return ImportCSVResponse(
                    success=False,
                    message=str(ve),
                    imported_count=0,
                    errors= [error['msg'] for error in ve.errors()]
                ).model_dump()
            
        except Exception as e:
                print(f"{e}")

                return ImportCSVResponse(
                    success=False,
                    message= str(e),
                    imported_count=0,
                    errors= [str(e)]
                ).model_dump()

        return ImportCSVResponse(
            success=True,
            message=f"Importation Finished with success!",
            imported_count=0,
            errors=None
        ).model_dump()

    except Exception as e:
        print(f"An error occured during importation {e}")
        return ImportCSVResponse(
            success=False,
            message="Server error during importation",
            imported_count=0,
            errors= [str(e)]
        ).model_dump()



def export_all_to_csv(Warehouses, item_groups, items, suppliers, Materials, references):
    #____Warehouse____
    if Warehouses and len(Warehouses) > 0:
        print(f"Export Warehouses data to csv...")
        exported = export_warehouse_to_csv(Warehouses, output_dir + warehouse_filename)
        print(f"Finished: {exported}")

    #____Items Group____
    if item_groups and len(item_groups) > 0:
        print(f"Export Item Group data to csv...")
        exported = export_item_groups_to_csv(item_groups, output_dir + item_group_filename)
        print(f"Finished: {exported}")

            #____Items____
    
    if items and len(items) > 0:
        print(f"Export Items data to csv...")
        exported = export_items_to_csv(items, output_dir + items_filename)
        print(f"Finished: {exported}")

    #____Suppliers____
    if suppliers and len(suppliers) > 0:
        print(f"Export Suppliers data to csv...")
        exported = export_suppliers_to_csv(suppliers, output_dir + suppliers_filename)
        print(f"Finished: {exported}")
        #automated_csv_import(filename, 'Supplier')

    #____Materials____
    if Materials and len(Materials) > 0:
        print(f"Export Material Request data to csv...")
        exported = export_mat_requests_to_csv(Materials, items, output_dir + req_mat_filename)
        print(f"Finished: {exported}")
    
    #____Request For Quotation____
    if Materials and len(Materials) > 0:
        print(f"Export Request For Quotation data to csv...")
        exported = export_request_for_quoatation_to_csv(Materials, items, references, output_dir + req_for_quotation_filename)
        print(f"Finished: {exported}")
    
    #____Supplier Quotation____
    if Materials and len(Materials) > 0:
        print(f"Export upplier Quotation data to csv...")
        exported = export_supplier_quoatation_to_csv(Materials, items, references, output_dir + supplier_quotation_filename)
        print(f"Finished: {exported}")

    return True

def start_import():
    print("Start Importing...")
    try:
        automated_csv_import(output_dir + suppliers_filename, 'Supplier')
        #automated_csv_import(output_dir + warehouse_filename, 'Warehouse')
        automated_csv_import(output_dir + item_group_filename, 'Item Group')
        automated_csv_import(output_dir + items_filename, 'Item')
        automated_csv_import(output_dir + req_mat_filename, 'Material Request')
        automated_csv_import(output_dir + req_for_quotation_filename, 'Request For Quotation')
        automated_csv_import(output_dir + supplier_quotation_filename, 'Supplier Quotation', False)


        frappe.db.commit()
        print("Finished and committed.")

    except Exception as e:
        frappe.db.rollback()
        frappe.log_error(f"Global import error: {str(e)}", "start_import")
        print(f"❌ Import failed, rollback executed: {e}")


#======== Read csv files =============
def prepare_supplier(supplier_file, delimiter):
    # Lecture du contenu
    content = supplier_file.read().decode('utf-8')
    reader = csv.DictReader(io.StringIO(content), delimiter=delimiter)
    suppliers = []

    for idx, row in enumerate(reader, start=2):
        try:
            supplier = SupplierModel(**row)
            suppliers.append(supplier) 

        except ValidationError as ve:
            print(f"Supplier - There is an invalid data at line {idx} : {ve}")
            raise ValidationError(f"Supplier - There is an invalid data at line {idx}!")
            
        except Exception as ve:
            print(f"Supplier - There is an error at line {idx} : {ve}")
            raise ValidationError(f"Supplier - There is an error at line {idx}!")

    return suppliers

def prepare_material_request(material_request_file, delimiter):
    content = material_request_file.read().decode('utf-8')
    reader = csv.DictReader(io.StringIO(content), delimiter=delimiter)
    material_requests = []

    for idx, row in enumerate(reader, start=2):
        try:
            material = MaterialRequestModel(**row)
            material_requests.append(material) 

        except ValidationError as ve:
            print(f"Material - There is an invalid data at line {idx} : {ve}")
            raise ValueError(f"Material - There is an invalid data at line {idx}")
            
        except Exception as ve:
            print(f"Material - Unexpected error at line {idx} : {ve}")
            raise ValueError(f"Material - Unexpected error at line {idx}")

    return material_requests

def prepare_reference(reference_file, delimiter):
    content = reference_file.read().decode('utf-8')
    reader = csv.DictReader(io.StringIO(content), delimiter=delimiter)
    references = []

    for idx, row in enumerate(reader, start=2):
        try:
            material = ReferenceModel(**row)
            references.append(material) 

        except ValidationError as ve:
            print(f"file_name:{reference_file} - There is an invalid data at line {idx} : {ve}")
            raise ValueError(f"file_name:{reference_file} - There is an invalid data at line {idx}")
            
        except Exception as ve:
            print(f"file_name:{reference_file} - Unexpected error at line {idx} : {ve}")
            raise ValueError(f"file_name:{reference_file} - Unexpected error at line {idx}")

    return references
#============ XXXXXX ===================



#======== Extract Other Data From Files =============
def extract_item_groups(materials: List[MaterialRequestModel]):
    if materials is None:
        raise ValueError("Request Material is null")
    
    groupes: List[ItemGroupModel] = []
    added_names = set()
    
    for material in materials:
        groupe_name = material.item_groupe.strip()
        if groupe_name and groupe_name not in added_names:
            groupes.append(ItemGroupModel(item_group_name= groupe_name))
            added_names.add(groupe_name)

    return groupes

def extract_items(materials: List[MaterialRequestModel]):
    if materials is None:
        raise ValueError("Request Material is null")
    
    items: List[ItemModel] = []
    items_list = set()
    
    for material in materials:
        item_name = material.item_name.strip()
        if item_name and item_name not in items_list:
            items_list.add(material.item_name);    
            items.append(ItemModel(
                #default_unit_of_mesure= material.uom,
                item_code="", 
                item_group=material.item_groupe,
                item_name=material.item_name)
            )

    return items

def extract_warehouses(materials: List[MaterialRequestModel]):
    if materials is None:
        raise ValueError("Warehouses is null")

    warehouses: List[WarehouseModel] = []
    added_names = set()  # Pour suivre les noms déjà ajoutés

    for material in materials:
        warehouse_name = material.target_warehouse.strip()
        if warehouse_name and warehouse_name not in added_names:
            warehouses.append(WarehouseModel(warehouse_name=warehouse_name))
            added_names.add(warehouse_name)

    return warehouses
#============ XXXXXX ===================

#======== Export to csv =============
def export_item_groups_to_csv(item_groups: List[ItemGroupModel], filename: str):
    # Crée le dossier si besoin
    Path(filename).parent.mkdir(parents=True, exist_ok=True)

    # Ouvre le fichier en écriture
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)

        print(f"Creation Item_groupe csv...")
        # En-têtes CSV
        writer.writerow(['Item Group Name'])

        # Données
        for item_group in item_groups:
            writer.writerow([item_group.item_group_name])
        
        print(f"Creation Item-Group terminé...")

def export_warehouse_to_csv(warehouses: List[WarehouseModel], filename: str):
    # Crée le dossier si besoin
    Path(filename).parent.mkdir(parents=True, exist_ok=True)

    # Ouvre le fichier en écriture
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)

        print(f"Creation warehouses csv...")
        # En-têtes CSV
        writer.writerow(['warehouse_name', 'company', 'Parent Warehouse', 'Is Group'])

        # Données
        for w in warehouses:
            writer.writerow([w.warehouse_name, w.company, "All Warehouses - ITU", 0])
        
        print(f"Creation warehouses terminé...")

def export_items_to_csv(items: List[ItemModel], filename: str):
    # Crée le dossier si besoin
    Path(filename).parent.mkdir(parents=True, exist_ok=True)

    # Ouvre le fichier en écriture
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)

        print(f"Creation Item_groupe csv...")
        # En-têtes CSV
        writer.writerow(['Item Code', 'Item Group', 'Item Name', 'Default Unit of Measure'])

        # Données
        for item in items:
            writer.writerow([item.item_code, item.item_group, item.item_name, item.default_unit_of_mesure])
        
        print(f"Creation Item-Group terminé...")

def export_suppliers_to_csv(suppliers: List[SupplierModel], filename: str):
    # Crée le dossier si besoin
    Path(filename).parent.mkdir(parents=True, exist_ok=True)

    # Ouvre le fichier en écriture
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)

        print(f"Lancement creation csv...")
        # En-têtes CSV
        writer.writerow(['supplier_name', 'country', 'supplier_type'])

        # Données
        for supplier in suppliers:
            writer.writerow([supplier.supplier_name, supplier.country, supplier.type])
        
        print(f"Lancement terminé...")

def export_mat_requests_to_csv(materials: List[MaterialRequestModel], items: List[ItemModel], filename: str):
    # Crée le dossier si besoin
    Path(filename).parent.mkdir(parents=True, exist_ok=True)

    # Ouvre le fichier en écriture
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)

        print(f"Lancement creation csv...")
        # En-têtes CSV
        writer.writerow(['Series', 'Purpose', 'Company', 'Transaction Date', 'Item Code (Items)', 'Quantity (Items)', 'Required By (Items)', 'Stock UOM (Items)', 'UOM (Items)', 'UOM Conversion Factor (Items)', 'Item Group (Items)', 'Item Name (Items)', 'Warehouse (Items)'])

        # Données
        for m in materials:
            writer.writerow([ "MAT-MR-.YYYY.-", m.purpose, m.company, m.date, get_item_code(items, m), m.quantity, m.required_by, m.stocks_uom, m.uom, m.uom_conversion_factor, m.item_groupe, m.item_name, 'All Warehouses - ITU'])
        
        print(f"Lancement terminé...")

def export_request_for_quoatation_to_csv(materials: List[MaterialRequestModel], items: List[ItemModel], refs: List[ReferenceModel],filename: str):
    # Crée le dossier si besoin
    Path(filename).parent.mkdir(parents=True, exist_ok=True)

    # Ouvre le fichier en écriture
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)

        print(f"Lancement creation csv...")
        # En-têtes CSV
        writer.writerow(['Series', 'Company','Date', 'Status', 'Message for Supplier','Supplier (Suppliers)', 'Item Code (Items)', 'Quantity (Items)', 'Required Date (Items)', 'Stock UOM (Items)', 'UOM (Items)', 'UOM Conversion Factor (Items)', 'Warehouse (Items)'])

        # Données
        for m in materials:
            writer.writerow([ "PUR-RFQ-.YYYY.-", m.company, m.date, "Submitted", "Bonjour", get_supplier_by_ref(refs, m.ref), get_item_code(items, m), m.quantity, m.required_by, m.stocks_uom, m.uom, m.uom_conversion_factor, 'Stores - ITU'])
        
        print(f"Lancement terminé...")

def export_supplier_quoatation_to_csv(materials: List[MaterialRequestModel], items: List[ItemModel], refs: List[ReferenceModel],filename: str):
    # Crée le dossier si besoin
    Path(filename).parent.mkdir(parents=True, exist_ok=True)

    # Ouvre le fichier en écriture
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)

        print(f"Lancement creation csv...")
        # En-têtes CSV
        writer.writerow(['Series', 'Supplier','Company','Status','Date', 'Currency', 'Exchange Rate', 'Amount (Company Currency) (Items)', 'Item Code (Items)','Quantity (Items)','Rate (Company Currency) (Items)','Stock UOM (Items)', 'UOM (Items)', 'UOM Conversion Factor (Items)'])

        # Données
        for m in materials:
            writer.writerow([ "PUR-SQTN-.YYYY.-", get_supplier_by_ref(refs, m.ref), m.company, "Draft", m.date, "EUR", 0, 0, get_item_code(items, m), m.quantity, 0, m.stocks_uom, m.uom, m.uom_conversion_factor])
        
        print(f"Lancement terminé...")

#============ XXXXXX ===================

def get_item_code(items: List[ItemModel], material: MaterialRequestModel):
    for i in items:
        if i.item_group == material.item_groupe and i.item_name == material.item_name:
            return i.item_code
    
    raise ValueError(f"Item:{material.item_name} don't have a Item Code")

def get_supplier_by_ref(refs: List[ReferenceModel], ref: str):
    for i in refs:
        if i.ref_request_quotation == ref:
            return i.supplier
    
    raise ValueError(f"Ref:{ref} don't match to a req_quotation reference")


def automated_csv_import(file_path, doctype_name, submit=True):
    try:
        file_name = os.path.basename(file_path)

        # Create Data Import document first
        data_import = frappe.get_doc({
            "doctype": "Data Import",
            "reference_doctype": doctype_name,
            "import_type": "Insert New Records",
            "submit_after_import": submit,
            "overwrite": 0
        })
        # Save first to get a name assigned
        data_import.insert()
        #frappe.db.commit()

        # Now create and attach the file with the correct attached_to_name
        with open(file_path, 'rb') as f:
            file_content = f.read()
            
        # Create the file document with attached_to_name already set
        file_doc = frappe.get_doc({
            "doctype": "File",
            "file_name": file_name,
            "is_private": 1,
            "content": file_content,
            "attached_to_doctype": "Data Import",
            "attached_to_name": data_import.name,  # Set this immediately and correctly
            "attached_to_field": "import_file"
        })
        file_doc.insert()
        #frappe.db.commit()
        print(f"File created with name: {file_doc.name} and URL: {file_doc.file_url}")
        
        # Update the Data Import with the file reference
        data_import.import_file = file_doc.file_url
        data_import.save()
        #frappe.db.commit()
        
        print("Starting the import process")
        data_import.start_import()
        #frappe.db.commit()
        print("Import started in background.")
        
        return {
            "status": "success",
            "message": "Import started successfully",
            "data_import_name": data_import.name
        }
    except Exception as e:
        print(f"CSV import error: {str(e)}", "automated_csv_import")
        raise Exception(f"An error occurred during CSV final import")



# def automated_csv_import(file_path, doctype_name):
#     try:
#         file_name = os.path.basename(file_path)
#         print(f"file_name: {file_name}")
        
#         # Create Data Import document first
#         data_import = frappe.get_doc({
#             "doctype": "Data Import",
#             "reference_doctype": doctype_name,
#             "import_type": "Insert New Records",
#             "submit_after_import": 1,
#             "overwrite": 0
#         })
        
#         # Save first to get a name assigned
#         data_import.insert()
#         frappe.db.commit()
#         print(f"Created Data Import with name: {data_import.name}")
        
#         # Now create and attach the file with the correct attached_to_name
#         with open(file_path, 'rb') as f:
#             file_content = f.read()
            
#         # Create the file document with attached_to_name already set
#         file_doc = frappe.get_doc({
#             "doctype": "File",
#             "file_name": file_name,
#             "is_private": 1,
#             "content": file_content,
#             "attached_to_doctype": "Data Import",
#             "attached_to_name": data_import.name,  # Set this immediately and correctly
#             "attached_to_field": "import_file"
#         })
#         file_doc.insert()
#         frappe.db.commit()
#         print(f"File created with name: {file_doc.name} and URL: {file_doc.file_url}")
        
#         # Update the Data Import with the file reference
#         data_import.import_file = file_doc.file_url
#         data_import.save()
#         frappe.db.commit()
        
#         print("Starting the import process")
#         data_import.start_import()
#         frappe.db.commit()
#         print("Import started in background.")
        
#         return {
#             "status": "success",
#             "message": "Import started successfully",
#             "data_import_name": data_import.name
#         }
#     except Exception as e:
#         frappe.log_error(f"CSV import error: {str(e)}", "automated_csv_import")
#         return {
#             "status": "error",
#             "message": f"An error occurred during CSV import: {str(e)}"
#         }

