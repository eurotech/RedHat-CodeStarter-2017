package org.eclipse.kura.demo.opcua.server.emulation;

import java.util.function.Function;
import java.util.function.Supplier;

public class Block<T> implements Simulator<T> {

    private Function<Double, T> value;
    private Supplier<Long> durationSupplier;
    private long startTime;
    private long endTime;

    public Block(Function<Double, T> value, Supplier<Long> durationSupplier) {
        this.durationSupplier = durationSupplier;
        this.value = value;
    }

    public SimulationState<T> get() {
        double alpha = (double) (System.currentTimeMillis() - startTime) / (double) (endTime - startTime);
        return new SimulationState<>(System.currentTimeMillis() > endTime, value.apply(alpha));
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + durationSupplier.get();
    }

}
