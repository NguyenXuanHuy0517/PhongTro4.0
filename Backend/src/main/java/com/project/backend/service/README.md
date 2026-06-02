# Services

Service layer chứa nghiệp vụ chính của backend.

| Nhóm | Trách nhiệm |
| --- | --- |
| `auth/` | Xác thực, đăng ký, reset password, email auth |
| `admin/` | Dashboard, host status, room audit, revenue |
| `host/` | Area, room, tenant, contract, invoice, deposit, issue, notification, report, upload |
| `tenant/` | Dashboard, contract, invoice, issue, service, chatbot, profile, rental join |

Quy ước:

- Service là nơi xử lý transaction và rule nghiệp vụ.
- Không trả entity thô ra controller nếu đã có DTO/mapper.
- Tích hợp ngoài như AI service, Cloudinary, email nên được cô lập trong service chuyên trách.
