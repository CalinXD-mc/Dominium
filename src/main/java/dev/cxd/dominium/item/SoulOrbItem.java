package dev.cxd.dominium.item;

import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulOrbItem extends CustomRarityItem {
    public SoulOrbItem(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (MinecraftClient.getInstance().options.advancedItemTooltips) {
            tooltip.add(Text.literal("Obtained by Killing a Player on Soul Sand.").formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
