import machine, time

class MQ131:
    def __init__(self, pin_number=26, adc_ref=3.3, sensor_supply=5.0, Rload=10000, R0 = 202118.4):
        self.adc = machine.ADC(pin_number)
        self.adc_ref = adc_ref
        self.sensor_supply = sensor_supply
        self.Rload = Rload
        # Value taken from a previous calibration
        self.R0 = R0

    def read_sensor(self):
        # Calculates the ADC input voltage based on its raw value. It converts the digital value read into a real voltage
        # V_adc = (raw / 65535) * adc_ref, where raw is the input value from the ADC, 65535 is the maximum value of the ADC,
        # and adc_ref is the reference voltage for the ADC in the Raspberry Pi Pico W
        raw = self.adc.read_u16()
        voltage = (raw / 65535) * self.adc_ref
        if voltage <= 0:
            return None

        # Determine the sensor resistance value based on the voltage measured at a specific point in the circuit, compared to a known load resistance.
        # Rs = RLoad * ((sensor_supply / voltage) - 1), where RLoad is the reference resistance, sensor_supply is the sensor supply voltage, and 
        # voltage is the voltage measured at the sensor output, obtained by reading the ADC
        Rs = self.Rload * (self.sensor_supply / voltage - 1)
        return Rs

    def read_sensor_average(self, cal_time):
        total_Rs = 0
        count = 0
        start = time.ticks_ms()     # Remember the start time

        while time.ticks_diff(time.ticks_ms(), start) < cal_time * 1000:
            res = self.read_sensor()    # Read the value from the sensor
            if res is not None:
                Rs = res
                total_Rs += Rs
                count += 1
            time.sleep(0.1)
        if count == 0:
            print("Read failed")
            return None
        
        return count, total_Rs

    def calibrate_sensor(self, cal_time=600, ambient_ppb=10):
        print("Calibrating... ")
        
        count, total_Rs = self.read_sensor_average(cal_time)

        # The arithmetic mean of the values ​​is calculated
        avg_Rs = total_Rs / count

        # We estimate the corresponding Rs/R0 ratio for a known gas concentration
        expected_ratio = self.ppb_to_ratio(ambient_ppb)

        # Calculate R0 using the measured average Rs and the expected ratio
        new_R0 = avg_Rs / expected_ratio

        print("Calibration completed. New value of R0 for O3 = {:.1f} Ω".format(new_R0))
        return new_R0

    def calc_ppb(self, Rs):
        # Calculate the O3 concentration (ppb) based on the ratio RS/R0 based on documentation.
        ratio = Rs / self.R0
        return self.ratio_to_ppb(ratio)

    # Converts a O3 concentration in ppb into an estimated Rs/R0 ratio, using a single linear relationship based on the defined calibration points
    def ppb_to_ratio(self, ppb):
        calibration_points = [
            (10, 1),
            (50, 2),
            (100, 2.7),
            (200, 4),
            (500, 6),
            (1000, 8)
        ]

        # Minimum threshold for very low concentrations, return highest expected ratio
        if ppb <= calibration_points[0][0]:
            return calibration_points[0][1]
        # Maximum threshold for very high concentrations, return lowest expected ratio
        if ppb >= calibration_points[-1][0]:
            return calibration_points[-1][1]
        # Linear interpolation
        for i in range(1, len(calibration_points)):
            x0, y0 = calibration_points[i - 1]
            x1, y1 = calibration_points[i]
            if x0 <= ppb <= x1:
                slope = (y1 - y0) / (x1 - x0)
                return y0 + slope * (ppb - x0)

        # Converts a RS/R0 ratio into an estimated O3 concentration in ppb, using linear interpolation on defined intervals from documentation
    def ratio_to_ppb(self, ratio):
        calibration_points = [
            (10, 1),
            (50, 2),
            (100, 2.7),
            (200, 4),
            (500, 6),
            (1000, 8)
        ]

        # If the ratio is less than or equal to the lowest defined ratio, return the minimum concentration
        if ratio <= calibration_points[0][1]:
            return calibration_points[0][0]

        # If the ratio is greater than or equal to the highest defined ratio, return the maximum concentration
        if ratio >= calibration_points[-1][1]:
            return calibration_points[-1][0]

        # Search for the interval corresponding to the ratio
        for i in range(1, len(calibration_points)):
            prev_ppb, prev_ratio = calibration_points[i - 1]
            next_ppb, next_ratio = calibration_points[i]
            if prev_ratio <= ratio <= next_ratio:
                # Linear interpolation formula:
                # ratio = prev_ratio + (next_ratio - prev_ratio) * (ppb - prev_ppb) / (next_ppb - prev_ppb)
                # Take ppb from the equation
                # ppb = prev_ppb + (next_ppb - prev_ppb) * (ratio - prev_ratio) / (next_ratio - prev_ratio)
                ppb = prev_ppb + (next_ppb - prev_ppb) * (ratio - prev_ratio) / (next_ratio - prev_ratio)
                return ppb

    def get_data(self):
        res = self.read_sensor()
        if res is not None:
            Rs = res
            ppb = self.calc_ppb(Rs)
            return ppb
        else:
            return None

"""
# Using example
if __name__ == '__main__':
    sensor = MQ131()
    sensor.calibrate_sensor(cal_time=60, ambient_ppb=25)

    while True:
        o3 = sensor.get_data()
        if o3 is not None:
            print(f"O3: {o3:.3f} ppb")
        else:
            print("Invalid reading!")
        time.sleep(1)
"""