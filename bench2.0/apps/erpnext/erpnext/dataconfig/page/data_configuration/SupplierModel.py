import csv
from typing import List
from pathlib import Path
from pydantic import BaseModel, field_validator


class SupplierModel(BaseModel):
    supplier_name: str
    country: str
    type: str

    @field_validator("supplier_name", "country", mode="before")
    @classmethod
    def not_empty(cls, v):
        if not str(v).strip():
            raise ValueError("Field is mandatory")
        return v

    @field_validator("type", mode="before")
    @classmethod
    def validate_supplier_type(cls, v):
        valid_supplier_type = ["Company", "Individual", "Partnership"]
        if not str(v).strip():
            raise ValueError("Supplier type is required")
        if v not in valid_supplier_type:
            raise ValueError(f"Invalid Supplier Type: {v}. Must be one of: {', '.join(valid_supplier_type)}")
        return v


def export_suppliers_to_csv(suppliers: List[SupplierModel], filename: str):
    # Crée le dossier si besoin
    Path(filename).parent.mkdir(parents=True, exist_ok=True)

    # Ouvre le fichier en écriture
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)

        # En-têtes CSV
        writer.writerow(['supplier_name', 'country', 'type'])

        # Données
        for supplier in suppliers:
            writer.writerow([supplier.supplier_name, supplier.country, supplier.type])


# Exemple d'utilisation :
if __name__ == "__main__":
    suppliers = [
        SupplierModel(supplier_name="ABC Corp", country="USA", type="Company"),
        SupplierModel(supplier_name="John Doe", country="Canada", type="Individual"),
    ]

    export_suppliers_to_csv(suppliers, "output/suppliers.csv")
    print("Export terminé.")
