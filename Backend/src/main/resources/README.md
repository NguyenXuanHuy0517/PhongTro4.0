# Resources

Thư mục này chứa cấu hình runtime của Spring Boot.

| File | Vai trò |
| --- | --- |
| `application.properties` | Cấu hình chung, đọc secret qua biến môi trường |
| `application-local.properties` | Override khi chạy local |
| `application-staging.properties` | Override staging |
| `application-prod.properties` | Override production |

Không đặt secret thật trực tiếp trong các file này. Dùng biến môi trường hoặc secret manager của môi trường deploy.
