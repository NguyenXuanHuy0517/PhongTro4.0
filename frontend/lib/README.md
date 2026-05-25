# Flutter Lib

`lib/` là code chính của ứng dụng Flutter.

```text
lib/
├── core/          # Hạ tầng UI và helper dùng chung
├── data/          # Model và service gọi API
├── providers/     # State management bằng ChangeNotifier
├── presentation/  # Screens theo role
├── app_router.dart
└── main.dart
```

Luồng phổ biến:

```text
Screen -> Provider -> Data Service -> Backend API
       <- Provider state <- DTO/Model <-
```
