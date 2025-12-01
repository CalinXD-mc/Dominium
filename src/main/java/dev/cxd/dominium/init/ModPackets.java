package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ParticleSpawnPacketData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier PARTICLE_SPAWN_ID = new Identifier(Dominium.MOD_ID, "particle_spawn");

    public static void initializePackets() {
        ServerPlayNetworking.registerGlobalReceiver(PARTICLE_SPAWN_ID, (server, player, handler, buf, responseSender) -> {
            ParticleSpawnPacketData packet = new ParticleSpawnPacketData(buf);
            server.execute(() -> {
                // Server-side handling if needed
            });
        });
    }
}
