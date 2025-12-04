package dev.cxd.dominium.item.experimental;

import dev.cxd.dominium.init.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LuckyDiceItem extends Item {

    //Cost: 4
    //Max Profit: 10
    //1st try: 10
    //2nd try: 8
    //3rd try: 6
    //4th try: 4
    //5th try: 2
    //6th try: 0 (no profit)
    //7th try: loss

    public LuckyDiceItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // Check for 2 diamonds
            if (user.getInventory().count(Items.DIAMOND) < 2) {
                user.sendMessage(Text.literal("You need 2 diamonds to roll the dice!")
                        .formatted(Formatting.RED), false);
                return TypedActionResult.fail(stack);
            }

            removeDiamonds(user, 2);

            int roll = weightedRoll(world);

            user.removeStatusEffect(StatusEffects.HEALTH_BOOST);

            Text message;
            StatusEffectInstance effect = null;

            switch (roll) {
                case 1 -> message = Text.literal("🎲 1!").formatted(Formatting.DARK_RED);
                case 2 -> message = Text.literal("🎲 2!").formatted(Formatting.RED);
                case 3 -> message = Text.literal("🎲 3!").formatted(Formatting.RED);
                case 4 -> {
                    message = Text.literal("🎲 4! Bonus +2❤").formatted(Formatting.GREEN);
                    effect = new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 20 * 60, 0);
                }
                case 5 -> {
                    message = Text.literal("🎲 5! Bonus +4❤").formatted(Formatting.GREEN);
                    effect = new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 20 * 120, 1);
                }
                case 6 -> {
                    message = Text.literal("🎲 6! Nice! Bonus +6❤")
                            .formatted(Formatting.DARK_GREEN);
                    effect = new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 20 * 180, 2);
                    user.giveItemStack(new ItemStack(Items.DIAMOND, 16));
                    stack.decrement(1);
                }
                default -> message = Text.literal("Error?").formatted(Formatting.GRAY);
            }

            world.playSound(null, user.getBlockPos(), ModSounds.DICE_ROLL, SoundCategory.PLAYERS, 1.0F, 1.0F);

            user.sendMessage(message, false);

            if (effect != null) {
                user.addStatusEffect(effect);
            }
        }

        return TypedActionResult.success(stack);
    }

    private int weightedRoll(World world) {
        double r = world.getRandom().nextDouble();

        if (r < 0.1833) return 1;
        else if (r < 0.3666) return 2;
        else if (r < 0.5499) return 3;
        else if (r < 0.7332) return 4;
        else if (r < 0.9165) return 5;
        else return 6; // 8.35% chance
    }

    private void removeDiamonds(PlayerEntity player, int count) {
        int remaining = count;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack slot = player.getInventory().getStack(i);
            if (slot.getItem() == Items.DIAMOND) {
                int slotCount = slot.getCount();
                if (slotCount >= remaining) {
                    slot.decrement(remaining);
                    return;
                } else {
                    remaining -= slotCount;
                    slot.decrement(slotCount);
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Spend 2 Diamonds for a chance to win 16!").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}
