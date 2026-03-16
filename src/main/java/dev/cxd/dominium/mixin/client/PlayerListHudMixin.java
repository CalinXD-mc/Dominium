package dev.cxd.dominium.mixin.client;

import dev.cxd.dominium.Dominium;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void obfuscateGhostTabName(PlayerListEntry entry,
                                       CallbackInfoReturnable<Text> cir) {
        if (!Dominium.GHOST_UUIDS.contains(entry.getProfile().getId())) return;

        MutableText obfuscated = Text.literal("")
                .append(Text.literal(entry.getProfile().getName())
                        .formatted(Formatting.OBFUSCATED));
        cir.setReturnValue(obfuscated);
    }
}