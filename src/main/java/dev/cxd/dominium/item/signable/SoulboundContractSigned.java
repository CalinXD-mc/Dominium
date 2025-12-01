package dev.cxd.dominium.item.signable;

import dev.cxd.dominium.init.ModComponents;
import dev.cxd.dominium.init.ModDamageTypes;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.init.ModStatusEffects;
import dev.cxd.dominium.utils.CustomRarityItem;
import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SoulboundContractSigned extends CustomRarityItem {
    public SoulboundContractSigned(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        ItemStack offhand = user.getOffHandStack();

        if (offhand.isEmpty()) return TypedActionResult.pass(stack);

        // get target player UUID
        UUID targetUUID = UUID.fromString(ModComponents.getVesselUuid(stack));
        if (targetUUID == null) return TypedActionResult.pass(stack);

        PlayerEntity target = world.getPlayerByUuid(targetUUID);
        if (target == null) return TypedActionResult.pass(stack);

        if (offhand.isOf(Items.NETHERITE_SWORD)) {
            DamageSource damageSource = new DamageSource(
                    world.getRegistryManager()
                            .get(RegistryKeys.DAMAGE_TYPE)
                            .entryOf(ModDamageTypes.DISOBEDIENCE_DAMAGE));
            target.damage(damageSource, 4.0F);
        } else if (offhand.isOf(Items.ENDER_PEARL)) {
            user.teleport(target.getX(), target.getY(), target.getZ());
        } else if (offhand.isOf(Items.BLAZE_POWDER)) {
            target.setOnFireFor(5);
        } else if (offhand.isOf(ModItems.DOMINIC_ORB)) {
            if (target.getActiveStatusEffects() == ModStatusEffects.DAMNED) {
                target.removeStatusEffect(ModStatusEffects.DAMNED);
            } else if (target.getActiveStatusEffects() != ModStatusEffects.DAMNED) {
                target.addStatusEffect(new StatusEffectInstance(ModStatusEffects.DAMNED, 20 * 60, 0));
            }
        } else if (offhand.isOf(Items.IRON_BLOCK)) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 20, 10));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 20, 250, false, false));
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String desc = ModComponents.getPlayerNameForSoulOwning(stack);
        String debugOne = ModComponents.getVesselUuid(stack);

        if ((desc == null || desc.isEmpty()) && (debugOne == null || debugOne.isEmpty())) {
            tooltip.add(Text.literal("ERM, /give?").formatted(Formatting.GOLD, Formatting.ITALIC));
        } else {
            if (desc != null && !desc.isEmpty()) {
                tooltip.add(Text.literal("Holds " + desc + "'s soul").formatted(Formatting.AQUA));
            }
            if (debugOne != null && !debugOne.isEmpty() && MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Right Click with a Netherite Sword in your offhand to deal Damage to the Signer.").formatted(Formatting.DARK_AQUA));
            }
            if (debugOne != null && !debugOne.isEmpty() && MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Right Click with a Ender Pearl in your offhand to teleport to the Signer.").formatted(Formatting.DARK_AQUA));
            }
            if (debugOne != null && !debugOne.isEmpty() && MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Right Click with a Blaze Powder in your offhand to set the Signer on Fire.").formatted(Formatting.DARK_AQUA));
            }
            if (debugOne != null && !debugOne.isEmpty() && MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Right Click with a Iron Block in your offhand to deal Stun to the Signer.").formatted(Formatting.DARK_AQUA));
            }
            if (debugOne != null && !debugOne.isEmpty() && MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Right Click with a [SOMETHING] in your offhand to [Word] the Signer in [You're Not Getting This From The Code].").formatted(Formatting.DARK_AQUA).formatted(Formatting.OBFUSCATED));
            }
            if (debugOne != null && !debugOne.isEmpty() && MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Debug UUID: " + debugOne).formatted(Formatting.DARK_GRAY));
            }
        }

        super.appendTooltip(stack, world, tooltip, context);
    }
}
