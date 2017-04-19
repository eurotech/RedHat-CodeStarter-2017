package org.eclipse.kura.demo.opcua.server.emulation;

import java.io.IOException;
import java.util.function.Function;

import org.eclipse.kura.demo.opcua.server.Sensor;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;

public class SimulatedOutputSensor<T> extends Sensor {

    private T value;
    private Function<T, T> adapter;

    public SimulatedOutputSensor(String name, NodeId opcuaType, T initialValue, Function<T, T> adapter) {
        super(name, opcuaType);
        this.value = initialValue;
        this.adapter = adapter;
    }

    @Override
    public void init() throws IOException {
    }

    @Override
    public UByte getAccessLevel(AttributeContext context, VariableNode node) throws UaException {
        return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_WRITE));
    }

    @Override
    public UByte getUserAccessLevel(AttributeContext context, VariableNode node) throws UaException {
        return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_WRITE));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(AttributeContext context, VariableNode node, DataValue value) throws UaException {
        this.value = adapter.apply((T) value.getValue().getValue());
    }

    @Override
    public DataValue getValue(AttributeContext context, VariableNode node) throws UaException {
        return new DataValue(new Variant(value));
    }

    @Override
    public void close() throws Exception {
    }

}
