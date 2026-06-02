# Models

`models/` chứa Pydantic schema cho request/response của AI service.

- `schemas.py`: schema chatbot.
- `anomaly_schemas.py`: schema kiểm tra anomaly.

Schema là contract HTTP, giúp FastAPI sinh OpenAPI docs tại `/docs`.
