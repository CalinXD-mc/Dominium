package dev.cxd.dominium.mixin;

import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ParticleSpawnPacketData;
import dev.cxd.dominium.custome.packets.ParticleSpawnPacket;
import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.init.*;
import dev.cxd.dominium.item.EternalDivinityItem;
import dev.cxd.dominium.utils.DelayedTaskScheduler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void dominiumDoNotDieStuff(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        assert source.getAttacker() != null;
        LivingEntity sourceAttacker = (LivingEntity) source.getAttacker();

        // OUTSIDE BORDER IMMORTALITY LOGIC
        if (self instanceof ServerPlayerEntity player) {
            WorldBorder border = player.getWorld().getWorldBorder();
            boolean outside = !border.contains(player.getBlockPos());

            if (outside && !source.isOf(DamageTypes.OUT_OF_WORLD)) {
                float newHealth = player.getHealth() - amount;

                // If lethal, reduce to near death instead
                if (newHealth <= 0.5F) {
                    player.setHealth(0.5F);
                    cir.setReturnValue(true); // let Minecraft think it dealt damage
                    return;
                }
            }
        }

        // EXISTING DAMNED / SOUL_STRAIN LOGIC
        if (self instanceof ServerPlayerEntity player) {
            StatusEffectInstance damned = player.getStatusEffect(ModStatusEffects.DAMNED);

            // Handle "Damned" death prevention
            if (damned != null && amount >= player.getHealth() && !source.isOf(DamageTypes.GENERIC_KILL)) {

                //add soul strain
                player.addStatusEffect(new StatusEffectInstance((StatusEffect) ModStatusEffects.SOUL_STRAIN, 180 * 20, damned.getAmplifier(), false, false, true));

                // Prevent death
                player.setHealth(1.0F);
                cir.setReturnValue(false);

                // Spawn EternalDivinityChainsEntity
                ServerWorld world = (ServerWorld) player.getWorld();
                EternalDivinityChainsEntity chain = ModEntities.ETERNAL_DIVINITY_CHAINS.create(world);
                if (chain != null) {
                    chain.setBoundPlayer(player.getUuid());
                    chain.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0, 0);
                    world.spawnEntity(chain);
                }

                // Visual / audio feedback
                world.spawnParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1, player.getZ(), 30, 0.5, 0.5, 0.5, 0.01);
                player.sendMessage(Text.literal("Judgement.").formatted(Formatting.GOLD), true);

                for (ServerPlayerEntity p : world.getPlayers()) {
                    world.playSound(null, p.getX(), p.getY(), p.getZ(),
                            SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }

                Text message = Text.literal(player.getName().getString() + " fulfilled their duty")
                        .formatted(Formatting.WHITE);
                for (ServerPlayerEntity p : world.getPlayers()) {
                    p.sendMessage(message, false);
                }

                player.removeStatusEffect(ModStatusEffects.DAMNED);
                return;
            }

            // Prevent death from Soul Strain
            if (self.hasStatusEffect(ModStatusEffects.SOUL_STRAIN) && !source.isOf(DamageTypes.GENERIC_KILL)) {
                cir.setReturnValue(false);
            }

            if (self.hasStatusEffect(ModStatusEffects.REGRET) && !source.isOf(DamageTypes.GENERIC_KILL)) {
                cir.setReturnValue(false);
            }
        }

        if (self instanceof ServerPlayerEntity victim && sourceAttacker instanceof  ServerPlayerEntity attacker) {
            if (attacker.getMainHandStack().isOf(ModItems.GILDED_ONYX) && amount >= victim.getHealth()) {
                //add soul strain
                victim.addStatusEffect(new StatusEffectInstance((StatusEffect) ModStatusEffects.SOUL_STRAIN, 180 * 20, 0, false, false, true));

                // Prevent death
                victim.setHealth(1.0F);
                cir.setReturnValue(false);

                // Spawn EternalDivinityChainsEntity
                ServerWorld world = (ServerWorld) victim.getWorld();
                EternalDivinityChainsEntity chain = ModEntities.ETERNAL_DIVINITY_CHAINS.create(world);
                if (chain != null) {
                    chain.setBoundPlayer(victim.getUuid());
                    chain.refreshPositionAndAngles(victim.getX(), victim.getY(), victim.getZ(), 0, 0);
                    world.spawnEntity(chain);
                }

                for (ServerPlayerEntity p : world.getPlayers()) {
                    world.playSound(null, p.getX(), p.getY(), p.getZ(),
                            SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }

            for (ItemStack stack : attacker.getInventory().main) {
                if (stack.isOf(ModItems.ETERNAL_DIVINITY)) {
                    int durability = EternalDivinityItem.getDurability(stack);

                    if (amount >= victim.getHealth()) {
                        victim.setHealth(1.0F);
                        cir.setReturnValue(false);

                        ServerWorld serverWorld = (ServerWorld) victim.getWorld();

                        BlockPos groundPos = serverWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, victim.getBlockPos());
                        victim.teleport(groundPos.getX() + 0.5, groundPos.getY(), groundPos.getZ() + 0.5);
                        victim.setVelocity(Vec3d.ZERO);
                        victim.velocityModified = true;

                        serverWorld.spawnParticles(ModParticles.DOMINIC_SYMBOL,
                                victim.getX(), victim.getY(), victim.getZ(),
                                1, 0, 0, 0, 0);

                        DelayedTaskScheduler.schedule(victim.getServer(), 40, () -> {
                            victim.changeGameMode(GameMode.SPECTATOR);

                            attacker.addStatusEffect(new StatusEffectInstance(ModStatusEffects.REGRET, 100, 0, true, false, true));

                            EternalDivinityItem.spawnParticles(stack, victim, attacker);

                            if (durability == 1) {
                                Vec3d pos = victim.getPos();

                                double radius = 8.0D;
                                int noOfExplosions = 24;

                                for (int i = 0; i < noOfExplosions; i++) {
                                    double angle = (Math.PI * 2 / 8) * i;
                                    double x = pos.x + Math.cos(angle) * radius;
                                    double z = pos.z + Math.sin(angle) * radius;
                                    double y = pos.y;

                                    serverWorld.createExplosion(
                                            null,
                                            x, y, z,
                                            12.0f,
                                            true,
                                            World.ExplosionSourceType.TNT
                                    );
                                }
                            }
                        });

                        EternalDivinityItem.setDurability(stack, durability - 1);
                    }
                    break;
                }
            }

        }
    }
}