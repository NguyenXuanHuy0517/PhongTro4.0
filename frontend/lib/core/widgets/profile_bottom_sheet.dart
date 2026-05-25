import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';

import '../../providers/auth_provider.dart';
import '../../providers/notification_badge_provider.dart';
import '../session/session_store.dart';
import '../theme/app_colors.dart';
import '../theme/app_text_styles.dart';
import 'app_button.dart';
import 'app_text_field.dart';
import 'confirm_dialog.dart';

class ProfileBottomSheet extends StatefulWidget {
  const ProfileBottomSheet({super.key});

  static Future<void> show(BuildContext context) {
    return showModalBottomSheet(
      context: context,
      builder: (context) => const ProfileBottomSheet(),
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      isScrollControlled: true,
    );
  }

  @override
  State<ProfileBottomSheet> createState() => _ProfileBottomSheetState();
}

class _ProfileBottomSheetState extends State<ProfileBottomSheet> {
  late String? _fullName;
  late String? _email;
  late String? _role;
  int? _userId;

  @override
  void initState() {
    super.initState();
    final auth = context.read<AuthProvider>();
    final user = auth.user;
    _fullName = user?.fullName ?? SessionStore.instance.fullName;
    _email = user?.email ?? SessionStore.instance.email;
    _role = user?.role ?? SessionStore.instance.role;
    // Always read from SessionStore — it is persisted and always available
    _userId = SessionStore.instance.userId ?? user?.userId;
  }

  String _getRoleLabel(String? role) {
    if (role == 'ADMIN') return 'Quản trị viên';
    if (role == 'HOST') return 'Chủ trọ';
    if (role == 'TENANT') return 'Người thuê';
    return role ?? 'Người dùng';
  }

  Future<void> _handleLogout() async {
    final confirmed = await ConfirmDialog.show(
      context,
      title: 'Đăng xuất',
      message: 'Bạn có chắc chắn muốn đăng xuất?',
      confirmLabel: 'Đăng xuất',
      cancelLabel: 'Hủy',
      destructive: true,
    );

    if (!mounted || confirmed != true) return;
    context.read<NotificationBadgeProvider>().reset();
    await context.read<AuthProvider>().logout();
    if (!mounted) return;
    context.go('/login');
  }

  void _goToProfile() {
    Navigator.pop(context);
    final r = (_role ?? '').toUpperCase();
    if (r == 'ADMIN') {
      context.push('/admin/profile');
    } else if (r == 'HOST') {
      context.push('/host/profile');
    } else {
      context.push('/tenant/profile');
    }
  }

