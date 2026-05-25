import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../core/session/session_store.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_text_styles.dart';
import '../../../core/widgets/app_button.dart';
import '../../../core/widgets/app_card.dart';
import '../../../core/widgets/app_loading.dart';
import '../../../core/widgets/app_text_field.dart';
import '../../../core/widgets/avatar_picker_widget.dart';
import '../../../core/widgets/host_bottom_nav.dart';
import '../../../data/models/tenant_profile_model.dart';
import '../../../data/services/tenant_profile_service.dart';
import '../../../providers/auth_provider.dart';
import '../../../providers/notification_badge_provider.dart';
import '../../../providers/theme_provider.dart';

class HostProfileScreen extends StatefulWidget {
  const HostProfileScreen({super.key});

  @override
  State<HostProfileScreen> createState() => _HostProfileScreenState();
}

class _HostProfileScreenState extends State<HostProfileScreen> {
  final _profileService = TenantProfileService(); // Reuse same basic profile getters
  final _nameCtrl = TextEditingController();
  final _phoneCtrl = TextEditingController();

  int? _userId;
  String? _avatarUrl;
  String _fullName = '';
  String _email = '';
  String _phone = '';
  bool _editMode = false;
  bool _loadingProfile = true;
  bool _saving = false;

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  @override
  void dispose() {
    _nameCtrl.dispose();
    _phoneCtrl.dispose();
    super.dispose();
  }

  Future<void> _loadProfile() async {
    final auth = context.read<AuthProvider>();
    final cachedUser = auth.user;
    final userId = await auth.getUserId() ?? cachedUser?.userId;

    if (!mounted) return;

    if (cachedUser != null) {
      setState(() {
        _fullName = cachedUser.fullName;
        _email = cachedUser.email;
        _nameCtrl.text = cachedUser.fullName;
      });
    }

    if (userId == null) {
      setState(() => _loadingProfile = false);
      _showSnack('Không tìm thấy thông tin tài khoản.', AppColors.error);
      return;
    }

    setState(() {
      _userId = userId;
      _loadingProfile = true;
    });

    try {
      final profile = await _profileService.getProfile(userId);
      if (!mounted) return;
      _applyProfile(profile);
    } catch (e) {
      if (!mounted) return;
      _showSnack('Không thể tải hồ sơ: ${_friendlyError(e)}', AppColors.error);
    } finally {
      if (mounted) setState(() => _loadingProfile = false);
    }
  }

  Future<void> _save() async {
    final userId = _userId;
    final fullName = _nameCtrl.text.trim();
    final phone = _phoneCtrl.text.trim();

    if (userId == null) {
      _showSnack('Không tìm thấy tài khoản để cập nhật.', AppColors.error);
      return;
    }
    if (fullName.isEmpty) {
      _showSnack('Họ tên không được để trống.', AppColors.error);
      return;
    }

    FocusScope.of(context).unfocus();
    setState(() => _saving = true);

    try {
      final profile = await _profileService.updateProfile(
        userId: userId,
        fullName: fullName,
        phoneNumber: phone,
        avatarUrl: _avatarUrl,
      );
      if (!mounted) return;
      _applyProfile(profile);
      setState(() => _editMode = false);
      _showSnack('Cập nhật hồ sơ thành công.', AppColors.success);
    } catch (e) {
      if (!mounted) return;
      _showSnack(
        'Không thể cập nhật hồ sơ: ${_friendlyError(e)}',
        AppColors.error,
      );
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  Future<void> _logout() async {
    context.read<NotificationBadgeProvider>().reset();
    await context.read<AuthProvider>().logout();
    if (!mounted) return;
    context.go('/login');
  }

  void _toggleEditMode() {
    setState(() {
      if (_editMode) {
        _nameCtrl.text = _fullName;
        _phoneCtrl.text = _phone;
      }
      _editMode = !_editMode;
    });
  }

  void _applyProfile(TenantProfileModel profile) {
    setState(() {
      _userId = profile.userId;
      _fullName = profile.fullName;
      _email = profile.email;
      _phone = profile.phoneNumber;
      _avatarUrl = _sanitize(profile.avatarUrl);
      _nameCtrl.text = profile.fullName;
      _phoneCtrl.text = profile.phoneNumber;
    });
    SessionStore.instance.updateProfile(
      fullName: profile.fullName,
      email: profile.email,
    );
    context.read<AuthProvider>().refreshSessionUser();
  }

  Future<void> _saveAvatarToPrefs(String url) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      if (url.isEmpty) {
        await prefs.remove('avatar_url');
      } else {
        await prefs.setString('avatar_url', url);
      }
    } catch (_) {}
  }

