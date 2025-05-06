# from datetime import date
# from pydantic import BaseModel, field_validator
# from typing import List
# from typing_extensions import Annotated

# class MaterialRequestModel(BaseModel):
#     date: date
#     supplier: str
#     company: str
#     purpose: str

#     items: List[str]
#     items_code: List[str]
#     qty: List[float]
#     required_by: date = date.today()
#     stocks_uom: str = "Nos"
#     uom: List[str]
#     uom_conversion_factor: List[str]
#     target_warehouse: str = "Test warehouse - BICID"

#     @field_validator("company", "series", "purpose", mode="before")
#     @classmethod
#     def not_empty(cls, v):
#         if not str(v).strip():
#             raise ValueError("Field is mandatory")
#         return v

#     @field_validator("items_code", mode="before")
#     @classmethod
#     def split_items_code(cls, v):
#         if not v.strip():
#             raise ValueError("Item_code is mandatory")
#         return [x.strip() for x in v.split(",")]

#     @field_validator("qty", mode="before")
#     @classmethod
#     def split_qty(cls, v):
#         if not v.strip():
#             raise ValueError("Qty is mandatory")
#         qty_list = [float(x) for x in v.split(",")]
#         if any(q <= 0 for q in qty_list):
#             raise ValueError("Each quantity must be greater than 0")
#         return qty_list

#     @field_validator("uom", mode="before")
#     @classmethod
#     def validate_uom(cls, v):
#         valid_uoms = [
#             "Ambapere", "Acre", "Acre (US)", "Ampere", "Ampere-Hour", "Ampere-Minute",
#             "Ampere-Second", "Are", "Area", "Arshin"
#         ]
#         if not v.strip():
#             raise ValueError("UOM is mandatory")
#         uom_list = [x.strip() for x in v.split(",")]
#         for u in uom_list:
#             if u not in valid_uoms:
#                 raise ValueError(f"Invalid UOM: {u}. Must be one of: {', '.join(valid_uoms)}")
#         return uom_list
