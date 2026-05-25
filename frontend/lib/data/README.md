# Data

`data/` chứa lớp giao tiếp dữ liệu của frontend.

| Thư mục | Vai trò |
| --- | --- |
| `models/` | Dart model, JSON parsing và value objects |
| `services/` | Dio API clients theo module nghiệp vụ |

Provider gọi service, service trả model hoặc collection model. Không đặt logic hiển thị trong data layer.
