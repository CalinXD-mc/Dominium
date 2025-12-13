package dev.cxd.dominium.datagen;

import dev.cxd.dominium.init.ModBlocks;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.utils.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {


    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.WAYSTONE)
                .pattern("DDD")
                .pattern("DED")
                .pattern("DLD")
                .input('D', Items.DEEPSLATE_TILES)
                .input('E', Items.ENDER_PEARL)
                .input('L', Items.LODESTONE)
                .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE))
                .criterion(hasItem(Items.LODESTONE), conditionsFromItem(Items.LODESTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.WANDERER_TABLET)
                .pattern("BBB")
                .pattern("BEB")
                .pattern("BBB")
                .input('B', Items.BLACKSTONE)
                .input('E', Items.ENDER_EYE)
                .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.IDOL)
                .pattern("DED")
                .pattern("DND")
                .pattern("DED")
                .input('D', Items.DEEPSLATE_TILES)
                .input('E', Items.END_CRYSTAL)
                .input('N', Items.NETHERITE_INGOT)
                .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE))
                .criterion(hasItem(Items.LODESTONE), conditionsFromItem(Items.LODESTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.VESSEL)
                .pattern("DDD")
                .pattern("DED")
                .pattern(" A ")
                .input('D', Items.DEEPSLATE_TILES)
                .input('E', Items.ENDER_EYE)
                .input('A', ModItems.SOUL_ORB)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CONTRACT)
                .pattern("BD ")
                .pattern("DR ")
                .pattern("   ")
                .input('B', Items.BOOK)
                .input('D', Items.DEEPSLATE_TILES)
                .input('R', ModItems.SOUL_ORB)
                .criterion(hasItem(Items.BOOK), conditionsFromItem(Items.BOOK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SOULBOUND_CONTRACT)
                .pattern(" 2 ")
                .pattern("212")
                .pattern(" 2 ")
                .input('1', ModItems.CONTRACT)
                .input('2', ModItems.DOMINIC_ORB)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SOUL_CANDLE, 8)
                .pattern("CCC")
                .pattern("CSC")
                .pattern("CCC")
                .input('S', ModItems.SOUL_ORB)
                .input('C', Items.CANDLE)
                .criterion(hasItem(Items.CANDLE), conditionsFromItem(Items.CANDLE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SOUL_SANDSTONE)
                .pattern("SS ")
                .pattern("SS ")
                .pattern("   ")
                .input('S', Items.SOUL_SAND)
                .criterion(hasItem(Items.SOUL_SAND), conditionsFromItem(Items.SOUL_SAND))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SMOOTH_SOUL_SANDSTONE)
                .pattern("SS ")
                .pattern("SS ")
                .pattern("   ")
                .input('S', ModBlocks.SOUL_SANDSTONE)
                .criterion(hasItem(ModBlocks.SOUL_SANDSTONE), conditionsFromItem(ModBlocks.SOUL_SANDSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SMOOTH_SOUL_SANDSTONE_SLAB, 6)
                .pattern("SSS")
                .pattern("   ")
                .pattern("   ")
                .input('S', ModBlocks.SMOOTH_SOUL_SANDSTONE)
                .criterion(hasItem(ModBlocks.SMOOTH_SOUL_SANDSTONE), conditionsFromItem(ModBlocks.SMOOTH_SOUL_SANDSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SMOOTH_SOUL_SANDSTONE_STAIRS, 4)
                .pattern("S  ")
                .pattern("SS ")
                .pattern("SSS")
                .input('S', ModBlocks.SMOOTH_SOUL_SANDSTONE)
                .criterion(hasItem(ModBlocks.SMOOTH_SOUL_SANDSTONE), conditionsFromItem(ModBlocks.SMOOTH_SOUL_SANDSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SMOOTH_SOUL_SANDSTONE_WALL, 6)
                .pattern("SSS")
                .pattern("SSS")
                .pattern("   ")
                .input('S', ModBlocks.SMOOTH_SOUL_SANDSTONE)
                .criterion(hasItem(ModBlocks.SMOOTH_SOUL_SANDSTONE), conditionsFromItem(ModBlocks.SMOOTH_SOUL_SANDSTONE))
                .offerTo(exporter);

//        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ANCIENT_BONE_ALLOY_CHUNK)
//                .pattern("21 ")
//                .pattern("   ")
//                .pattern("   ")
//                .input('1', ModItems.DOMINIC_ORB)
//                .input('2', Items.NETHERITE_INGOT)
//                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
//                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ANCIENT_BONE_ALLOY_CHUNK, 4)
                .pattern("1  ")
                .pattern("   ")
                .pattern("   ")
                .input('1', ModItems.ANCIENT_BONE_ALLOY)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ANCIENT_BONE_ALLOY)
                .pattern("11 ")
                .pattern("11 ")
                .pattern("   ")
                .input('1', ModItems.ANCIENT_BONE_ALLOY_CHUNK)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ETERNAL_DIVINITY)
                .pattern("22 ")
                .pattern("21 ")
                .pattern("  2")
                .input('1', ModItems.ANCIENT_BONE_ALLOY)
                .input('2', ModItems.ANCIENT_BONE_ALLOY_CHUNK)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.GILDED_ONYX)
                .pattern(" 2 ")
                .pattern("323")
                .pattern(" 1 ")
                .input('1', ModItems.ETERNAL_DIVINITY)
                .input('2', Items.OBSIDIAN)
                .input('3', ModItems.ANCIENT_BONE_ALLOY_CHUNK)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.DOMINIC_DAGGER)
                .pattern(" 1 ")
                .pattern(" 1 ")
                .pattern(" 2 ")
                .input('1', ModItems.ANCIENT_BONE_ALLOY)
                .input('2', ModItems.ANCIENT_BONE_ALLOY_CHUNK)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SOULTRAP_SPETUM)
                .pattern(" 21")
                .pattern(" 12")
                .pattern("2  ")
                .input('1', ModItems.ANCIENT_BONE_ALLOY)
                .input('2', ModItems.ANCIENT_BONE_ALLOY_CHUNK)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.DOMINIC_EFFIGY)
                .pattern("111")
                .pattern("121")
                .pattern(" 1 ")
                .input('1', Items.STONE)
                .input('2', ModItems.DOMINIC_ORB)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

        offerSmelting(exporter, List.of(Items.SOUL_SAND), RecipeCategory.MISC, ModBlocks.SOUL_GLASS,
                0.7f, 160, "bone_pile_from_bone_dust");
    }
}
