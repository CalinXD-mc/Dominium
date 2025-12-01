package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.component.ChunkBlockCacheComponent;
import dev.cxd.dominium.component.IChunkBlockCacheComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class ModComponents implements WorldComponentInitializer {

    // === CCA chunk-block-cache component ===
    private static final ComponentKey<IChunkBlockCacheComponent> CHUNK_BLOCK_CACHE_COMPONENT_KEY =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    new Identifier(Dominium.MOD_ID, "chunk_block_cache_component"),
                    IChunkBlockCacheComponent.class
            );

    public static Optional<IChunkBlockCacheComponent> getChunkBlockCache(World world) {
        return CHUNK_BLOCK_CACHE_COMPONENT_KEY.maybeGet(world);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(CHUNK_BLOCK_CACHE_COMPONENT_KEY, ChunkBlockCacheComponent.class, ChunkBlockCacheComponent::new);
    }

    public static void setVesselUuid(ItemStack stack, String uuid) {
        stack.getOrCreateNbt().putString("vessel_uuid", uuid);
    }

    public static String getVesselUuid(ItemStack stack) {
        return stack.getOrCreateNbt().getString("vessel_uuid");
    }

    public static void setPlayerNameForSoulOwning(ItemStack stack, String name) {
        stack.getOrCreateNbt().putString("player_name_for_soul_owning", name);
    }

    public static String getPlayerNameForSoulOwning(ItemStack stack) {
        return stack.getOrCreateNbt().getString("player_name_for_soul_owning");
    }

    public static int getContractSigned(ItemStack stack) {
        return stack.getOrCreateNbt().getInt("contract_signed");
    }

    public static void setContractSigned(ItemStack stack, int signed) {
        stack.getOrCreateNbt().putInt("contract_signed", signed);
    }

    public static int getWandererTabletSigned(ItemStack stack) {
        return stack.getOrCreateNbt().getInt("wandering_tablet_signed");
    }

    public static void setWandererTabletSigned(ItemStack stack, int signed) {
        stack.getOrCreateNbt().putInt("wandering_tablet_signed", signed);
    }

    public static void initialize() {
        Dominium.LOGGER.info("Initializing Mod Components for " + Dominium.MOD_ID);
    }
}
