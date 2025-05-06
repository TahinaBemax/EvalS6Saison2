import csv
from typing import List
from pathlib import Path
from pydantic import BaseModel, field_validator


class ItemGroupModel(BaseModel):
    item_groupe: str
    item_name: str
    items_code: str

    @field_validator("item_groupe", "item_name", mode="before")
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

