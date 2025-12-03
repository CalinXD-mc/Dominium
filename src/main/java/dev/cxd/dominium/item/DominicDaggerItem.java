package dev.cxd.dominium.item;

import dev.cxd.dominium.init.ModStatusEffects;
import dev.cxd.dominium.utils.CanBanPeopleItem;
import dev.cxd.dominium.utils.MarkableItem;
import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DominicDaggerItem extends SwordItem implements MarkableItem, CanBanPeopleItem {
    private final ModRarities rarity;

    public DominicDaggerItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings, ModRarities rarity) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.rarity = rarity;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!player.getWorld().isClient()) {
            if (!player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
                if (entity instanceof PlayerEntity target) {
                    target.addStatusEffect(new StatusEffectInstance(ModStatusEffects.DAMNED, 20 * 60 * 3, 0, false, true, true));
                    player.getItemCooldownManager().set(stack.getItem(), 20 * 60);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public Text getName() {
        Text baseName = Text.translatable(this.getTranslationKey());
        Style style = Style.EMPTY.withFont(new Identifier("minecraft", "default"));

        return baseName.copy().setStyle(style.withColor(Formatting.GOLD));
    }

    @Override
    public Text getName(ItemStack stack) {
        Text baseName = super.getName(stack);

        return baseName.copy().setStyle(Style.EMPTY.withColor(rarity.color));
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
            tooltip.add(Text.literal(mark).formatted(Formatting.GOLD));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
