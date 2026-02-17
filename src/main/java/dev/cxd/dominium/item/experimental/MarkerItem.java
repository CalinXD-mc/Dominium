package dev.cxd.dominium.item.experimental;

import dev.cxd.dominium.utils.MarkableItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class MarkerItem extends Item {
    String defaultMark;

    public MarkerItem(Settings settings, String mark) {
        super(settings);
        this.defaultMark = mark;
    }

    /**
     * Gets the mark from the ItemStack's NBT, or returns the default mark if not set
     */
    private String getMark(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("Mark")) {
            return nbt.getString("Mark");
        }
        return defaultMark;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        return super.useOnEntity(stack, player, entity, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND) return TypedActionResult.pass(player.getStackInHand(hand));

        ItemStack markerStack = player.getStackInHand(hand);
        ItemStack toMark = player.getOffHandStack();

        if (toMark.getItem() instanceof MarkableItem markable) {
            if (!world.isClient) {
                String mark = getMark(markerStack);
                markable.setMark(toMark, mark);
                player.sendMessage(Text.literal("Marked item with: \"" + mark + "\""), true);
            }
            return TypedActionResult.success(markerStack);
        }

        return TypedActionResult.pass(markerStack);
    }
}