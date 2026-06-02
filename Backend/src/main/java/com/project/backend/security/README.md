# Security

Thư mục này chứa lớp bảo mật của backend.

| File | Vai trò |
| --- | --- |
| `JwtAuthFilter.java` | Đọc bearer token và set authentication context |
| `JwtUtils.java` | Sinh, đọc và validate JWT |
| `UserDetailsServiceImpl.java` | Load user cho Spring Security |

`jwt.secret` phải là Base64 secret đủ mạnh và được truyền qua biến môi trường trong môi trường thật.
