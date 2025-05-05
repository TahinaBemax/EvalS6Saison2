from typing import List

import frappe
import csv
import io
from erpnext.dataconfig.page.data_configuration.csvModels import ImportCSVResponse
from erpnext.dataconfig.page.data_configuration.MaterialRequestModel import MaterialRequestModel
from pydantic import ValidationError
import debugpy
debugpy.listen(("0.0.0.0", 8001))
print("⏳ Waiting for debugger attach...")
debugpy.wait_for_client()

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


@frappe.whitelist(allow_guest=True)
def import_csv():
    try:
        req_for_quota_file = frappe.request.files.get('req_for_quota_file')
        material_req_file = frappe.request.files.get('mat_req_file')

        delimiter = frappe.form_dict.get('delimiter')
        print(f"DELIMITER {delimiter}, - file {material_req_file}")

        if not material_req_file:
            return ImportCSVResponse(
                success=False,
                message="No file received",
                imported_count=0,
                errors=["No file uploaded."]
            ).dict()

        mat_reqs_imported_rows = 0
        mat_reqs_csv_rows = 0
        mat_requests = prepare_material_request(material_req_file, delimiter)

        try:
            mat_reqs_imported_rows, mat_reqs_csv_rows = save_material_request(mat_requests) 

        except ValidationError as ve:
                frappe.logger().error(ve)

                return ImportCSVResponse(
                    success=False,
                    message=ve,
                    imported_count=0,
                    errors= [error['msg'] for error in ve.errors()]
                ).dict()
            
        except Exception as ve:
                frappe.logger().error(ve)

                return ImportCSVResponse(
                    success=False,
                    message=ve,
                    imported_count=0,
                    errors= [error['msg'] for error in ve.errors()]
                ).dict()

        return ImportCSVResponse(
            success=True,
            message=f"Material Request: csv_rows:${mat_reqs_csv_rows}, imported_rows:${mat_reqs_imported_rows}.",
            imported_count=mat_reqs_imported_rows,
            errors=None
        ).dict()

    except Exception as e:
        frappe.logger().exception("An error occured during importation")
        return ImportCSVResponse(
            success=False,
            message="Server error during importation",
            imported_count=0,
            errors= e.errors
        ).dict()


def prepare_material_request(material_req_file, delimiter):
    # Lecture du contenu
    content = material_req_file.read().decode('utf-8')
    reader = csv.DictReader(io.StringIO(content), delimiter=delimiter)
    mat_requests = []

    for idx, row in enumerate(reader, start=2):
        try:
            mat_request = MaterialRequestModel(**row)
            mat_requests.append(mat_request) 

        except ValidationError as ve:
            raise ValidationError(f"Doctype:Material Request - There is an invalid data at line {idx} : {ve}")
            
        except Exception as ve:
            raise ValidationError(f"Doctype:Material Request - There is an error at line {idx} : {ve}")

    return mat_requests


def save_material_request(mat_requests: list[MaterialRequestModel]):
    imported_count = 0

    for idx, row in enumerate(mat_requests):
        try:
            # Construire la liste des items
            items = []
            for i in range(len(row.items_code)):
                items.append({
                    "item_code": row.items_code[i],
                    "item_name": row.items[i] if len(row.items) > i else "",
                    "qty": float(row.qty[i]),
                    "required_by": row.required_by,
                    "stock_uom": row.stocks_uom,
                    "uom": row.uom[i],
                    "conversion_factor": float(row.uom_conversion_factor[i]),
                    "warehouse": row.target_warehouse
                })

            doc = frappe.get_doc({
                "doctype": "Material Request",
                "series": row.series,
                "transaction_date": row.transaction_date,
                "supplier": row.supplier,
                "company": row.company,
                "purpose": row.purpose,
                "schedule_date": row.required_by,
                "items": items
            })

            doc.insert(ignore_permissions=True)
            imported_count += 1

        except ValidationError as ve:
            raise ValidationError(ve)

        except Exception as e:
            raise ValidationError(e)
    return [imported_count, mat_requests.count()]

# @frappe.whitelist(allow_guest=True)
# def import_csv():
#     uploaded_file = frappe.request.files.get('file')
#     delimiter = frappe.form_dict.get('delimiter')

#     if not uploaded_file:
#         frappe.throw("Aucun fichier reçu.")

#     # Lecture du contenu
#     content = uploaded_file.read().decode('utf-8')
#     reader = csv.DictReader(io.StringIO(content), delimiter=delimiter)

#     imported_count = 0
#     errors = []

#     for idx, row in enumerate(reader, start=2):  # Ligne 2 = première après l'en-tête
#         try:
#             # Validation avec Pydantic
#             customer = CustomerCSVModel(**row)

#             # Insertion dans la base de données
#             # frappe.get_doc({
#             #     "doctype": "Customer",
#             #     "first_name": customer.first_name,
#             #     "last_name": customer.last_name,
#             #     "email": customer.email,
#             #     "phone": customer.phone
#             # }).insert(ignore_permissions=True)

#             imported_count += 1
#             print(f"Customer name: {customer.customer_name} \n Customer type: {customer.customer_type} \n")

#         except ValidationError as e:
#             error_msg = f"Erreur à la ligne {idx} : {e}"
#             frappe.logger().error(error_msg)
#             errors.append(error_msg)
#             raise ValueError(error_msg)

#         except Exception as ex:
#             frappe.logger().error(f"Erreur Frappe à la ligne {idx} : {ex}")
#             errors.append(f"Ligne {idx} : {ex}")
#             raise ex

#     #frappe.db.commit()
#     frappe.msgprint(f"{imported_count} clients importés avec succès.")

#     return "Importation terminée"
