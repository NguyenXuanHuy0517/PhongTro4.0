import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_text_styles.dart';
import '../../../core/utils/currency_utils.dart';
import '../../../core/utils/date_utils.dart';
import '../../../core/widgets/app_button.dart';
import '../../../core/widgets/app_card.dart';
import '../../../core/widgets/app_loading.dart';
import '../../../core/widgets/app_text_field.dart';
import '../../../data/models/room_model.dart';
import '../../../providers/auth_provider.dart';
import '../../../providers/deposit_provider.dart';
import '../../../providers/room_provider.dart';
import '../../../providers/tenant_provider.dart';

class DepositFormScreen extends StatefulWidget {
  const DepositFormScreen({super.key});

  @override
  State<DepositFormScreen> createState() => _DepositFormScreenState();
}

class _DepositFormScreenState extends State<DepositFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _amountCtrl = TextEditingController();
  final _noteCtrl = TextEditingController();
  final _tenantNameCtrl = TextEditingController();
  final _tenantEmailCtrl = TextEditingController();
  final _tenantPhoneCtrl = TextEditingController();
  final _tenantIdCardCtrl = TextEditingController();
  final _tenantPasswordCtrl = TextEditingController();

  int? _hostId;
  int? _selectedRoomId;
  int? _selectedTenantId;
  DateTime? _expectedCheckIn;
  bool _createNewTenant = false;
  bool _loading = false;
  bool _bootstrapping = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  @override
  void dispose() {
    _amountCtrl.dispose();
    _noteCtrl.dispose();
    _tenantNameCtrl.dispose();
    _tenantEmailCtrl.dispose();
    _tenantPhoneCtrl.dispose();
    _tenantIdCardCtrl.dispose();
    _tenantPasswordCtrl.dispose();
    super.dispose();
  }

  Future<void> _loadData() async {
    final hostId = await context.read<AuthProvider>().getUserId();
    if (!mounted) return;

    _hostId = hostId;
    if (hostId == null) {
      setState(() => _bootstrapping = false);
      return;
    }

    await Future.wait([
      context.read<RoomProvider>().fetchRooms(hostId),
      context.read<TenantProvider>().fetchTenants(hostId),
    ]);
    if (!mounted) return;

    final availableRooms = context.read<RoomProvider>().availableRooms;
    final tenants = context.read<TenantProvider>().tenants;
    setState(() {
      _selectedRoomId = availableRooms.length == 1
          ? availableRooms.first.roomId
          : null;
      _selectedTenantId = tenants.length == 1 ? tenants.first.userId : null;
      _createNewTenant = tenants.isEmpty;
      _bootstrapping = false;
    });
  }

  RoomModel? get _selectedRoom {
    if (_selectedRoomId == null) return null;
    for (final room in context.read<RoomProvider>().rooms) {
      if (room.roomId == _selectedRoomId) return room;
    }
    return null;
  }

  Future<void> _pickExpectedCheckIn() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: _expectedCheckIn ?? now.add(const Duration(days: 1)),
      firstDate: DateTime(now.year - 1),
      lastDate: DateTime(now.year + 10),
    );
    if (picked == null) return;
    setState(() => _expectedCheckIn = picked);
  }

  Future<void> _createRoom() async {
    await context.push('/host/rooms/new');
    if (!mounted || _hostId == null) return;
    await context.read<RoomProvider>().fetchRooms(_hostId!);
    if (!mounted) return;

    final rooms = context.read<RoomProvider>().availableRooms;
    if (rooms.length == 1) {
      setState(() => _selectedRoomId = rooms.first.roomId);
    }
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_selectedRoomId == null) {
      _showMessage('Vui lòng chọn phòng trống', isError: true);
      return;
    }

    setState(() => _loading = true);

    int? tenantId = _selectedTenantId;
    if (_createNewTenant) {
      final tenant = await context
          .read<TenantProvider>()
          .createTenantAndReturn({
            'fullName': _tenantNameCtrl.text.trim(),
            'email': _tenantEmailCtrl.text.trim(),
            'phoneNumber': _tenantPhoneCtrl.text.trim(),
            'idCardNumber': _tenantIdCardCtrl.text.trim(),
            'password': _tenantPasswordCtrl.text,
          });
      tenantId = tenant?.userId;
    }

    if (!mounted) return;
    if (tenantId == null) {
      setState(() => _loading = false);
      _showMessage(
        context.read<TenantProvider>().error ?? 'Tạo người thuê thất bại',
        isError: true,
      );
      return;
    }

    final amount = double.tryParse(_amountCtrl.text.replaceAll(',', '').trim());
    final ok = await context.read<DepositProvider>().createDeposit({
      'tenantId': tenantId,
      'roomId': _selectedRoomId,
      'amount': amount,
      'expectedCheckIn': _expectedCheckIn?.toIso8601String().split('T').first,
      'note': _noteCtrl.text.trim(),
    });

    if (!mounted) return;
    setState(() => _loading = false);

    if (!ok) {
      _showMessage(
        context.read<DepositProvider>().error ?? 'Tạo đặt cọc thất bại',
        isError: true,
      );
      return;
    }

    _showMessage('Tạo đặt cọc thành công');
    context.pop(true);
  }

  void _showMessage(String message, {bool isError = false}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? AppColors.error : AppColors.success,
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final bg = isDark ? AppColors.darkBg : AppColors.lightBg;
    final fg = isDark ? AppColors.darkFg : AppColors.lightFg;
    final subtext = isDark ? AppColors.darkSubtext : AppColors.lightSubtext;
    final border = isDark ? AppColors.darkBorder : AppColors.lightBorder;
    final rooms = context.watch<RoomProvider>().availableRooms;
    final tenants = context.watch<TenantProvider>().tenants;
    final selectedRoom = _selectedRoom;

    return Scaffold(
      backgroundColor: bg,
      appBar: AppBar(
        backgroundColor: bg,
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios_new_rounded, color: fg, size: 20),
          onPressed: () => context.pop(),
        ),
        title: Text('Tạo đặt cọc', style: AppTextStyles.h3.copyWith(color: fg)),
      ),
      body: _bootstrapping
          ? const AppLoading()
          : SingleChildScrollView(
              padding: const EdgeInsets.all(24),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '1. Chọn phòng giữ chỗ',
                      style: AppTextStyles.h3.copyWith(color: fg),
                    ),
                    const SizedBox(height: 12),
                    _DropdownField<int>(
                      label: 'Phòng trống *',
                      hint: 'Chọn phòng AVAILABLE',
                      value: _selectedRoomId,
                      items: rooms
                          .map(
                            (room) => DropdownMenuItem(
                              value: room.roomId,
                              child: Text(
                                '${room.roomCode} - ${room.areaName}',
                              ),
                            ),
                          )
                          .toList(),
                      onChanged: (value) => setState(() {
                        _selectedRoomId = value;
                        final room = _selectedRoom;
                        if (room != null && _amountCtrl.text.trim().isEmpty) {
                          _amountCtrl.text = room.basePrice.toStringAsFixed(0);
                        }
                      }),
                      border: border,
                      fg: fg,
                      subtext: subtext,
                    ),
                    if (rooms.isEmpty) ...[
                      const SizedBox(height: 8),
                      Text(
                        'Chưa có phòng trống để đặt cọc.',
                        style: AppTextStyles.bodySmall.copyWith(color: subtext),
                      ),
                      TextButton.icon(
                        onPressed: _createRoom,
                        icon: const Icon(Icons.add_business_outlined),
                        label: const Text('Thêm phòng mới'),
                      ),
                    ],
                    if (selectedRoom != null) ...[
                      const SizedBox(height: 12),
                      AppCard(
                        padding: const EdgeInsets.all(16),
                        child: _PreviewRows(
                          rows: [
                            (
                              'Phòng',
                              '${selectedRoom.roomCode} - ${selectedRoom.areaName}',
                            ),
                            (
                              'Giá phòng',
                              CurrencyUtils.format(selectedRoom.basePrice),
                            ),
                            (
                              'Giá điện',
                              CurrencyUtils.format(selectedRoom.elecPrice),
                            ),
                            (
                              'Giá nước',
                              CurrencyUtils.format(selectedRoom.waterPrice),
                            ),
                          ],
                        ),
                      ),
                    ],
                    const SizedBox(height: 24),
                    Text(
                      '2. Người thuê đặt cọc',
                      style: AppTextStyles.h3.copyWith(color: fg),
                    ),
                    const SizedBox(height: 12),
                    SegmentedButton<bool>(
                      segments: const [
                        ButtonSegment(
                          value: false,
                          label: Text('Chọn có sẵn'),
                          icon: Icon(Icons.person_search_outlined),
                        ),
                        ButtonSegment(
                          value: true,
                          label: Text('Tạo mới'),
                          icon: Icon(Icons.person_add_alt_1_outlined),
                        ),
                      ],
                      selected: {_createNewTenant},
                      onSelectionChanged: (values) =>
                          setState(() => _createNewTenant = values.first),
                    ),
                    const SizedBox(height: 12),
                    if (_createNewTenant)
                      _NewTenantFields(
                        nameCtrl: _tenantNameCtrl,
                        emailCtrl: _tenantEmailCtrl,
                        phoneCtrl: _tenantPhoneCtrl,
                        idCardCtrl: _tenantIdCardCtrl,
                        passwordCtrl: _tenantPasswordCtrl,
                      )
                    else
                      _DropdownField<int>(
                        label: 'Người thuê *',
                        hint: 'Chọn người thuê',
                        value: _selectedTenantId,
                        items: tenants
                            .map(
                              (tenant) => DropdownMenuItem(
                                value: tenant.userId,
                                child: Text(
                                  '${tenant.fullName} - ${tenant.phoneNumber}',
                                ),
                              ),
                            )
                            .toList(),
                        onChanged: (value) =>
                            setState(() => _selectedTenantId = value),
                        validator: (value) => value == null
                            ? 'Vui lòng chọn người thuê hoặc tạo mới'
                            : null,
                        border: border,
                        fg: fg,
                        subtext: subtext,
                      ),
                    const SizedBox(height: 24),
                    Text(
                      '3. Thông tin cọc',
                      style: AppTextStyles.h3.copyWith(color: fg),
                    ),
                    const SizedBox(height: 16),
                    AppTextField(
                      label: 'Số tiền cọc *',
                      hint: '1500000',
                      controller: _amountCtrl,
                      keyboardType: TextInputType.number,
                      prefixIcon: Icons.savings_outlined,
                      validator: (value) {
                        final parsed = double.tryParse(
                          (value ?? '').replaceAll(',', '').trim(),
                        );
                        if (parsed == null || parsed <= 0) {
                          return 'Vui lòng nhập số tiền cọc hợp lệ';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    _DateField(
                      label: 'Ngày dự kiến vào',
                      value: _expectedCheckIn == null
                          ? null
                          : AppDateUtils.formatDate(
                              _expectedCheckIn!.toIso8601String(),
                            ),
                      onTap: _pickExpectedCheckIn,
                      border: border,
                      fg: fg,
                      subtext: subtext,
                    ),
                    const SizedBox(height: 16),
                    AppTextField(
                      label: 'Ghi chú',
                      hint: 'Ví dụ: đã chuyển khoản, hẹn ký hợp đồng...',
                      controller: _noteCtrl,
                      maxLines: 4,
                    ),
                    const SizedBox(height: 32),
                    AppButton(
                      label: 'Tạo đặt cọc',
                      icon: Icons.savings_outlined,
                      loading: _loading,
                      onPressed: _submit,
                    ),
                  ],
                ),
              ),
            ),
    );
  }
}

