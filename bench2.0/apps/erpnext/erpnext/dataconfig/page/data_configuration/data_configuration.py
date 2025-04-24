from typing import List
import frappe
import csv
import io
from erpnext.dataconfig.page.data_configuration.csvModels import CustomerCSVModel, ImportCSVResponse
from pydantic import ValidationError

@frappe.whitelist()
def delete_all_data():
    # Implémente la logique de suppression ici
    frappe.db.sql("DELETE FROM `tabCustomer`")
    frappe.db.commit()
    return "OK"

@frappe.whitelist(allow_guest=True)
def import_csv():
    try:
        uploaded_file = frappe.request.files.get('file')
        delimiter = frappe.form_dict.get('delimiter')

        if not uploaded_file:
            return ImportCSVResponse(
                success=False,
                message="No file received",
                imported_count=0,
                errors=["No file uploaded."]
            ).dict()

        # Lecture du contenu
        content = uploaded_file.read().decode('utf-8')
        reader = csv.DictReader(io.StringIO(content), delimiter=delimiter)

        imported_count = 0
        errors: List[str] = []

        for idx, row in enumerate(reader, start=2):
            try:
                # Validation
                customer = CustomerCSVModel(**row)

                # Insertion dans la base (décommenter en production)
                frappe.get_doc({
                    "doctype": "Customer",
                    "customer_name": customer.customer_name,
                    "customer_type": customer.customer_type,
                }).insert(ignore_permissions=True)

                frappe.logger().info(f"Customer added : {customer}")
                imported_count += 1

            except ValidationError as ve:
                frappe.logger().error(ve)
                errors = [error['msg'] for error in ve.errors()]

                return ImportCSVResponse(
                    success=False,
                    message=f"There is an invalid data at line {idx} : {ve}",
                    imported_count=0,
                    errors= errors
                ).dict()
            
            except Exception as ve:
                frappe.logger().error(ve)
                errors = [error['msg'] for error in ve.errors()]

                return ImportCSVResponse(
                    success=False,
                    message=f"There is an error at line {idx} : {ve}",
                    imported_count=0,
                    errors= errors
                ).dict()

        return ImportCSVResponse(
            success=len(errors) == 0,
            message=f"{imported_count} row(s) imported.",
            imported_count=imported_count,
            errors=errors
        ).dict()

    except Exception as e:
        frappe.logger().exception("An error occured during importation")
        return ImportCSVResponse(
            success=False,
            message="Server error during importation",
            imported_count=0,
            errors= e.errors
        ).dict()



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
