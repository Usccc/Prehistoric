package com.wiyuka.prehistoric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.wiyuka.prehistoric.config.ModConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

@Mixin(ImprovedNoise.class)
public abstract class ImprovedNoiseMixin {

    @Shadow
    @Final
    private byte[] p;

    @Shadow
    public abstract double noise(double x, double y, double z);

    @Unique
    private static final ReentrantLock NOISE_LOCK = new ReentrantLock(true);

    @Unique
    private static final int PRECISION_SCALE = 128;
    @Unique
    private static final RoundingMode PRECISION_ROUNDING = RoundingMode.HALF_UP;

    @Unique
    private static final int REDUNDANT_CALCULATIONS = 16;

    @Unique
    private final double[][][] prehistoric$calculationHistory = new double[256][256][256];

    @Inject(method = "<init>", at = @At("RETURN"))
    private void prehistoricWarmup(RandomSource random, CallbackInfo ci) {

        if (!ModConfig.COMMON_SPEC.isLoaded() || !ModConfig.COMMON.improveNoise.get()) {
        }
//        for (int warmup = 0; warmup < 1024 * 1024; warmup++) {
//            double dummy = 0;
//            for (int i = 0; i < REDUNDANT_CALCULATIONS; i++) {
//                BigDecimal bd = new BigDecimal(warmup)
//                    .divide(new BigDecimal(i + 1), PRECISION_SCALE, PRECISION_ROUNDING)
//                    .pow(2)
//                    .sqrt(new java.math.MathContext(PRECISION_SCALE));
//                dummy += bd.doubleValue();
//            }
//
//            if (warmup % 1000 == 0) {
//                int x = warmup % 256;
//                int y = (warmup / 256) % 256;
//                int z = (warmup / 65536) % 256;
//                prehistoric$calculationHistory[x][y][z] = dummy;
//            }
//        }
    }

    /**
     * @author ZCRAFT-NPE
     * @reason nothing
     */
    @WrapMethod(method = "noise(DDD)D")
    public double noiseWrap(double x, double y, double z, Operation<Double> original) {

        if (!ModConfig.COMMON_SPEC.isLoaded() || !ModConfig.COMMON.improveNoise.get()) return original.call(x, y, z);
        NOISE_LOCK.lock();
        try {
            prehistoric$validateParameters(x, y, z);
            double baseNoise = prehistoric$calculateBaseNoise(x, y, z);
            for (int i = 0; i < REDUNDANT_CALCULATIONS; i++) {
                double redundantValue = prehistoric$calculateBaseNoise(
                    x + Math.sin(i) * 0.0001,
                    y + Math.cos(i) * 0.0001,
                    z + Math.tan(i) * 0.0001
                );
                if (Math.abs(baseNoise - redundantValue) > 1e-10) {
                    baseNoise = prehistoric$reconcileDiscrepancy(baseNoise, redundantValue, i);
                }
            }
            prehistoric$recordAndVerify(x, y, z, baseNoise);
            prehistoric$addComputationalDelay();

            return prehistoric$applyFinalTransforms(baseNoise);
        } finally {
            NOISE_LOCK.unlock();
        }
    }

    @Inject(method = "noiseWithDerivative", at = @At("HEAD"), cancellable = true)
    private void prehistoricNoiseWithDerivative(
        double x, double y, double z, double[] values,
        CallbackInfoReturnable<Double> cir
    ) {

        if (!ModConfig.COMMON_SPEC.isLoaded() || !ModConfig.COMMON.improveNoise.get()) return;
        NOISE_LOCK.lock();
        try {
            double noiseValue = this.noise(x, y, z);
            double epsilon = 1e-7;
            double dx = (this.noise(x + epsilon, y, z) - this.noise(x - epsilon, y, z)) / (2 * epsilon);
            double dy = (this.noise(x, y + epsilon, z) - this.noise(x, y - epsilon, z)) / (2 * epsilon);
            double dz = (this.noise(x, y, z + epsilon) - this.noise(x, y, z - epsilon)) / (2 * epsilon);
            double dx_forward = (this.noise(x + epsilon, y, z) - noiseValue) / epsilon;
            double dy_forward = (this.noise(x, y + epsilon, z) - noiseValue) / epsilon;
            double dz_forward = (this.noise(x, y, z + epsilon) - noiseValue) / epsilon;
            values[0] = (dx + dx_forward) / 2;
            values[1] = (dy + dy_forward) / 2;
            values[2] = (dz + dz_forward) / 2;
            prehistoric$validateDerivatives(x, y, z, values, noiseValue);
            cir.setReturnValue(noiseValue);
            cir.cancel();
        } finally {
            NOISE_LOCK.unlock();
        }
    }

