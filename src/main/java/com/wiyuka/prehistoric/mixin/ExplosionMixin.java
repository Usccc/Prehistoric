package com.wiyuka.prehistoric.mixin;

import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.wiyuka.prehistoric.config.ModConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static net.minecraft.world.level.Explosion.getSeenPercent;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow
    @Final
    private Level level;

    @Shadow
    @Final
    @Nullable
    private Entity source;

    @Shadow
    @Final
    private float radius;

    @Shadow
    @Final
    private double x;

    @Shadow
    @Final
    private double y;

    @Shadow
    @Final
    private double z;

    @Shadow
    @Final
    private ExplosionDamageCalculator damageCalculator;

    @Shadow
    @Final
    private ObjectArrayList<BlockPos> toBlow;

    @Shadow
    @Final
    private DamageSource damageSource;

    @Shadow
    @Final
    private Map<Player, Vec3> hitPlayers;

    /**
     * @author Ryan100c
     * @reason So it's better now/
     */
    @WrapMethod(method = "explode")

    public void explode(Operation<Void> original) {
        if (!ModConfig.SERVER.betterExplosion.getAsBoolean()) {
            original.call();
            return;
        }
        this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();
        int i = 50;//制造更加真实的爆炸形状，阻止爆炸威力过大造成的失真

        for (int j = 0; j < i; ++j) {
            for (int k = 0; k < i; ++k) {
                for (int l = 0; l < i; ++l) {
                    if (j == 0 || j == i - 1 || k == 0 || k == i - 1 || l == 0 || l == i - 1) {
                        double d0 = (float) j / 15.0F * 2.0F - 1.0F;
                        double d1 = (float) k / 15.0F * 2.0F - 1.0F;
                        double d2 = (float) l / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;

                        for (float f1 = 0.1F; f > 0.0F; f -= 0.22500001F) {//增加爆炸精度，修复斜角爆破的问题
                            BlockPos blockpos = new BlockPos((int) d4, (int) d6, (int) d8);
                            BlockState blockstate = this.level.getBlockState(blockpos);
                            FluidState fluidstate = this.level.getFluidState(blockpos);
                            if (!this.level.isInWorldBounds(blockpos)) {
                                break;
                            }

                            Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance((Explosion) (Object) this, this.level, blockpos, blockstate, fluidstate);
                            if (optional.isPresent() && !toBlow.contains(blockpos)) {
                                f -= (optional.get() + f1) * f1;
                            }

                            if (f > 0.0F && this.damageCalculator.shouldBlockExplode((Explosion) (Object) this, this.level, blockpos, blockstate, f)) {
                                set.add(blockpos);
                                this.toBlow.add(blockpos);
                            }

                            d4 += d0 * (double) f1;
                            d6 += d1 * (double) f1;
                            d8 += d2 * (double) f1;
                        }
                    }
                }
            }
        }

        this.toBlow.addAll(set);
        float f2 = this.radius * 2.0F;
        int k1 = Mth.floor(this.x - (double) f2 - (double) 1.0F);
        int l1 = Mth.floor(this.x + (double) f2 + (double) 1.0F);
        int i2 = Mth.floor(this.y - (double) f2 - (double) 1.0F);
        int i1 = Mth.floor(this.y + (double) f2 + (double) 1.0F);
        int j2 = Mth.floor(this.z - (double) f2 - (double) 1.0F);
        int j1 = Mth.floor(this.z + (double) f2 + (double) 1.0F);
        List<Entity> list = this.level.getEntities(this.source, new AABB(k1, i2, j2, l1, i1, j1));
        EventHooks.onExplosionDetonate(this.level, (Explosion) (Object) this, list, f2);
        Vec3 vec3 = new Vec3(this.x, this.y, this.z);

        for (Entity entity : list) {
            if (!entity.ignoreExplosion((Explosion) (Object) this)) {
                double d11 = Math.sqrt(entity.distanceToSqr(vec3)) / (double) f2;
                if (d11 <= (double) 1.0F) {
                    double d5 = entity.getX() - this.x;
                    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                    double d9 = entity.getZ() - this.z;
                    double d12 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d12 != (double) 0.0F) {
                        d5 /= d12;
                        d7 /= d12;
                        d9 /= d12;
                        if (this.damageCalculator.shouldDamageEntity((Explosion) (Object) this, entity)) {
                            entity.hurt(this.damageSource, this.damageCalculator.getEntityDamageAmount((Explosion) (Object) this, entity));
                        }

                        double d13 = ((double) 1.0F - d11) * (double) getSeenPercent(vec3, entity) * (double) this.damageCalculator.getKnockbackMultiplier(entity);
                        double d10;
                        if (entity instanceof LivingEntity livingentity) {
                            d10 = d13 * ((double) 1.0F - livingentity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
                        } else {
                            d10 = d13;
                        }

                        d5 *= d10;
                        d7 *= d10;
                        d9 *= d10;
                        Vec3 vec31 = new Vec3(d5, d7, d9);
                        vec31 = EventHooks.getExplosionKnockback(this.level, (Explosion) (Object) this, entity, vec31);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
                        if (entity instanceof Player player) {
                            if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                this.hitPlayers.put(player, vec31);
                            }
                        }

                        entity.onExplosionHit(this.source);
                    }
                }
            }
        }

    }

}
