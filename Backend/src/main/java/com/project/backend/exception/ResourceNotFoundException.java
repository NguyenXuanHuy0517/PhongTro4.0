package com.project.backend.exception;

/**
 * Vai trò: Thành phần xử lý ngoại lệ của module auth-service.
 * Chức năng: Chuẩn hóa cách biểu diễn và xử lý lỗi liên quan đến resource not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
