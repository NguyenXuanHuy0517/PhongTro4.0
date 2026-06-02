import json
import datetime
import re
from app.core.config import get_real_temperature
from app.services.anomaly_detector import check_anomaly_with_isolation_forest, trigger_anomaly_notification_to_host


def _infer_month_from_message(message: str):
    """
    Suy luận tháng từ nội dung tin nhắn của người dùng.
    Trả về (thang, nam) hoặc None nếu không suy luận được.
    """
    msg_lower = message.lower().strip()
    now = datetime.date.today()

    # "tháng này", "tháng hiện tại", "this month"
    if any(kw in msg_lower for kw in ["tháng này", "tháng hiện tại", "this month", "thang nay"]):
        return now.month, now.year

    # "tháng trước", "tháng vừa rồi", "tháng rồi"
    if any(kw in msg_lower for kw in ["tháng trước", "tháng vừa rồi", "tháng rồi", "thang truoc"]):
        prev = now.replace(day=1) - datetime.timedelta(days=1)
        return prev.month, prev.year

    # "tháng sau"
    if any(kw in msg_lower for kw in ["tháng sau", "tháng tới", "thang sau"]):
        if now.month == 12:
            return 1, now.year + 1
        return now.month + 1, now.year

    # Trích xuất số tháng cụ thể: "tháng 3", "tháng 12", "t3", "t12"
    match = re.search(r'(?:tháng|thang|t)\s*(\d{1,2})', msg_lower)
    if match:
        m = int(match.group(1))
        if 1 <= m <= 12:
            # Nếu tháng > tháng hiện tại thì giả sử năm trước
            year = now.year if m <= now.month else now.year - 1
            return m, year

    return None


