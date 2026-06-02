# Scheduler

Scheduled jobs tự động hóa nghiệp vụ định kỳ.

| File | Vai trò |
| --- | --- |
| `ContractExpiryScheduler.java` | Xử lý hợp đồng sắp/hết hạn |
| `InvoiceScheduler.java` | Hỗ trợ chu kỳ hóa đơn |
| `NotificationScheduler.java` | Tác vụ thông báo |
| `ReportScheduler.java` | Tác vụ báo cáo |

Khi chỉnh scheduler, cần kiểm tra idempotency để job chạy lại không tạo dữ liệu trùng hoặc trạng thái sai.
