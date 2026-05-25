import 'package:flutter_test/flutter_test.dart';
import 'package:frontend/data/models/deposit_model.dart';

void main() {
  test('DepositModel.fromJson parses ids needed for follow-up actions', () {
    final deposit = DepositModel.fromJson({
      'depositId': 33,
      'tenantId': 11,
      'roomId': 22,
      'tenantName': 'Nguyen Van A',
      'roomCode': 'P101',
      'amount': 1500000,
      'expectedCheckIn': '2026-05-20',
      'status': 'CONFIRMED',
      'note': 'Da nhan chuyen khoan',
      'depositDate': '2026-05-11T09:30:00',
    });

    expect(deposit.depositId, 33);
    expect(deposit.tenantId, 11);
    expect(deposit.roomId, 22);
    expect(deposit.status, 'CONFIRMED');
  });
}
