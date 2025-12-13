package dev.cxd.dominium.item;

import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DominicOrbItem extends CustomRarityItem {
    public DominicOrbItem(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (ModConfig.dominicOrbsInSurvival) {
            if (MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Obtained by killing Withers.").formatted(Formatting.GRAY).formatted(Formatting.OBFUSCATED));
            }
        } else {
            if (MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Obtained by Something a Something with Something.").formatted(Formatting.GRAY).formatted(Formatting.OBFUSCATED));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
