import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_text_styles.dart';
import '../../../providers/auth_provider.dart';
import '../../../data/services/api_client.dart';
import '../../../core/constants/api_constants.dart';
import '../../../core/widgets/tenant_bottom_nav.dart';

class _Message {
  final String text;
  final bool isUser;
  final bool isAnomaly;
  _Message({required this.text, required this.isUser, this.isAnomaly = false});
}

class TenantChatbotScreen extends StatefulWidget {
  const TenantChatbotScreen({super.key});
  @override
  State<TenantChatbotScreen> createState() => _TenantChatbotScreenState();
}

class _TenantChatbotScreenState extends State<TenantChatbotScreen> {
  final _ctrl = TextEditingController();
  final _scroll = ScrollController();
  final _messages = <_Message>[];
  bool _sending = false;
  bool _loadingHistory = true;
  int? _userId;

  @override
  void initState() {
    super.initState();
    _init();
  }

  Future<void> _init() async {
    _userId = await context.read<AuthProvider>().getUserId();
    await _loadChatHistory();
  }

  /// Tải lịch sử chat từ server
  Future<void> _loadChatHistory() async {
    if (_userId == null) {
      setState(() => _loadingHistory = false);
      return;
    }

    try {
      final dio = ApiClient.instance.tenantDio;
      final res = await dio.get(
        ApiConstants.chatbotHistory,
        queryParameters: {'userId': _userId},
      );

      final data = res.data['data'] as List<dynamic>?;
      if (data != null && data.isNotEmpty) {
        final historyMessages = <_Message>[];
        for (final item in data) {
          // Thêm câu hỏi của user
          final userQ = item['userQuestion'] as String?;
          if (userQ != null && userQ.isNotEmpty) {
            historyMessages.add(_Message(text: userQ, isUser: true));
          }
          // Thêm câu trả lời của bot
          final botR = item['botResponse'] as String?;
          if (botR != null && botR.isNotEmpty) {
            historyMessages.add(_Message(text: botR, isUser: false));
          }
        }

        if (mounted) {
          setState(() {
            // Thêm tin nhắn chào mặc định ở đầu
            _messages.add(_Message(
              text:
                  'Xin chào! Tôi là trợ lý SmartRoom. Bạn có thể hỏi tôi về hóa đơn, '
                  'hợp đồng, quy định khu trọ, hoặc báo sự cố.',
              isUser: false,
            ));
            _messages.addAll(historyMessages);
          });
          _scrollToBottom();
        }
      } else {
        // Không có lịch sử -> hiển thị tin nhắn chào
        if (mounted) {
          setState(() {
            _messages.add(_Message(
              text:
                  'Xin chào! Tôi là trợ lý SmartRoom. Bạn có thể hỏi tôi về hóa đơn, '
                  'hợp đồng, quy định khu trọ, hoặc báo sự cố.',
              isUser: false,
            ));
          });
        }
      }
    } catch (_) {
      // Lỗi load history -> hiển thị tin nhắn chào bình thường
      if (mounted) {
        setState(() {
          _messages.add(_Message(
            text:
                'Xin chào! Tôi là trợ lý SmartRoom. Bạn có thể hỏi tôi về hóa đơn, '
                'hợp đồng, quy định khu trọ, hoặc báo sự cố.',
            isUser: false,
          ));
        });
      }
    } finally {
      if (mounted) setState(() => _loadingHistory = false);
    }
  }

  @override
  void dispose() {
    _ctrl.dispose();
    _scroll.dispose();
    super.dispose();
  }

