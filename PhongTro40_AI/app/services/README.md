# Services

Service layer của AI service.

| File | Vai trò |
| --- | --- |
| `dialogflow_service.py` | Gọi Dialogflow ES `detect_intent` |
| `intent_handler.py` | Map intent sang truy vấn/logic nghiệp vụ |
| `anomaly_detector.py` | Isolation Forest để phát hiện tiêu thụ bất thường |

Các truy vấn database nên đi qua helper trong `db/`. Không ghi secret hoặc credential trực tiếp trong service.
