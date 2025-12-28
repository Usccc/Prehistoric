package com.wiyuka.prehistoric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ModelBlockRenderer.class)
public class SectionBuilderMixin {@WrapMethod(method = "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V")
public void tesselateWithAO(
    BlockAndTintGetter level,
    BakedModel model,
    BlockState state,
    BlockPos pos,
    PoseStack poseStack,
    VertexConsumer consumer,
    boolean checkSides,
    RandomSource random,
    long seed,
    int packedOverlay,
    ModelData modelData,
    RenderType renderType,
    Operation<Void> original
) {
    random.setSeed(seed);
    PoseStack.Pose pose = poseStack.last();
    Matrix4f mat = pose.pose();
    Matrix3f normalMat = pose.normal();
    BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();

    for (Direction dir : Direction.values()) {
        int i = LightTexture.FULL_BLOCK;
        List<BakedQuad> quads = model.getQuads(state, dir, random, modelData, renderType);
        prehistoric$voxelizeQuads(quads, consumer, mat, normalMat,i, packedOverlay);
    }

    List<BakedQuad> quads = model.getQuads(state, null, random, modelData, renderType);
    int i = LightTexture.FULL_BLOCK;
    prehistoric$voxelizeQuads(quads, consumer, mat, normalMat, i,packedOverlay);
}

    @Unique
    private static void prehistoric$computeVoxelUV(
        Direction dir,
        int x, int y, int z,
        int vx0, int vy0, int vz0,
        int vx1, int vy1, int vz1,
        float minU, float minV,
        float maxU, float maxV,
        float[] outUV
    ) {
        float u, v;
        switch (dir) {
            case NORTH, SOUTH -> {
                u = (x - vx0) / (float)(vx1 - vx0 + 1);
                v = (y - vy0) / (float)(vy1 - vy0 + 1);
            }
            case UP, DOWN -> {
                u = (x - vx0) / (float)(vx1 - vx0 + 1);
                v = (z - vz0) / (float)(vz1 - vz0 + 1);
            }
            case EAST, WEST -> {
                u = (z - vz0) / (float)(vz1 - vz0 + 1);
                v = (y - vy0) / (float)(vy1 - vy0 + 1);
            }
            default -> {
                u = 0;
                v = 0;
            }
        }
        outUV[0] = minU + u * (maxU - minU);
        outUV[1] = minV + v * (maxV - minV);
    }

    @Unique
    private void prehistoric$voxelizeQuads(
        List<BakedQuad> quads,
        VertexConsumer consumer,
        Matrix4f mat,
        Matrix3f normalMat,
        int light,
        int overlay
    ) {
        for (BakedQuad quad : quads) {
            int[] data = quad.getVertices();

            float minX = 1, minY = 1, minZ = 1;
            float maxX = 0, maxY = 0, maxZ = 0;
            float minU = 1, minV = 1;
            float maxU = 0, maxV = 0;
            int packedLight = 0;

            for (int i = 0; i < 4; i++) {
                int base = i * 8;
                float x = Float.intBitsToFloat(data[base]);
                float y = Float.intBitsToFloat(data[base + 1]);
                float z = Float.intBitsToFloat(data[base + 2]);
                float u = Float.intBitsToFloat(data[base + 4]);
                float v = Float.intBitsToFloat(data[base + 5]);

                // 提取光照信息 (通常在索引6)
                if (i == 0) {
                    packedLight = light;
                }

                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                minZ = Math.min(minZ, z);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                maxZ = Math.max(maxZ, z);
                minU = Math.min(minU, u);
                minV = Math.min(minV, v);
                maxU = Math.max(maxU, u);
                maxV = Math.max(maxV, v);
            }

            int vx0 = Mth.clamp((int)(minX * 16), 0, 15);
            int vy0 = Mth.clamp((int)(minY * 16), 0, 15);
            int vz0 = Mth.clamp((int)(minZ * 16), 0, 15);
            int vx1 = Mth.clamp((int)(maxX * 16), 0, 15);
            int vy1 = Mth.clamp((int)(maxY * 16), 0, 15);
            int vz1 = Mth.clamp((int)(maxZ * 16), 0, 15);

            Direction quadDir = quad.getDirection();

            for (int x = vx0; x <= vx1; x++) {
                for (int y = vy0; y <= vy1; y++) {
                    for (int z = vz0; z <= vz1; z++) {
                        prehistoric$emitVoxelCube(
                            consumer, mat, normalMat,
                            x, y, z,
                            overlay, packedLight,
                            minU, minV, maxU, maxV,
                            vx0, vy0, vz0, vx1, vy1, vz1,
                            quadDir
                        );
                    }
                }
            }
        }
    }

