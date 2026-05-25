class DepositModel {
  final int depositId;
  final int tenantId;
  final int roomId;
  final String tenantName;
  final String roomCode;
  final double amount;
  final String? expectedCheckIn;
  final String status;
  final String? note;
  final String? depositDate;

  DepositModel({
    required this.depositId,
    required this.tenantId,
    required this.roomId,
    required this.tenantName,
    required this.roomCode,
    required this.amount,
    this.expectedCheckIn,
    required this.status,
    this.note,
    this.depositDate,
  });

  factory DepositModel.fromJson(Map<String, dynamic> json) {
    return DepositModel(
      depositId: json['depositId'],
      tenantId: json['tenantId'] ?? 0,
      roomId: json['roomId'] ?? 0,
      tenantName: json['tenantName'] ?? '',
      roomCode: json['roomCode'] ?? '',
      amount: (json['amount'] ?? 0).toDouble(),
      expectedCheckIn: json['expectedCheckIn'],
      status: json['status'] ?? 'PENDING',
      note: json['note'],
      depositDate: json['depositDate'],
    );
  }
}
