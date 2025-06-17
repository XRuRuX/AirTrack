from machine import Pin, UART
import time
from pms5003 import PMS5003
from dht22 import DHT22
from packet import PacketBuilder
from mics6814 import MICS6814
from mq131 import MQ131

# Initialize Bluetooth HC-05
uart_bluetooth = UART(0, 9600, 8, None)

# Initialize PMS5003 Sensor
uart_pms5003 = UART(1, 9600, 8, None)

# Initialize DHT22 Sensor
dht22 = DHT22(pin_number=2)

# Initialize MQ131 Sensor
mq131 = MQ131(pin_number=26)

# Initialize MICS6814 Sensor
mics6814 = MICS6814()

while True:
     # Check if there is any data available on UART
    if ((uart_pms5003.any() > 0) and (dht22 != None) and (mq131 != None)):
        data = uart_pms5003.read(32)  # Read 32 bytes (standard packet size of PMS5003)
        resultPMS5003 = PMS5003.get_data(data)
        time.sleep_ms(2000)

        print(f"PM2.5: {resultPMS5003['PM2.5']} ug/m3")
        print(f"PM10: {resultPMS5003['PM10']} ug/m3")

        dht_data = dht22.get_data()
        if dht_data:
            print(f"Temperature: {dht_data['Temperature']} °C")
            print(f"Humidity: {dht_data['Humidity']} %")

        mq131_data = mq131.get_data()
        if mq131_data:
            print(f"Ozone concentration: {mq131_data} ppb")

        co = round(mics6814.get_data(27), 2)
        no2 = round(mics6814.get_data(28), 2)
        if no2 and co:
            print(f"CO: {co} ppm")
            print(f"NO2: {no2} ppb")
            
        # Build packet according to documentation
        packet = PacketBuilder.encode("A7", 1, 22, resultPMS5003['PM2.5'], resultPMS5003['PM10'], mq131_data, co, no2, dht_data['Temperature'], dht_data['Humidity'])

        print(packet)
        uart_bluetooth.write(packet)
    