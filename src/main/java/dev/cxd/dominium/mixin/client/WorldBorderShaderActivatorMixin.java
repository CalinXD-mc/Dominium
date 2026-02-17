package dev.cxd.dominium.mixin.client;

import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.satin.ShaderHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class WorldBorderShaderActivatorMixin {
    @Unique
    private static final ShaderHandler shaderHandler = new ShaderHandler();
    @Unique
    private static boolean wasDead = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        WorldBorder border = player.getWorld().getWorldBorder();
        Vec3d pos = player.getPos();

        boolean outsideBorder = !border.contains(BlockPos.ofFloored(pos));

        boolean onNetherRoof =
                player.getWorld().getRegistryKey() == net.minecraft.world.World.NETHER
                        && player.getY() >= ModConfig.NETHER_ROOF_HEIGHT
                        && !player.getWorld().isSkyVisible(player.getBlockPos());

        boolean shouldApplyShader = outsideBorder || onNetherRoof;

        shaderHandler.updateBorderShader(outsideBorder);
        shaderHandler.updateNetherRoofShader(onNetherRoof);

        if (!wasDead && player.isDead() && shouldApplyShader) {
            wasDead = true;
            System.out.println("Player died with shader active!");
        }

        if (wasDead && !player.isDead()) {
            wasDead = false;
        }
    }

}
