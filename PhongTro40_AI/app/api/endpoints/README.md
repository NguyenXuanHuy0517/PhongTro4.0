# Endpoints

FastAPI endpoint implementations.

| File | Method/Path | Service chính |
| --- | --- | --- |
| `chat.py` | `POST /api/v1/chat` | `dialogflow_service`, `intent_handler` |
| `anomaly.py` | `POST /api/v1/anomaly/check` | `anomaly_detector` |

Endpoint nên bắt lỗi ở HTTP boundary và trả response rõ ràng, phần xử lý nghiệp vụ đặt ở `services/`.
