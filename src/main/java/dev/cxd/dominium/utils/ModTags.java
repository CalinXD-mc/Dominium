package dev.cxd.dominium.utils;

import dev.cxd.dominium.Dominium;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> METAL_DETECTOR_DETECTABLE_BLOCKS =
                createTag("metal_detector_detectable_blocks");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(Dominium.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> SMELTABLE_TO_ANCIENT_BONE_ALLOY =
                createTag("smeltable_to_ancient_bone_alloy");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(Dominium.MOD_ID, name));
        }
    }
}
