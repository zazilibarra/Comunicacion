# Needed modules will be imported and configured
import RPi.GPIO as GPIO
import time
import sys
import  socket
 
GPIO.setmode(GPIO.BCM)
 
# Declaration of the input pin which is connected with the sensor.
GPIO_PIN = 24
GPIO.setup(GPIO_PIN, GPIO.IN, pull_up_down = GPIO.PUD_UP)
 
# Break between the results will be defined here (in seconds)
delay_time = 1.0
 
#Socket local Sensor
Host = "127.0.0.5"
Port = 5000

sensor_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sensor_socket.connect((Host, Port))

print("Sensor de Obstaculos KY-032")
 
# main program loop
try:
        while True:
            raw_data = GPIO.input(GPIO_PIN)
            data = str(raw_data) + '\n'
            sensor_socket.send(data)
            time.sleep(delay_time)
 
# Scavenging work after the end of the program
except KeyboardInterrupt:
        GPIO.cleanup()
