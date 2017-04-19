package org.eclipse.kura.demo.opcua.server.emulation;

import java.util.function.Supplier;

public interface Simulator<T> extends Supplier<SimulationState<T>> {

    public void reset();
}
