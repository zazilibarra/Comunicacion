#!/usr/bin/python
import RPi.GPIO as GPIO
import time
import  socket

# here you can modify the break between the measurements
sleeptime = 1

#GPIO SETUP
channel = 21

# the one-wire input pin will be declared and the integrated pullup-resistor will be enabled
GPIO.setmode(GPIO.BCM)
GPIO.setup(channel, GPIO.IN)

#Socket local Sensor
Host = "127.0.0.5"
Port = 5000

sensor_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sensor_socket.connect((Host, Port))

print("Sensor de Flama KY-026")

def callback(channel):
	sensor_socket.send("1\n")
		
GPIO.add_event_detect(channel, GPIO.BOTH, bouncetime=300)  # let us know when the pin goes HIGH or LOW
GPIO.add_event_callback(channel, callback)  # assign function to GPIO PIN, Run function on change

# infinite loop
while True:
	time.sleep(sleeptime)
