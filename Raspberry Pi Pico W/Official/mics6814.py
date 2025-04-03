import machine, time

class MICS6814:
    def __init__(self, adc_pin_CO=27, adc_pin_NO2=28, adc_ref=3.3, sensor_supply=5.0, Rload=33000, R0_CO = 1156815.3, R0_NO2 = 18834710.0):
        self.adc_CO_n = adc_pin_CO
        self.adc_NO2_n = adc_pin_NO2
        self.adc_CO = machine.ADC(adc_pin_CO)
        self.adc_NO2 = machine.ADC(adc_pin_NO2)
        self.adc_ref = adc_ref
        self.sensor_supply = sensor_supply
        self.Rload = Rload
        # Value taken from a previous calibration
        self.R0_CO = R0_CO
        self.R0_NO2 = R0_NO2

    def read_sensor(self, adc_pin):
        # Calculates the ADC input voltage based on its raw value. It converts the digital value read into a real voltage
        # V_adc = (raw / 65535) * adc_ref, where raw is the input value from the ADC, 65535 is the maximum value of the ADC,
        # and adc_ref is the reference voltage for the ADC in the Raspberry Pi Pico W
        if adc_pin == self.adc_CO_n:
             raw = self.adc_CO.read_u16()
        elif adc_pin == self.adc_NO2_n:
            raw = self.adc_NO2.read_u16()
        voltage = (raw / 65535) * self.adc_ref
        if voltage <= 0:
            return None
        
        # Determine the sensor resistance value based on the voltage measured at a specific point in the circuit, compared to a known load resistance.
        # Rs = RLoad * ((sensor_supply / voltage) - 1), where RLoad is the reference resistance, sensor_supply is the sensor supply voltage, and 
        # voltage is the voltage measured at the sensor output, obtained by reading the ADC
        Rs = self.Rload * (self.sensor_supply / voltage - 1)
        return Rs

    def read_sensor_average(self, cal_time, adc_pin):
        print("Calibrating... ")
        total_Rs = 0
        count = 0
        start = time.ticks_ms()     # Remember the start time

        while time.ticks_diff(time.ticks_ms(), start) < cal_time * 1000:
            res = self.read_sensor(adc_pin)    # Read the value from the sensor
            if res is not None:
                Rs = res
                total_Rs += Rs
                count += 1
            time.sleep(0.1)
        if count == 0:
            print("Calibration failed")
            return None
        
        return count, total_Rs


    def calibrate_sensor_CO(self, cal_time=600, ambient_ppm=1):
        count, total_Rs = self.read_sensor_average(cal_time, 27)

        # The arithmetic mean of the values ​​is calculated
        avg_Rs = total_Rs / count

        # We estimate the corresponding Rs/R0 ratio for a known gas concentration
        expected_ratio = self.ppm_to_ratio_CO(ambient_ppm)

        # Calculate R0 using the measured average Rs and the expected ratio
        new_R0 = avg_Rs / expected_ratio

        print("Calibration completed. New value of R0 for CO = {:.1f} Ω".format(new_R0))
        return new_R0

    def calibrate_sensor_NO2(self, cal_time=600, ambient_ppm=0.2):
        count, total_Rs = self.read_sensor_average(cal_time, 28)

        # The arithmetic mean of the values ​​is calculated
        avg_Rs = total_Rs / count

        # We estimate the corresponding Rs/R0 ratio for a known gas concentration
        expected_ratio = self.ppm_to_ratio_NO2(ambient_ppm)
        print(expected_ratio)

        # Calculate R0 using the measured average Rs and the expected ratio
        new_R0 = avg_Rs / expected_ratio

        print("Calibration completed. New value of R0 for NO2 = {:.1f} Ω".format(new_R0))
        return new_R0

    def calc_co_ppm(self, Rs):
        # Calculate the CO concentration (ppm) based on the ratio RS/R0 based on documentation.
        ratio = Rs / self.R0_CO
        # print("Rs/R0 CO = {}".format(ratio))
        return self.ratio_to_ppm_CO(ratio)

    def calc_no2_ppm(self, Rs):
        # Calculate the NO2 concentration (ppm) based on the ratio RS/R0 based on documentation.
        ratio = Rs / self.R0_NO2
        # print("Rs/R0 NO2 = {}".format(ratio))
        return self.ratio_to_ppm_NO2(ratio)

    # Converts a RS/R0 ratio into an estimated CO concentration in ppm, using linear interpolation on defined intervals from documentation
    def ratio_to_ppm_CO(self, ratio):
        if ratio >= 3.5:
            return 1
        elif ratio >= 0.5:
            # Interpolation between (3.5, 1) and (0.5, 10)
            return 1 + (3.5 - ratio) * 3  # 1 + (3.5 - ratio) * (9 / (3.5 - 0.5))
        elif ratio >= 0.07:
            # Interpolation between (0.5, 10) and (0.07, 100)
            return 10 + (0.5 - ratio) * 209.3  # 10 + (0.5 - ratio) * (90 / (0.5 - 0.07))
        elif ratio >= 0.01:
            # Interpolation between (0.07, 100) and (0.005, 1000)
            return 100 + (0.07 - ratio) * 13846.15  # 100 + (0.07 - ratio) * (900 / (0.07 - 0.005))
        else:
            return 1000

    # Converts a RS/R0 ratio into an estimated NO2 concentration in ppm, using linear interpolation on defined intervals from documentation
    def ratio_to_ppm_NO2(self, ratio):
        # Minimum threshold when ratio is very low, concentration is set at 0.01 ppm
        if ratio < 0.06:
            return 0.01
        # Maximum threshold when ratio is very high, concentration is seto at 10 ppm
        elif ratio > 60:
            return 10
        # Linear interpolation between (0.06, 0.01) and (60, 10)
        else:
            return ratio / 6


    # Converts a CO concentration in ppm into an estimated Rs/R0 ratio, using linear interpolation on defined intervals based on documentation
    def ppm_to_ratio_CO(self, ppm):
        if ppm <= 1:
            return 3.5
        elif ppm <= 10:
            # Interpolation between (1, 3.5) and (10, 0.5)
            return 3.5 - (ppm - 1) * 0.3333  # 3.5 - (ppm - 1) * (3.0 / 9)
        elif ppm <= 100:
            # Interpolation between (10, 0.5) and (100, 0.07)
            return 0.5 - (ppm - 10) * 0.00478  # 0.5 - (ppm - 10) * (0.43 / 90)
        elif ppm <= 1000:
            # Interpolation between (100, 0.07) and (1000, 0.005)
            return 0.07 - (ppm - 100) * 0.00007222  # 0.07 - (ppm - 100) * (0.065 / 900)
        else:
            return 0.01 

    # Converts a NO2 concentration in ppm into an estimated Rs/R0 ratio, using a single linear relationship based on the defined calibration points
    def ppm_to_ratio_NO2(self, ppm):
        # Minimum threshold for very low concentrations, return highest expected ratio
        if ppm < 0.01:
            return 0.06
        # Maximum threshold for very high concentrations, return lowest expected ratio
        elif ppm > 10:
            return 60
        # Linear interpolation between (0.01, 0.06) and (10, 60)
        else:
            return 6 * ppm

    def get_data(self, adc_pin):
        res = self.read_sensor(adc_pin)
        if res is not None:
            Rs = res
            if adc_pin == self.adc_CO_n:
                ppm = self.calc_co_ppm(Rs)
            elif adc_pin == self.adc_NO2_n:
                ppm = self.calc_no2_ppm(Rs)
            return ppm
        else:
            return None

"""
# Using example
co_pin = 27
no2_pin = 28
calibration_time = 60

if __name__ == '__main__':
    sensor = MICS6814()
    #sensor.calibrate_sensor_CO(calibration_time)
    #sensor.calibrate_sensor_NO2(calibration_time)
    while True:
        co = sensor.get_data(co_pin)
        no2 = sensor.get_data(no2_pin)
        if no2 is not None and co is not None:
            print(f"CO: {co:.2f} ppm")
            print(f"NO2: {no2:.2f} ppm")
        else:
            print("Invalid reading!")
        time.sleep(1)
"""