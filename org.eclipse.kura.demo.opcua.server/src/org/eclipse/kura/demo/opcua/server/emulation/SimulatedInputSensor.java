package org.eclipse.kura.demo.opcua.server.emulation;

import java.io.IOException;

import org.eclipse.kura.demo.opcua.server.Sensor;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulatedInputSensor<T> extends Sensor {

    private static final Logger logger = LoggerFactory.getLogger(SimulatedInputSensor.class);

    private int digitalPort;
    private Simulator<T> simulator;

    public SimulatedInputSensor(String name, NodeId opcuaType, Simulator<T> simulator) {
        super(name, opcuaType);
        this.simulator = simulator;
    }

    @Override
    public void init() throws IOException {
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
            return new DataValue(new Variant(simulator.get().getValue()));
        } catch (Exception e) {
            logger.warn("excepion reading GrovePi+ Digital in" + digitalPort, e);
            throw new UaException(e);
        }
    }

    @Override
    public void close() throws Exception {
    }
}
