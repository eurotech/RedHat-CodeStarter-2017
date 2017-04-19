package org.eclipse.kura.demo.opcua.server.emulation;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Simulation {

    private static final Logger logger = LoggerFactory.getLogger(Simulation.class);

    private static final Random random = new Random();

    public static <T> Simulator<T> block(Function<Double, T> value, Supplier<Long> durationSupplier) {
        return new Block<>(value, durationSupplier);
    }

    public static <T> Simulator<T> loop(Simulator<T> simulator) {
        return new Loop<>(simulator);
    }

    @SafeVarargs
    public static <T> Simulator<T> chain(Simulator<T>... simulators) {
        return new Chain<>(simulators);
    }

    public static Function<Double, Double> sin(double avg, double peak) {
        return (alpha) -> avg + peak * Math.sin(2 * Math.PI * alpha);
    }

    public static <T> Supplier<T> constant(T value) {
        return () -> value;
    }

    public static Supplier<Long> randomLong(long average, long stdDev) {
        return () -> average + (long) (random.nextGaussian() * (double) stdDev);
    }

    public static Supplier<Long> nonNegative(Supplier<Long> wrapped) {

        return () -> {
            final Long result = wrapped.get();
            return result > 0 ? result : 0;
        };
    }

    public static Function<Double, Integer> easeInteger(final int start, final int end) {
        return (alpha) -> (int) ((1.0f - alpha) * start + alpha * end);
    }

    public static Simulator<Integer> intSquareWave(int down, int up, Supplier<Long> downDuration,
            Supplier<Long> transitionDuration, Supplier<Long> upDuration) {
        return loop(chain(block((alpha) -> down, downDuration), block(easeInteger(down, up), transitionDuration),
                block((alpha) -> up, upDuration), block(easeInteger(up, down), transitionDuration)));
    }

    public static Simulator<Boolean> boolSquareWave(Supplier<Long> downDuration, Supplier<Long> upDuration) {
        return loop(chain(block((alpha) -> false, downDuration), block((alpha) -> true, upDuration)));
    }
}
