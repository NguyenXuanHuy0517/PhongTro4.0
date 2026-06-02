# Mappers

Mapper chuyển đổi giữa JPA entity và DTO.

Quy ước:

- Mapper không gọi repository hoặc service.
- Mapper không chứa nghiệp vụ.
- Khi response cần dữ liệu tổng hợp, service chuẩn bị dữ liệu trước rồi mapper format sang DTO.
