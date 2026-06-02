from google.cloud import dialogflow
import os
from app.core.config import PROJECT_ID

def detect_intent_with_ai(session_id: str, text: str):
    try:
        session_client = dialogflow.SessionsClient()
        session = session_client.session_path(PROJECT_ID, session_id)
        
        text_input = dialogflow.TextInput(text=text, language_code="vi")
        query_input = dialogflow.QueryInput(text=text_input)
        
        response = session_client.detect_intent(
            request={"session": session, "query_input": query_input}
        )
        
        intent_name = response.query_result.intent.display_name
        parameters = {key: value for key, value in response.query_result.parameters.items()}
        
        return intent_name, parameters
        
    except Exception as e:
        print(f"Lỗi kết nối Dialogflow: {e}")
        return "Unknown_Intent", {}
