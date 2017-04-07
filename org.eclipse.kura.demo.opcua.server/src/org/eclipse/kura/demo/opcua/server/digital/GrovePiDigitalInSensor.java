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
import org.iot.raspberry.grovepi.GroveDigitalIn;
import org.iot.raspberry.grovepi.GrovePi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrovePiDigitalInSensor extends Sensor {

    private static final Logger logger = LoggerFactory.getLogger(GrovePiDigitalInSensor.class);

    private int digitalPort;
    private GroveDigitalIn digitalIn;
    private GrovePi grovePi;

    public GrovePiDigitalInSensor(String name, int digitalPort, GrovePi grovePi) {
        super(name, Identifiers.Boolean);
        this.digitalPort = digitalPort;
        this.grovePi = grovePi;
    }

    @Override
    public void init() throws IOException {
        this.digitalIn = grovePi.getDigitalIn(digitalPort);
    }

    @Override
    public UByte getAccessLevel(AttributeContext context, VariableNode node) throws UaException {
        return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_ONLY));
    }

    @Override
    public UByte getUserAccessLevel(AttributeContext context, VariableNode node) throws UaException {
        return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_ONLY));
    }

    @Override
    public DataValue getValue(AttributeContext context, VariableNode node) throws UaException {
        try {
            return new DataValue(new Variant(digitalIn.get()));
        } catch (Exception e) {
            logger.warn("excepion reading GrovePi+ Digital in" + digitalPort, e);
            throw new UaException(e);
        }
    }

    @Override
    public void close() throws Exception {
    }
}