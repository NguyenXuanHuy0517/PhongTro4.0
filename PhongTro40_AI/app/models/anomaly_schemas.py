from pydantic import BaseModel

class AnomalyCheckRequest(BaseModel):
    room_code: str
    billing_month: int
    billing_year: int
    elec_usage: float
    water_usage: float

class AnomalyCheckResponse(BaseModel):
    status: str
    is_elec_anomaly: bool
    is_water_anomaly: bool
