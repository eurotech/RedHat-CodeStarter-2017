package org.eclipse.kura.demo.opcua.server.digital;

import java.io.IOException;

import org.eclipse.kura.demo.opcua.server.Sensor;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.iot.raspberry.grovepi.GroveDigitalOut;
import org.iot.raspberry.grovepi.GrovePi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrovePiDigitalOutSensor extends Sensor {

    private static final Logger logger = LoggerFactory.getLogger(GrovePiDigitalOutSensor.class);

    private boolean value;
    private int digitalPort;
    private GroveDigitalOut digitalOut;
    private GrovePi grovePi;

    public GrovePiDigitalOutSensor(String name, int digitalPort, GrovePi grovePi) {
        super(name, Identifiers.Boolean);
        this.digitalPort = digitalPort;
        this.grovePi = grovePi;
    }

    @Override
    public void init() throws IOException {
        this.digitalOut = grovePi.getDigitalOut(digitalPort);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            logger.warn("interrupted", e);
        }
        this.digitalOut.set(value);
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
        try {
            boolean newValue = (Boolean) value.getValue().getValue();
            digitalOut.set(newValue);
            this.value = newValue;
        } catch (Exception e) {
            logger.warn("excepion writing GrovePi+ Digital out" + digitalPort, e);
            throw new UaException(e);
        }
    }

    @Override
    public DataValue getValue(AttributeContext context, VariableNode node) throws UaException {
        return new DataValue(new Variant(value));
    }

    @Override
    public void close() throws Exception {
    }

}