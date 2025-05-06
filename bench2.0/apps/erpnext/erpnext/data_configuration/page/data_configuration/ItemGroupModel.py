from pydantic import BaseModel, field_validator


class ItemGroupModel(BaseModel):
    item_group_name: str

    @field_validator("item_group_name", mode="before")
    @classmethod
    def not_empty(cls, v):
        if not str(v).strip():
            raise ValueError("Field is mandatory")
        return v

