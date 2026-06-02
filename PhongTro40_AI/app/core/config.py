import os
import calendar
import datetime
import requests

# CẤU HÌNH MÔI TRƯỜNG & DIALOGFLOW
GOOGLE_APPLICATION_CREDENTIALS = os.environ.get(
    "GOOGLE_APPLICATION_CREDENTIALS",
    os.environ.get("DIALOGFLOW_CREDENTIALS_PATH", "dialogflow-key.json")
)
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = GOOGLE_APPLICATION_CREDENTIALS
PROJECT_ID = os.environ.get("DIALOGFLOW_PROJECT_ID", "phongtro40-bot-ctfn")

# CẤU HÌNH DATABASE MySQL
DB_CONFIG = {
    "host": os.environ.get("DB_HOST", "localhost"),
    "user": os.environ.get("DB_USER", "root"),
    "password": os.environ.get("DB_PASSWORD", ""),
    "database": os.environ.get("DB_NAME", "smartroomms"),
    "port": int(os.environ.get("DB_PORT", 3306))
}

def get_real_temperature(thang: int, nam: int) -> float:
    try:
        start_date = f"{nam}-{thang:02d}-01"
        last_day = calendar.monthrange(nam, thang)[1]
        end_date = f"{nam}-{thang:02d}-{last_day}"
        today = datetime.date.today()
        if nam == today.year and thang == today.month:
            end_date = today.strftime("%Y-%m-%d")

        lat, lon = 21.01, 105.52  # Tọa độ Hòa Lạc
        url = f"https://archive-api.open-meteo.com/v1/archive?latitude={lat}&longitude={lon}&start_date={start_date}&end_date={end_date}&daily=temperature_2m_mean&timezone=Asia%2FBangkok"

        response = requests.get(url, timeout=5)
        if response.status_code == 200:
            data = response.json()
            daily_temps = data.get("daily", {}).get("temperature_2m_mean", [])
            valid_temps = [t for t in daily_temps if t is not None]
            if valid_temps: 
                return round(sum(valid_temps) / len(valid_temps), 1)
    except Exception as e:
        print(f"Lỗi gọi API thời tiết: {e}")
        
    # Fallback
    return {
        1: 17.0, 2: 18.5, 3: 22.0, 4: 26.0, 
        5: 32.5, 6: 35.0, 7: 34.5, 8: 33.0, 
        9: 29.0, 10: 25.5, 11: 22.0, 12: 18.0
    }.get(thang, 25.0)
