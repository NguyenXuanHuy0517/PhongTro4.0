# Providers

Providers quản lý state bằng `ChangeNotifier`.

Nhóm provider chính:

- Auth/session: `auth_provider.dart`
- Host: area, room, tenant, contract, invoice, deposit, issue, notification, report
- Tenant: dashboard, invoice list, issue list, notification list
- Admin: dashboard, host, room audit, revenue
- UI: theme, notification badge, paged list state

Provider nên giữ trạng thái loading/error/data và gọi data service. Widget không nên tự gọi HTTP trực tiếp.