  void _showChangePasswordDialog() {
    Navigator.pop(context); // close bottom sheet first
    final oldPasswordCtrl = TextEditingController();
    final newPasswordCtrl = TextEditingController();

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (ctx) {
        bool loading = false;

        return StatefulBuilder(
          builder: (ctx, setDialogState) => AlertDialog(
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
                onPressed: loading ? null : () => Navigator.pop(ctx),
                child: const Text('Hủy'),
              ),
              ElevatedButton(
                onPressed: loading
                    ? null
                    : () async {
                        final old = oldPasswordCtrl.text.trim();
                        final newPw = newPasswordCtrl.text.trim();

                        if (old.isEmpty || newPw.isEmpty) {
                          ScaffoldMessenger.of(ctx).showSnackBar(
                            SnackBar(
                              content: const Text('Vui lòng nhập đầy đủ thông tin'),
                              backgroundColor: AppColors.error,
                              behavior: SnackBarBehavior.floating,
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(10),
                              ),
                            ),
                          );
                          return;
                        }

                        final uid = _userId;
                        if (uid == null) {
                          ScaffoldMessenger.of(ctx).showSnackBar(
                            SnackBar(
                              content: const Text('Không xác định được tài khoản'),
                              backgroundColor: AppColors.error,
                              behavior: SnackBarBehavior.floating,
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(10),
                              ),
                            ),
                          );
                          return;
                        }

                        setDialogState(() => loading = true);
                        bool ok = false;
                        String? errMsg;
                        try {
                          ok = await context.read<AuthProvider>().changePassword(
                                userId: uid,
                                oldPassword: old,
                                newPassword: newPw,
                              );
                          if (!ok) {
                            errMsg = context.read<AuthProvider>().error ??
                                'Mật khẩu cũ không đúng';
                          }
                        } catch (e) {
                          errMsg = 'Lỗi kết nối: ${e.toString()}';
                        } finally {
                          // Reset loading state regardless of outcome
                          if (ctx.mounted) {
                            setDialogState(() => loading = false);
                          }
                        }

                        if (!ctx.mounted) return;
                        Navigator.pop(ctx);

                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(
                            content: Text(
                              ok ? 'Đổi mật khẩu thành công' : (errMsg ?? 'Đổi mật khẩu thất bại'),
                            ),
                            backgroundColor: ok ? AppColors.success : AppColors.error,
                            behavior: SnackBarBehavior.floating,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10),
                            ),
                          ),
                        );
                      },
                child: loading
                    ? const SizedBox(
                        width: 18,
                        height: 18,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          color: Colors.white,
                        ),
                      )
                    : const Text('Xác nhận'),
              ),
            ],
          ),
        );
      },
    );
  }



  void _showHelpDialog() {
    Navigator.pop(context); // close bottom sheet first
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Trợ giúp & Hỗ trợ'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Bạn cần hỗ trợ? Liên hệ với chúng tôi qua:'),
            const SizedBox(height: 16),
            _SupportRow(Icons.phone_rounded, 'Hotline: 0987.654.321'),
            const SizedBox(height: 12),
            _SupportRow(Icons.chat_bubble_outline_rounded, 'Zalo: 0987.654.321'),
            const SizedBox(height: 12),
            _SupportRow(Icons.email_outlined, 'Email: support@smartroom.vn'),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text('Đóng'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final bg = isDark ? AppColors.darkBg : AppColors.lightBg;
    final card = isDark ? AppColors.darkCard : AppColors.lightCard;
    final fg = isDark ? AppColors.darkFg : AppColors.lightFg;
    final subtext = isDark ? AppColors.darkSubtext : AppColors.lightSubtext;

    return Container(
      decoration: BoxDecoration(
        color: bg,
        borderRadius: const BorderRadius.vertical(top: Radius.circular(20)),
      ),
      child: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Text(
                    'Tài khoản của tôi',
                    style: AppTextStyles.h2.copyWith(color: fg),
                  ),
                  const Spacer(),
                  IconButton(
                    icon: Icon(Icons.close_rounded, color: subtext),
                    onPressed: () => Navigator.pop(context),
                  ),
                ],
              ),
              const SizedBox(height: 24),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: card,
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(
                    color: isDark ? AppColors.darkBorder : AppColors.lightBorder,
                  ),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Center(
                      child: Container(
                        width: 64,
                        height: 64,
                        decoration: BoxDecoration(
                          gradient: const LinearGradient(
                            colors: AppColors.gradient,
                            begin: Alignment.topLeft,
                            end: Alignment.bottomRight,
                          ),
                          borderRadius: BorderRadius.circular(32),
                        ),
                        child: const Icon(
                          Icons.person_rounded,
                          color: Colors.white,
                          size: 32,
                        ),
                      ),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'Họ và tên',
                      style: AppTextStyles.caption.copyWith(color: subtext),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      _fullName ?? 'Chưa cập nhật',
                      style: AppTextStyles.body.copyWith(color: fg),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'Email',
                      style: AppTextStyles.caption.copyWith(color: subtext),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      _email ?? 'Chưa cập nhật',
                      style: AppTextStyles.body.copyWith(color: fg),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'Vai trò',
                      style: AppTextStyles.caption.copyWith(color: subtext),
                    ),
                    const SizedBox(height: 4),
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 6,
                      ),
                      decoration: BoxDecoration(
                        color: AppColors.accent.withValues(alpha: 0.1),
                        borderRadius: BorderRadius.circular(6),
                      ),
                      child: Text(
                        _getRoleLabel(_role),
                        style: AppTextStyles.bodySmall.copyWith(
                          color: AppColors.accent,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),
              _buildMenuItem(
                icon: Icons.person_outline_rounded,
                title: 'Chỉnh sửa hồ sơ',
                onTap: _goToProfile,
                fg: fg,
                subtext: subtext,
              ),
              _buildMenuItem(
                icon: Icons.lock_outline_rounded,
                title: 'Đổi mật khẩu',
                onTap: _showChangePasswordDialog,
                fg: fg,
                subtext: subtext,
              ),
              _buildMenuItem(
                icon: Icons.help_outline_rounded,
                title: 'Trợ giúp & Hỗ trợ',
                onTap: _showHelpDialog,
                fg: fg,
                subtext: subtext,
              ),
              const SizedBox(height: 24),
              AppButton(
                label: 'Đăng xuất',
                onPressed: _handleLogout,
                variant: AppButtonVariant.outlined,
                icon: Icons.logout_rounded,
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildMenuItem({
    required IconData icon,
    required String title,
    required VoidCallback onTap,
    required Color fg,
    required Color subtext,
  }) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(8),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 12),
            child: Row(
              children: [
                Icon(icon, color: subtext, size: 20),
                const SizedBox(width: 12),
                Expanded(
                  child: Text(
                    title,
                    style: AppTextStyles.body.copyWith(color: fg),
                  ),
                ),
                Icon(Icons.chevron_right_rounded, color: subtext, size: 20),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _SupportRow extends StatelessWidget {
  final IconData icon;
  final String label;

  const _SupportRow(this.icon, this.label);

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Icon(icon, size: 20, color: AppColors.accent),
        const SizedBox(width: 12),
        Text(label, style: AppTextStyles.body),
      ],
    );
  }
}
