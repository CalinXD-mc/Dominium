package dev.cxd.dominium.mixin;

import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.init.ModEntities;
import dev.cxd.dominium.init.ModStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
    }
}