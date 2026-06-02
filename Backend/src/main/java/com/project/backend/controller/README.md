# Controllers

Thư mục này chứa REST controllers, chia theo vai trò:

| Thư mục | Prefix chính | Nội dung |
| --- | --- | --- |
| `auth/` | `/api/auth` | Login, register, refresh token, logout, forgot/reset/change password |
| `admin/` | `/api/admin` | Dashboard, host management, room audit, revenue |
| `host/` | `/api/host` | Quản lý vận hành khu trọ của chủ trọ |
| `tenant/` | `/api/tenant` | Trải nghiệm người thuê và chatbot |

Controller nên mỏng: nhận request, gọi service phù hợp, trả `ApiResponse`/DTO. Nghiệp vụ không nên đặt trực tiếp tại đây.
