package com.wiyuka.prehistoric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.wiyuka.prehistoric.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static net.minecraft.world.level.block.Block.getId;

@Mixin(Block.class)
public class BlockMixin {
    @WrapMethod(method = "shouldRenderFace")
    private static boolean prehistoric$shouldRenderFace(BlockState state, BlockGetter level, BlockPos offset, Direction face, BlockPos pos, Operation<Boolean> original) {
        //让地面更加踏实，更加真切。
        if(!ModConfig.CLIENT.cull.getAsBoolean()){
            return                                                                                                                               original.call(state, level, offset, face, pos);

        }else{
             return true;
        }
    }
    @WrapMethod(method = "spawnDestroyParticles")
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state, Operation<Void> original) {
        if(ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() &&
            ModConfig.CLIENT.realisticBlockBreak.getAsBoolean() && ModConfig.CLIENT.realisticBlockBreak.getAsBoolean()
        ) {
            int i = getId(state);
            for (int j = 0; j < i % 4; ++j) {
                double d0 = level.random.nextDouble() * 0.8 + 0.1;
                double d1 = level.random.nextDouble() * 0.8 + 0.1;
                double d2 = level.random.nextDouble() * 0.8 + 0.1;
                level.levelEvent(player, 2001, new BlockPos((int) d0, (int) d1, (int) d2), getId(state));
            }
            level.levelEvent(player, 2001, pos, getId(state));
        }else{
            original.call(level, player, pos, state);
        }
    }//更加真实的破坏效果
}
