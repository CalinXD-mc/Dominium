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
            .icon(() -> new ItemStack(ModItems.CONTRACT))
            .displayName(Text.translatable("itemGroup.dominium.dominium"))
            .entries((context, entries) -> {
                entries.add(ModBlocks.SOUL_SANDSTONE);

                entries.add(ModBlocks.SMOOTH_SOUL_SANDSTONE);
                entries.add(ModBlocks.SMOOTH_SOUL_SANDSTONE_SLAB);
                entries.add(ModBlocks.SMOOTH_SOUL_SANDSTONE_STAIRS);
                entries.add(ModBlocks.SMOOTH_SOUL_SANDSTONE_WALL);

                entries.add(ModBlocks.SOUL_CANDLE);
                entries.add(ModBlocks.SOUL_GLASS);

                entries.add(ModBlocks.SUSPICIOUS_BONE_BLOCK);
                entries.add(Items.BONE);
//                entries.add(ModItems.WITHERED_BONE);
//                entries.add(ModItems.HUMAN_BONE);
//                entries.add(ModItems.BONE_PILE);
//                entries.add(ModItems.BONE_DUST);

                entries.add(ModBlocks.ANCIENTER_DEBRIS);

                entries.add(ModBlocks.WAYSTONE);
                entries.add(ModBlocks.IDOL);
                //entries.add(ModBlocks.VESSEL);

                entries.add(ModItems.CONTRACT);
                entries.add(ModItems.WANDERER_TABLET);

                entries.add(ModItems.SOUL_ORB);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.DOMINIC_ORB);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.DOMINIC_EFFIGY);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.ETERNAL_DIVINITY);

                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.DOMINIC_DAGGER);
                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.FIXED_DOMINIC_DAGGER);
                if (ModConfig.showDominicItemsInTab) entries.add(ModItems.BROKEN_DOMINIC_DAGGER_PIECE);

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

            // Register the group only if config is true
            Registry.register(Registries.ITEM_GROUP,
                    new Identifier("dominium", "dominium_experimental"),
                    EXPERIMENTAL_DOMINIUM_GROUP);
        }
    }

}
