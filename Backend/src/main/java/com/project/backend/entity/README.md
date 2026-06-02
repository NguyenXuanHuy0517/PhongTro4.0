# Entities

Entities ánh xạ các bảng chính của database `smartroomms`.

Nhóm domain chính:

- User/Role: tài khoản và phân quyền.
- MotelArea/Room/RoomStatusHistory: khu trọ, phòng và lịch sử trạng thái.
- Contract/ContractService/Deposit: hợp đồng, dịch vụ đăng ký và đặt cọc.
- Invoice/Issue/Notification: hóa đơn, sự cố và thông báo.
- Equipment/RoomAsset: tài sản và phân bổ vào phòng.
- ChatbotHistory: lịch sử chat AI.

Entity nên chỉ mô tả dữ liệu và quan hệ. Logic nghiệp vụ đặt trong service.