  Future<void> _send() async {
    final text = _ctrl.text.trim();
    if (text.isEmpty || _userId == null) return;
    _ctrl.clear();
    setState(() {
      _messages.add(_Message(text: text, isUser: true));
      _sending = true;
    });
    _scrollToBottom();

    try {
      final dio = ApiClient.instance.tenantDio;
      final res = await dio.post(
        ApiConstants.chatbot,
        queryParameters: {'userId': _userId},
        data: {'message': text},
      );
      final reply =
          res.data['data']?['reply'] as String? ??
          'Xin lỗi, tôi chưa hiểu câu hỏi này.';
      final isAnomaly = res.data['data']?['isAnomaly'] as bool? ?? res.data['data']?['anomaly'] as bool? ?? false;
      if (!mounted) return;
      setState(() => _messages.add(_Message(text: reply, isUser: false, isAnomaly: isAnomaly)));
    } catch (_) {
      if (!mounted) return;
      setState(
        () => _messages.add(
          _Message(
            text: 'Không thể kết nối server. Vui lòng thử lại.',
            isUser: false,
          ),
        ),
      );
    } finally {
      if (mounted) setState(() => _sending = false);
      _scrollToBottom();
    }
  }

  void _scrollToBottom() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scroll.hasClients) {
        _scroll.animateTo(
          _scroll.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

  /// Kiểm tra xem có nên hiển thị suggestion chips không
  /// Chỉ hiển thị khi chỉ có 1 tin nhắn (tin nhắn chào) và không có lịch sử
  bool get _showSuggestions => _messages.length == 1 && !_loadingHistory;

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final bg = isDark ? AppColors.darkBg : AppColors.lightBg;
    final fg = isDark ? AppColors.darkFg : AppColors.lightFg;
    final cardBg = isDark ? AppColors.darkCard : AppColors.lightCard;
    final border = isDark ? AppColors.darkBorder : AppColors.lightBorder;

    return Scaffold(
      backgroundColor: bg,
      appBar: AppBar(
        backgroundColor: bg,
        title: Row(
          children: [
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                gradient: const LinearGradient(colors: AppColors.gradient),
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Icon(
                Icons.smart_toy_outlined,
                color: Colors.white,
                size: 16,
              ),
            ),
            const SizedBox(width: 10),
            Text(
              'Trợ lý SmartRoom',
              style: AppTextStyles.h3.copyWith(color: fg),
            ),
          ],
        ),
      ),
      body: Column(
        children: [
          // Messages
          Expanded(
            child: _loadingHistory
                ? const Center(
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        CircularProgressIndicator(color: AppColors.accent),
                        SizedBox(height: 12),
                        Text('Đang tải lịch sử trò chuyện...'),
                      ],
                    ),
                  )
                : ListView.builder(
                    controller: _scroll,
                    padding: const EdgeInsets.all(16),
                    itemCount: _messages.length + (_sending ? 1 : 0),
                    itemBuilder: (_, i) {
                      if (_sending && i == _messages.length) {
                        return const _TypingBubble();
                      }
                      return _Bubble(msg: _messages[i], isDark: isDark);
                    },
                  ),
          ),

          // Suggestions - chỉ hiển thị khi chưa có lịch sử chat
          if (_showSuggestions)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Wrap(
                spacing: 8,
                runSpacing: 6,
                children: [
                          'Hóa đơn tháng này bao nhiêu?',
                          'Hợp đồng còn bao lâu?',
                          'Giá điện nước là bao nhiêu?',
                          'Quy định giờ giấc?',
                          'Báo cáo sự cố thiết bị',
                        ]
                        .map(
                          (q) => ActionChip(
                            label: Text(q, style: AppTextStyles.caption),
                            onPressed: () {
                              _ctrl.text = q;
                              _send();
                            },
                          ),
                        )
                        .toList(),
              ),
            ),

          // Input
          Container(
            padding: EdgeInsets.fromLTRB(
              16,
              8,
              16,
              MediaQuery.of(context).viewInsets.bottom + 16,
            ),
            decoration: BoxDecoration(
              color: cardBg,
              border: Border(top: BorderSide(color: border)),
            ),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _ctrl,
                    style: AppTextStyles.body.copyWith(color: fg),
                    decoration: InputDecoration(
                      hintText: 'Nhập câu hỏi...',
                      hintStyle: AppTextStyles.body.copyWith(
                        color: isDark
                            ? AppColors.darkSubtext
                            : AppColors.lightSubtext,
                      ),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(24),
                        borderSide: BorderSide(color: border),
                      ),
                      enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(24),
                        borderSide: BorderSide(color: border),
                      ),
                      focusedBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(24),
                        borderSide: const BorderSide(
                          color: AppColors.accent,
                          width: 1.5,
                        ),
                      ),
                      contentPadding: const EdgeInsets.symmetric(
                        horizontal: 16,
                        vertical: 10,
                      ),
                    ),
                    onSubmitted: (_) => _send(),
                    textInputAction: TextInputAction.send,
                  ),
                ),
                const SizedBox(width: 8),
                GestureDetector(
                  onTap: _send,
                  child: Container(
                    width: 44,
                    height: 44,
                    decoration: const BoxDecoration(
                      gradient: LinearGradient(colors: AppColors.gradient),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.send_rounded,
                      color: Colors.white,
                      size: 20,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
      bottomNavigationBar: const TenantBottomNav(currentIndex: 4),
    );
  }
}

