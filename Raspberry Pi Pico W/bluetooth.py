from machine import Pin, UART

# Initialize UART channel and Baud Rate
uart = UART(0, 9600)

# Set up LED pin as output
led = Pin('LED', Pin.OUT)

while True:
    # Check if there is any data available on UART
    if uart.any() > 0:
        data = uart.read()  
        print(data)

        # Turn the LED on if the command is "on"
        if "on" in data.decode('utf-8'):
            led.value(1)  
            print('LED on')
            uart.write('LED on\n') 

        # Turn the LED off if the command is "off"
        elif "off" in data.decode('utf-8'):
            led.value(0)  
            print('LED off')
            uart.write('LED off\n')  
