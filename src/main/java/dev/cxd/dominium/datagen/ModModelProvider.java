package dev.cxd.dominium.datagen;

import dev.cxd.dominium.init.ModBlocks;
import dev.cxd.dominium.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        BlockStateModelGenerator.BlockTexturePool smoothSoulSandstonePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.SMOOTH_SOUL_SANDSTONE);

//        blockStateModelGenerator.blockStateCollector.accept(
//                BlockStateModelGenerator.createSingletonBlockState(ModBlocks.SOUL_SANDSTONE,
//                        registerCubeColumnModel(blockStateModelGenerator,
//                                ModBlocks.SOUL_SANDSTONE, "soul_sandstone", "soul_sandstone_bottom", "soul_sandstone_top"))
//        );

        blockStateModelGenerator.registerSingleton(ModBlocks.SOUL_SANDSTONE, TexturedModel.CUBE_BOTTOM_TOP);
        smoothSoulSandstonePool.stairs(ModBlocks.SMOOTH_SOUL_SANDSTONE_STAIRS);
        smoothSoulSandstonePool.slab(ModBlocks.SMOOTH_SOUL_SANDSTONE_SLAB);
        smoothSoulSandstonePool.wall(ModBlocks.SMOOTH_SOUL_SANDSTONE_WALL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.CONTRACT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CONTRACT_SIGNED, Models.GENERATED);
        itemModelGenerator.register(ModItems.SOULBOUND_CONTRACT, Models.GENERATED);
        itemModelGenerator.register(ModItems.SOULBOUND_CONTRACT_SIGNED, Models.GENERATED);

        itemModelGenerator.register(ModItems.WANDERER_TABLET, Models.GENERATED);
        itemModelGenerator.register(ModItems.SIGNED_WANDERER_TABLET, Models.GENERATED);

        itemModelGenerator.register(ModItems.SOUL_ORB, Models.GENERATED);
        itemModelGenerator.register(ModItems.DOMINIC_ORB, Models.GENERATED);

        itemModelGenerator.register(ModItems.DOMINIC_EFFIGY, Models.GENERATED);

        itemModelGenerator.register(ModItems.SOULTRAP_SPETUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.CONVENANT_OF_THE_PROVIDENT_SIGNED, Models.GENERATED);

        itemModelGenerator.register(ModItems.ANCIENT_BONE_ALLOY, Models.GENERATED);
        itemModelGenerator.register(ModItems.ANCIENT_BONE_ALLOY_CHUNK, Models.GENERATED);

        itemModelGenerator.register(ModItems.GILDED_ONYX, Models.GENERATED);
        itemModelGenerator.register(ModItems.ETERNAL_DIVINITY, Models.GENERATED);
        itemModelGenerator.register(ModItems.ETERNAL_DIVINITY_PIECE1, Models.GENERATED);
        itemModelGenerator.register(ModItems.ETERNAL_DIVINITY_PIECE2, Models.GENERATED);
        itemModelGenerator.register(ModItems.ETERNAL_DIVINITY_PIECE3, Models.GENERATED);
        itemModelGenerator.register(ModItems.BROKEN_ETERNAL_DIVINITY, Models.GENERATED);
        itemModelGenerator.register(ModItems.ITEM_MARKER, Models.GENERATED);
    }

//    protected Identifier registerCubeColumnModel(BlockStateModelGenerator blockStateModelGenerator, Block block, String side, String bottom, String top) {
//        return Models.CUBE_BOTTOM_TOP.upload(
//                block,
//                new TextureMap()
//                        .put(TextureKey.SIDE, new Identifier(Dominium.MOD_ID, "block/" + side))
//                        .put(TextureKey.BOTTOM, new Identifier(Dominium.MOD_ID, "block/" + bottom))
//                        .put(TextureKey.TOP, new Identifier(Dominium.MOD_ID, "block/" + top)),
//                blockStateModelGenerator.modelCollector
//        );
//    }

}
