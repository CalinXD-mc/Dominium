package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
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

                entries.add(ModItems.DOMINIC_EFFIGY);

                entries.add(ModItems.DOMINIC_ORB);

                entries.add(ModItems.DOMINIC_DAGGER);
                entries.add(ModItems.FIXED_DOMINIC_DAGGER);
                entries.add(ModItems.BROKEN_DOMINIC_DAGGER_PIECE);

                entries.add(ModItems.SOULTRAP_SPETUM);

//                entries.add(ModItems.LUCKY_DICE);
//                entries.add(ModItems.SINFUL_DICE);
            })
            .build();

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(Dominium.MOD_ID, "dominium_group"), DOMINIUM_GROUP);
    }

}
