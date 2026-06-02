# AI Service

`PhongTro40_AI` là microservice FastAPI phục vụ chatbot và phát hiện bất thường tiêu thụ điện/nước cho SmartRoomMS. Backend gọi service này khi tenant sử dụng chatbot hoặc khi cần kiểm tra anomaly.

## Công nghệ

| Nhóm | Công nghệ |
| --- | --- |
| API | FastAPI, Uvicorn |
| NLP | Google Dialogflow ES |
| ML | scikit-learn, Isolation Forest |
| Database | mysql-connector-python |
| External data | Open-Meteo Archive API |

## Endpoint

| Method | Path | Mục đích |
| --- | --- | --- |
| `POST` | `/api/v1/chat` | Nhận câu hỏi, gọi Dialogflow, xử lý intent và trả câu trả lời |
| `POST` | `/api/v1/anomaly/check` | Kiểm tra dữ liệu tiêu thụ bất thường |
| `GET` | `/docs` | Swagger UI do FastAPI sinh |

## Cấu trúc

```text
app/
├── api/endpoints/     # FastAPI routers
├── core/              # Config môi trường, DB, Dialogflow, weather
├── db/                # MySQL connection pool
├── models/            # Pydantic schemas
├── services/          # Dialogflow, intent handler, anomaly detector
└── main.py            # FastAPI entrypoint
```

## Cấu hình

Copy [`.env.example`](./.env.example) và đặt biến môi trường tương ứng.

| Biến | Mục đích |
| --- | --- |
| `DB_HOST`, `DB_USER`, `DB_PASSWORD`, `DB_NAME`, `DB_PORT` | Kết nối MariaDB |
| `DIALOGFLOW_PROJECT_ID` | Google Dialogflow project id |
| `GOOGLE_APPLICATION_CREDENTIALS` | Đường dẫn tới service-account JSON local |

Không commit `dialogflow-key.json` hoặc service account key. File này đã được ignore.

## Chạy local

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

macOS/Linux:

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

## Kiểm tra nhanh

```bash
python -m compileall app
```

## Docker

```bash
docker build -t smartroom-ai .
docker run --rm -p 8000:8000 --env-file .env smartroom-ai
```
