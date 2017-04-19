package org.eclipse.kura.demo.opcua.server.emulation;

public class SimulationState<T> {

    private boolean completed;
    private T value;

    public SimulationState(boolean completed, T value) {
        this.completed = completed;
        this.value = value;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
