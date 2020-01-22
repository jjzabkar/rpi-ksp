# rpiksp

Integrate a Raspberry Pi 4 and Kerbal Space Program (KSP) using [kRPC-java](https://krpc.github.io/krpc/java/client.html) and [Pi4J](https://pi4j.com/1.2/example/control.html).

## Prerequisites

### On Build Machine

Install krpc-java : https://krpc.github.io/krpc/java/client.html

### On Raspberry Pi

Update WiringPi on RPi4 to 2.52

http://wiringpi.com/wiringpi-updated-to-2-52-for-the-raspberry-pi-4b/

# How To

```bash
mvn package;
scp target/*jar pi@RASPBERRY_PI_ADDRESS:~/java/
ssh pi@RASPBERRY_PI_ADDRESS;
java -jar ~/java/rpiksp-version.jar
```

# Reference 

* Wiring Pi pinout: https://pinout.xyz/pinout/wiringpi