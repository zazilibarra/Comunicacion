#!/usr/bin/python
import RPi.GPIO as GPIO
import time
import  socket

# here you can modify the break between the measurements
sleeptime = 1

#GPIO SETUP
sound = 17

# the one-wire input pin will be declared and the integrated pullup-resistor will be enabled
GPIO.setmode(GPIO.BCM)
GPIO.setup(sound, GPIO.IN)

#Socket local Sensor
Host = "127.0.0.5"
Port = 5000

sensor_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sensor_socket.connect((Host, Port))

print("Sensor de Sonido con Microfono KY-038")

def callback(sound):
	sin = GPIO.input(sound)
        if sin:
		raw_data = sin
		data = str(raw_data) + '\n'
		sensor_socket.send(data)
		
GPIO.add_event_detect(sound, GPIO.BOTH, bouncetime=300)  # let us know when the pin goes HIGH or LOW
GPIO.add_event_callback(sound, callback)  # assign function to GPIO PIN, Run function on change

# infinite loop
while True:
	time.sleep(1)
