
from pydantic import BaseModel, field_validator


class WarehouseModel(BaseModel):
    warehouse_name: str
    company: str = "IT University"

    @field_validator("company", "warehouse_name", mode="before")
    @classmethod
    def not_empty(cls, v):
        if not str(v).strip():
            raise ValueError("Field is mandatory")
        return v


