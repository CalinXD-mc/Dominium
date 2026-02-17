package dev.cxd.dominium.mixin;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.utils.FactionManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerStepMixin extends LivingEntity {
    protected PlayerStepMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void cancelStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (Dominium.GHOST_UUIDS.contains(this.getUuid())) {
            ci.cancel();
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onPlayerDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity victim = (PlayerEntity) (Object) this;

        if (!(victim instanceof ServerPlayerEntity)) {
            return;
        }

        Entity attacker = source.getAttacker();
        if (!(attacker instanceof ServerPlayerEntity)) {
            return;
        }

        UUID victimUuid = victim.getUuid();
        UUID attackerUuid = attacker.getUuid();

        if (FactionManager.areSameFaction(victimUuid, attackerUuid)) {
            String factionName = FactionManager.getPlayerFaction(victimUuid);

            if (!FactionManager.isPVPAllowed(factionName)) {
                cir.setReturnValue(false);
            }
        }
    }
}