    @Unique
    private void prehistoric$emitVoxelCube(
        VertexConsumer vc,
        Matrix4f mat,
        Matrix3f normalMat,
        int x, int y, int z,
        int overlay, int light,
        float minU, float minV, float maxU, float maxV,
        int vx0, int vy0, int vz0,
        int vx1, int vy1, int vz1,
        Direction originalDir
    ) {
        float s = 1f / 16f;
        float x0 = x * s;
        float y0 = y * s;
        float z0 = z * s;
        float x1 = x0 + s;
        float y1 = y0 + s;
        float z1 = z0 + s;

        float[] uv = new float[2];

        // +Z (SOUTH)
        prehistoric$computeVoxelUV(Direction.SOUTH, x, y, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        float u0 = uv[0], v0 = uv[1];
        prehistoric$computeVoxelUV(Direction.SOUTH, x+1, y, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        float u1 = uv[0];
        prehistoric$computeVoxelUV(Direction.SOUTH, x, y+1, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        float v1 = uv[1];

        vc.addVertex(mat, x0, y0, z1).setColor(255,255,255,255).setUv(u0,v0).setOverlay(overlay).setLight(light).setNormal(0,0,1);
        vc.addVertex(mat, x1, y0, z1).setColor(255,255,255,255).setUv(u1,v0).setOverlay(overlay).setLight(light).setNormal(0,0,1);
        vc.addVertex(mat, x1, y1, z1).setColor(255,255,255,255).setUv(u1,v1).setOverlay(overlay).setLight(light).setNormal(0,0,1);
        vc.addVertex(mat, x0, y1, z1).setColor(255,255,255,255).setUv(u0,v1).setOverlay(overlay).setLight(light).setNormal(0,0,1);

        // -Z (NORTH)
        prehistoric$computeVoxelUV(Direction.NORTH, x, y, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        u0 = uv[0]; v0 = uv[1];
        prehistoric$computeVoxelUV(Direction.NORTH, x+1, y, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        u1 = uv[0];
        prehistoric$computeVoxelUV(Direction.NORTH, x, y+1, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        v1 = uv[1];

        vc.addVertex(mat, x1, y0, z0).setColor(255,255,255,255).setUv(u0,v0).setOverlay(overlay).setLight(light).setNormal(0,0,-1);
        vc.addVertex(mat, x0, y0, z0).setColor(255,255,255,255).setUv(u1,v0).setOverlay(overlay).setLight(light).setNormal(0,0,-1);
        vc.addVertex(mat, x0, y1, z0).setColor(255,255,255,255).setUv(u1,v1).setOverlay(overlay).setLight(light).setNormal(0,0,-1);
        vc.addVertex(mat, x1, y1, z0).setColor(255,255,255,255).setUv(u0,v1).setOverlay(overlay).setLight(light).setNormal(0,0,-1);

        // +X (EAST)
        prehistoric$computeVoxelUV(Direction.EAST, x, y, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        u0 = uv[0]; v0 = uv[1];
        prehistoric$computeVoxelUV(Direction.EAST, x, y, z+1, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        u1 = uv[0];
        prehistoric$computeVoxelUV(Direction.EAST, x, y+1, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        v1 = uv[1];

        vc.addVertex(mat, x1, y0, z1).setColor(255,255,255,255).setUv(u0,v0).setOverlay(overlay).setLight(light).setNormal(1,0,0);
        vc.addVertex(mat, x1, y0, z0).setColor(255,255,255,255).setUv(u1,v0).setOverlay(overlay).setLight(light).setNormal(1,0,0);
        vc.addVertex(mat, x1, y1, z0).setColor(255,255,255,255).setUv(u1,v1).setOverlay(overlay).setLight(light).setNormal(1,0,0);
        vc.addVertex(mat, x1, y1, z1).setColor(255,255,255,255).setUv(u0,v1).setOverlay(overlay).setLight(light).setNormal(1,0,0);

        // -X (WEST)
        prehistoric$computeVoxelUV(Direction.WEST, x, y, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        u0 = uv[0]; v0 = uv[1];
        prehistoric$computeVoxelUV(Direction.WEST, x, y, z+1, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        u1 = uv[0];
        prehistoric$computeVoxelUV(Direction.WEST, x, y+1, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        v1 = uv[1];

        vc.addVertex(mat, x0, y0, z0).setColor(255,255,255,255).setUv(u0,v0).setOverlay(overlay).setLight(light).setNormal(-1,0,0);
        vc.addVertex(mat, x0, y0, z1).setColor(255,255,255,255).setUv(u1,v0).setOverlay(overlay).setLight(light).setNormal(-1,0,0);
        vc.addVertex(mat, x0, y1, z1).setColor(255,255,255,255).setUv(u1,v1).setOverlay(overlay).setLight(light).setNormal(-1,0,0);
        vc.addVertex(mat, x0, y1, z0).setColor(255,255,255,255).setUv(u0,v1).setOverlay(overlay).setLight(light).setNormal(-1,0,0);

        // +Y (UP)
        prehistoric$computeVoxelUV(Direction.UP, x, y, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        u0 = uv[0]; v0 = uv[1];
        prehistoric$computeVoxelUV(Direction.UP, x+1, y, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        u1 = uv[0];
        prehistoric$computeVoxelUV(Direction.UP, x, y, z+1, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        v1 = uv[1];

        vc.addVertex(mat, x0, y1, z1).setColor(255,255,255,255).setUv(u0,v0).setOverlay(overlay).setLight(light).setNormal(0,1,0);
        vc.addVertex(mat, x1, y1, z1).setColor(255,255,255,255).setUv(u1,v0).setOverlay(overlay).setLight(light).setNormal(0,1,0);
        vc.addVertex(mat, x1, y1, z0).setColor(255,255,255,255).setUv(u1,v1).setOverlay(overlay).setLight(light).setNormal(0,1,0);
        vc.addVertex(mat, x0, y1, z0).setColor(255,255,255,255).setUv(u0,v1).setOverlay(overlay).setLight(light).setNormal(0,1,0);

        // -Y (DOWN)
        prehistoric$computeVoxelUV(Direction.DOWN, x, y, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        u0 = uv[0]; v0 = uv[1];
        prehistoric$computeVoxelUV(Direction.DOWN, x+1, y, z, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        u1 = uv[0];
        prehistoric$computeVoxelUV(Direction.DOWN, x, y, z+1, vx0, vy0, vz0, vx1, vy1, vz1, minU, minV, maxU, maxV, uv);
        v1 = uv[1];

        vc.addVertex(mat, x0, y0, z0).setColor(255,255,255,255).setUv(u0,v0).setOverlay(overlay).setLight(light).setNormal(0,-1,0);
        vc.addVertex(mat, x1, y0, z0).setColor(255,255,255,255).setUv(u1,v0).setOverlay(overlay).setLight(light).setNormal(0,-1,0);
        vc.addVertex(mat, x1, y0, z1).setColor(255,255,255,255).setUv(u1,v1).setOverlay(overlay).setLight(light).setNormal(0,-1,0);
        vc.addVertex(mat, x0, y0, z1).setColor(255,255,255,255).setUv(u0,v1).setOverlay(overlay).setLight(light).setNormal(0,-1,0);
    }

}
