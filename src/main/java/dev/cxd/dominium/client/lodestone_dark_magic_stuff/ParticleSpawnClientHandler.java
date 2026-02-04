package dev.cxd.dominium.client.lodestone_dark_magic_stuff;

import dev.cxd.dominium.custome.packets.ParticleSpawnPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class ParticleSpawnClientHandler {
    public static void handle(MinecraftClient client, ParticleSpawnPacketData data) {
        client.execute(() -> {
            if (client.world == null) return;

            Color startColor = new Color(data.startingColor);
            Color endColor = new Color(data.endingColor);
            Vec3d pos = data.position;

            switch (data.particleType) {
                case "spetum_jump_particle":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "spetum_jump_particle");
                    break;
                case "spetum_slam_particle":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "spetum_slam_particle");
                    break;
                case "spetum_kill_particle":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "spetum_kill_particle");
                    break;
                case "dominium_dagger_fixed_particles":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "spetum_kill_particle");
                    break;
                case "eternal_divinity_1":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "eternal_divinity_1");
                    break;
                case "eternal_divinity_2":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "eternal_divinity_2");
                    break;
                case "eternal_divinity_3":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "eternal_divinity_3");
                    break;
                case "rotated_beam_south_45":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "rotated_beam_south_45");
                    break;
                case "screen_shake_light":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "screen_shake_light");
                    break;
                case "screen_shake_medium":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "screen_shake_medium");
                    break;
                case "screen_shake_heavy":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "screen_shake_heavy");
                    break;
                case "screen_shake_extreme":
                    ParticleSpawnPacket.spawnCustomParticle(client.world, pos, startColor, endColor, "screen_shake_extreme");
                    break;
            }
        });
    }
}
