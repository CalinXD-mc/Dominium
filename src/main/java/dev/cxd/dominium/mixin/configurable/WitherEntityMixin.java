package dev.cxd.dominium.mixin.configurable;

import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.init.ModItems;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherEntity.class)
public class WitherEntityMixin {

    @Inject(method = "dropEquipment", at = @At("TAIL"))
    private void dropDominicOrb(DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo ci) {
        if (ModConfig.dominicOrbsInSurvival) {
            WitherEntity wither = (WitherEntity) (Object) this;
            if (!wither.getWorld().isClient()) {
                wither.dropItem(ModItems.DOMINIC_ORB);
            }
        }
    }
}