class _NewTenantFields extends StatelessWidget {
  final TextEditingController nameCtrl;
  final TextEditingController emailCtrl;
  final TextEditingController phoneCtrl;
  final TextEditingController idCardCtrl;
  final TextEditingController passwordCtrl;

  const _NewTenantFields({
    required this.nameCtrl,
    required this.emailCtrl,
    required this.phoneCtrl,
    required this.idCardCtrl,
    required this.passwordCtrl,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        AppTextField(
          label: 'Họ và tên *',
          hint: 'Nguyễn Văn A',
          controller: nameCtrl,
          prefixIcon: Icons.person_outline_rounded,
          validator: (value) =>
              (value ?? '').trim().isEmpty ? 'Vui lòng nhập họ tên' : null,
        ),
        const SizedBox(height: 16),
        AppTextField(
          label: 'Số điện thoại *',
          hint: '0901234567',
          controller: phoneCtrl,
          keyboardType: TextInputType.phone,
          prefixIcon: Icons.phone_outlined,
          validator: (value) {
            final phone = (value ?? '').trim();
            if (phone.isEmpty) return 'Vui lòng nhập số điện thoại';
            if (phone.length < 10) return 'Số điện thoại không hợp lệ';
            return null;
          },
        ),
        const SizedBox(height: 16),
        AppTextField(
          label: 'Email *',
          hint: 'tenant@example.com',
          controller: emailCtrl,
          keyboardType: TextInputType.emailAddress,
          prefixIcon: Icons.mail_outline_rounded,
          validator: (value) {
            final email = (value ?? '').trim();
            if (email.isEmpty) return 'Vui lòng nhập email';
            if (!email.contains('@')) return 'Email không hợp lệ';
            return null;
          },
        ),
        const SizedBox(height: 16),
        AppTextField(
          label: 'CCCD / CMND',
          hint: '001234567890',
          controller: idCardCtrl,
          keyboardType: TextInputType.number,
          prefixIcon: Icons.badge_outlined,
        ),
        const SizedBox(height: 16),
        AppTextField(
          label: 'Mật khẩu *',
          hint: 'Tối thiểu 6 ký tự',
          controller: passwordCtrl,
          obscure: true,
          prefixIcon: Icons.lock_outline_rounded,
          validator: (value) {
            if ((value ?? '').isEmpty) return 'Vui lòng nhập mật khẩu';
            if (value!.length < 6) return 'Mật khẩu tối thiểu 6 ký tự';
            return null;
          },
        ),
      ],
    );
  }
}

