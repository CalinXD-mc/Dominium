package dev.cxd.dominium.mixin.client;

import dev.cxd.dominium.Dominium;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.SignedMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.UUID;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin {

    @ModifyVariable(method = "onChatMessage", at = @At("HEAD"), argsOnly = true)
    private SignedMessage obfuscateGhostChatName(SignedMessage message) {
        UUID sender = message.link().sender();
        if (!Dominium.GHOST_UUIDS.contains(sender)) return message;

        return message;
    }
}
