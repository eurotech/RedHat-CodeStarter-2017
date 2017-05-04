# RedHat-CodeStarter-2017

This repository contains all the projects and dependencies needed to create the .dp file that has to be installed in the Raspberry Pi.

The repository is composed by:
 - grovepi-libs: dependencies project
 - org.eclipse.kura.codestarter.opcua.server: main project. The target Jar needs to be wrapped as a .dp file in order to be installed into the Raspberry Pi
 - org.eclipse.kura.db.server: optional project to expose the Kura Database.

 Building from source will require only to invoke **mvn clean install** from the root folder. This will trigger the building of all the dependencies and projects in this repository.

 In org.eclipse.kura.codestarter.opcua.server there exists a precompiled .dp file that can be downloaded and installed in the Raspberry Pi. 
