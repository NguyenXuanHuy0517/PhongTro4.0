# DTOs

DTO là contract dữ liệu giữa API và client.

| Thư mục | Nội dung |
| --- | --- |
| `auth/` | Login, register, refresh, reset password |
| `admin/` | Dashboard, host, revenue, room audit |
| `host/` | Area, room, tenant, contract, invoice, deposit, equipment, issue, notification, report |
| `tenant/` | Contract, invoice, issue, service, chatbot, profile, notification |
| `common/` | Response wrapper và paging |

DTO giúp tránh trả entity trực tiếp ra client và giữ API ổn định hơn khi schema thay đổi.
