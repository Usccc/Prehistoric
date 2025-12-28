package com.wiyuka.prehistoric.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyArg(
        method = "collide",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/AABB;expandTowards(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;"
        ),
        index = 0
    )
    private Vec3 modifyExpandVec(Vec3 originalVec) {
        return originalVec.multiply(2.0, 2.0, 2.0);//充分收集实体碰撞体，以确保碰撞完全执行，且结果准确。
    }

    @ModifyArg(
        method = "collideBoundingBox",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/AABB;expandTowards(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;"
        ),
        index = 0
    )
    private static Vec3 modifyExpandVec2(Vec3 originalVec) {
        return originalVec.multiply(2.0, 2.0, 2.0);//充分收集方块碰撞体，以确保碰撞完全执行，且结果准确。
    }

}
