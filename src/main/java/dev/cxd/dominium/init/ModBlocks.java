package dev.cxd.dominium.init;

//import dev.cxd.carver_decor.CarversDecor;
//import dev.cxd.carver_decor.block.*;
//import dev.cxd.carver_decor.block.IronWalkwayBlock;
//import dev.cxd.carver_decor.block.misc.RiggedEndPortalBlock;
import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.block.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block ANCIENTER_DEBRIS = registerBlock("ancienter_debris", new Block(FabricBlockSettings.copyOf(Blocks.ANCIENT_DEBRIS)));

    public static final Block SUSPICIOUS_BONE_BLOCK = registerBlock("suspicious_bone_block",
            new SuspiciousBoneBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .sounds(BlockSoundGroup.STONE)
                    .strength(2.0F, 2.0F)
                    .requiresTool()
                    .dropsNothing()
                    .breakInstantly()
            ));

    public static final Block SOUL_SANDSTONE = registerBlock("soul_sandstone",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .sounds(BlockSoundGroup.STONE)
                    .strength(0.8F, 0.8F)
                    .requiresTool()
            ));
    public static final Block SMOOTH_SOUL_SANDSTONE = registerBlock("smooth_soul_sandstone",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .sounds(BlockSoundGroup.STONE)
                    .strength(2.0F, 6.0F)
                    .requiresTool()
            ));
    public static final Block SMOOTH_SOUL_SANDSTONE_STAIRS = registerBlock("smooth_soul_sandstone_stairs",
            new StairsBlock(ModBlocks.SMOOTH_SOUL_SANDSTONE.getDefaultState(), FabricBlockSettings.copyOf(Blocks.SMOOTH_SANDSTONE)));
    public static final Block SMOOTH_SOUL_SANDSTONE_SLAB = registerBlock("smooth_soul_sandstone_slab",
            new SlabBlock(FabricBlockSettings.copyOf(Blocks.SMOOTH_SANDSTONE)));
    public static final Block SMOOTH_SOUL_SANDSTONE_WALL = registerBlock("smooth_soul_sandstone_wall",
            new WallBlock(FabricBlockSettings.copyOf(Blocks.SMOOTH_SANDSTONE)));

    public static final Block SOUL_GLASS = registerBlock("soul_glass",
            new SoulGlass(FabricBlockSettings.copyOf(Blocks.GLASS)));

    public static final Block WAYSTONE = registerBlock("waystone",
            new WaystoneBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.GRAY)
                    .sounds(BlockSoundGroup.LODESTONE)
                    .strength(4.0F, 50.0F)
                    .requiresTool()
                    .nonOpaque()
                    .solidBlock(Blocks::never)
                    .blockVision(Blocks::never)
            ));

    public static final Block IDOL = registerBlock("idol",
            new IdolBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.GRAY)
                    .sounds(BlockSoundGroup.LODESTONE)
                    .strength(3.5F, 50.0F)
                    .requiresTool()
                    .nonOpaque()
                    .solidBlock(Blocks::never)
                    .blockVision(Blocks::never)
            ));

    public static final Block VESSEL = registerBlock("vessel",
            new VesselBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.GRAY)
                    .sounds(BlockSoundGroup.LODESTONE)
                    .strength(3.5F, 50.0F)
                    .requiresTool()
                    .nonOpaque()
                    .solidBlock(Blocks::never)
                    .blockVision(Blocks::never)
            ));

    public static final Block SOUL_CANDLE = registerBlock("soul_candle", new WardingSoulCandleBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.BROWN)
            .sounds(BlockSoundGroup.CANDLE)
            .strength(0.5F, 2.0F)
            .nonOpaque()
            .solidBlock(Blocks::never)
            .blockVision(Blocks::never)
    ));

    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Dominium.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Dominium.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Dominium.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void initialize() {
        Dominium.LOGGER.info("Initializing Mod Blocks for " + Dominium.MOD_ID);


    }
}
