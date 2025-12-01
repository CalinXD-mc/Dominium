package dev.cxd.dominium.item.signable;


import dev.cxd.dominium.init.ModComponents;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.utils.CustomRarityItem;
import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulboundContractUnsignedItem extends CustomRarityItem {
    public SoulboundContractUnsignedItem(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack unsignedStack = user.getStackInHand(hand);

        if(user.isSneaking()) {
            if (ModComponents.getContractSigned(unsignedStack) != 0) {
                return TypedActionResult.fail(unsignedStack);
            }

            ItemStack signedStack = new ItemStack(ModItems.SOULBOUND_CONTRACT_SIGNED);

            ModComponents.setVesselUuid(signedStack, user.getUuid().toString());
            ModComponents.setContractSigned(signedStack, 1);
            ModComponents.setPlayerNameForSoulOwning(signedStack, user.getName().getString());
            user.setStackInHand(hand, signedStack);

            return TypedActionResult.success(signedStack, world.isClient());
        }

        return TypedActionResult.success(unsignedStack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Sneak + Right Click to Sign.").formatted(Formatting.DARK_AQUA));
        if (MinecraftClient.getInstance().options.advancedItemTooltips) {
            tooltip.add(Text.literal("").formatted(Formatting.DARK_RED, Formatting.BOLD));
            tooltip.add(Text.literal("WARNING!!!").formatted(Formatting.DARK_RED, Formatting.BOLD));
            tooltip.add(Text.literal("Signing this means you consent to giving").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("your life away to the contract. Whoever holds it").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("will have absolute power over you, and there’s").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("nothing you’ll be able to do to fight back other").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("than comply with the holder.").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("SO MAYBE DON’T SIGN IT!!!").formatted(Formatting.DARK_RED, Formatting.BOLD));
            tooltip.add(Text.literal("").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Note written by,").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("  Someone Something").formatted(Formatting.GRAY).formatted(Formatting.OBFUSCATED));
        }
    }
}

