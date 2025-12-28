package com.wiyuka.prehistoric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.*;
import com.wiyuka.prehistoric.config.ModConfig;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @WrapMethod(method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V")
    private void prehistoric$renderSnowAndRainOriginal(LightTexture lightTexture, float partialTick,
                                                       double camX, double camY, double camZ,
                                                       Operation<Void> original) {
        if( ModConfig.CLIENT_SPEC.isLoaded() || !ModConfig.CLIENT.realisticRain.get()) {
            original.call(lightTexture, partialTick, camX, camY, camZ);
            return;
        }
        for(int i = 0; i < new Random(new Random(new Random(new Random(new Random().nextLong()).nextLong()).nextLong()).nextLong()).nextInt(20); i++) {
            double offsetX = (Math.random() - 0.5) * 0.5; // -0.25 ~ 0.25
            double offsetY = (Math.random() - 0.5) * 0.5;
            double offsetZ = (Math.random() - 0.5) * 0.5;

            PoseStack matrixStack = new PoseStack();
            matrixStack.pushPose(); // 保存原状态

            matrixStack.translate(offsetX, offsetY, offsetZ);

            original.call(lightTexture, partialTick, camX + offsetX, camY + offsetY, camZ + offsetZ);

            matrixStack.popPose();
        }
    }

    /**
     * @author Ryan100c
     * @reason cooler sky!!!
     */
    @Overwrite
    private static MeshData buildSkyDisc(Tesselator tesselator, float y) {


        float f = Math.signum(y) * 512.0F;
        float f1 = 512.0F;
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        bufferbuilder.addVertex(0.0F, y, 0.0F);

        for(int i = -180; i <= 180; i += 45) {
            bufferbuilder.addVertex(f * Mth.cos((float)i * ((float)Math.PI / 180F)), y, 512.0F * Mth.sin((float)i * ((float)Math.PI / 180F)));
        }

        if(ModConfig.CLIENT_SPEC.isLoaded() || !ModConfig.CLIENT.fancySky.get()) return bufferbuilder.buildOrThrow();

        return prehistoric$buildSphere(1000,20,3);//更加真实的天空
    }
    @Unique
    private static MeshData prehistoric$buildSphere(float radius, int latSegments, int lonSegments) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);

        for (int lat = 0; lat < latSegments; lat++) {
            float theta1 = (float) lat / latSegments * (float) Math.PI;           // 0 -> π
            float theta2 = (float) (lat + 1) / latSegments * (float) Math.PI;

            for (int lon = 0; lon <= lonSegments; lon++) {
                float phi = (float) lon / lonSegments * ((float) Math.PI * 2);   // 0 -> 2π

                // 顶点1
                float x1 = radius * (float)(Math.sin(theta1) * Math.cos(phi));
                float y1 = radius * (float)Math.cos(theta1);
                float z1 = radius * (float)(Math.sin(theta1) * Math.sin(phi));

                // 顶点2
                float x2 = radius * (float)(Math.sin(theta2) * Math.cos(phi));
                float y2 = radius * (float)Math.cos(theta2);
                float z2 = radius * (float)(Math.sin(theta2) * Math.sin(phi));

                bufferbuilder.addVertex(x1, y1, z1);
                bufferbuilder.addVertex(x2, y2, z2);
            }
        }

        return bufferbuilder.build();
    }
    /**
     * @author Ryan100c
     * @reason better stars
     */
    @WrapMethod(method = "drawStars")
    private MeshData drawStars(Tesselator tesselator, Operation<MeshData> original) {

        if(!ModConfig.CLIENT_SPEC.isLoaded() || !ModConfig.CLIENT.enableStars.get()) return original.call(tesselator);

        RandomSource randomsource = RandomSource.create(114514L);//是的
        int starCount = ModConfig.CLIENT.starCount.getAsInt();
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);

        for(int j = 0; j < starCount; j++) {
            float fx = randomsource.nextFloat() * 2.0F - 1.0F;
            float fy = randomsource.nextFloat() * 2.0F - 1.0F;
            float fz = randomsource.nextFloat() * 2.0F - 1.0F;

            float lenSq = Mth.lengthSquared(fx, fy, fz);
            if (!(lenSq <= 0.01F) && !(lenSq >= 1.0F)) {
                Vector3f center = new Vector3f(fx, fy, fz).normalize(100.0F);

                float radius = 0.15F;

                int segments = (int)ModConfig.CLIENT.starRadius.getAsDouble();//真实的星星
                for (int k = 0; k < segments; k++) {
                    float angle1 = (float) (k * 2 * Math.PI / segments);
                    float angle2 = (float) ((k + 1) * 2 * Math.PI / segments);

                    Vector3f offset1 = new Vector3f(radius * Mth.cos(angle1), radius * Mth.sin(angle1), 0);
                    Vector3f offset2 = new Vector3f(radius * Mth.cos(angle2), radius * Mth.sin(angle2), 0);

                    Quaternionf q = (new Quaternionf()).rotateTo(new Vector3f(0,0,-1), center);
                    offset1.rotate(q);
                    offset2.rotate(q);

                    bufferbuilder.addVertex(center);
                    bufferbuilder.addVertex(center.add(offset1));
                    bufferbuilder.addVertex(center.add(offset2));
                }
            }
        }

        return bufferbuilder.buildOrThrow();
    }


}
