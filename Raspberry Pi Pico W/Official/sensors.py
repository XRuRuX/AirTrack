import machine
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
        self.pin = machine.Pin(pin_number)

    # Blocking function for retrieving data according to the documentation
    def get_data(self):
        while True:
            try:
                pin = self.pin

                # Send the start signal 
                pin.init(machine.Pin.OUT)
                pin.value(0)
                time.sleep_ms(2)    # Send a low start signal and hold it for at least 1ms

                # Switch the pin to input mode with pull-up
                pin.init(machine.Pin.IN, machine.Pin.PULL_UP)

                # Wait for the response signal from the sensor
                if machine.time_pulse_us(pin, 0, 100) < 0:  # The sensor sends a low response signal and holds it for 80us  
                    raise Exception("Timeout on LOW response signal from sensor")
                if machine.time_pulse_us(pin, 1, 100) < 0:  # The sensor sends a high response signal and holds it for 80us, indicating the start of data transmission
                    raise Exception("Timeout on HIGH response signal from sensor")

                # Read the 40 bits of data
                bits = []
                for i in range(40):
                    if machine.time_pulse_us(pin, 0, 100) < 0:  # The sensor sends a low signal first and holds it for 50us
                        raise Exception("Timeout at the beginning of a bit")
                    pulse = machine.time_pulse_us(pin, 1, 100)  # The sensor then sends a high signal and holds it for 26-28us (if the bit is 0) or 70us (if the bit is 1)
                    if pulse < 0:
                        raise Exception("Timeout at the HIGH pulse of the bit")
                    # Set the cutoff at 50us in case of errors, as 50us is the middle between 27us and 70us  
                    if pulse > 50:          
                        bits.append(1)
                    else:
                        bits.append(0)

                # Group the 40 bits into 5 bytes
                bytes_list = []
                for i in range(5):
                    value = 0
                    for j in range(8):
                        value = (value << 1) | bits[i*8+j]
                    bytes_list.append(value)

                # Verify the checksum
                checksum = (sum(bytes_list[:4])) & 0xFF # Keep only the last 8 bits (one byte) of the total sum  
                if checksum != bytes_list[4]:
                    raise Exception("Invalid checksum: expected {}, got {}".format(checksum, bytes_list[4]))

                # Extract the temperature and the humidity
                humidity = ((bytes_list[0] << 8) | bytes_list[1]) / 10.0    # The first byte contains the integer value, and the second byte contains the fractional part  
                temp_raw = ((bytes_list[2] & 0x7F) << 8) | bytes_list[3]    # Clear the sign bit (if it is 1) to calculate the absolute value of the temperature
                temperature = temp_raw / 10.0          
                if bytes_list[2] & 0x80:    # If the sign bit is 1, the temperature is negative
                    temperature = -temperature

                return {
                    'Temperature': temperature,
                    'Humidity': humidity
                }

            except Exception as e:
                print("Error reading DHT22:", e)
                time.sleep(2)

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
