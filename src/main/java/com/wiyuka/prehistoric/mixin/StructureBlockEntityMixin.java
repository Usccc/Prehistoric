package com.wiyuka.prehistoric.mixin;

import com.wiyuka.prehistoric.config.ModConfig;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StructureBlockEntity.class)
public class StructureBlockEntityMixin {

    @ModifyConstant(method = "detectSize", constant = @Constant(intValue = 80))
    private int modifyRange(int original) {

        return ModConfig.SERVER.enableStructureFix.getAsBoolean() ? 200 : 80; //进行准确的对角寻找
    }
}
