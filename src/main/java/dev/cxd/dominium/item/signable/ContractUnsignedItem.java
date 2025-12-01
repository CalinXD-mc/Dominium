package dev.cxd.dominium.item.signable;


import dev.cxd.dominium.init.ModComponents;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WritableBookItem;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ContractUnsignedItem extends WritableBookItem {
    private final ModRarities rarity;

    public ContractUnsignedItem(Settings settings, ModRarities rarity) {
        super(settings);
        this.rarity = rarity;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack unsignedStack = user.getStackInHand(hand);

        if(user.isSneaking()) {
            if (ModComponents.getContractSigned(unsignedStack) != 0) {
                return TypedActionResult.fail(unsignedStack);
            }

            ItemStack signedStack = new ItemStack(ModItems.CONTRACT_SIGNED);

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

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public Text getName(ItemStack stack) {
        Text baseName = super.getName(stack);

        return baseName.copy().setStyle(Style.EMPTY.withColor(rarity.color));
    }

}

