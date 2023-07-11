import socket
import wmi
import psutil
import datetime
import time
import subprocess
import pymysql
from pynvml import *
from comtypes import client
from datetime import timedelta

class DataClient:

    def find_openhardwaremonitor_hwmon(self):
        wmi = self.wmi
        temperature_infos = wmi.Sensor()
        for sensor in temperature_infos:
            if sensor.SensorType == 'Temperature':
                if sensor.Name.startswith("CPU Package"):
                    return sensor

        print("Error: OpenHardwareMonitor not found")
        return None

    def __init__(self, ip, port):
        self.server_ip = ip
        self.server_port = port
        self.state = None
        self.wmi = wmi.WMI(namespace="root/OpenHardwareMonitor")
        self.hwmon = self.find_openhardwaremonitor_hwmon()
        self.first_run = True  # 처음 실행 표시를 위한 변수 추가

    def is_openhardwaremonitor_running(self):
        for proc in psutil.process_iter():
            if proc.name() == "OpenHardwareMonitor.exe":
                time.sleep(2)
                return True
        return False

    def run_openhardwaremonitor_exe(self, path="OpenHardwareMonitor.exe"):
        if self.is_openhardwaremonitor_running():
            print("OpenHardwareMonitor is already running.")
            return
        try:
            subprocess.run(['powershell', 'Start-Process', path, '-Verb', 'RunAs'])
            print(f"{path} was executed")
            print("관리저 권한으로 실행 되었습니다.")
        except Exception as e:
            print(f"Error: Could not execute {path}.")
            print(e)

    def get_client_ip(self):
        try:
            hostname = socket.gethostname()
            ip_address = socket.gethostbyname(hostname)
            return ip_address
        except Exception as e:
            print(f"Error: {e}")
            return None

    def get_cpu_temperature(self):
        hwmon = self.find_openhardwaremonitor_hwmon()

        if hwmon is None:
            return 0.0

        try:
            return hwmon.Value
        except BaseException as err:
            print("Failed to read temperature:", err)
            return None

    def get_cpu_usage(self):
        return psutil.cpu_percent()

    def get_gpu_temperature(self):
        return nvmlDeviceGetTemperature(gpu_handle, NVML_TEMPERATURE_GPU)

    def get_gpu_usage(self):
        gpu_util = nvmlDeviceGetUtilizationRates(gpu_handle)
        return gpu_util.gpu

    def connect_to_database(self, host, user, password, database_name):
        self.connection = pymysql.connect(
            host=host,
            user=user,
            password=password,
            db=database_name,
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )
        self.cursor = self.connection.cursor()

    def save_data_to_database(self, data):

        # 처음 실행 상태 확인 후 조건 처리 추가
        if self.first_run:
            data["state"] = "on"
            self.first_run = False
        else:
            data["state"] = "normal"

        query = f"""
        INSERT INTO tempdata
        (ip, timestamp, cpu_temp, cpu_usage, gpu_temp, gpu_usage, state)
        VALUES
        ('{data["client_ip"]}', '{data["timestamp"]}', {data["cpu_temp"]},
        {data["cpu_usage"]}, {data["gpu_temp"]}, {data["gpu_usage"]}, '{data["state"]}')
        """

        try:
            self.cursor.execute(query)
            self.connection.commit()
        except Exception as e:
            print(f"Error: {e}")

if __name__ == "__main__":
    nvmlInit()
    gpu_handle = nvmlDeviceGetHandleByIndex(0)

    # MariaDB 접속 정보 (변경해야 할 수도 있습니다)
    host = "192.168.65.132"
    user = "root"
    password = "devk"
    database_name = "temperature"

    client = DataClient("192.168.65.132", 9090)
    client.connect_to_database(host, user, password, database_name)
    client.run_openhardwaremonitor_exe() # OpenHardwareMonitor.exe 실행

    next_data_send_time = None

    while True:
        try:
            current_time = datetime.datetime.now()
            is_data_send_due = True if next_data_send_time is None or current_time >= next_data_send_time else False

            if is_data_send_due:
                # Fetch the data
                client_ip = client.get_client_ip()
                cpu_temp = round(client.get_cpu_temperature(), 1)  # Change
                cpu_usage = round(client.get_cpu_usage(), 1)  # Change
                gpu_temp = round(client.get_gpu_temperature(), 1)  # Change
                gpu_usage = round(client.get_gpu_usage(), 1)  # Change

                # Get the current system time
                timestamp = current_time.strftime('%Y-%m-%d %H:%M:%S')

                # Print the fetched data on the console
                print("#########################")
                print("Client IP:", client_ip, '\n')
                print("Timestamp:", timestamp, '\n')
                print("CPU Temperature:", cpu_temp, "°C", '\n')
                print("CPU Usage:", cpu_usage, "%", '\n')
                print("GPU Temperature:", gpu_temp, "°C", '\n')
                print("GPU Usage:", gpu_usage, "%", '\n')
                print("#########################")
                print("\n")

                # Build the data payload
                data = {
                    "client_ip": client_ip,
                    "timestamp": timestamp,
                    "cpu_temp": cpu_temp,
                    "cpu_usage": cpu_usage,
                    "gpu_temp": gpu_temp,
                    "gpu_usage": gpu_usage,
                    "state": "",
                }
                client.save_data_to_database(data)

                # 처음 이후 5분 단위 시간을 구하기
                current_minutes = current_time.minute
                next_5_minute_mark = (current_minutes // 5 + 1) * 5
                next_data_send_time = current_time.replace(minute=next_5_minute_mark % 60, second=0, microsecond=0)
                
                if next_5_minute_mark >= 60:  # 다음 시간으로 넘어갈 경우 시간을 증가시킵니다.
                    next_data_send_time += timedelta(hours=1)

            time.sleep(1)  # 긴 지연시간 (예: 300초) 동안 전체 시스템을 차단하는 대신 루프에서 더욱 정교한 제어를 허용하기 위해 작은 값으로 변경

        except Exception as e:
            print(f"Error: {e}")
            time.sleep(5)
            break

    nvmlShutdown()