package org.eclipse.kura.demo.opcua.server.emulation;

public class Loop<T> implements Simulator<T> {

    private Simulator<T> wrapped;

    public Loop(Simulator<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public SimulationState<T> get() {
        SimulationState<T> currentState;

        while ((currentState = wrapped.get()).isCompleted()) {
            wrapped.reset();
        }
        return new SimulationState<>(false, currentState.getValue());
    }

    @Override
    public void reset() {
        wrapped.reset();
    }

}
