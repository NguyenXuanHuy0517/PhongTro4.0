# Project Structure

Tài liệu này mô tả cách đọc nhanh cấu trúc SmartRoomMS sau khi dọn thư mục project.

## Root

```text
Project/
├── Backend/
├── frontend/
├── PhongTro40_AI/
├── db.sql
├── docker-compose.yml
├── Phan_Tich_Chi_Tiet_AI_PhongTro40.docx
├── .gitignore
├── PROJECT_STRUCTURE.md
└── README.md
```

| Đường dẫn | Nội dung |
| --- | --- |
| `Backend/` | Backend monolith Spring Boot cho toàn bộ nghiệp vụ. |
| `frontend/` | Flutter app dùng chung cho mobile và web. |
| `PhongTro40_AI/` | Microservice FastAPI cho chatbot và anomaly detection. |
| `db.sql` | Schema, view và dữ liệu seed cho MariaDB/MySQL. |
| `docker-compose.yml` | Chạy MariaDB, Backend, AI service và frontend web. |
| `Phan_Tich_Chi_Tiet_AI_PhongTro40.docx` | Tài liệu phân tích AI đi kèm dự án. |

## Backend

```text
Backend/
├── src/main/java/com/project/backend/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── enums/
│   ├── exception/
│   ├── mapper/
│   ├── repository/
│   ├── scheduler/
│   ├── security/
│   └── service/
├── src/main/resources/
├── src/test/
├── Dockerfile
├── pom.xml
└── README.md
```

Backend đi theo mô hình layer quen thuộc của Spring: controller nhận request, service xử lý nghiệp vụ, repository truy cập dữ liệu, entity ánh xạ bảng, dto/mapper định hình dữ liệu vào ra.

## Frontend

```text
frontend/
├── lib/
│   ├── core/
│   ├── data/
│   ├── presentation/
│   ├── providers/
│   ├── app_router.dart
│   └── main.dart
├── assets/
├── android/
├── ios/
├── test/
├── web/
├── pubspec.yaml
└── README.md
```

Frontend tách theo trách nhiệm: `core` chứa theme/widget/utils dùng chung, `data` chứa model và service gọi API, `providers` quản lý state, `presentation` chứa màn hình theo vai trò.

## AI Service

```text
PhongTro40_AI/
├── app/
│   ├── api/
│   ├── core/
│   ├── db/
│   ├── models/
│   ├── services/
│   └── main.py
├── Dockerfile
├── requirements.txt
└── README.md
```

AI service nhận request chatbot/anomaly từ backend, gọi Dialogflow để nhận diện intent, truy vấn MariaDB khi cần và trả kết quả dạng JSON.

## Artefact đã dọn

Các mục sau là output có thể tái tạo nên đã được xoá khỏi workspace và đưa vào ignore:

- `__MACOSX/`, `.DS_Store`
- `.idea/`, `.vscode/`
- `Backend/target/`, `Backend/logs/`, `*.class`
- `frontend/.dart_tool/`, `frontend/build/`, `frontend/android/.gradle/`, `frontend/android/.kotlin/`
- `frontend/ios/Pods/`, `frontend/ios/.symlinks/`, `frontend/ios/Flutter/ephemeral/`
- `PhongTro40_AI/.venv/`

## Ghi chú về Git

Root `Project/` hiện chưa có `.git`. Bên trong `Backend/` và `PhongTro40_AI/` có lịch sử git riêng. Nếu muốn publish toàn bộ project như một monorepo, hãy quyết định rõ một trong hai hướng:

- Giữ `Backend/` và `PhongTro40_AI/` như submodule/subtree.
- Sao lưu lịch sử rồi bỏ `.git` lồng bên trong trước khi khởi tạo git ở root.

Không nên xoá các thư mục `.git` lồng nhau nếu chưa chắc chắn vì thao tác đó làm mất lịch sử local của từng phần.