class _Bubble extends StatelessWidget {
  final _Message msg;
  final bool isDark;
  const _Bubble({required this.msg, required this.isDark});
  @override
  Widget build(BuildContext context) {
    final isUser = msg.isUser;
    return Align(
      alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
        constraints: BoxConstraints(
          maxWidth: MediaQuery.of(context).size.width * 0.75,
        ),
        decoration: BoxDecoration(
          color: isUser
              ? AppColors.accent
              : (msg.isAnomaly
                  ? Colors.red.withAlpha(25) // Approx 0.1 opacity
                  : (isDark ? AppColors.darkCard : AppColors.lightCard)),
          borderRadius: BorderRadius.only(
            topLeft: const Radius.circular(16),
            topRight: const Radius.circular(16),
            bottomLeft: isUser ? const Radius.circular(16) : Radius.zero,
            bottomRight: isUser ? Radius.zero : const Radius.circular(16),
          ),
          border: isUser
              ? null
              : Border.all(
                  color: msg.isAnomaly
                      ? Colors.red
                      : (isDark ? AppColors.darkBorder : AppColors.lightBorder),
                ),
        ),
        child: Text(
          msg.text,
          style: AppTextStyles.body.copyWith(
            color: isUser
                ? Colors.white
                : (msg.isAnomaly
                    ? Colors.red[700]
                    : (isDark ? AppColors.darkFg : AppColors.lightFg)),
          ),
        ),
      ),
    );
  }
}

class _TypingBubble extends StatelessWidget {
  const _TypingBubble();
  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return Align(
      alignment: Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: isDark ? AppColors.darkCard : AppColors.lightCard,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(
            color: isDark ? AppColors.darkBorder : AppColors.lightBorder,
          ),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            _Dot(delay: 0),
            const SizedBox(width: 4),
            _Dot(delay: 150),
            const SizedBox(width: 4),
            _Dot(delay: 300),
          ],
        ),
      ),
    );
  }
}

class _Dot extends StatefulWidget {
  final int delay;
  const _Dot({required this.delay});
  @override
  State<_Dot> createState() => _DotState();
}

class _DotState extends State<_Dot> with SingleTickerProviderStateMixin {
  late AnimationController _ctrl;
  late Animation<double> _anim;
  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );
    _anim = Tween(
      begin: 0.3,
      end: 1.0,
    ).animate(CurvedAnimation(parent: _ctrl, curve: Curves.easeInOut));
    Future.delayed(Duration(milliseconds: widget.delay), () {
      if (mounted) _ctrl.repeat(reverse: true);
    });
  }

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) => FadeTransition(
    opacity: _anim,
    child: Container(
      width: 6,
      height: 6,
      decoration: const BoxDecoration(
        color: AppColors.accent,
        shape: BoxShape.circle,
      ),
    ),
  );
}
