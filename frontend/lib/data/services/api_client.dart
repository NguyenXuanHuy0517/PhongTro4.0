import 'package:dio/dio.dart';
import '../../core/constants/api_constants.dart';
import '../../core/session/session_store.dart';

class ApiClient {
  static ApiClient? _instance;
  late final Dio _dio;

  ApiClient._() {
    _dio = _createDio(ApiConstants.baseUrl);
  }

  static ApiClient get instance {
    _instance ??= ApiClient._();
    return _instance!;
  }

  Dio get authDio => _dio;
  Dio get hostDio => _dio;
  Dio get tenantDio => _dio;
  Dio get adminDio => _dio;

  Dio _createDio(String baseUrl) {
    final dio = Dio(
      BaseOptions(
        baseUrl: baseUrl,
        connectTimeout: const Duration(seconds: 10),
        receiveTimeout: const Duration(seconds: 10),
        headers: {'Content-Type': 'application/json'},
      ),
    );

    dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) {
          final token = SessionStore.instance.token;
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }

          // Xóa Content-Type khi upload file để Dio tự set multipart
          if (options.data is FormData) {
            options.headers.remove('Content-Type');
          }

          handler.next(options);
        },
        onError: (error, handler) {
          handler.next(error);
        },
      ),
    );

    return dio;
  }
}
