package dev.cxd.dominium.effect;

import dev.cxd.dominium.init.ModStatusEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class GhostedEffect extends StatusEffect {

    public GhostedEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFFFFFF);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity instanceof ServerPlayerEntity player) {
            player.changeGameMode(GameMode.SURVIVAL);
            player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.REGRET, 20 * 15, 0, true, false, false));
            player.teleport(0, 200, 0);
        }
        super.onRemoved(entity, attributes, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}