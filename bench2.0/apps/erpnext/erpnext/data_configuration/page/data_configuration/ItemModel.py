from typing import Optional
from pydantic import BaseModel, PrivateAttr, field_validator

item_counter = 0
class ItemModel(BaseModel):
    item_group: str
    item_name: str
    item_code: Optional[str]
    default_unit_of_mesure: Optional[str] = "Nos"

    @field_validator("item_group", "item_name", mode="before")
    @classmethod
    def not_empty(cls, v):
        if not str(v).strip():
            raise ValueError("Field is mandatory")
        return v

    @field_validator("item_code", mode="before")
    @classmethod
    def set_item_code(cls, v):
        global item_counter
        item_counter += 1
        return f"ITEM-{item_counter:05d}"

