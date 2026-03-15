package dev.cxd.dominium.datagen;

import dev.cxd.dominium.init.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.SOUL_SANDSTONE);
        addDrop(ModBlocks.SMOOTH_SOUL_SANDSTONE);
        addDrop(ModBlocks.SMOOTH_SOUL_SANDSTONE_STAIRS);
        slabDrops(ModBlocks.SMOOTH_SOUL_SANDSTONE_SLAB);
        addDrop(ModBlocks.SMOOTH_SOUL_SANDSTONE_WALL);
        addDropWithSilkTouch(ModBlocks.SOUL_GLASS);
        addDrop(ModBlocks.WAYSTONE);
        addDrop(ModBlocks.IDOL);
        addDrop(ModBlocks.VESSEL);
        addDrop(ModBlocks.SOUL_CANDLE);
        addDrop(ModBlocks.SWAPPER);
        addDrop(ModBlocks.DOMINIC_BRICKS);
        addDrop(ModBlocks.DOMINIC_BRICKS_STAIRS);
        addDrop(ModBlocks.DOMINIC_BRICKS_WALL);
        addDrop(ModBlocks.CHISELED_DOMINIC_BRICKS);
        slabDrops(ModBlocks.DOMINIC_BRICKS_SLAB);
    }
}
