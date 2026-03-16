package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ParticleSpawnPacketData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModPackets {
    public static final Identifier PARTICLE_SPAWN_ID = new Identifier(Dominium.MOD_ID, "particle_spawn");

    public static final Identifier SOULBOUND_ACTION_ID =
            new Identifier(Dominium.MOD_ID, "soulbound_action");
    public static final Identifier OPEN_SOULBOUND_SCREEN_ID =
            new Identifier(Dominium.MOD_ID, "open_soulbound_screen");
    public static final Identifier SOUL_DEBT_HINT_ID =
            new Identifier(Dominium.MOD_ID, "soul_debt_hint");
    public static final Identifier SHOW_CLAIM_RADIUS_ID =
            new Identifier(Dominium.MOD_ID, "show_claim_radius");
    public static final Identifier CLAIM_PRESENCE_ID =
            new Identifier(Dominium.MOD_ID, "claim_presence");

    public static void initializePackets() {
        ServerPlayNetworking.registerGlobalReceiver(PARTICLE_SPAWN_ID, (server, player, handler, buf, responseSender) -> {
            ParticleSpawnPacketData packet = new ParticleSpawnPacketData(buf);
            server.execute(() -> {
                // Server-side handling if needed
            });
        });
    }
}
