package org.eclipse.kura.demo.opcua.server.i2c;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.eclipse.kura.demo.opcua.server.Sensor;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.dio.DeviceManager;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

public class GroveTemperatureSensorDIO extends Sensor {

    private static final Logger logger = LoggerFactory.getLogger(GroveTemperatureSensorDIO.class);
    
    private static final byte DEVICE_ADDRESS = 0x44;    
    
    private ByteBuffer readTempCommand = ByteBuffer.wrap(new byte[] {0x2C, 0x06});
    private ByteBuffer rcvBuf = ByteBuffer.allocate(6).order(ByteOrder.BIG_ENDIAN);
    
    private I2CDevice device;
    
    public GroveTemperatureSensorDIO(String name) {
        super(name, Identifiers.Double);
    }

    @Override
    public void init() throws IOException {
        I2CDeviceConfig config = new I2CDeviceConfig(1, DEVICE_ADDRESS, 7, 400000);
        device = (I2CDevice) DeviceManager.open(I2CDevice.class, config);
    }

    @Override
    public UByte getAccessLevel(AttributeContext context, VariableNode node) throws UaException {
        return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_ONLY));
    }

    @Override
    public UByte getUserAccessLevel(AttributeContext context, VariableNode node) throws UaException {
        return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_ONLY));
    }
    
    private double readTemperature() throws IOException, InterruptedException {
    	
    	readTempCommand.rewind();
    	
    	device.begin();
    	device.write(readTempCommand);
    	device.end();
    	
    	Thread.sleep(600);
    	
    	rcvBuf.rewind();
    	
    	device.begin();
    	device.read(rcvBuf);
    	device.end();
    	
    	rcvBuf.rewind();
    	
    	int rawTemp = (int) (rcvBuf.getShort() & 0xffff);
    	double tempCelsius = -45.0f + (175.0f * ((double) rawTemp) / 65535.0f);
    	
    	return tempCelsius;
    }
    
    @Override
    public DataValue getValue(AttributeContext context, VariableNode node) throws UaException {
        try {
            return new DataValue(new Variant(readTemperature()));
        } catch (Exception e) {
        	logger.warn("got exception", e);
            throw new UaException(e);
        }
    }

    @Override
    public void close() throws Exception {
        try {
            device.close();
        } catch (IOException e) {
            logger.warn("failed to close device", e);
        }
    }
}

