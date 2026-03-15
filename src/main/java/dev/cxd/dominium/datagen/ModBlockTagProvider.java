package dev.cxd.dominium.datagen;

import dev.cxd.dominium.init.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.SOUL_SANDSTONE)
                .add(ModBlocks.SMOOTH_SOUL_SANDSTONE)
                .add(ModBlocks.SMOOTH_SOUL_SANDSTONE_STAIRS)
                .add(ModBlocks.SMOOTH_SOUL_SANDSTONE_SLAB)
                .add(ModBlocks.SMOOTH_SOUL_SANDSTONE_WALL)
                .add(ModBlocks.SOUL_GLASS)
                .add(ModBlocks.WAYSTONE)
                .add(ModBlocks.IDOL)
                .add(ModBlocks.VESSEL)
                .add(ModBlocks.OBELISK)
                .add(ModBlocks.SWAPPER)
                .add(ModBlocks.DOMINIC_BRICKS)
                .add(ModBlocks.DOMINIC_BRICKS_STAIRS)
                .add(ModBlocks.DOMINIC_BRICKS_SLAB)
                .add(ModBlocks.DOMINIC_BRICKS_WALL)
                .add(ModBlocks.CHISELED_DOMINIC_BRICKS);

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.WAYSTONE)
                .add(ModBlocks.IDOL)
                .add(ModBlocks.VESSEL)
                .add(ModBlocks.SWAPPER);

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.OBELISK);

        getOrCreateTagBuilder(BlockTags.STAIRS)
                .add(ModBlocks.SMOOTH_SOUL_SANDSTONE_STAIRS)
                .add(ModBlocks.DOMINIC_BRICKS_STAIRS);

        getOrCreateTagBuilder(BlockTags.SLABS)
                .add(ModBlocks.SMOOTH_SOUL_SANDSTONE_SLAB)
                .add(ModBlocks.DOMINIC_BRICKS_SLAB);

        getOrCreateTagBuilder(BlockTags.WALLS)
                .add(ModBlocks.SMOOTH_SOUL_SANDSTONE_WALL)
                .add(ModBlocks.DOMINIC_BRICKS_WALL);
    }
}
