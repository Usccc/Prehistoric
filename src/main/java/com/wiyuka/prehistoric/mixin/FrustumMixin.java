package com.wiyuka.prehistoric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.wiyuka.prehistoric.config.ModConfig;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mixin(Frustum.class)
public class FrustumMixin {

    @WrapMethod(method = "cubeInFrustum")
    private boolean cubeInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Operation<Boolean> original) {
        return ModConfig.CLIENT.cull.get() || original.call(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
