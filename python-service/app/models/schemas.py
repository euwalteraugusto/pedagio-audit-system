from pydantic import BaseModel
from typing import List, Optional, Dict

class AuditResponse(BaseModel):
    total_linhas: int
    total_inconsistencias: Optional[int] = 0
    valor_total_divergente: Optional[float] = 0.0
    status: str = "SUCCESS"
    detalhes: Optional[List[Dict]] = []