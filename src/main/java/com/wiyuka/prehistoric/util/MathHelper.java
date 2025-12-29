package com.wiyuka.prehistoric.util;

import org.jetbrains.annotations.NotNull;

import java.math.*;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static com.wiyuka.prehistoric.util.ThreadedExecutor.supplyAsync;

/**
 * A static class that provides functions for math calculations.
 *
 * @author MorningMC
 */
public class MathHelper {
    /** The default rounds of calculations executed in {@link #averageSample}. Temporarily hard-coded. */
    public static final long DEFAULT_ROUND = 1 << 10; // TODO: replace this field with a config entry
    
    /** Whether allows {@link BigDecimal} calculations. Temporarily hard-coded. */
    public static final boolean ALLOW_BIGINTEGER = true; // TODO: replace this field with a config entry
    
    /**
     * Reduce errors by averaging multiple calculations.
     *
     * @param calculation The {@link Supplier} instance that contains the calculation.
     * @param round       The number of calculations. More calculations get more accurate result while giving up time.
     *                    This value must be positive. In this case, it is recommended to set a large value.
     * @return The averaged calculation result.
     * @throws RuntimeException If {@code round} is negative or zero, or if the {@link Future} instance of
     *                          {@code calculation} supplier and/or{@link #averageSampleSync} was canceled, completed
     *                          exceptionally, and/or the current thread was interrupted while waiting.
     * @apiNote To deal with the heavy performance cost of {@link BigDecimal}, multiple async tasks are used to execute
     *          the {@code calculation} supplier and this method itself.
     */
    public static long averageSample(Supplier<Long> calculation, long round) {
        if (ALLOW_BIGINTEGER) {
            return averageSample(() -> BigDecimal.valueOf(calculation.get()), BigInteger.valueOf(round)).longValue();
        }
        return (long) averageSample(() -> (double) calculation.get(), round);
    }

    /**
     * Reduce errors by averaging multiple calculations.
     *
     * @param calculation The {@link Supplier} instance that contains the calculation.
     * @param round       The number of calculations. More calculations get more accurate result while giving up time.
     *                    This value must be positive. In this case, it is recommended to set a large value.
     * @return The averaged calculation result.
     * @throws RuntimeException If {@code round} is negative or zero, or if the {@link Future} instance of
     *                          {@code calculation} supplier and/or{@link #averageSampleSync} was canceled, completed
     *                          exceptionally, and/or the current thread was interrupted while waiting.
     * @apiNote 1. Fractional {@code round} value will have the same effect as {@code (long) round}, as it will be cast
     *          to {@code long} or {@link BigDecimal} anyway during processing.
     *          2. To deal with the heavy performance cost, multiple async tasks are used to execute the {@code calculation}
     *          supplier and this method itself.
     */
    public static float averageSample(Supplier<Float> calculation, float round) {
        if (ALLOW_BIGINTEGER) {
            return averageSample(() -> BigDecimal.valueOf(calculation.get()), BigInteger.valueOf((long) round)).floatValue();
        }
        return (float) averageSample(() -> (double) calculation.get(), round);
    }

    /**
     * Reduce errors by averaging multiple calculations.
     *
     * @param calculation The {@link Supplier} instance that contains the calculation.
     * @param round       The number of calculations. More calculations get more accurate result while giving up time.
     *                    This value must be positive. In this case, it is recommended to set a large value.
     * @return The averaged calculation result.
     * @throws RuntimeException If {@code round} is negative or zero, or if the {@link Future} instance of
     *                          {@code calculation} supplier and/or{@link #averageSampleSync} was canceled, completed
     *                          exceptionally, and/or the current thread was interrupted while waiting.
     * @apiNote 1. Fractional {@code round} value will have the same effect as {@code (long) round}, as it will be cast
     *          to {@code long} or {@link BigDecimal} anyway during processing.
     *          2. To deal with the heavy performance cost, multiple async tasks are used to execute the {@code calculation}
     *          supplier and this method itself.
     */
    public static double averageSample(Supplier<Double> calculation, double round) {
        if (ALLOW_BIGINTEGER) {
            return averageSample(() -> BigDecimal.valueOf(calculation.get()), BigInteger.valueOf((long) round)).doubleValue();
        }
        return supplyAsync(() -> averageSampleSync(calculation, (long) round));
    }

