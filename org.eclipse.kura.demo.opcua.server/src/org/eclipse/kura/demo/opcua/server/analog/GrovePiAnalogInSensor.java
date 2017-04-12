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
import org.iot.raspberry.grovepi.GroveAnalogIn;
import org.iot.raspberry.grovepi.GrovePi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrovePiAnalogInSensor extends Sensor {

    private static final Logger logger = LoggerFactory.getLogger(GrovePiAnalogInSensor.class);

    private int analogPort;
    private GroveAnalogIn analogIn;
    private GrovePi grovePi;

    public GrovePiAnalogInSensor(String name, int analogPort, GrovePi grovePi) {
        super(name, Identifiers.Integer);
        this.analogPort = analogPort;
        this.grovePi = grovePi;
    }

    public void init() throws IOException {
        this.analogIn = grovePi.getAnalogIn(analogPort, 4);
    }

    @Override
    public UByte getAccessLevel(AttributeContext context, VariableNode node) throws UaException {
        return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_ONLY));
    }

    @Override
    public UByte getUserAccessLevel(AttributeContext context, VariableNode node) throws UaException {
        return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_ONLY));
    }

    private int getSample() throws IOException {
        byte[] rawData = this.analogIn.get();
        int result = 0;
        result |= rawData[2] & 0xff;
        result |= ((int) (rawData[1] & 0x0f)) << 8;
        return result;
    }

    @Override
    public DataValue getValue(AttributeContext context, VariableNode node) throws UaException {
        try {
            return new DataValue(new Variant(getSample()));
        } catch (Exception e) {
            logger.warn("excepion reading GrovePi+ Analog in" + analogPort, e);
            throw new UaException(e);
        }
    }

    @Override
    public void close() throws Exception {
    }
}
