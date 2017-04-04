package org.eclipse.kura.demo.opcua.server.gpio;

import java.io.IOException;

import org.eclipse.kura.gpio.GPIOService;
import org.eclipse.kura.gpio.KuraGPIODirection;
import org.eclipse.kura.gpio.KuraGPIOMode;
import org.eclipse.kura.gpio.KuraGPIOPin;
import org.eclipse.kura.gpio.KuraGPIOTrigger;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.delegates.AttributeDelegate;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GpioOutAttributeDelegate implements AttributeDelegate {

    private static final Logger logger = LoggerFactory.getLogger(GpioOutAttributeDelegate.class);

    private int pinNumber;
    private GPIOService gpioService;
    private boolean value;
    private KuraGPIOPin pin = null;

    public GpioOutAttributeDelegate(int pinNumber, GPIOService gpioService) throws IOException {
        this.pinNumber = pinNumber;
        this.gpioService = gpioService;
        this.pin = openPin(pinNumber);
    }

    @Override
    public UByte getAccessLevel(AttributeContext context, VariableNode node) throws UaException {
        return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_WRITE));
    }

    @Override
    public UByte getUserAccessLevel(AttributeContext context, VariableNode node) throws UaException {
        return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_WRITE));
    }

    @Override
    public void setValue(AttributeContext context, VariableNode node, DataValue value) throws UaException {
        logger.info("write request received");
        try {
            pin.setValue((Boolean) value.getValue().getValue());
        } catch (Exception e) {
            logger.warn("excepion writing GPIO " + pinNumber, e);
            throw new UaException(e);
        }
    }

    private KuraGPIOPin openPin(int pinNumber) throws IOException {
        KuraGPIOPin pin = null;
        try {
            pin = gpioService.getPinByTerminal(pinNumber, KuraGPIODirection.OUTPUT, KuraGPIOMode.OUTPUT_OPEN_DRAIN,
                    KuraGPIOTrigger.NONE);
            pin.open();
            return pin;
        } catch (Exception e) {
            logger.warn("excepion opening GPIO " + pinNumber, e);
            throw new IOException(e);
        }
    }

    public void shutdown() {
        try {
            pin.close();
        } catch (Exception e) {
            logger.warn("excepion closing GPIO " + pinNumber, e);
        }
    }

    @Override
    public DataValue getValue(AttributeContext context, VariableNode node) throws UaException {
        return new DataValue(new Variant(value));
    }
}
