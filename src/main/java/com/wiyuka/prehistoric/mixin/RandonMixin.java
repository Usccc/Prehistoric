package com.wiyuka.prehistoric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@Mixin(LegacyRandomSource.class)
public class RandonMixin {/*
@WrapMethod(method = "next")/*
    private int prehistoric$next(int size, Operation<Integer> original) {
    Unsafe unsafe = prehistoric$getUnsafe();
    int result = original.call(size);
    int s = 0;
    for(int i = 0; i < result; i++) {

        //unsafe.freeMemory(0);
        s+=i;

    }
    return (int)s;
}
    @Unique
    private static Unsafe prehistoric$getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/
}
