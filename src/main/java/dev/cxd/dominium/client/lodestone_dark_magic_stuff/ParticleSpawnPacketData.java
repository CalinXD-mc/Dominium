package dev.cxd.dominium.client.lodestone_dark_magic_stuff;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class ParticleSpawnPacketData {
    public final Vec3d position;
    public final int startingColor;
    public final int endingColor;
    public final String particleType;

    public ParticleSpawnPacketData(Vec3d position, int start, int end, String type) {
        this.position = position;
        this.startingColor = start;
        this.endingColor = end;
        this.particleType = type;
    }

    public ParticleSpawnPacketData(PacketByteBuf buf) {
        this(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                buf.readInt(), buf.readInt(), buf.readString(64));
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeDouble(position.x);
        buf.writeDouble(position.y);
        buf.writeDouble(position.z);
        buf.writeInt(startingColor);
        buf.writeInt(endingColor);
        buf.writeString(particleType);
    }
}
