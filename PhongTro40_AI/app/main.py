import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.endpoints import chat, anomaly

app = FastAPI(
    title="AI Chatbot API - PHÒNG TRỌ 4.0",
    description="Hệ thống AI Chatbot",
    version="3.0.0" 
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Gắn routing từ module api
app.include_router(chat.router, prefix="/api/v1", tags=["Chatbot"])
app.include_router(anomaly.router, prefix="/api/v1", tags=["Anomaly"])

if __name__ == '__main__':
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
