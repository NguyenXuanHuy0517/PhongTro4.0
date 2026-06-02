# Backend

Backend là REST API chính của SmartRoomMS, viết bằng Spring Boot và Java 17. Module này xử lý xác thực, phân quyền, nghiệp vụ quản lý phòng trọ, kết nối MariaDB, upload ảnh qua Cloudinary, gửi email và gọi AI service.

## Công nghệ

| Nhóm | Công nghệ |
| --- | --- |
| Runtime | Java 17 |
| Framework | Spring Boot 4.0.4 |
| Build | Maven wrapper |
| Data | Spring Data JPA, Hibernate, MariaDB JDBC |
| Security | Spring Security, JWT, BCrypt |
| Tích hợp | Cloudinary, Spring Mail, FastAPI AI service |
| Observability | Spring Actuator |

## Cấu trúc

```text
src/main/java/com/project/backend/
├── config/       # Security, CORS, Cloudinary
├── controller/   # REST API theo role
├── dto/          # Request/response contracts
├── entity/       # JPA entities
├── enums/        # Domain statuses
├── exception/    # Global error handling
├── mapper/       # Entity <-> DTO mapping
├── repository/   # Spring Data repositories
├── scheduler/    # Scheduled jobs
├── security/     # JWT filter, token utils, user details
└── service/      # Business logic
```

## API groups

| Prefix | Nhóm chức năng |
| --- | --- |
| `/api/auth` | Login, register, refresh token, logout, forgot/reset/change password |
| `/api/admin` | Dashboard, host management, room audit, revenue |
| `/api/host` | Area, room, tenant, contract, invoice, deposit, service, equipment, issue, notification, report |
| `/api/tenant` | Dashboard, contract, invoice, issue, service, chatbot, profile, notification, rental join |
| `/actuator` | Health, metrics, info |

## Cấu hình

Copy [`.env.example`](./.env.example) và truyền các biến vào shell, IDE hoặc Docker Compose. Các secret thật không nằm trong `application.properties`.

Biến quan trọng:

| Biến | Mục đích |
| --- | --- |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Kết nối MariaDB |
| `JWT_SECRET` | Base64 secret ký access token |
| `RENTAL_INVITE_SECRET` | Base64 secret ký QR/link mời thuê |
| `CLOUDINARY_*` | Upload avatar, ảnh phòng, minh chứng thanh toán |
| `MAIL_*` | Gửi email reset password |
| `AI_SERVICE_URL` | URL FastAPI AI service |

## Chạy local

```powershell
.\mvnw spring-boot:run
```

Nếu chạy từ macOS/Linux:

```bash
./mvnw spring-boot:run
```

## Test

```powershell
.\mvnw test
```

## Docker

```bash
docker build -t smartroom-backend .
docker run --rm -p 8080:8080 --env-file .env smartroom-backend
```

Trong workflow đầy đủ, nên chạy bằng `docker compose up --build -d` ở thư mục root.
