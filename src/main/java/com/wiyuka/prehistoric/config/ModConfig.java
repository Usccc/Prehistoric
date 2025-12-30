package com.wiyuka.prehistoric.config;

import com.wiyuka.prehistoric.util.MathHelper;
import com.wiyuka.prehistoric.util.ThreadHelper;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.math.BigDecimal;

public class ModConfig {
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec SERVER_SPEC;
    public static final ModConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;
    public static final Server SERVER;
    public static final Common COMMON;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        CLIENT = new Client(builder);
        CLIENT_SPEC = builder.build();
        SERVER = new Server(builder);
        SERVER_SPEC = builder.build();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }
    
    public static class Client {
        public final ModConfigSpec.BooleanValue realisticBlockBreak;
        public final ModConfigSpec.BooleanValue cull;
        public final ModConfigSpec.BooleanValue fancySky;
        public final ModConfigSpec.BooleanValue renderLog;
        public final ModConfigSpec.BooleanValue enableStars;
        public final ModConfigSpec.IntValue starCount;
        public final ModConfigSpec.DoubleValue starRadius;
        public final ModConfigSpec.BooleanValue realisticRain;
        public final ModConfigSpec.BooleanValue garbageMore;
        public final ModConfigSpec.BooleanValue fpsOptimize;
        public final ModConfigSpec.BooleanValue visionOptimization;

        Client(ModConfigSpec.Builder builder) {
            builder.push("beautiful");
            enableStars = builder
                .comment("好看的星星")
                .define("enableNiceStars", true);
            fancySky = builder
                .comment("华丽的天空")
                .define("fancySky", true);
            starCount = builder
                .comment("渲染多少星星")
                .defineInRange("starCount", 1500, 0, 10000);
            starRadius = builder
                .comment("圆润地")
                .defineInRange("starRadius", 50, 10.0, 100);
            garbageMore = builder
                .comment("更多垃圾")
                .define("garbageMore", false);
            cull = builder
                .comment("面剔除")
                .define("cull", true);
            builder.pop();
            
            builder.push("weather");
            realisticRain = builder
                .comment("更真实的雨滴效果")
                .define("realisticRain", true);
            renderLog = builder
                .comment("启用渲染日志")
                .define("renderLog", false);
            builder.pop();
            
            builder.push("performance");
            fpsOptimize = builder
                .comment("FPS优化")
                .define("fpsOptimize", true);
            realisticBlockBreak = builder
                .comment("更真实的方块破坏效果")
                .define("realisticBlockBreak", true);
            visionOptimization = builder
                .comment("视野优化")
                .define("visionOptimization", false);
            builder.pop();
        }
    }

    public static class Server {
        public final ModConfigSpec.BooleanValue enableStructureFix;
        public final ModConfigSpec.BooleanValue betterExplosion;

        Server(ModConfigSpec.Builder builder) {
            builder.push("all");
            betterExplosion = builder
                .comment("更好的爆炸计算")
                .define("betterExplosion", true);
            enableStructureFix = builder
                .comment("启用结构方块优化")
                .define("enableStructureFix", true);
            builder.pop();
        }
    }

    public static class Common {
        public final ModConfigSpec.BooleanValue autoSave;
        public final ModConfigSpec.BooleanValue improveNoise;
        public final ModConfigSpec.BooleanValue safePacket;
        
        /** Whether allows {@link BigDecimal} calculations. */
        public final ModConfigSpec.BooleanValue bigDecimal;
        
        /** The default rounds of calculations executed in {@link MathHelper#averageSample}. */
        public final ModConfigSpec.LongValue defaultRoundAveraged;
        
        /** Whether allows async executions. Temporarily hard-coded. */
        public final ModConfigSpec.BooleanValue allowAsync;
        public final ModConfigSpec.BooleanValue yesGc;
        public final ModConfigSpec.BooleanValue secureLogger;
        
        /** Whether allows busy {@link ThreadHelper#onSpinWait}. */
        public final ModConfigSpec.BooleanValue allowBusyWait;
        
        /** Whether allows busy {@link ThreadHelper#sleep}. */
        public final ModConfigSpec.BooleanValue allowBusySleep;
        
        /** Whether allows to establish memory fence through {@link ThreadHelper#fullFence()}. */
        public final ModConfigSpec.BooleanValue allowMemoryFence;

        Common(ModConfigSpec.Builder builder) {
            builder.push("secureLogger");
            secureLogger = builder
                .comment("启用安全日志记录器")
                .define("secureLogger", true);
            yesGc = builder
                .comment("启用及时垃圾回收")
                .define("yesGc", false);
            bigDecimal = builder
                .comment("使用大数字进行准确计算")
                .define("bigDecimal", false);
            defaultRoundAveraged = builder
                .comment("在MathHelper.averageSample中默认执行的计算次数")
                .defineInRange("defaultRoundAveraged", 1024, 1, Long.MAX_VALUE);
            allowAsync = builder
                .comment("启用异步运算")
                .define("allowAsync", true);
            safePacket = builder
                .comment("启用安全数据包处理")
                .define("safePacket", true);
            improveNoise = builder
                .comment("改进噪声函数")
                .define("improveNoise", false);
            autoSave = builder
                .comment("启用自动保存功能")
                .define("autoSave", true);
            allowBusyWait = builder
                .comment("启用忙等待")
                .define("allowBusyWait", true);
            allowBusySleep = builder
                .comment("启用忙睡眠")
                .define("allowBusySleep", true);
            allowMemoryFence = builder
                .comment("启用内存墙")
                .define("allowMemoryFence", true);
            builder.pop();
        }
    }
}
