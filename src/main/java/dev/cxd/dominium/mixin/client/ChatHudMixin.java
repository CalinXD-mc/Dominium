package dev.cxd.dominium.mixin.client;

import dev.cxd.dominium.Dominium;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;)V",
            at = @At("HEAD"), argsOnly = true)
    private Text obfuscateGhostInChat(Text message) {
        String raw = message.getString();
        for (java.util.UUID ghostUuid : Dominium.GHOST_UUIDS) {
            net.minecraft.client.MinecraftClient client =
                    net.minecraft.client.MinecraftClient.getInstance();
            if (client.getNetworkHandler() == null) continue;

            net.minecraft.client.network.PlayerListEntry entry =
                    client.getNetworkHandler().getPlayerListEntry(ghostUuid);
            if (entry == null) continue;

            String ghostName = entry.getProfile().getName();
            if (raw.contains(ghostName)) {
                return replaceNameWithObfuscated(message, ghostName);
            }
        }
        return message;
    }

    private Text replaceNameWithObfuscated(Text original, String name) {
        String raw = original.getString();
        if (!raw.contains(name)) return original;

        MutableText result = Text.literal(
                raw.replace(name, "§k" + name + "§r")
        ).setStyle(original.getStyle());
        return result;
    }
}