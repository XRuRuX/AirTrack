import machine, time

class DHT22():
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
                time.sleep_ms(200)