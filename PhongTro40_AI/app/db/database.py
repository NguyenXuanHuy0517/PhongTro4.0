import mysql.connector
from app.core.config import DB_CONFIG

def get_db_connection():
    return mysql.connector.connect(**DB_CONFIG)
