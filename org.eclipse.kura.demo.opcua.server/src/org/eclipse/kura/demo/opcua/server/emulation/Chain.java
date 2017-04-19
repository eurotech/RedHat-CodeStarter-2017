package org.eclipse.kura.demo.opcua.server.emulation;

import java.util.Arrays;
import java.util.Iterator;

public class Chain<T> implements Simulator<T> {

    private Simulator<T>[] simulators;
    private Simulator<T> currentSimulator;
    private Iterator<Simulator<T>> iter;

    @SafeVarargs
    public Chain(Simulator<T>... simulators) {
        if (simulators.length == 0) {
            throw new IllegalArgumentException("simulator list cannot be empty");
        }
        this.simulators = simulators;
        reset();
    }

    @Override
    public SimulationState<T> get() {
        SimulationState<T> state = currentSimulator.get();
        while ((state = currentSimulator.get()).isCompleted()) {
            if (!iter.hasNext()) {
                break;
            }
            currentSimulator = iter.next();
            currentSimulator.reset();
        }
        return state;
    }

    @Override
    public void reset() {
        iter = Arrays.stream(simulators).iterator();
        currentSimulator = iter.next();
        currentSimulator.reset();
    }

}
