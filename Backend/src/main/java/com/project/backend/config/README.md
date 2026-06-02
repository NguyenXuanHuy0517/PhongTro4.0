# Config

Spring configuration classes.

| File | Vai trò |
| --- | --- |
| `SecurityConfig.java` | Security filter chain, endpoint authorization |
| `CorsConfig.java` | CORS policy |
| `CloudinaryConfig.java` | Cloudinary client bean |

Không hardcode secret trong config. Giá trị nhạy cảm phải đi qua `application.properties` và biến môi trường.
