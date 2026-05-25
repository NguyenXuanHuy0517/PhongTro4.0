# Models

Dart models ánh xạ JSON từ Backend.

Quy ước:

- Giữ parsing JSON rõ ràng và chịu được giá trị null khi API có thể trả thiếu field.
- Không đặt logic UI trong model.
- Khi backend đổi DTO, cập nhật model và test parsing liên quan.
