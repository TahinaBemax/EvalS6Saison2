from frappe import _

def get_data():
    return [
        {
            "label": _("Outils Importation"),
            "items": [
                {
                    "type": "page",
                    "name": "data-configuration",
                    "label": _("Importation de données"),
                    "icon": "octicon octicon-cloud-upload"
                }
            ]
        }
    ]
