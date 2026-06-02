import numpy as np
from sklearn.ensemble import IsolationForest

def check_anomaly_with_isolation_forest(history_usages, current_usage):
    if len(history_usages) < 3: 
        return False
        
    all_data = history_usages + [current_usage]
    X = np.array(all_data).reshape(-1, 1)
    
    model = IsolationForest(contamination='auto', random_state=42)
    model.fit(X)
    predictions = model.predict(X)
    
    return predictions[-1] == -1

def trigger_anomaly_notification_to_host(cursor, db, room_code, nhiet_do_tb, thang, so_dien_tieu_thu, he_so_nhiet):
    try:
        # Lấy thông tin host_id và room_id từ room_code
        query = "SELECT host_id, room_id FROM v_room_overview WHERE room_code = %s LIMIT 1"
        cursor.execute(query, (room_code,))
        row = cursor.fetchone()
        
        if row:
            host_id = row['host_id']
            room_id = row['room_id']
            
            title = "Cảnh báo tiêu thụ điện năng bất thường"
            if he_so_nhiet == 'HOT':
                body = f"Phòng {room_code} có mức tiêu thụ điện tháng {thang} tăng mạnh ({so_dien_tieu_thu} kWh). Có thể do thời tiết nóng ({nhiet_do_tb}°C). Vui lòng lưu ý hoặc nhắc nhở người thuê vệ sinh điều hòa."
            else:
                body = f"CẢNH BÁO: Phòng {room_code} tiêu thụ điện tăng vọt ({so_dien_tieu_thu} kWh) trong khi thời tiết khá mát ({nhiet_do_tb}°C). Khả năng cao có thiết bị rò rỉ điện. Chủ trọ cần cử người kiểm tra ngay!"
            
            # Gửi system notification cho chủ trọ
            insert_query = """
                INSERT INTO notifications (user_id, type, title, body, ref_type, ref_id)
                VALUES (%s, 'SYSTEM', %s, %s, 'ROOM', %s)
            """
            cursor.execute(insert_query, (host_id, title, body, room_id))
            db.commit()
    except Exception as e:
        print(f"Lỗi khi gửi thông báo cảnh báo cho chủ trọ: {e}")
