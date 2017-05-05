# RedHat-CodeStarter-2017

This repository contains all the projects and dependencies needed to create the .dp file that has to be installed in the Raspberry Pi.

## Get Started:
On your host PC:
 - Install Virtual Box from here: https://www.virtualbox.org/wiki/Downloads selecting the proper distribution, depending on your host operating system and architecture;
 - Download the CodeStarter VM from here: https://s3-us-west-2.amazonaws.com/kura-repo/RedHatSummit/vagrant_codestarter_default_v3.ova
 - Start your Virtual Box installation and import the downloaded VM image by selecting the Import Appliance option from the File menu. When importing, please remember to reset the VM MAC address by clicking on the proper option;

On your Raspberry Pi:
- Download the Raspberry Pi system image from here: https://s3-us-west-2.amazonaws.com/kura-repo/RedHatSummit/codestarter-rpi.img.xz and use dd or any other equivalent tool to flash the Raspberry Pi’s SD card. Suggested SD Card will have at least 16 GB available and Class 10.
- Clone this CodeStarter repository. The repository is composed by:
  - **grovepi-libs**: dependencies project
  - **org.eclipse.kura.codestarter.opcua.server**: main project. The target Jar needs to be wrapped as a .dp file in order to be installed into the Raspberry Pi
  - **org.eclipse.kura.db.server**: optional project to expose the Kura Database.

  Building from source will require only to invoke **mvn clean install** from the root folder. This will trigger the building of all the dependencies and projects in this repository.

  In org.eclipse.kura.codestarter.opcua.server there exists a precompiled .dp file that can be downloaded and installed in the Raspberry Pi.
- Access the Kura Web Ui in the Raspberry Pi by accessing, with your browser the address: http://<your-raspberry-ip>/kura
- Select the Packages tab and install the codestarter.opcua.dp available in the org.eclipse.kura.codestarter.opcua.server package
