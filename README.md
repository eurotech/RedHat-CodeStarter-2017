# RedHat-CodeStarter-2017

This repository contains all the projects and dependencies needed to create the .dp file that has to be installed in the Raspberry Pi.

## Get Started:
Download the Presentation at: https://goo.gl/ZOE9cF

Use the following list to procure the needed components:

| Component Name          | Quantity      | Link                                                           |
| ----------------------- | ------------- | -------------------------------------------------------------- |
| RaspberryPi 3           | 1             | https://www.seeedstudio.com/Raspberry-Pi-3-Model-B-p-2625.html |
| Micro SD 16GB Class 10  | 1             | https://www.amazon.com/SanDisk-Ultra-Micro-Adapter-SDSQUNC-016G-GN6MA/dp/B010Q57SEE/ref=sr_1_3?ie=UTF8&qid=1494264720&sr=8-3&keywords=micro+sd+16gb+class+10 |
| Micro USB Cable         | 1             | https://www.seeedstudio.com/Micro-USB-Cable-48cm-p-1475.html |
| GrovePi+                | 1             | https://www.seeedstudio.com/GrovePi+-p-2241.html |
| Grove - Red LED         | 1             | https://www.seeedstudio.com/Grove-Red-LED-p-1142.html |
| Grove - Buzzer          | 1             | https://www.seeedstudio.com/Grove-Buzzer-p-768.html |
| Grove - Digital Light Sensor               | 1             | https://www.seeedstudio.com/Grove-Digital-Light-Sensor-p-1281.html |
| Grove - Water Sensor                | 1             | https://www.seeedstudio.com/Grove-Water-Sensor-p-748.html |
| Grove - Mini Fan v1.1                | 1             | https://www.seeedstudio.com/Grove-Mini-Fan-v1.1-p-2685.html |
| Grove - Temperature&Humidity Sensor (SHT31)              | 1             | https://www.seeedstudio.com/Grove-Temperature&amp;Humidity-Sensor-(SHT31)-p-2655.html |
| Grove - Blue Wrapper 1*1(4 PCS pack) | 1             | https://www.seeedstudio.com/Grove-Blue-Wrapper-1*1(4-PCS-pack)-p-2580.html |
| Grove - Blue Wrapper 1*2(4 PCS pack) | 1             | https://www.seeedstudio.com/Grove-Blue-Wrapper-1*2(4-PCS-pack)-p-2583.html |

### On your host PC:
 - Install Virtual Box from here: https://www.virtualbox.org/wiki/Downloads selecting the proper distribution, depending on your host operating system and architecture;
 - Download the CodeStarter VM from here: https://s3-us-west-2.amazonaws.com/kura-repo/RedHatSummit/vagrant_codestarter_default_v3.ova
 - Start your Virtual Box installation and import the downloaded VM image by selecting the Import Appliance option from the File menu. When importing, please remember to reset the VM MAC address by clicking on the proper option;

### On your Raspberry Pi:
- Download the Raspberry Pi system image from here: https://s3-us-west-2.amazonaws.com/kura-repo/RedHatSummit/raspberry_codestarter_v2.img.bz2 and use dd or any other equivalent tool to flash the Raspberry Pi’s SD card.
- Connect the sensors and actuators following what described in the following table:

| Component Name          | GrovePi+ Connector      |
| ----------------------- | ------------- |
| Grove - Red LED         | D4            |
| Grove - Buzzer          | D6             |
| Grove - Digital Light Sensor               | I2C-2             |
| Grove - Water Sensor                | D2             |
| Grove - Mini Fan v1.1                | D3            |
| Grove - Temperature&Humidity Sensor (SHT31)              | I2C-1             |

- Start the Raspberry Pi

You can also recompile the org.eclipse.kura.codestarter.opcua.server project by following the next steps:
- Clone this CodeStarter repository. The repository is composed by:
  - **grovepi-libs**: dependencies project
  - **org.eclipse.kura.codestarter.opcua.server**: main project. The target Jar needs to be wrapped as a .dp file in order to be installed into the Raspberry Pi
  - **org.eclipse.kura.db.server**: optional project to expose the Kura Database.

  Building from source will require only to invoke **mvn clean install** from the root folder. This will trigger the building of all the dependencies and projects in this repository.
  A jar and a dp file will be available in the org.eclipse.kura.codestarter.opcua.server target folder. 

At this point you can:

- Access the Kura Web Ui in the Raspberry Pi by accessing, with your browser the address: http://<your-raspberry-ip>/kura
- Select the Packages tab and remove any existing bundle already installed.
- Install the codestarter.opcua.dp available in the org.eclipse.kura.codestarter.opcua.server package by loading it from your web browser.

### Start the VM and test the emulated environment
At this point you can start your VM and, following the slides, create your own Wire graph experimenting with the simulated PLC running in your VM.

### Test the graph with your hardware PLC
When done with the simulated environment, you can test your graph with the Raspberry Pi and the connected sensors. In order to do so, what you need to perform is to access the Kura Web Ui in your VM. In the left menu select the OPC-UA Driver you created in slide 32, modify the **endpoint.ip** value changing it from **127.0.0.1** to the IP of your Raspberry Pi. _A stop and restart of the VM Kura instance may be required._

