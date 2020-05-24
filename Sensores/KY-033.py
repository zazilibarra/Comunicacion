import RPi.GPIO as GPIO
import time
import sys
import socket

GPIO.setmode(GPIO.BCM)

GPIO_PIN = 24
GPIO.setup(GPIO_PIN,GPIO.IN,pull_up_down = GPIO.PUD_UP)

delay_time = 1.0

Host = "127.0.0.5"
Port = 5000

sensor_socket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
sensor_socket.connect((Host,Port))

print("Sensor de Linea KY-033 [Presione CTR + C para finalizar]")

try:
    while True:
        raw_data = GPIO.input(GPIO_PIN)
        data = str(raw_data) + '\n'
        sensor_socket.send(data)
        time.sleep(delay_time)
        
except KeyboardInterrupt:
    GPIO.cleanup()
