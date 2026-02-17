package dev.cxd.dominium.item;

import dev.cxd.dominium.init.ModStatusEffects;
import dev.cxd.dominium.utils.MarkableItem;
import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProvedientConvenantItem extends CustomRarityItem implements MarkableItem {
    public ProvedientConvenantItem(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            if (!player.getWorld().isClient()) {
                if (player.getActiveStatusEffects() == ModStatusEffects.SOUL_STRAIN) {
                    player.removeStatusEffect(ModStatusEffects.SOUL_STRAIN);
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void setMark(ItemStack stack, String mark) {
        stack.getOrCreateNbt().putString("Mark", mark);
    }

    @Override
    public String getMark(ItemStack stack) {
        return stack.hasNbt() ? stack.getNbt().getString("Mark") : "";
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String mark = getMark(stack);
        if (!mark.isEmpty()) {
            tooltip.add(Text.literal("Given by " + mark + ".").formatted(Formatting.GOLD));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
