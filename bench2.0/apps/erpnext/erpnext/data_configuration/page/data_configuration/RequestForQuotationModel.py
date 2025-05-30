from datetime import date, datetime
import random
from typing import List
from pydantic import BaseModel, field_validator
from typing_extensions import Annotated
from erpnext.data_configuration.page.data_configuration.ReferenceModel import ReferenceModel

class RequestForQuotationModel(BaseModel):
    #given fields
    Series: str = ""
    Company: str = "IT University"
    Date: date
    Status: str = "IT University"
    Message: str = "IT University"
    supplier: str = ""
    item_code: str
    quantity: float
    required_by: date
    stocks_uom: str = "Nos"
    uom: str = "Abampere"
    uom_conversion_factor: str = 1

    # #required fields

    @field_validator("item_code", "supplier", "required_by", "quantity", mode="before")
    @classmethod
    def not_empty(cls, v):
        if not str(v).strip():
            raise ValueError("Field is mandatory")
        return v


    @field_validator("date", "required_by", mode="before")
    @classmethod
    def check_date(cls, v):
        if not str(v).strip():
            raise ValueError("Date is mandatory")

        date_str = str(v).strip()
        accepted_formats = [
            "%Y-%m-%d",  # ISO
            "%Y/%m/%d",  # ISO with slashes
            "%d-%m-%Y",  # French with dashes
            "%d/%m/%Y"   # French with slashes
        ]

        for fmt in accepted_formats:
            try:
                parsed_date = datetime.strptime(date_str, fmt)
                return parsed_date.date()  # ou return date_str si tu veux garder la chaîne d’origine
            except ValueError:
                continue

        raise ValueError(f"Invalid date format: '{date_str}'. Accepted formats: YYYY-MM-DD, YYYY/MM/DD, DD-MM-YYYY, DD/MM/YYYY")


    @field_validator("quantity", mode="before")
    @classmethod
    def check_qty(cls, v):
        if not str(v).strip():
            raise ValueError("Quantity is mandatory")
        if float(v) <= 0:
            raise ValueError("Quantity must be greater than 0")
        return v

    @field_validator("uom", mode="before")
    @classmethod
    def get_uom(clv, v):
        valid_uoms = [
            "Abampere", "Acre", "Acre (US)", "Ampere", "Ampere-Hour", "Ampere-Minute",
            "Ampere-Second", "Are", "Area", "Arshin"
        ]
        #return valid_uoms[random.randint(0, len(valid_uoms))]
        return "Abampere"
