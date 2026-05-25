# Services

Services là lớp gọi Backend API bằng Dio.

Quy ước:

- Mỗi file service bám theo một domain: auth, area, room, contract, invoice, issue, admin, tenant.
- Service chỉ xử lý HTTP, parse response và ném lỗi có ngữ cảnh.
- Provider chịu trách nhiệm quản lý state loading/error/data cho UI.
