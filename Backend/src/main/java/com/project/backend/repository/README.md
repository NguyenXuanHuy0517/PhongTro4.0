# Repositories

Repositories dùng Spring Data JPA để truy cập MariaDB.

## Vai trò

- Định nghĩa query theo entity.
- Cung cấp các method tìm kiếm, phân trang, thống kê.
- Giữ logic data access tách khỏi service.

## Lưu ý

- Ưu tiên method query của Spring Data hoặc `@Query` có parameter binding.
- Không nối chuỗi SQL từ input người dùng.
- Các view trong `db.sql` có thể được đọc qua native query khi cần báo cáo/tổng hợp.
