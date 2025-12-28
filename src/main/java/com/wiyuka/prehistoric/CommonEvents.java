package com.wiyuka.prehistoric;

import static com.wiyuka.prehistoric.util.ThreadedExecutor.runAsync;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.wiyuka.prehistoric.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class CommonEvents {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.save.failed"));

    static int tickCount = 0;

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {
        if (ModConfig.COMMON.autoSave.get() || ++tickCount % 5 == 0) {
            runAsync(() -> event.getServer().saveEverything(false, true, true)); // Do not supress log. LOG FLOOD!
        }
    }
}