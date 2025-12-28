package com.wiyuka.prehistoric.mixin;

import com.wiyuka.prehistoric.util.MathHelper;
import com.wiyuka.prehistoric.config.ModConfig;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    private BigDecimal prehistoric$fibonacciVision(BigDecimal n) {
        // TODO: BigDecimal calculation that does not follow the configuration
        if (n.compareTo(BigDecimal.valueOf(1)) <= 0) {
            return n;
        }
        // Decompose the Fibonacci calculation into parallel sub-problems for robust, isolated computation
        BigDecimal r1 = MathHelper.averageSample(() -> prehistoric$fibonacciVision(n.subtract(BigDecimal.valueOf(1))), BigDecimal.valueOf(MathHelper.DEFAULT_ROUND));
        BigDecimal r2 = MathHelper.averageSample(() -> prehistoric$fibonacciVision(n.subtract(BigDecimal.valueOf(2))), BigDecimal.valueOf(MathHelper.DEFAULT_ROUND));
        return r1.add(r2);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void prehistoricVisionOptimization(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        if(ModConfig.CLIENT.visionOptimization.get()) {
            BigDecimal vision = prehistoric$fibonacciVision(BigDecimal.valueOf(2));
            BigDecimal angle = new BigDecimal("0.0");
            for (int i = 0; i < 1000; i++) {
                BigDecimal finalAngle = angle; // Finalize angle in order to execute averageSample successfully
                int finalI = i; // Same as above
                angle = MathHelper.averageSample(() -> finalAngle.add(java.math.BigDecimal.valueOf(Math.sin(finalI * 0.01))), BigDecimal.valueOf(MathHelper.DEFAULT_ROUND));
            }
        }
    }
}