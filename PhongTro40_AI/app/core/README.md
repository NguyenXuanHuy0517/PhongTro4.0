# Core

`core/` chứa cấu hình dùng chung cho AI service.

| File | Vai trò |
| --- | --- |
| `config.py` | Đọc biến môi trường, cấu hình Dialogflow, database và weather helper |

Credential Dialogflow nên được truyền qua `GOOGLE_APPLICATION_CREDENTIALS`. Không commit service account JSON.
