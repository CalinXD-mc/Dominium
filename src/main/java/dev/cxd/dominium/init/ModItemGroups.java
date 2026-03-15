package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.config.ModConfig;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class ModItemGroups {
    public static final ItemGroup DOMINIUM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.SOUL_ORB))
            .displayName(Text.translatable("itemGroup.dominium.dominium"))
            .entries((context, entries) -> {
                entries.add(ModItems.SOUL_ORB);

                entries.add(ModBlocks.SOUL_SANDSTONE);

                entries.add(ModBlocks.SMOOTH_SOUL_SANDSTONE);
                entries.add(ModBlocks.SMOOTH_SOUL_SANDSTONE_SLAB);
                entries.add(ModBlocks.SMOOTH_SOUL_SANDSTONE_STAIRS);
                entries.add(ModBlocks.SMOOTH_SOUL_SANDSTONE_WALL);

                entries.add(ModBlocks.SOUL_GLASS);
                entries.add(ModBlocks.SOUL_CANDLE);

                entries.add(ModItems.CONTRACT);
                entries.add(ModItems.WANDERER_TABLET);

                entries.add(ModBlocks.WAYSTONE);
                entries.add(ModBlocks.IDOL);
                entries.add(ModBlocks.VESSEL);
                entries.add(ModBlocks.SWAPPER);
                entries.add(ModItems.VASSAL_ITEM);
                entries.add(ModItems.EYE_OF_THE_APEX);
                entries.add(ModBlocks.OBELISK);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.DOMINIC_ORB);

                if (ModConfig.showDominicItemsInTab) entries.add(ModBlocks.DOMINIC_BRICKS);
                if (ModConfig.showDominicItemsInTab) entries.add(ModBlocks.DOMINIC_BRICKS_SLAB);
                if (ModConfig.showDominicItemsInTab) entries.add(ModBlocks.DOMINIC_BRICKS_STAIRS);
                if (ModConfig.showDominicItemsInTab) entries.add(ModBlocks.DOMINIC_BRICKS_WALL);
                if (ModConfig.showDominicItemsInTab) entries.add(ModBlocks.CHISELED_DOMINIC_BRICKS);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.ANCIENT_BONE_ALLOY);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.ANCIENT_BONE_ALLOY_CHUNK);

                entries.add(ModItems.UNDEAD_NECKLACE);
                entries.add(ModItems.SPIDER_NECKLACE);
                entries.add(ModItems.ETHEREAL_NECKLACE);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.SOULBOUND_CONTRACT);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.DOMINIC_EFFIGY);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.ETERNAL_DIVINITY);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.BROKEN_ETERNAL_DIVINITY);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.ETERNAL_DIVINITY_PIECE1);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.ETERNAL_DIVINITY_PIECE2);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.ETERNAL_DIVINITY_PIECE3);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.SOULTRAP_SPETUM);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.GILDED_ONYX);
            })
            .build();

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(Dominium.MOD_ID, "dominium_group"), DOMINIUM_GROUP);
        if (ModConfig.showExperimentalItemsInTab) {
            ItemGroup EXPERIMENTAL_DOMINIUM_GROUP = FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.CONTRACT))
                    .displayName(Text.translatable("itemGroup.dominium.dominium_experimental"))
                    .entries((context, entries) -> {
                        entries.add(ModItems.ITEM_MARKER);
                    })
                    .build();

            Registry.register(Registries.ITEM_GROUP,
                    new Identifier("dominium", "dominium_experimental"),
                    EXPERIMENTAL_DOMINIUM_GROUP);
        }
    }

}
