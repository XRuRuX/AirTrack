import machine, time, math

# ADC configuration GPIO26 - ADC0
ADC_PIN = 26
adc = machine.ADC(ADC_PIN)

adc_ref = 3.3       # ADC reference voltage
Vc = 5.0            # Sensor circuit supply voltage (5V)
RL = 1000000        # Load resistance RL in ohms (1 MΩ) - recommendation according to documentation

# Calibration: measurement in fresh air for 5 minutes
calibration_time = 300  # 300 seconds = 5 minutes
readings = []
print("Calibration in fresh air (5 minutes)...")
start_time = time.time()

while time.time() - start_time < calibration_time:
    raw = adc.read_u16() 
    voltage_adc = (raw / 65535) * adc_ref  # convert to voltage (V) read by ADC
    VRL = voltage_adc
    readings.append(VRL)
    time.sleep(1)

avg_VRL = sum(readings) / len(readings)

# VRL = Vc * (RL / (RL + R0))  =>  R0 = RL * (Vc/VRL - 1)
R0 = RL * (Vc / avg_VRL - 1)
print("Calibration completed!")
print("Average voltage VRL = {:.3f} V".format(avg_VRL))
print("R0 (resistance in fresh air) = {:.3f} ohms".format(R0))
print("We assume that this state corresponds to ~10 ppb ozone.\n")
