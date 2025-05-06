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
            references = prepare_reference(ref_file, delimiter)


            #if suppliers and len(suppliers) > 0:
             #   filename= "/mnt/c/Users/TahinaBemax/Desktop/EvaluationS6/Saison_2/bench2.0/sites/itu.erpnext/private/files/supplier_extracted.csv"
                #export_suppliers_to_csv(suppliers, filename)
                #automated_csv_import(filename, 'Supplier')

        except ValidationError as ve:
                frappe.logger().error(ve)

                return ImportCSVResponse(
                    success=False,
                    message=ve,
                    imported_count=0,
                    errors= [error['msg'] for error in ve.errors()]
                ).model_dump()
            
        except Exception as e:
                frappe.logger().error(e)

                return ImportCSVResponse(
                    success=False,
                    message= str(e),
                    imported_count=0,
                    errors= [str(e)]
                ).model_dump()

        return ImportCSVResponse(
            success=True,
            message=f"Material Request: csv_rows:${len(references)}, imported_rows:${0}.",
            imported_count=len(references),
            errors=None
        ).model_dump()

    except Exception as e:
        frappe.logger().exception(f"An error occured during importation {e}")
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
            frappe.logger.error(f"Supplier - There is an invalid data at line {idx} : {ve}")
            raise ValidationError(f"Supplier - There is an invalid data at line {idx}!")
            
        except Exception as ve:
            frappe.logger.error(f"Supplier - There is an error at line {idx} : {ve}")
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
            frappe.logger.error(f"Material - There is an invalid data at line {idx} : {ve}")
            raise ValueError(f"Material - There is an invalid data at line {idx}")
            
        except Exception as ve:
            frappe.logger.error(f"Material - Unexpected error at line {idx} : {ve}")
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
            frappe.logger.error(f"file_name:{reference_file} - There is an invalid data at line {idx} : {ve}")
            raise ValueError(f"file_name:{reference_file} - There is an invalid data at line {idx}")
            
        except Exception as ve:
            frappe.logger.error(f"file_name:{reference_file} - Unexpected error at line {idx} : {ve}")
            raise ValueError(f"file_name:{reference_file} - Unexpected error at line {idx}")

    return references
#============ XXXXXX ===================

#======== Extract Other Data From Files =============

def extract_item_groups(materials: List[MaterialRequestModel]):
    groupes: List[ItemGroupModel]

    if materials is None:
        raise ValueError("Request Material is null")
    
    for material in materials:
        any(grp.item_group_name == material.item_groupe for grp in groupes)
        if material.item_groupe not in groupes:
            groupes.append(ItemGroupModel(material.item_groupe))

    return groupes

def extract_items(materials: List[MaterialRequestModel]):
    items: List[ItemModel]

    if materials is None:
        raise ValueError("Request Material is null")
    
    for material in materials:
        any(grp.item_group_name == material.item_groupe for grp in items)
        if material.item_groupe not in items:
            items.append(ItemGroupModel(material.item_groupe))

    return items
#============ XXXXXX ===================

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

