from machine import Pin, UART
import utime

# Defining UART channel and Baud Rate
uart = UART(0, 38400)

def send_command(command):
    uart.write(command + '\r\n')
    utime.sleep(1)

def read_response():
    if uart.any():
        data = uart.read()  # Get data
        return str(data, 'UTF-8')
    return None

def main():
    while True:
        command = input("Enter command: ")
        send_command(command)
        
        response = read_response()
        if response:
            print("Response:", response)

if __name__ == "__main__":
    main()
