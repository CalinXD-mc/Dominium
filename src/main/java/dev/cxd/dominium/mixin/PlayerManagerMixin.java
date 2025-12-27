package dev.cxd.dominium.mixin;

import dev.cxd.dominium.Dominium;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(
            method = "broadcast(Lnet/minecraft/text/Text;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelBroadcast(Text message, boolean overlay, CallbackInfo ci) {
        PlayerManager manager = (PlayerManager) (Object) this;
        String text = message.getString();

        for (var ghostUuid : Dominium.GHOST_UUIDS) {
            ServerPlayerEntity ghostPlayer = manager.getPlayer(ghostUuid);
            if (ghostPlayer == null) continue;

            String ghostName = ghostPlayer.getName().getString();

            if (text.contains(ghostName + " joined the game") || text.contains(ghostName + " left the game")) {
                ci.cancel();
                return;
            }
        }
    }
}