  String? _sanitize(String? url) {
    if (url == null) return null;
    final t = url.trim();
    return t.isEmpty ? null : t;
  }

  String _friendlyError(Object e) {
    final msg = e.toString();
    if (msg.contains('SocketException') || msg.contains('connection')) {
      return 'Không có kết nối mạng';
    }
    if (msg.startsWith('Exception: ')) return msg.substring(11);
    return msg;
  }

  void _showSnack(String message, Color color) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: color,
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

    return Scaffold(
      backgroundColor: bg,
      appBar: AppBar(
        backgroundColor: bg,
        title: Text(
          'Hồ sơ của tôi',
          style: AppTextStyles.h3.copyWith(color: fg),
        ),
        actions: [
          IconButton(
            icon: Icon(
              isDark ? Icons.light_mode_outlined : Icons.dark_mode_outlined,
              color: subtext,
              size: 22,
            ),
            onPressed: () => context.read<ThemeProvider>().toggleTheme(),
          ),
          TextButton(
            onPressed: (_loadingProfile || _saving) ? null : _toggleEditMode,
            child: Text(
              _editMode ? 'Huỷ' : 'Sửa',
              style: const TextStyle(color: AppColors.accent),
            ),
          ),
        ],
      ),
      body: _loadingProfile
          ? const AppLoading()
          : RefreshIndicator(
              onRefresh: _loadProfile,
              child: SingleChildScrollView(
                physics: const AlwaysScrollableScrollPhysics(),
                padding: const EdgeInsets.all(24),
                child: Column(
                  children: [
                    Center(
                      child: _userId == null
                          ? const AppLoading()
                          : AvatarPickerWidget(
                              currentUrl: _avatarUrl,
                              userId: _userId!,
                              role: 'HOST',
                              size: 100,
                              onUploaded: (url) {
                                final clean = url.trim().isEmpty ? null : url;
                                setState(() => _avatarUrl = clean);
                                _saveAvatarToPrefs(url);
                              },
                            ),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      _fullName,
                      style: AppTextStyles.h3.copyWith(color: fg),
                      textAlign: TextAlign.center,
                    ),
                    Text(
                      _email,
                      style: AppTextStyles.bodySmall.copyWith(color: subtext),
                      textAlign: TextAlign.center,
                    ),

                    const SizedBox(height: 24),

                    AppCard(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Thông tin cá nhân',
                            style: AppTextStyles.body.copyWith(
                              color: fg,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                          const SizedBox(height: 16),

                          if (_editMode) ...[
                            AppTextField(
                              label: 'Họ và tên',
                              controller: _nameCtrl,
                              prefixIcon: Icons.person_outline_rounded,
                            ),
                            const SizedBox(height: 12),
                            AppTextField(
                              label: 'Số điện thoại',
                              controller: _phoneCtrl,
                              keyboardType: TextInputType.phone,
                              prefixIcon: Icons.phone_outlined,
                            ),
                            const SizedBox(height: 16),
                            AppButton(
                              label: 'Lưu thay đổi',
                              onPressed: _save,
                              loading: _saving,
                            ),
                          ] else ...[
                            _InfoRow(
                              Icons.person_outline_rounded,
                              'Họ tên',
                              _fullName,
                              subtext,
                              fg,
                            ),
                            Divider(color: border, height: 20),
                            _InfoRow(
                              Icons.mail_outline_rounded,
                              'Email',
                              _email,
                              subtext,
                              fg,
                            ),
                            Divider(color: border, height: 20),
                            _InfoRow(
                              Icons.phone_outlined,
                              'Điện thoại',
                              _phone.isEmpty ? 'Chưa cập nhật' : _phone,
                              subtext,
                              _phone.isEmpty ? subtext : fg,
                            ),
                          ],
                        ],
                      ),
                    ),

                    const SizedBox(height: 16),

                    AppCard(
                      child: Column(
                        children: [
                          _LinkRow(
                            Icons.receipt_long_outlined,
                            'Quản lý hóa đơn',
                            () => context.push('/host/invoices'),
                            subtext,
                            fg,
                          ),
                          Divider(color: border, height: 1),
                          _LinkRow(
                            Icons.group_outlined,
                            'Quản lý người thuê',
                            () => context.push('/host/tenants'),
                            subtext,
                            fg,
                          ),
                          Divider(color: border, height: 1),
                          _LinkRow(
                            Icons.report_outlined,
                            'Quản lý khiếu nại',
                            () => context.push('/host/issues'),
                            subtext,
                            fg,
                          ),
                          Divider(color: border, height: 1),
                          _LinkRow(
                            Icons.lock_outline_rounded,
                            'Đổi mật khẩu',
                            _showChangePasswordDialog,
                            subtext,
                            fg,
                          ),
                          Divider(color: border, height: 1),
                          _LinkRow(
                            Icons.help_outline_rounded,
                            'Trợ giúp & Hỗ trợ',
                            _showHelpDialog,
                            subtext,
                            fg,
                          ),
                        ],
                      ),
                    ),

                    const SizedBox(height: 16),

                    AppCard(
                      onTap: _logout,
                      child: Row(
                        children: [
                          Container(
                            width: 40,
                            height: 40,
                            decoration: BoxDecoration(
                              color: AppColors.error.withValues(alpha: 0.1),
                              borderRadius: BorderRadius.circular(10),
                            ),
                            child: const Icon(
                              Icons.logout_rounded,
                              color: AppColors.error,
                              size: 20,
                            ),
                          ),
                          const SizedBox(width: 12),
                          Text(
                            'Đăng xuất',
                            style: AppTextStyles.body.copyWith(
                              color: AppColors.error,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                          const Spacer(),
                          Icon(Icons.chevron_right_rounded, color: subtext),
                        ],
                      ),
                    ),

                    const SizedBox(height: 32),
                  ],
                ),
              ),
            ),
    );
  }

