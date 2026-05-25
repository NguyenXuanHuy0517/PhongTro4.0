# Frontend

Frontend là ứng dụng Flutter của SmartRoomMS, dùng chung cho mobile và web. Ứng dụng có luồng riêng cho Admin, Host và Tenant, gọi Backend qua Dio và quản lý state bằng Provider.

## Công nghệ

| Nhóm | Công nghệ |
| --- | --- |
| Framework | Flutter, Dart SDK `^3.10.4` |
| State | Provider |
| Routing | GoRouter |
| HTTP | Dio |
| Local storage | SharedPreferences |
| UI | Material, custom theme, Inter/Calistoga/JetBrains Mono |

## Cấu trúc

```text
lib/
├── core/          # constants, session, theme, utils, widgets
├── data/          # models và API services
├── providers/     # ChangeNotifier state
├── presentation/  # Screens theo role
├── app_router.dart
└── main.dart
```

## Vai trò màn hình

| Khu vực | Nội dung chính |
| --- | --- |
| `presentation/auth` | Login, register, reset password |
| `presentation/admin` | Dashboard, host, room audit, revenue, profile |
| `presentation/host` | Area, room, tenant, deposit, contract, invoice, issue, notification, service |
| `presentation/tenant` | Dashboard, contract, invoice, issue, service, chatbot, notification, profile, rental join |

## Chạy local

```powershell
flutter pub get
flutter run
```

Chạy web:

```powershell
flutter run -d chrome
```

## Kiểm tra

```powershell
flutter analyze
flutter test
```

## Artefact không commit

Các thư mục như `.dart_tool/`, `build/`, `android/.gradle/`, `ios/Pods/`, `ios/.symlinks/` và `ios/Flutter/ephemeral/` là output có thể tạo lại bằng Flutter/CocoaPods nên đã được ignore.
