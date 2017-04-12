package org.eclipse.kura.demo.opcua.server.analog;

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
import org.iot.raspberry.grovepi.GroveAnalogOut;
import org.iot.raspberry.grovepi.GrovePi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrovePiAnalogOutSensor extends Sensor {

    private static final Logger logger = LoggerFactory.getLogger(GrovePiAnalogOutSensor.class);

    private int digitalPort;
    private GroveAnalogOut analogOut;
    private GrovePi grovePi;
    private double value;

    public GrovePiAnalogOutSensor(String name, int digitalPort, GrovePi grovePi) {
        super(name, Identifiers.Double);
        this.digitalPort = digitalPort;
        this.grovePi = grovePi;
    }

    @Override
    public void init() throws IOException {
        this.analogOut = grovePi.getAnalogOut(digitalPort);
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
    public DataValue getValue(AttributeContext context, VariableNode node) throws UaException {
        return new DataValue(new Variant(value));
    }

    @Override
    public void setValue(AttributeContext context, VariableNode node, DataValue value) throws UaException {
        try {
            double newValue = (Double) value.getValue().getValue();
            newValue = Math.min(Math.max(newValue, 0.0f), 1.0f);

            analogOut.set((int) (newValue * 255.0f));
            this.value = newValue;
        } catch (Exception e) {
            logger.warn("excepion writing GrovePi+ Analog out" + digitalPort, e);
            throw new UaException(e);
        }
    }

    @Override
    public void close() throws Exception {
    }
}
