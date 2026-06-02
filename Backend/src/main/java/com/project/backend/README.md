# Backend Package

Package `com.project.backend` chứa toàn bộ code Java của REST API. Luồng xử lý chuẩn:

```text
Controller -> Service -> Repository -> Entity/Database
             Mapper/DTO ở ranh giới dữ liệu vào ra
```

## Quy ước

- `controller/` chỉ nhận request, validate ở mức boundary và trả response.
- `service/` giữ nghiệp vụ, transaction và orchestration giữa repository/tích hợp ngoài.
- `repository/` chỉ định nghĩa truy vấn dữ liệu.
- `entity/` phản ánh schema database.
- `dto/` là contract API, không để entity rò trực tiếp ra client.
- `mapper/` chuyển đổi giữa entity và DTO.
- `security/` chứa JWT filter, utility ký/đọc token và user details.