    /**
     * Reduce errors by averaging multiple calculations. Since the {@code calculation} returns a {@link BigDecimal},
     * it is impossible to process it without {@link BigDecimal}, {@link #ALLOW_BIGINTEGER} is ignored.
     *
     * @param calculation The {@link Supplier} instance that contains the calculation. The calculation result must
     *                    be an instance of {@link BigDecimal}.
     * @param round       The number of calculations. More calculations get more accurate result while giving up time.
     *                    This value must be positive. In this case, it is recommended to set a large value.
     * @return The averaged calculation result.
     * @throws RuntimeException If {@code round} is negative or zero, or if the {@link Future} instance of
     *                          {@code calculation} supplier and/or{@link #averageSampleSync} was canceled, completed
     *                          exceptionally, and/or the current thread was interrupted while waiting.
     * @apiNote To deal with the heavy performance cost of {@link BigDecimal}, multiple async tasks are used to execute
     *          the {@code calculation} supplier and this method itself.
     */
    public static @NotNull BigDecimal averageSample(Supplier<BigDecimal> calculation, BigInteger round) {
        return supplyAsync(() -> averageSampleSync(calculation, round));
    }
    
    /**
     * Reduce errors by averaging multiple calculations. This is a synchronized version of {@link #averageSample}
     * and should not be called from anywhere except {@link #averageSample}.
     *
     * @param calculation The {@link Supplier} instance that contains the calculation.
     * @param round       The number of calculations. More calculations get more accurate result while giving up time.
     *                    This value must be positive. In this case, it is recommended to set a large value.
     * @return The averaged calculation result.
     * @throws IndexOutOfBoundsException If {@code round} is negative or zero.
     * @throws RuntimeException          If the {@link Future} instance of {@code calculation} supplier was canceled,
     *                                   completed exceptionally, and/or the current thread was interrupted while waiting.
     * @apiNote To deal with heavy performance cost, async tasks are used to execute the {@code calculation} supplier.
     */
    private static double averageSampleSync(Supplier<Double> calculation, long round) {
        // Check if round is out of bound
        if (round <= 0) {
            throw new IndexOutOfBoundsException(round);
        }
        
        double result = 0;
        for (long i = 0; i < round; i++) { // Same as above
            result += ThreadedExecutor.supplyAsync(calculation);
        }
        return result / round;
    }
    
    /**
     * Reduce errors by averaging multiple calculations. This is a synchronized version of {@link #averageSample}
     * and should not be called from anywhere except {@link #averageSample}.
     *
     * @param calculation The {@link Supplier} instance that contains the calculation. The calculation result must
     *                    be an instance of {@link BigDecimal}.
     * @param round       The number of calculations. More calculations get more accurate result while giving up time.
     *                    This value must be positive. In this case, it is recommended to set a large value.
     * @return The averaged calculation result.
     * @throws IndexOutOfBoundsException If {@code round} is negative or zero.
     * @throws RuntimeException          If the {@link Future} instance of {@code calculation} supplier was canceled,
     *                                   completed exceptionally, and/or the current thread was interrupted while waiting.
     * @apiNote To deal with heavy performance cost of {@link BigDecimal}, async tasks are used to execute the
     *          {@code calculation} supplier.
     */
    private static @NotNull BigDecimal averageSampleSync(Supplier<BigDecimal> calculation, BigInteger round) {
        // Check if round is out of bound
        if (round.compareTo(BigInteger.valueOf(0)) <= 0) { // Use BigInteger.valueOf(0) instead of BigInteger.ZERO to ensure new instances of BigInteger are created
            throw new IndexOutOfBoundsException(round.longValue());
        }

        BigDecimal result = BigDecimal.valueOf(0); // Same as above
        for (BigInteger i = BigInteger.valueOf(0); i.compareTo(round) < 0; i = i.add(BigInteger.valueOf(1))) { // Same as above
            result = result.add(ThreadedExecutor.supplyAsync(calculation));
        }
        return result.divide(new BigDecimal(round), RoundingMode.UNNECESSARY);
    }
}