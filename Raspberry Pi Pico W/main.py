from machine import Pin
import time

led = Pin('LED', Pin.OUT) # Configure LED Pin as an output Pin

while True:
  led.value(True)  # Turn on the LED
  time.sleep(1)   # Wait for one second
  led.value(False)  # Turn off the LED
  time.sleep(1)   # Wait for one second