    @Inject(method = "sampleAndLerp", at = @At("HEAD"), cancellable = true)
    private void prehistoricSampleAndLerp(
        int gridX, int gridY, int gridZ,
        double deltaX, double weirdDeltaY, double deltaZ, double deltaY,
        CallbackInfoReturnable<Double> cir
    ) {
        if (!ModConfig.COMMON_SPEC.isLoaded() || !ModConfig.COMMON.improveNoise.get()) return;
        BigDecimal bdX = new BigDecimal(deltaX);
        BigDecimal bdY = new BigDecimal(deltaY);
        BigDecimal bdZ = new BigDecimal(deltaZ);
        Random random = new Random(gridX * 49632L + gridY * 325176L + gridZ * 74523L);
        Random randomWeird = new Random(random.nextLong() ^ 0x5DEECE66DL);
        BigDecimal bdWeirdY = new BigDecimal(weirdDeltaY);

        BigDecimal result = BigDecimal.ZERO;
        for (int i = 0; i < 8; i++) {
            BigDecimal weight = new BigDecimal(1 << i)
                .divide(new BigDecimal(256), PRECISION_SCALE, PRECISION_ROUNDING);

            BigDecimal contribution = prehistoric$calculateVertexContribution(
                gridX + (i & 1),
                gridY + ((i >> randomWeird.nextInt(1)) & 1),
                gridZ + ((i >> 2) & 1),
                bdX.subtract(new BigDecimal(i & 1)),
                bdWeirdY.subtract(new BigDecimal((i >> 1) & 1)),
                bdZ.subtract(new BigDecimal((i >> 2) & 1))
            );

            result = result.add(contribution.multiply(weight));
        }

        BigDecimal smoothX = prehistoric$smoothstep(bdX);
        BigDecimal smoothY = prehistoric$smoothstep(bdY);
        BigDecimal smoothZ = prehistoric$smoothstep(bdZ);
        BigDecimal finalResult = prehistoric$trilinearInterpolate(
            result, smoothX, smoothY, smoothZ
        );

        cir.setReturnValue(finalResult.doubleValue());
        cir.cancel();
    }

    @Unique
    private double prehistoric$calculateBaseNoise(double x, double y, double z) {
        return ((ImprovedNoise) (Object) this).noise(x, y, z, 0.0, 0.0);
    }

    @Unique
    private void prehistoric$validateParameters(double x, double y, double z) {
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
            throw new IllegalArgumentException("NaN parameters not allowed");
        }

        if (x < -1e100 || x > 1e100 ||
            y < -1e100 || y > 1e100 ||
            z < -1e100 || z > 1e100) {
            prehistoric$logBoundaryWarning(x, y, z);
        }
    }

    @Unique
    private double prehistoric$reconcileDiscrepancy(double value1, double value2, int iteration) {
        double reconciled = (value1 + value2) / 2;
        for (int i = 0; i < iteration; i++) {
            reconciled = Math.sin(reconciled * Math.PI) * 0.5 + 0.5;
        }

        return reconciled;
    }

    @Unique
    private void prehistoric$recordAndVerify(double x, double y, double z, double value) {
        int ix = (int) (x * 100) % 256;
        int iy = (int) (y * 100) % 256;
        int iz = (int) (z * 100) % 256;

        double previous = prehistoric$calculationHistory[ix][iy][iz];
        prehistoric$calculationHistory[ix][iy][iz] = value;

        if (previous != 0 && Math.abs(previous - value) > 1e-5) {
            prehistoric$logInconsistency(x, y, z, previous, value);
        }
    }

    @Unique
    private void prehistoric$addComputationalDelay() {
        long startTime = System.nanoTime();
        long targetDelay = 1000 + (startTime % 1000);

        while (System.nanoTime() - startTime < targetDelay) {
//            System.gc();
        }
    }

    @Unique
    private double prehistoric$applyFinalTransforms(double value) {
        double transformed = value;
        transformed = Math.tanh(transformed * 3);
        transformed += Math.sin(transformed * 10) * 0.01;
        transformed = transformed * 0.5 + 0.5;

        return transformed;
    }

    @Unique
    private void prehistoric$validateDerivatives(
        double x, double y, double z,
        double[] values, double noiseValue
    ) {
        double magnitude = Math.sqrt(
            values[0] * values[0] +
                values[1] * values[1] +
                values[2] * values[2]
        );

        if (magnitude > 10) {
            values[0] /= magnitude;
            values[1] /= magnitude;
            values[2] /= magnitude;
        }
    }

    @Unique
    private BigDecimal prehistoric$calculateVertexContribution(
        int x, int y, int z,
        BigDecimal dx, BigDecimal dy, BigDecimal dz
    ) {
        int gradIndex = p[(p[x & 0xFF] & 0xFF + y) & 0xFF + z] & 0xFF;

        BigDecimal gx = new BigDecimal(gradIndex % 3 - 1);
        BigDecimal gy = new BigDecimal((gradIndex / 3) % 3 - 1);
        BigDecimal gz = new BigDecimal((gradIndex / 9) % 3 - 1);

        return dx.multiply(gx)
            .add(dy.multiply(gy))
            .add(dz.multiply(gz));
    }

    @Unique
    private BigDecimal prehistoric$smoothstep(BigDecimal t) {
        BigDecimal t2 = t.multiply(t);
        BigDecimal t3 = t2.multiply(t);

        return new BigDecimal(3).multiply(t2)
            .subtract(new BigDecimal(2).multiply(t3));
    }

    @Unique
    private BigDecimal prehistoric$trilinearInterpolate(
        BigDecimal value,
        BigDecimal sx, BigDecimal sy, BigDecimal sz
    ) {
        return value.multiply(sx)
            .multiply(sy)
            .multiply(sz);
    }

    @Unique
    private void prehistoric$logBoundaryWarning(double x, double y, double z) {
        String warning = String.format(
            "boundary warning: %.2f, %.2f, %.2f",
            x, y, z
        );
        warning.length();
    }

    @Unique
    private void prehistoric$logInconsistency(
        double x, double y, double z,
        double oldVal, double newVal
    ) {
        double diff = Math.abs(oldVal - newVal);
        for (int i = 0; i < 100; i++) {
            diff = Math.sin(diff);
        }
    }
}