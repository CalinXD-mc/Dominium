package dev.cxd.dominium.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.cxd.dominium.config.ModConfig;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;

public class NetherRoofHeightProvider extends HeightProvider {
    public static final NetherRoofHeightProvider INSTANCE = new NetherRoofHeightProvider();
    public static final Codec<NetherRoofHeightProvider> CODEC = Codec.unit(INSTANCE);

    public static HeightProviderType<NetherRoofHeightProvider> TYPE;

    @Override
    public int get(Random random, HeightContext context) {
        return ModConfig.NETHER_ROOF_HEIGHT + 1;
    }

    @Override
    public HeightProviderType<?> getType() {
        return TYPE;
    }
}