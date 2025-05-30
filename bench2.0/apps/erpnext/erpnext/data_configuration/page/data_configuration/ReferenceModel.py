import csv
from typing import List
from pathlib import Path
from pydantic import BaseModel, field_validator


class ReferenceModel(BaseModel):
    ref_request_quotation: str
    supplier: str

    @field_validator("ref_request_quotation", "supplier", mode="before")
    @classmethod
    def not_empty(cls, v):
        if not str(v).strip():
            raise ValueError("Field is mandatory")
        return v

