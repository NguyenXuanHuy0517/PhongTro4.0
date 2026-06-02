# API

FastAPI routers được đặt trong `api/endpoints/`.

| File | Endpoint | Vai trò |
| --- | --- | --- |
| `chat.py` | `POST /api/v1/chat` | Nhận message và context, trả câu trả lời chatbot |
| `anomaly.py` | `POST /api/v1/anomaly/check` | Kiểm tra dữ liệu tiêu thụ bất thường |

Router chỉ nên xử lý HTTP boundary và chuyển phần nghiệp vụ sang `services/`.