class _DropdownField<T> extends StatelessWidget {
  final String label;
  final String hint;
  final T? value;
  final List<DropdownMenuItem<T>> items;
  final void Function(T?) onChanged;
  final String? Function(T?)? validator;
  final Color border;
  final Color fg;
  final Color subtext;

  const _DropdownField({
    required this.label,
    required this.hint,
    required this.value,
    required this.items,
    required this.onChanged,
    this.validator,
    required this.border,
    required this.fg,
    required this.subtext,
  });

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final cardColor = isDark ? AppColors.darkCard : AppColors.lightCard;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: AppTextStyles.label.copyWith(color: subtext)),
        const SizedBox(height: 6),
        DropdownButtonFormField<T>(
          initialValue: value,
          isExpanded: true,
          items: items,
          onChanged: onChanged,
          validator: validator,
          dropdownColor: cardColor,
          style: AppTextStyles.body.copyWith(color: fg),
          decoration: InputDecoration(
            hintText: hint,
            hintStyle: AppTextStyles.body.copyWith(color: subtext),
            filled: true,
            fillColor: cardColor,
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: BorderSide(color: border),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: const BorderSide(color: AppColors.accent),
            ),
            errorBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: const BorderSide(color: AppColors.error),
            ),
            focusedErrorBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: const BorderSide(color: AppColors.error),
            ),
          ),
        ),
      ],
    );
  }
}

