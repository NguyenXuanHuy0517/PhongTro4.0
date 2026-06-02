from fastapi import APIRouter, HTTPException
from app.models.anomaly_schemas import AnomalyCheckRequest, AnomalyCheckResponse
from app.db.database import get_db_connection
from app.services.anomaly_detector import check_anomaly_with_isolation_forest, trigger_anomaly_notification_to_host
from app.core.config import get_real_temperature

router = APIRouter()

@router.post("/anomaly/check", response_model=AnomalyCheckResponse)
async def check_anomaly(request: AnomalyCheckRequest):
    try:
        db = get_db_connection()
        is_elec_anomaly = False
        is_water_anomaly = False
        
        try:
            cursor = db.cursor(dictionary=True)
            
            # Check electricity anomaly
            if request.elec_usage > 0:
                history_query = "SELECT (elec_new - elec_old) AS usage_kwh FROM v_invoice_detail WHERE room_code = %s AND billing_month != %s ORDER BY billing_year ASC, billing_month ASC"
                cursor.execute(history_query, (request.room_code, request.billing_month))
                history_usages = [row['usage_kwh'] for row in cursor.fetchall()]
                
                is_elec_anomaly = check_anomaly_with_isolation_forest(history_usages, request.elec_usage)
                
                if is_elec_anomaly:
                    nhiet_do_tb = get_real_temperature(request.billing_month, request.billing_year)
                    he_so_nhiet = 'HOT' if nhiet_do_tb >= 32.0 else 'NORMAL'
                    trigger_anomaly_notification_to_host(
                        cursor=cursor, 
                        db=db, 
                        room_code=request.room_code, 
                        nhiet_do_tb=nhiet_do_tb, 
                        thang=request.billing_month, 
                        so_dien_tieu_thu=request.elec_usage, 
                        he_so_nhiet=he_so_nhiet
                    )

            # Note: For water anomaly, we could implement a similar logic if needed in the future
            # if request.water_usage > 0:
            #     # Similar Isolation Forest logic for water
            #     pass
                
        finally:
            if 'cursor' in locals():
                cursor.close()
            db.close()

        return AnomalyCheckResponse(
            status="success",
            is_elec_anomaly=is_elec_anomaly,
            is_water_anomaly=is_water_anomaly
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
