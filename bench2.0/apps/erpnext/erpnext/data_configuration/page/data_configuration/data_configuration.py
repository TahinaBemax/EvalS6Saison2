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
from pydantic import ValidationError


#base dir
output_dir = "/mnt/c/Users/TahinaBemax/Desktop/EvaluationS6/Saison_2/bench2.0/sites/itu.erpnext/private/files/"
item_group_filename = "item_groups_extracted.csv"
items_filename = "items_extracted.csv"


@frappe.whitelist()
def delete_all_data():
    #frappe.db.sql("DELETE FROM `tabItem`")

    #Demande de besoin
    frappe.db.sql("DELETE FROM `tabMaterial Request`")
    frappe.db.sql("DELETE FROM `tabMaterial Request Item`")

    #Demande de devis
    frappe.db.sql("DELETE FROM `tabRequest for Quotation`")
    frappe.db.sql("DELETE FROM `tabRequest for Quotation Item`")
    frappe.db.sql("DELETE FROM `tabRequest for Quotation Supplier`")

    #Devis Fournisseur
    frappe.db.sql("DELETE FROM `tabSupplier`")
    frappe.db.sql("DELETE FROM `tabSupplier Quotation`")
    frappe.db.sql("DELETE FROM `tabSupplier Quotation Item`")

    #Commandes
    frappe.db.sql("DELETE FROM `tabPurchase Order`")
    frappe.db.sql("DELETE FROM `tabPurchase Order Item`")

    #Factures
    frappe.db.sql("DELETE FROM `tabPurchase Invoice`")
    frappe.db.sql("DELETE FROM `tabPurchase Invoice Item`")
    frappe.db.sql("DELETE FROM `tabPurchase Invoice Advance`")

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
            #suppliers = prepare_supplier(supplier_file, delimiter)
            Materials = prepare_material_request(material_request_file, delimiter)
            #references = prepare_reference(ref_file, delimiter)

            item_groups = extract_item_groups(Materials)
            items = extract_items(Materials)

            print(f"Item Groups:{item_groups}")
            print(f"Items:{items}")

            if item_groups and len(item_groups) > 0:
                print(f"Export Item Group data to csv...")
                exported = export_item_groups_to_csv(item_groups, output_dir + item_group_filename)
                print(f"Finished: {exported}")

            if items and len(items) > 0:
                print(f"Export Items data to csv...")
                exported = export_items_to_csv(items, output_dir + items_filename)
                print(f"Finished: {exported}")

            #if suppliers and len(suppliers) > 0:
             #   filename= "/mnt/c/Users/TahinaBemax/Desktop/EvaluationS6/Saison_2/bench2.0/sites/itu.erpnext/private/files/supplier_extracted.csv"
                #export_suppliers_to_csv(suppliers, filename)
                #automated_csv_import(filename, 'Supplier')

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
            message=f"Csv_rows: 0, imported_rows:${0}.",
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
    groupes: List[ItemGroupModel] = []

    if materials is None:
        raise ValueError("Request Material is null")
    
    for material in materials:
        if any(grp.item_group_name == material.item_groupe for grp in groupes):
            pass

        groupes.append(ItemGroupModel(item_group_name= material.item_groupe))

    return groupes

def extract_items(materials: List[MaterialRequestModel]):
    items: List[ItemModel] = []
    items_list = []

    if materials is None:
        raise ValueError("Request Material is null")
    
    for material in materials:
        if any(item == material.item_name for item in items_list):
            pass

        items_list.append(material.item_name);    
        items.append(ItemModel(
            default_unit_of_mesure= material.uom,
            item_code="", 
            item_group=material.item_groupe,
            item_name=material.item_name)
        )

    return items
#============ XXXXXX ===================

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


def automated_csv_import(file_path, doctype_name):
    try:
        file_name = os.path.basename(file_path)
        print(f"file_name: {file_name}")
        
        # Create Data Import document first
        data_import = frappe.get_doc({
            "doctype": "Data Import",
            "reference_doctype": doctype_name,
            "import_type": "Insert New Records",
            "submit_after_import": 1,
            "overwrite": 0
        })
        
        # Save first to get a name assigned
        data_import.insert()
        frappe.db.commit()
        print(f"Created Data Import with name: {data_import.name}")
        
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
        frappe.db.commit()
        print(f"File created with name: {file_doc.name} and URL: {file_doc.file_url}")
        
        # Update the Data Import with the file reference
        data_import.import_file = file_doc.file_url
        data_import.save()
        frappe.db.commit()
        
        print("Starting the import process")
        data_import.start_import()
        frappe.db.commit()
        print("Import started in background.")
        
        return data_import.name
    except Exception as e:
        print(f"Error during import: {str(e)}")
        frappe.log_error(f"CSV import error: {str(e)}")
        raise Exception(f"An error occurred: {str(e)}")

