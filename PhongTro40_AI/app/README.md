# AI App Package

`app/` là package FastAPI chính của AI service.

```text
app/
├── api/        # HTTP routers
├── core/       # Config và helper external API
├── db/         # MySQL connection
├── models/     # Pydantic schemas
├── services/   # Dialogflow, intent handling, anomaly detection
└── main.py     # FastAPI app
```

Luồng chatbot:

```text
POST /api/v1/chat -> Dialogflow -> Intent handler -> MariaDB/logic -> ChatResponse
```
