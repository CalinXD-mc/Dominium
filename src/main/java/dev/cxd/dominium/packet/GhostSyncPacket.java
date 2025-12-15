package dev.cxd.dominium.packet;

import dev.cxd.dominium.Dominium;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GhostSyncPacket {

    public static void send(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();

        // Write the number of ghost UUIDs
        buf.writeInt(Dominium.GHOST_UUIDS.size());

        // Write each UUID
        for (UUID uuid : Dominium.GHOST_UUIDS) {
            buf.writeUuid(uuid);
        }

        ServerPlayNetworking.send(player, Dominium.GHOST_SYNC_PACKET_ID, buf);
    }

    public static void sendToAll(Iterable<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            send(player);
        }
    }

    public static Set<UUID> read(PacketByteBuf buf) {
        int size = buf.readInt();
        Set<UUID> ghosts = new HashSet<>();

        for (int i = 0; i < size; i++) {
            ghosts.add(buf.readUuid());
        }

        return ghosts;
    }
}