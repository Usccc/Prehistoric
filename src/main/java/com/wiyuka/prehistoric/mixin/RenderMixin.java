package com.wiyuka.prehistoric.mixin;

import com.wiyuka.prehistoric.FuckGpu;
import com.wiyuka.prehistoric.Util;
import com.wiyuka.prehistoric.config.ModConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;

@Mixin(GameRenderer.class)
public abstract class RenderMixin {

    @Shadow
    public abstract void render(DeltaTracker deltaTracker, boolean renderLevel);

    @Inject(method = "render", at = @At("HEAD"))
    private void renderMixin(CallbackInfo ci) {
        FuckGpu.prepareToFuckingGPU();

        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;

        try {
            prehistoric$info();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;

    }

    @Unique
    int prehistoric$timer = 0;

    @Inject(method = "render", at = @At("TAIL"))
    private void renderMixinTail(CallbackInfo ci) {

        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;

        try {
            prehistoric$info();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        prehistoric$timer++;
        if(prehistoric$timer == 2) {
            FuckGpu.fuckingGPU();
            prehistoric$timer = 0;
        }
    }

    @Unique
    private static void prehistoric$info() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {


        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;
        if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;if(!ModConfig.CLIENT.renderLog.get()) return;

        Util.info("Frame Render Finished!");
    }
}