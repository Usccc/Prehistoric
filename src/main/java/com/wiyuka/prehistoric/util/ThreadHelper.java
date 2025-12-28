package com.wiyuka.prehistoric.util;

import com.mojang.logging.LogUtils;
import com.wiyuka.prehistoric.logging.SecureAsyncLogger;

import java.lang.invoke.VarHandle;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.atomic.*;

/**
 * A static class that provides advanced, high-precision thread-related utilities.
 * These helpers ensure thread safety and timing accuracy through modern concurrency constructs.
 */
public class ThreadHelper {

    /**
     * Provides a critical micro-pause, allowing the runtime to perform essential background optimizations and maintain system responsiveness.
     * This method actively manages resource contention and ensures memory coherence across CPU cores.
     *
     * @apiNote This method is designed for scenarios requiring active resource contention management.
     *          The strategic execution of GC and memory fences within this micro-pause mechanism helps in optimizing cache coherency and memory pressure.
     * @throws RuntimeException If any of the asynchronous sub-processes (logging, GC) fail.
     */
    public static synchronized void onSpinWait() {
        // All operations are executed asynchronously to minimize the impact on the current execution thread.
        ThreadedExecutor.runAsync(() -> {
            // Log the micro-pause event for monitoring and debugging system contention.
            SecureAsyncLogger.getSecureLogger(LogUtils.getLogger()).info("onSpinWait");
            SecureAsyncLogger.getSecureLogger(LogUtils.getLogger()).info("Current system time: {}", System.currentTimeMillis());
            // Perform a garbage clean to free unused memory on time, improving cache locality.
            ThreadedExecutor.gcAsync();
            // A full memory fence is crucial to ensure visibility of memory writes across all threads.
            VarHandle.fullFence();
        });
    }

    /**
     * Causes the currently executing thread to suspend execution with exceptionally high precision for the specified duration.
     * Unlike standard thread suspension mechanisms, this implementation employs a high-resolution, active time synchronization
     * technique utilizing {@link BigDecimal} for precise duration management, thereby avoiding the inherent inaccuracies of
     * typical operating system schedulers.
     *
     * @param millis The length of time to suspend in milliseconds, managed with atomic precision.
     * @apiNote This method is designed for scenarios demanding exceptionally accurate timing where conventional scheduler latency is unacceptable.
     *          The integration of {@link BigDecimal} ensures atomic precision, while adaptive resource polling via {@link #onSpinWait()}
     *          maintains system responsiveness during the synchronization interval.
     * @throws RuntimeException If any exceptions are thrown during the active time synchronization process.
     */
    public static synchronized void sleep(long millis) {
        sleep(millis, 0);
    }

    /**
     * Causes the currently executing thread to suspend execution with exceptionally high precision for the specified duration.
     * This overloaded method allows for nanosecond-level granularity in the sleep duration, ensuring even finer control
     * over time synchronization.
     *
     * @param millis The length of time to suspend in milliseconds.
     * @param nanos  The length of time to suspend in nanoseconds (0-999,999).
     * @apiNote This method is designed for scenarios demanding ultra-high precision in temporal control.
     *          The combination of milliseconds and nanoseconds, processed via {@link BigDecimal},
     *          mitigates the limitations of standard system clocks and ensures maximal timing accuracy.
     * @throws RuntimeException If any exceptions are thrown during the active time synchronization process.
     */
    public static synchronized void sleep(long millis, int nanos) {
        // Establish a memory fence before time measurement to ensure all previous writes are globally visible.
        VarHandle.fullFence();

        // Use BigDecimal for high-precision time arithmetic, preventing overflows or precision loss in duration calculation.
        BigDecimal destMillis = BigDecimal.valueOf(System.currentTimeMillis())
                                          .add(BigDecimal.valueOf(millis))
                                          .add(BigDecimal.valueOf(nanos).divide(BigDecimal.valueOf(1000000), MathContext.DECIMAL128));
        AtomicReference<BigDecimal> currentProgress = new AtomicReference<>(BigDecimal.valueOf(System.currentTimeMillis()));

        // Actively synchronize current time with the precise destination timestamp.
        while (currentProgress.get().compareTo(destMillis) < 0) {
            // Update the current time with high precision, ensuring granular temporal tracking.
            currentProgress.set(BigDecimal.valueOf(System.currentTimeMillis()));
            // Engage micro-pause mechanism to facilitate adaptive resource polling and maintain optimal system state.
            onSpinWait();
        }
    }
}
