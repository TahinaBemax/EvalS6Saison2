import frappe

@frappe.whitelist()
def delete_all_data():
    # Implémente la logique de suppression ici
    frappe.db.sql("DELETE FROM `tabCustomer`")
    frappe.db.commit()
    return "OK"

@frappe.whitelist(allow_guest=True)
def import_csv():
    from apps.frappe.frappe.utils.file_manager import save_uploaded
    import csv
    import io

    uploaded_file = frappe.request.files.get('file')
    delimiter = frappe.form_dict.get('delimiter')

    if not uploaded_file:
        frappe.throw("Aucun fichier reçu.")

    content = uploaded_file.read().decode('utf-8')
    csv_reader = csv.reader(io.StringIO(content), delimiter=delimiter)

    for row in csv_reader:
        frappe.logger().info(row)
        # insère les données ici

    return "Importation terminée"
