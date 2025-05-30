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

