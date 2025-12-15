package dev.cxd.dominium.client.lodestone_dark_magic_stuff;

import dev.cxd.dominium.init.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ModClientPackets {
    public static void initializeClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.PARTICLE_SPAWN_ID, (client, handler, buf, responseSender) -> {
            ParticleSpawnPacketData data = new ParticleSpawnPacketData(buf);
            ParticleSpawnClientHandler.handle(client, data);
        });
    }
}
