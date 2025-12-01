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
                .input('A', ModBlocks.ANCIENTER_DEBRIS)
                .criterion(hasItem(ModBlocks.ANCIENTER_DEBRIS), conditionsFromItem(ModBlocks.ANCIENTER_DEBRIS))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CONTRACT)
                .pattern("BD ")
                .pattern("DR ")
                .pattern("   ")
                .input('B', Items.BOOK)
                .input('D', Items.DEEPSLATE_TILES)
                .input('R', Items.RED_DYE)
                .criterion(hasItem(Items.BOOK), conditionsFromItem(Items.BOOK))
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

//        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.LUCKY_DICE)
//                .pattern(" D ")
//                .pattern("DGD")
//                .pattern(" D ")
//                .input('D', Items.DIAMOND)
//                .input('G', Items.GOLD_BLOCK)
//                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
//                .offerTo(exporter);
//
//        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SINFUL_DICE)
//                .pattern(" D ")
//                .pattern("DGD")
//                .pattern(" D ")
//                .input('D', Items.DIAMOND_BLOCK)
//                .input('G', ModItems.LUCKY_DICE)
//                .criterion(hasItem(ModItems.LUCKY_DICE), conditionsFromItem(ModItems.LUCKY_DICE))
//                .offerTo(exporter);

//        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BONE_PILE)
//                .pattern("123")
//                .pattern("   ")
//                .pattern("   ")
//                .input('1', Items.BONE)
//                .input('2', ModItems.WITHERED_BONE)
//                .input('3', ModItems.HUMAN_BONE)
//                .criterion(hasItem(Items.BONE), conditionsFromItem(Items.BONE))
//                .offerTo(exporter);

//        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ANCIENT_NETHERITE)
//                .pattern("122")
//                .pattern("333")
//                .pattern("333")
//                .input('1', Items.NETHERITE_INGOT)
//                .input('2', Items.GOLD_INGOT)
//                .input('3', ModItems.ANCIENT_SCRAP)
//                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
//                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ANCIENT_BONE_ALLOY_CHUNK)
                .pattern("21 ")
                .pattern("   ")
                .pattern("   ")
                .input('1', ModItems.DOMINIC_ORB)
                .input('2', Items.NETHERITE_INGOT)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ANCIENT_BONE_ALLOY)
                .pattern("11 ")
                .pattern("11 ")
                .pattern("   ")
                .input('1', ModItems.ANCIENT_BONE_ALLOY_CHUNK)
                .criterion(hasItem(Items.BEDROCK), conditionsFromItem(Items.BEDROCK))
                .offerTo(exporter);

//        offerSmelting(exporter, List.of(ModItems.BONE_PILE), RecipeCategory.MISC, ModItems.BONE_DUST,
//                0.7f, 160, "bone_pile_from_bone_dust");

        offerSmelting(exporter, List.of(Items.SOUL_SAND), RecipeCategory.MISC, ModBlocks.SOUL_GLASS,
                0.7f, 160, "bone_pile_from_bone_dust");

//        offerSmelting(exporter, List.of(ModBlocks.ANCIENTER_DEBRIS), RecipeCategory.MISC, ModItems.ANCIENT_SCRAP,
//                0.7f, 1600, "bone_pile_from_bone_dust");

        offerSmelting(exporter, List.of(ModItems.BROKEN_DOMINIC_DAGGER_PIECE), RecipeCategory.MISC, ModItems.ANCIENT_BONE_ALLOY,
                0.7f, 1600, "bone_pile_from_bone_dust");

//        StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Items.IRON_INGOT), RecipeCategory.MISC, ModBlocks.IRON_WALKWAY, 8);
//        StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Items.IRON_INGOT), RecipeCategory.MISC, ModBlocks.IRON_WALKWAY_STAIRS, 4);

    }
}
