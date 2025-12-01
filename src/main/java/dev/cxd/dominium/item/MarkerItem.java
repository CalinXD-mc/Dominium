package dev.cxd.dominium.item;

import dev.cxd.dominium.utils.MarkableItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class MarkerItem extends Item {
    String mark;

    public MarkerItem(Settings settings, String mark) {
        super(settings);
        this.mark = mark;
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

        ItemStack toMark = player.getOffHandStack();
        if (toMark.getItem() instanceof MarkableItem markable) {
            if (!world.isClient) {
                markable.setMark(toMark, mark); // put whatever text you want here
                player.sendMessage(Text.literal("Marked item!"), true);
            }
            return TypedActionResult.success(player.getStackInHand(hand));
        }

        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}