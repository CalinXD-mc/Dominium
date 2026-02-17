package dev.cxd.dominium.item.necklaces;

import dev.cxd.dominium.utils.MarkableItem;
import dev.cxd.dominium.utils.ModRarities;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EtherealNecklaceItem extends TrinketItem implements MarkableItem {

    private final ModRarities rarity;
    private int tickCounter = 0;
    public static final UUID ETHEREAL_HEALTH_UUID =
            UUID.fromString("23c6f3a1-8c7b-4c6d-9a3a-1a9f6d2e1b23");

    public EtherealNecklaceItem(Settings settings, ModRarities rarity) {
        super(settings);
        this.rarity = rarity;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!slot.inventory().getSlotType().getName().equals("necklace")) return;
        if (!(entity instanceof PlayerEntity player)) return;

        tickCounter++;
        super.tick(stack, slot, entity);
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
        boolean advanced = MinecraftClient.getInstance().options.advancedItemTooltips;

        if (!mark.isEmpty() && !advanced) {
            tooltip.add(Text.literal(mark).formatted(Formatting.GOLD));
        }

        if (advanced) {
            if (!mark.isEmpty()) {
                tooltip.add(Text.literal(mark).formatted(Formatting.GOLD));
                tooltip.add(Text.literal(" "));
            }

            tooltip.add(
                    Text.literal("An Ancient Artifact that Empowers its Owner with the Powers & Downsides of an ")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal("Something").formatted(Formatting.OBFUSCATED))
            );

            tooltip.add(Text.literal(" "));

            tooltip.add(Text.literal("⚠ Rooflings ").formatted(Formatting.DARK_RED).append(Text.literal("will kill you in one hit while worn").formatted(Formatting.RED)));
        }

        super.appendTooltip(stack, world, tooltip, context);
    }


    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!slot.inventory().getSlotType().getName().equals("necklace")) return;
        if (!(entity instanceof PlayerEntity player)) return;

        var attr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attr == null) return;
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!slot.inventory().getSlotType().getName().equals("necklace")) return;
        if (!(entity instanceof PlayerEntity player)) return;

        var attr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attr != null) {
            attr.removeModifier(ETHEREAL_HEALTH_UUID);
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy()
                .setStyle(Style.EMPTY.withColor(rarity.color));
    }
}
