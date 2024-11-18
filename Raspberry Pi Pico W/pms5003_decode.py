from machine import Pin, UART
import time

# Initialize UART
uart = UART(1, baudrate=9600, bits=8, parity=None, stop=1)

# Set up LED pin as output
led = Pin('LED', Pin.OUT)

while True:
    # Check if there is any data available on UART
    if uart.any() > 0:
        data = uart.read(32)  # Read 32 bytes (standard packet size of PMS5003)
        
        if data and len(data) == 32:
            # Check packet start signature (0x42, 0x4d)
            if data[0] == 0x42 and data[1] == 0x4d:
                # Check checksum
                checksum = sum(data[:30]) & 0xFFFF
                received_checksum = (data[30] << 8) | data[31]
                
                if checksum != received_checksum:
                    print("Invalid checksum!")
                    led.off()
                    continue

                # Decode the relevant data using the PMS5003 data structure
                pm1_0_cf = (data[4] << 8) | data[5]
                pm2_5_cf = (data[6] << 8) | data[7]
                pm10_cf = (data[8] << 8) | data[9]
                pm1_0 = (data[10] << 8) | data[11]
                pm2_5 = (data[12] << 8) | data[13]
                pm10 = (data[14] << 8) | data[15]

                # Display the decoded values
                print("PM1.0:", pm1_0, "ug/m3")
                print("PM2.5:", pm2_5, "ug/m3")
                print("PM10:", pm10, "ug/m3")

                # Lights the LED as a signal that a valid packet has been received
                led.on()
                time.sleep_ms(100)  # A short delay for visibility
                led.off()
            else:
                print("Invalid packet")
                led.off()  # Turn off the LED if the packet is not valid
        else:
            print("Insufficient data")
            led.off()  # Turn off the LED if the data is not complete
