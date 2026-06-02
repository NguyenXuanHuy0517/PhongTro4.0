from fastapi import APIRouter, HTTPException
from app.models.schemas import ChatRequest, ChatResponse, ChatResponseData
from app.db.database import get_db_connection
from app.services.dialogflow_service import detect_intent_with_ai
from app.services.intent_handler import handle_intent

router = APIRouter()

@router.post("/chat", response_model=ChatResponse)
async def chat_with_ai(request: ChatRequest):
    try:
        user_id = request.user_context.user_id
        phong_id_context = request.user_context.phong_id
        message = request.message
        session_id = request.session_id

        # 1. Nhận diện intent qua Dialogflow
        intent_name, parameters = detect_intent_with_ai(session_id, message)

        db = get_db_connection()
        try:
            cursor = db.cursor(dictionary=True)
            
            # 2. Xử lý logic nghiệp vụ theo intent
            reply_message, is_anomaly = handle_intent(
                intent_name=intent_name, 
                parameters=parameters, 
                user_id=user_id, 
                phong_id_context=phong_id_context, 
                cursor=cursor, 
                db=db,
                original_message=message
            )
            
        finally:
            if 'cursor' in locals():
                cursor.close()
            db.close()

        return ChatResponse(
            status="success",
            data=ChatResponseData(
                reply_message=reply_message,
                intent_detected=intent_name,
                is_anomaly=is_anomaly
            )
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
