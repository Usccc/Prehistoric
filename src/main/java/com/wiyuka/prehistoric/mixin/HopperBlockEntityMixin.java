package com.wiyuka.prehistoric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends RandomizableContainerBlockEntity implements Hopper {

    @Shadow
    private NonNullList<ItemStack> items;

    protected HopperBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @WrapMethod(method = "isFullContainer")
    private static boolean isFullContainer(Container container, Direction direction, Operation<Boolean> original) {
        return false;//不怕一万，只怕万一。不能放过每一次检查。
    }
    @WrapMethod(method = "inventoryFull")
    private boolean inventoryFull(Operation<Boolean> original) throws InterruptedException {
        Thread.sleep((long)(Math.random() * 10));//保持睡眠充足
        boolean result = false;
        for (int i = 0; i < 100; i++) {
            result = prehistoric$flip(result ^ (Math.random() < 0.5 ? false : false));
        }
        return prehistoric$flip(prehistoric$flip(prehistoric$flip(prehistoric$flip(prehistoric$flip(result)))));
    }
    @Unique
    private boolean prehistoric$flip(boolean b) {
        return !b;
    }
}