  void _showHelpDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Trợ giúp & Hỗ trợ'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Bạn cần hỗ trợ? Liên hệ với chúng tôi qua:'),
            const SizedBox(height: 16),
            _SupportRow(
              Icons.phone_rounded,
              'Hotline: 0987.654.321',
              () {},
            ),
            const SizedBox(height: 12),
            _SupportRow(
              Icons.chat_bubble_outline_rounded,
              'Zalo: 0987.654.321',
              () {},
            ),
            const SizedBox(height: 12),
            _SupportRow(
              Icons.email_outlined,
              'Email: support@smartroom.vn',
              () {},
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Đóng'),
          ),
        ],
      ),
    );
  }

  void _showChangePasswordDialog() {
    final oldPasswordCtrl = TextEditingController();
    final newPasswordCtrl = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Đổi mật khẩu'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            AppTextField(
              controller: oldPasswordCtrl,
              label: 'Mật khẩu cũ',
              obscure: true,
            ),
            const SizedBox(height: 16),
            AppTextField(
              controller: newPasswordCtrl,
              label: 'Mật khẩu mới',
              obscure: true,
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Hủy'),
          ),
          ElevatedButton(
            onPressed: () async {
              if (oldPasswordCtrl.text.isEmpty || newPasswordCtrl.text.isEmpty) {
                _showSnack('Vui lòng nhập đầy đủ thông tin', AppColors.error);
                return;
              }
              if (_userId == null) return;
              final ok = await context.read<AuthProvider>().changePassword(
                    userId: _userId!,
                    oldPassword: oldPasswordCtrl.text,
                    newPassword: newPasswordCtrl.text,
                  );
              if (ok) {
                if (mounted) Navigator.pop(context);
                _showSnack('Đổi mật khẩu thành công', AppColors.success);
              } else {
                if (mounted) {
                  _showSnack(
                    context.read<AuthProvider>().error ?? 'Lỗi đổi mật khẩu',
                    AppColors.error,
                  );
                }
              }
            },
            child: const Text('Xác nhận'),
          ),
        ],
      ),
    );
  }
}

class _SupportRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;

  const _SupportRow(this.icon, this.label, this.onTap);

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Row(
        children: [
          Icon(icon, size: 20, color: AppColors.accent),
          const SizedBox(width: 12),
          Text(label, style: AppTextStyles.body),
        ],
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final Color labelColor;
  final Color valueColor;

  const _InfoRow(
    this.icon,
    this.label,
    this.value,
    this.labelColor,
    this.valueColor,
  );

  @override
  Widget build(BuildContext context) => Row(
    children: [
      Icon(icon, size: 18, color: labelColor),
      const SizedBox(width: 12),
      Expanded(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              label,
              style: AppTextStyles.caption.copyWith(color: labelColor),
            ),
            const SizedBox(height: 2),
            Text(
              value,
              style: AppTextStyles.body.copyWith(
                color: valueColor,
                fontWeight: FontWeight.w500,
              ),
            ),
          ],
        ),
      ),
    ],
  );
}

class _LinkRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;
  final Color subtext;
  final Color fg;

  const _LinkRow(this.icon, this.label, this.onTap, this.subtext, this.fg);

  @override
  Widget build(BuildContext context) => ListTile(
    contentPadding: EdgeInsets.zero,
    leading: Icon(icon, color: AppColors.accent, size: 22),
    title: Text(label, style: AppTextStyles.body.copyWith(color: fg)),
    trailing: Icon(Icons.chevron_right_rounded, color: subtext, size: 20),
    onTap: onTap,
  );
}
