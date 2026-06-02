# Exceptions

Exception handling dùng để chuẩn hóa lỗi trả về API.

| File | Vai trò |
| --- | --- |
| `GlobalExceptionHandler.java` | Map exception sang HTTP response |
| `ResourceNotFoundException.java` | Lỗi không tìm thấy tài nguyên |

Không trả stack trace hoặc chi tiết nội bộ ra client trong môi trường production.
