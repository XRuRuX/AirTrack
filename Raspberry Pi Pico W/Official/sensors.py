import machine
import dht
import time
import math

class Sensor:
    def get_data(raw_data):
        raise NotImplementedError("Subclasses must implement this method")

class PMS5003(Sensor):
    def validate_packet(raw_data):
         # Check packet size
        if len(raw_data) != 32:
             print("Invalid packet size\n")
             return False
        
        # Check packet start signature (0x42, 0x4d)
        if raw_data[0] != 0x42 or raw_data[1] != 0x4d:
             print("Invalid packet signature\n")
             return False

         # Check checksum
        checksum = sum(raw_data[:30]) & 0xFFFF # It only holds the last 16 bits of the sum because the recived checksum will be on only 16 bits
        received_checksum = (raw_data[30] << 8) | raw_data[31]
        if checksum != received_checksum:
            print("Invalid checksum!\n")
            return False
        
        return True
             
    def get_data(raw_data):
        if PMS5003.validate_packet(raw_data):
            pm2_5 = (raw_data[12] << 8) | raw_data[13]
            pm10 = (raw_data[14] << 8) | raw_data[15]

            return {"PM2.5": pm2_5, "PM10": pm10}
        else:
            return None

class DHT22(Sensor):
    def __init__(self, pin_number):
        self.dht_pin = machine.Pin(pin_number)
        self.dht_sensor = dht.DHT22(self.dht_pin)

    def get_data(self):
        try:
            self.dht_sensor.measure()
            temperature_celsius = self.dht_sensor.temperature()
            humidity_percent = self.dht_sensor.humidity()
            return {"Temperature": temperature_celsius, "Humidity": humidity_percent}
        except Exception as e:
            print("Error reading DHT22:", str(e))
            return None

class MQ131(Sensor):
    def __init__(self, pin_number, adc_ref=3.3, Vc=5.0, RL = 1000000, R0 = 98694240):
        self.adc = machine.ADC(pin_number)
        self.adc_ref = adc_ref
        self.Vc = Vc  
        self.RL = RL  
        self.R0 = R0  
    
    def get_data(self):
        try:
            raw = self.adc.read_u16()
            voltage_adc = (raw / 65535) * self.adc_ref
            VRL = voltage_adc
            Rs = self.RL * (self.Vc / VRL - 1)
            ozone_ppb, ratio = self.calc_ozone(Rs, self.R0)
            
            return ozone_ppb
        
        except Exception as e:
            print("Error reading MQ131:", str(e))
            return None

    # Function for calculating ozone concentration (ppb) based on the ratio Rs/R0
    def calc_ozone(self, Rs, R0):
        ratio = Rs / R0
        if ratio <= 0:
            return 0, ratio
        # Hypothetical logarithmic relationship: at Rs/R₀ = 1 → 10 ppb and at Rs/R₀ = 0.5 → ~200 ppb
        ozone_ppb = 10 ** ((-4.32 * math.log10(ratio)) + 1)
        return ozone_ppb, ratio