class _DateField extends StatelessWidget {
  final String label;
  final String? value;
  final VoidCallback onTap;
  final Color border;
  final Color fg;
  final Color subtext;

  const _DateField({
    required this.label,
    required this.value,
    required this.onTap,
    required this.border,
    required this.fg,
    required this.subtext,
  });

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final cardColor = isDark ? AppColors.darkCard : AppColors.lightCard;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: AppTextStyles.label.copyWith(color: subtext)),
        const SizedBox(height: 6),
        InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(12),
          child: Container(
            width: double.infinity,
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
            decoration: BoxDecoration(
              color: cardColor,
              border: Border.all(color: border),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Row(
              children: [
                Icon(Icons.event_available_outlined, size: 18, color: subtext),
                const SizedBox(width: 10),
                Text(
                  value ?? 'Chọn ngày',
                  style: AppTextStyles.body.copyWith(
                    color: value == null ? subtext : fg,
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class _PreviewRows extends StatelessWidget {
  final List<(String, String)> rows;

  const _PreviewRows({required this.rows});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final fg = isDark ? AppColors.darkFg : AppColors.lightFg;
    final subtext = isDark ? AppColors.darkSubtext : AppColors.lightSubtext;

    return Column(
      children: rows
          .map(
            (row) => Padding(
              padding: const EdgeInsets.only(bottom: 8),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  SizedBox(
                    width: 96,
                    child: Text(
                      row.$1,
                      style: AppTextStyles.bodySmall.copyWith(color: subtext),
                    ),
                  ),
                  Expanded(
                    child: Text(
                      row.$2,
                      style: AppTextStyles.bodySmall.copyWith(
                        color: fg,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          )
          .toList(),
    );
  }
}
