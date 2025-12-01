package dev.cxd.dominium.datagen;

import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.utils.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.SWORDS)
                .add(ModItems.DOMINIC_DAGGER.asItem())
                .add(ModItems.SOULTRAP_SPETUM.asItem());
        getOrCreateTagBuilder(ModTags.Items.SMELTABLE_TO_ANCIENT_BONE_ALLOY)
                .add(ModItems.BROKEN_DOMINIC_DAGGER_PIECE);
    }
}