import frappe
from frappe import _

@frappe.whitelist(allow_guest=True)
def get_rfq_by_supplier():
    supplier_name = frappe.form_dict.get("supplier_name")
    if not supplier_name:
        frappe.throw(_("Supplier name is required"))
        
    frappe.logger().info(f"Received supplier_name: {supplier_name}")

    rfqs = frappe.get_all("Request for Quotation",
        filters={"docstatus": ["<", 2]},
        fields=["name", "transaction_date", "status"],
        order_by="creation desc"
    )

    results = []
    for rfq in rfqs:
        doc = frappe.get_doc("Request for Quotation", rfq.name)
        for supplier in doc.suppliers:
            if supplier.supplier == supplier_name:
                results.append({
                    "name": doc.name,
                    "docstatus": doc.docstatus,
                    "company": doc.company,
                    "transaction_date": doc.transaction_date,
                    "schedule_date": doc.schedule_date,
                    "suppliers": [s.as_dict() for s in doc.suppliers],
                    "items": [i.as_dict() for i in doc.items]
                })
                break
    return results
