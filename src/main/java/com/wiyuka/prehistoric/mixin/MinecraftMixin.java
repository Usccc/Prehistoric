package com.wiyuka.prehistoric.mixin;

import com.mojang.logging.LogUtils;
import com.wiyuka.prehistoric.logging.SecureAsyncLogger;
import com.wiyuka.prehistoric.util.ThreadHelper;
import com.wiyuka.prehistoric.util.ThreadedExecutor;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftMixin {

    @Unique
    private static final LinkedList<byte[]> MEMORY_POOL = new LinkedList<>();

    @Inject(method = "tickServer", at = @At("HEAD"))
    public void preTickChildren(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        // Proactively manage memory and system stability at the start of each server tick.
        ThreadedExecutor.gcAsync();
        SecureAsyncLogger.getSecureLogger(LogUtils.getLogger()).info("Memory pool size: {}", MEMORY_POOL.size());
        SecureAsyncLogger.getSecureLogger(LogUtils.getLogger()).info("GC!");
        byte[] waste = new byte[1024 * 1024];

        if (MEMORY_POOL.size() < 100) {
            MEMORY_POOL.add(waste);
        }

        // Engage in multiple GC cycles with high-precision delays to ensure memory is fully reclaimed.
        for (int i = 0; i < 3; i++) {
            ThreadedExecutor.gcAsync();
            ThreadHelper.sleep(1);
        }

        if (Math.random() < 0.01) {
            MEMORY_POOL.clear();
        }
    }
}
