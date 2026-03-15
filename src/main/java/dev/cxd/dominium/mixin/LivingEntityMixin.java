package dev.cxd.dominium.mixin;

import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.init.*;
import dev.cxd.dominium.item.ban_items.BrokenEternalDivinityItem;
import dev.cxd.dominium.item.ban_items.EternalDivinityItem;
import dev.cxd.dominium.utils.DelayedTaskScheduler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void dominiumDoNotDieStuff(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof ServerPlayerEntity player
                && self.hasStatusEffect(ModStatusEffects.SOUL_DEBT)
                && !source.isOf(DamageTypes.GENERIC_KILL)
                && !source.isOf(DamageTypes.MAGIC)) {
            cir.setReturnValue(false);
            float doubledAmount = amount * 2f;
            player.getServer().execute(() ->
                    player.damage(player.getWorld().getDamageSources().magic(), doubledAmount)
            );
            return;
        }

        if (self instanceof ServerPlayerEntity player) {
            WorldBorder border = player.getWorld().getWorldBorder();
            boolean outside = !border.contains(player.getBlockPos());

            if (outside && !source.isOf(DamageTypes.OUT_OF_WORLD)) {
                float newHealth = player.getHealth() - amount;

                if (newHealth <= 0.5F) {
                    player.setHealth(0.5F);
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        if (self instanceof ServerPlayerEntity victim && source.getAttacker() instanceof ServerPlayerEntity attacker) {
            float mitigatedDamage = DamageUtil.getDamageLeft(
                    amount,
                    victim.getArmor(),
                    (float) victim.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
            );
            StatusEffectInstance resistance = victim.getStatusEffect(StatusEffects.RESISTANCE);
            if (resistance != null) {
                int level = resistance.getAmplifier() + 1;
                mitigatedDamage *= Math.max(0, 1 - (level * 0.2f));
            }
            mitigatedDamage = DamageUtil.getInflictedDamage(
                    mitigatedDamage,
                    EnchantmentHelper.getProtectionAmount(victim.getArmorItems(), source)
            );
            boolean wouldKill = mitigatedDamage >= victim.getHealth();

            if (attacker.getMainHandStack().isOf(ModItems.GILDED_ONYX) && wouldKill) {
                victim.addStatusEffect(new StatusEffectInstance((StatusEffect) ModStatusEffects.SOUL_STRAIN, 180 * 20, 0, false, false, true));

                victim.setHealth(1.0F);
                cir.setReturnValue(false);

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

            ItemStack offhandEternal = attacker.getOffHandStack();
            Iterable<ItemStack> eternalSources = offhandEternal.isOf(ModItems.ETERNAL_DIVINITY)
                    ? java.util.List.of(offhandEternal)
                    : attacker.getInventory().main;

            for (ItemStack stack : eternalSources) {
                if (stack.isOf(ModItems.ETERNAL_DIVINITY)) {
                    int durability = EternalDivinityItem.getDurability(stack);

                    if (wouldKill) {
                        MinecraftServer server = victim.getServer();
                        assert server != null;

                        victim.addStatusEffect(new StatusEffectInstance((StatusEffect) ModStatusEffects.SOUL_STRAIN, 8 * 20, 0, false, false, true));

                        server.getGameRules().get(GameRules.SHOW_DEATH_MESSAGES).set(false, server);
                        victim.changeGameMode(GameMode.SURVIVAL);

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
                            World world = victim.getWorld();

                            attacker.addStatusEffect(new StatusEffectInstance(ModStatusEffects.REGRET, 100, 0, true, false, true));

                            victim.damage(victim.getWorld().getDamageSources().genericKill(), Float.MAX_VALUE);
                            victim.changeGameMode(GameMode.SPECTATOR);

                            EternalDivinityItem.spawnParticles(stack, victim, attacker);

                            if (durability != 1) {
                                Text message = Text.literal(victim.getName().getString() + "'s existence was suspended")
                                        .formatted(Formatting.WHITE);
                                victim.addStatusEffect(new StatusEffectInstance((StatusEffect) ModStatusEffects.GHOSTED, 60 * 20 * ModConfig.eternalDivinityBadKillsSpectatorTime, 0, false, false, true));
                                for (PlayerEntity p : world.getPlayers()) {
                                    p.sendMessage(message, false);
                                }
                            }

                            for (PlayerEntity p : world.getPlayers()) {
                                world.playSound(null, p.getX(), p.getY(), p.getZ(),
                                        SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0F, 0.75F);
                            }

                            if (durability == 1) {
                                Text message = Text.literal(victim.getName().getString() + "'s existence was forfeited")
                                        .formatted(Formatting.WHITE);
                                for (PlayerEntity p : world.getPlayers()) {
                                    p.sendMessage(message, false);
                                }

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

                        server.getGameRules().get(GameRules.SHOW_DEATH_MESSAGES).set(true, server);
                    }
                    break;
                }
            }

            for (ItemStack stack : eternalSources) {
                if (stack.isOf(ModItems.BROKEN_ETERNAL_DIVINITY)) {
                    if (wouldKill) {
                        victim.addStatusEffect(new StatusEffectInstance((StatusEffect) ModStatusEffects.SOUL_STRAIN, 8 * 20, 0, false, false, true));

                        victim.setHealth(1.0F);
                        cir.setReturnValue(false);

                        ServerWorld serverWorld = (ServerWorld) victim.getWorld();
                        MinecraftServer server = victim.getServer();

                        BlockPos groundPos = serverWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, victim.getBlockPos());
                        victim.teleport(groundPos.getX() + 0.5, groundPos.getY(), groundPos.getZ() + 0.5);
                        victim.setVelocity(Vec3d.ZERO);
                        victim.velocityModified = true;

                        serverWorld.spawnParticles(ModParticles.DOMINIC_SYMBOL,
                                victim.getX(), victim.getY(), victim.getZ(),
                                1, 0, 0, 0, 0);

                        DelayedTaskScheduler.schedule(server, 40, () -> {
                            victim.addVelocity(0, 50d, 5d);
                            victim.velocityModified = true;

                            World world = victim.getWorld();

                            attacker.addStatusEffect(new StatusEffectInstance(ModStatusEffects.REGRET, 100, 0, true, false, true));

                            BrokenEternalDivinityItem.spawnParticles(victim);

                            for (PlayerEntity p : world.getPlayers()) {
                                world.playSound(null, p.getX(), p.getY(), p.getZ(),
                                        SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0F, 0.75F);
                            }

                            DelayedTaskScheduler.schedule(server, 100, () -> {
                                server.execute(() -> {
                                    victim.setVelocity(0D, 0D, 0D);
                                    victim.velocityModified = true;
                                    victim.teleport(ModConfig.brokenEternalDivinityCoordonates, 1500D, ModConfig.brokenEternalDivinityCoordonates);
                                });
                            });

                            if (ModConfig.shouldBrokenEternalDivinityDestroyItself) stack.decrement(1);
                        });
                    }
                    break;
                }
            }
        }
    }
}