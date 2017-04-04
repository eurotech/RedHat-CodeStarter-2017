package org.eclipse.kura.demo.opcua.server.i2c;

import java.io.IOException;

import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.delegates.AttributeDelegate;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.dio.DeviceManager;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

public class GroveDigitalLightSensorAttributeDelegate implements AttributeDelegate {

    private static final Logger logger = LoggerFactory.getLogger(GroveDigitalLightSensorAttributeDelegate.class);
    private static final byte DEVICE_ADDRESS = 0x29;

    private I2CDevice device;

    public GroveDigitalLightSensorAttributeDelegate() throws IOException {
        try {
            I2CDeviceConfig config = new I2CDeviceConfig(1, DEVICE_ADDRESS, 7, 400000);
            device = (I2CDevice) DeviceManager.open(I2CDevice.class, config);
            // INIT
            device.begin();
            device.write(0x80);
            device.write(0x03);
            device.write(0x81);
            device.write(0x11);
            device.write(0x86);
            device.write(0x00);
            device.end();
        } catch (Exception e) {
            throw new IOException("device initialization failed", e);
        }
    }

    @Override
    public DataValue getValue(AttributeContext context, VariableNode node) throws UaException {
        try {
            int L0, H0, L1, H1;

            device.write(0x8C);
            Thread.sleep(5);
            L0 = device.read();
            Thread.sleep(5);
            device.write(0x8D);
            Thread.sleep(5);
            H0 = device.read();
            Thread.sleep(5);
            device.write(0x8E);
            Thread.sleep(5);
            L1 = device.read();
            Thread.sleep(5);
            device.write(0x8F);
            Thread.sleep(5);
            H1 = device.read();
            int ch0 = (((H0 & 0xff) * 0x100) + L0) & 0xffff;
            int ch1 = (((H1 & 0xff) * 0x100) + L1) & 0xffff;

            return new DataValue(new Variant(calculateLux(ch0, ch1)));
        } catch (Exception e) {
            throw new UaException(e);
        }
    }

    public int calculateLux(int ch0, int ch1) {
        int b = 0;
        int m = 0;
        int scale = SENSOR_SCALE;
        int gain = SENSOR_GAIN;
        int type = SENSOR_TYPE;
        int chScale = 0;
        switch (scale) {
        case 0: // '\0'
            chScale = 29975;
            break;

        case 1: // '\001'
            chScale = 4071;
            break;

        default:
            chScale = 1024;
            break;
        }
        if (gain != 0)
            chScale <<= 4;
        ch0 = ch0 * chScale >> 10;
        ch1 = ch1 * chScale >> 10;
        long ratio1 = 0L;
        if (ch0 != 0)
            ratio1 = (ch1 << (RATIO_SCALE + 1)) / ch0;
        long ratio = ratio1 + 1L >> 1;
        switch (type) {
        case 0: // '\0'
            if (ratio >= 0L && ratio <= K1T) {
                b = B1T;
                m = M1T;
                break;
            }
            if (ratio <= K2T) {
                b = B2T;
                m = M2T;
                break;
            }
            if (ratio <= K3T) {
                b = B3T;
                m = M3T;
                break;
            }
            if (ratio <= K4T) {
                b = B4T;
                m = M4T;
                break;
            }
            if (ratio <= K5T) {
                b = B5T;
                m = M5T;
                break;
            }
            if (ratio <= K6T) {
                b = B6T;
                m = M6T;
                break;
            }
            if (ratio <= K7T) {
                b = B7T;
                m = M7T;
                break;
            }
            if (ratio > K8T) {
                b = B8T;
                m = M8T;
            }
            break;

        default:
            if (ratio >= 0L && ratio <= K1C) {
                b = B1C;
                m = M1C;
                break;
            }
            if (ratio <= K2C) {
                b = B2C;
                m = M2C;
                break;
            }
            if (ratio <= K3C) {
                b = B3C;
                m = M3C;
                break;
            }
            if (ratio <= K4C) {
                b = B4C;
                m = M4C;
                break;
            }
            if (ratio <= K5C) {
                b = B5C;
                m = M5C;
                break;
            }
            if (ratio <= K6C) {
                b = B6C;
                m = M6C;
                break;
            }
            if (ratio <= K7C) {
                b = B7C;
                m = M7C;
            }
            break;
        }
        int temp = ch0 * b - ch1 * m;
        if (temp < 0)
            temp = 0;
        int lux = (temp += 8192) >> LUX_SCALE;
        return lux;
    }

    public void shutdown() {
        try {
            device.close();
        } catch (IOException e) {
            logger.warn("failed to close device", e);
        }
    }

    private static final int SENSOR_SCALE = 1;
    private static final int SENSOR_GAIN = 0;
    private static final int SENSOR_TYPE = 1;

    private static final int RATIO_SCALE = 9;
    private static final int LUX_SCALE = 14;
    private static final int K1T = 64;
    private static final int B1T = 498;
    private static final int M1T = 446;
    private static final int K2T = 128;
    private static final int B2T = 532;
    private static final int M2T = 721;
    private static final int K3T = 192;
    private static final int B3T = 575;
    private static final int M3T = 891;
    private static final int K4T = 256;
    private static final int B4T = 624;
    private static final int M4T = 1022;
    private static final int K5T = 312;
    private static final int B5T = 367;
    private static final int M5T = 508;
    private static final int K6T = 410;
    private static final int B6T = 210;
    private static final int M6T = 251;
    private static final int K7T = 666;
    private static final int B7T = 24;
    private static final int M7T = 18;
    private static final int K8T = 666;
    private static final int B8T = 0;
    private static final int M8T = 0;
    private static final int K1C = 67;
    private static final int B1C = 516;
    private static final int M1C = 429;
    private static final int K2C = 133;
    private static final int B2C = 552;
    private static final int M2C = 705;
    private static final int K3C = 200;
    private static final int B3C = 595;
    private static final int M3C = 867;
    private static final int K4C = 266;
    private static final int B4C = 642;
    private static final int M4C = 991;
    private static final int K5C = 333;
    private static final int B5C = 375;
    private static final int M5C = 477;
    private static final int K6C = 410;
    private static final int B6C = 257;
    private static final int M6C = 295;
    private static final int K7C = 666;
    private static final int B7C = 55;
    private static final int M7C = 43;
}
