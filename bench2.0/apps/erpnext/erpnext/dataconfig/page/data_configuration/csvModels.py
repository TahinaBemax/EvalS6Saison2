
# apps/ton_app/models.py
from pydantic import BaseModel, validator
from typing import List, Optional

class ImportCSVResponse(BaseModel):
    success: bool
    message: str
    imported_count: int
    errors: Optional[List[str]] = []


class CustomerCSVModel(BaseModel):
    customer_name: str
    customer_type: str = 'company'

    @validator('customer_name')
    def not_empty(cls, v):
        if not v.strip():
            raise ValueError("Customer name must not empty")
        return v
    
    @validator('customer_type')
    def in_customer_types(cls, v):
        customer_types = ["company", "individual", "partnership"]
        
        # Vérifier si la valeur est vide
        if not v.strip():
            raise ValueError("Should not empty")
        
        # Vérifier si la valeur est dans la liste des types de clients
        if str(v).lower() not in customer_types:
            raise ValueError(f"The value must {', '.join(customer_types)}.")
        
        return v
    