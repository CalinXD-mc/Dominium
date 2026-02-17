package dev.cxd.dominium.item.necklaces;

import dev.cxd.dominium.utils.MarkableItem;
import dev.cxd.dominium.utils.ModRarities;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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

public class SpiderNecklaceItem extends TrinketItem implements MarkableItem {

    private final ModRarities rarity;
    private int tickCounter = 0;

    public SpiderNecklaceItem(Settings settings, ModRarities rarity) {
        super(settings);
        this.rarity = rarity;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!slot.inventory().getSlotType().getName().equals("necklace")) return;
        if (!(entity instanceof PlayerEntity player)) return;

        tickCounter++;
        World world = player.getWorld();
        BlockPos pos = player.getBlockPos();

        if (world.getLightLevel(LightType.BLOCK, pos) <= 7) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION,
                    220,
                    0,
                    true,
                    false
            ));
        }

        boolean daylight =
                        world.isDay()
                        && world.isSkyVisible(pos)
                        && world.getLightLevel(LightType.SKY, pos) > 0;

        if (daylight) player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 40, 3, true, false));

        if (player.horizontalCollision && !player.isSneaking()) {
            player.setVelocity(player.getVelocity().x, 0.2D, player.getVelocity().z);
            player.fallDistance = 0.0F;
        }

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
        if (!mark.isEmpty() && !MinecraftClient.getInstance().options.advancedItemTooltips) {
            tooltip.add(Text.literal(mark).formatted(Formatting.GOLD));
        }

        if (MinecraftClient.getInstance().options.advancedItemTooltips && mark.isEmpty()) {
            tooltip.add(Text.literal("An Ancient Artifact that Empowers its Owner with the Powers & Downsides of an Arachnid").formatted(Formatting.GRAY));
        }

        if (MinecraftClient.getInstance().options.advancedItemTooltips && !mark.isEmpty()) {
            tooltip.add(Text.literal(mark).formatted(Formatting.GOLD));
            tooltip.add(Text.literal(" "));
            tooltip.add(Text.literal("An Ancient Artifact that Empowers its Owner with the Powers & Downsides of an Arachnid").formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy()
                .setStyle(Style.EMPTY.withColor(rarity.color));
    }
}