def handle_intent(intent_name, parameters, user_id, phong_id_context, cursor, db, original_message=""):
    reply_message = "Xin lỗi, mình chưa hiểu ý bạn. Bạn có thể hỏi mình về hóa đơn, nợ đọng, hợp đồng, hoặc báo cáo sự cố nhé!"
    is_anomaly = False

    # Xử lý chuẩn hóa mã phòng (VD: 101 -> P101)
    phong_param = parameters.get("phong")
    room_code_query = f"P{int(phong_param)}" if phong_param else (
        phong_id_context if phong_id_context.startswith("P") else f"P{phong_id_context}"
    )

    if intent_name == 'Tra_Cuu_Hoa_Don':
        thang = int(parameters.get("thang", 0)) if parameters.get("thang") else None
        nam_override = None

        # Nếu Dialogflow không trích xuất được tháng, suy luận từ tin nhắn
        if not thang:
            inferred = _infer_month_from_message(original_message)
            if inferred:
                thang, nam_override = inferred
            else:
                # Mặc định: tháng hiện tại
                now = datetime.date.today()
                thang = now.month
                nam_override = now.year

        # Lúc này thang luôn có giá trị (đã suy luận mặc định ở trên)
        history_query = "SELECT (elec_new - elec_old) AS usage_kwh FROM v_invoice_detail WHERE room_code = %s AND billing_month != %s ORDER BY billing_year ASC, billing_month ASC"
        cursor.execute(history_query, (room_code_query, thang))
        history_usages = [row['usage_kwh'] for row in cursor.fetchall()]

        # Nếu có nam_override (suy luận được năm), ưu tiên truy vấn theo năm cụ thể
        if nam_override:
            current_query = "SELECT elec_old, elec_new, total_amount, billing_year FROM v_invoice_detail WHERE room_code = %s AND billing_month = %s AND billing_year = %s ORDER BY billing_year DESC LIMIT 1"
            cursor.execute(current_query, (room_code_query, thang, nam_override))
        else:
            current_query = "SELECT elec_old, elec_new, total_amount, billing_year FROM v_invoice_detail WHERE room_code = %s AND billing_month = %s ORDER BY billing_year DESC LIMIT 1"
            cursor.execute(current_query, (room_code_query, thang))
        invoice = cursor.fetchone()

        if invoice:
            so_dien_tieu_thu = invoice['elec_new'] - invoice['elec_old']
            tien_tong = invoice['total_amount']
            nam_hoa_don = invoice['billing_year']

            is_anomaly = check_anomaly_with_isolation_forest(history_usages, so_dien_tieu_thu)

            # Chặn cảnh báo người thuê, chuyển trực tiếp notification về Chủ nhà
            if is_anomaly:
                nhiet_do_tb = get_real_temperature(thang, nam_hoa_don)
                he_so_nhiet = 'HOT' if nhiet_do_tb >= 32.0 else 'NORMAL'
                # Call the background notification insertion
                trigger_anomaly_notification_to_host(cursor, db, room_code_query, nhiet_do_tb, thang, so_dien_tieu_thu, he_so_nhiet)
            
            # Cảnh báo cho người thuê đã được dỡ bỏ khỏi reply
            reply_message = f"Tổng tiền hóa đơn tháng {thang}/{nam_hoa_don} của bạn là {tien_tong:,.0f} VNĐ."
            
            # Check thên nợ đọng cùng lúc luôn vì đã bỏ intent Kiem_Tra_No_Dong
            query_no = "SELECT SUM(i.total_amount) as tong_no FROM invoices i JOIN contracts c ON i.contract_id = c.contract_id WHERE c.tenant_id = %s AND i.status IN ('UNPAID', 'OVERDUE')"
            cursor.execute(query_no, (user_id,))
            result_no = cursor.fetchone()
            if result_no and result_no['tong_no'] and result_no['tong_no'] > tien_tong: # Có nợ cũ ngoài bill tháng này
               reply_message += f" Lưu ý: Bạn đang có tổng nợ (bao gồm các tháng cũ) là {result_no['tong_no']:,.0f} VNĐ."

        else:
            nam_display = nam_override if nam_override else datetime.date.today().year
            reply_message = f"Mình không tìm thấy thông tin hóa đơn tháng {thang}/{nam_display} của bạn."

    elif intent_name == 'Hoi_Gia_Dich_Vu':
        query_room = "SELECT elec_price, water_price, area_id FROM v_room_overview WHERE room_code = %s LIMIT 1"
        cursor.execute(query_room, (room_code_query,))
        room_info = cursor.fetchone()
        if room_info:
            msg = f"Giá điện là {room_info['elec_price']:,.0f}đ/kWh, nước là {room_info['water_price']:,.0f}đ/m3."
            cursor.execute("SELECT service_name, price, unit_name FROM services WHERE area_id = %s AND is_active = 1", (room_info['area_id'],))
            services = cursor.fetchall()
            if services:
                svc_list = ", ".join([f"{s['service_name']} {s['price']:,.0f}đ/{s['unit_name']}" for s in services])
                msg += f" Ngoài ra: {svc_list}."
            reply_message = msg
        else:
            reply_message = "Mình không tìm thấy thông tin bảng giá của phòng bạn."

    elif intent_name == 'Thong_Tin_Hop_Dong':
        query = "SELECT contract_code, end_date, actual_rent_price FROM v_active_contracts WHERE tenant_id = %s LIMIT 1"
        cursor.execute(query, (user_id,))
        contract = cursor.fetchone()
        if contract:
            reply_message = f"Hợp đồng ({contract['contract_code']}) của bạn có giá thuê {contract['actual_rent_price']:,.0f}đ/tháng. Ngày hết hạn: {contract['end_date'].strftime('%d/%m/%Y')}."
        else:
            reply_message = "Bạn không có hợp đồng nào đang hiệu lực."

    elif intent_name == 'Bao_Cao_Su_Co':
        thiet_bi = parameters.get("thiet_bi", "thiết bị")
        cursor.execute("SELECT room_id FROM v_active_contracts WHERE tenant_id = %s LIMIT 1", (user_id,))
        room = cursor.fetchone()
        if room:
            title = f"Báo hỏng {thiet_bi}"
            desc = f"Người thuê báo qua Bot AI: sự cố liên quan đến {thiet_bi}."
            cursor.execute(
                "INSERT INTO issues (room_id, tenant_id, title, description) VALUES (%s, %s, %s, %s)",
                (room['room_id'], user_id, title, desc))
            db.commit()
            reply_message = f"Ghi nhận sự cố '{thiet_bi}'. Quản lý sẽ cử người kiểm tra sửa chữa nhé!"
        else:
            reply_message = "Bạn cần có hợp đồng đang hoạt động để gửi yêu cầu sửa chữa."

    elif intent_name == 'Tien_Do_Su_Co':
        query = "SELECT title, status FROM issues WHERE tenant_id = %s ORDER BY created_at DESC LIMIT 1"
        cursor.execute(query, (user_id,))
        issue = cursor.fetchone()
        if issue:
            status_dict = {'OPEN': 'Vừa tiếp nhận, đang chờ xử lý', 'PROCESSING': 'Đang được thợ kiểm tra xử lý',
                           'RESOLVED': 'Đã khắc phục xong (chờ bạn xác nhận)', 'CLOSED': 'Đã đóng'}
            st_text = status_dict.get(issue['status'], issue['status'])
            reply_message = f"Sự cố gần nhất ('{issue['title']}') đang ở trạng thái: {st_text}."
        else:
            reply_message = "Bạn không có yêu cầu sửa chữa nào gần đây."

    elif intent_name == 'Lien_He_Chu_Tro':
        query = "SELECT host_name, host_phone FROM v_room_overview WHERE room_code = %s LIMIT 1"
        cursor.execute(query, (room_code_query,))
        host = cursor.fetchone()
        if host:
            reply_message = f"Quản lý của bạn là {host['host_name']} - SĐT: {host['host_phone']}."
        else:
            reply_message = "Chưa cập nhật SĐT quản lý khu vực này."
            
    elif intent_name == 'Noi_Quy_Khu_Tro':
        reply_message = "Các nội quy chung:\n- Đóng cửa khu trọ lúc 23:00, mở cửa lúc 05:00 sáng. \n- Khách qua đêm phải báo trước với chủ trọ.\n- Giữ vệ sinh chung trật tự sau 22:00.\nMọi vi phạm có thể bị phạt theo hợp đồng!"
        
    elif intent_name == 'Default Welcome Intent':
        reply_message = "Xin chào! Mình là trợ lý AI khu trọ. Bạn có thể hỏi mình về tiền điện nước, nội quy, hoặc báo mạng hỏng nhé."
        
    else:
        # Fallback processing
        reply_message = "Xin lỗi, hiện tại mình chưa hiểu ý bạn lắm. Bạn có thể diễn đạt lại hoặc hỏi về: hóa đơn, nội quy, giá dịch vụ hoặc báo hỏng đồ nhé."

    return reply_message, is_anomaly
