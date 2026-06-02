from pydantic import BaseModel

class UserContext(BaseModel):
    user_id: str
    role: str
    khu_tro_id: str
    phong_id: str

class ChatRequest(BaseModel):
    session_id: str
    message: str
    user_context: UserContext

class ChatResponseData(BaseModel):
    reply_message: str
    intent_detected: str
    is_anomaly: bool

class ChatResponse(BaseModel):
    status: str
    data: ChatResponseData
