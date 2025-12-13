package dev.cxd.dominium.item.signable;

import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.init.*;
import dev.cxd.dominium.item.CustomRarityItem;
import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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

        // Get target player UUID
        UUID targetUUID = UUID.fromString(ModComponents.getVesselUuid(stack));
        if (targetUUID == null) return TypedActionResult.pass(stack);

        PlayerEntity target = world.getPlayerByUuid(targetUUID);
        if (target == null) return TypedActionResult.pass(stack);

        if (user.isSneaking()) {
            double distance = user.squaredDistanceTo(target);
            if (distance <= 10 * 10) { // 10 block radius
                if (target.hasStatusEffect(ModStatusEffects.SOUL_STRAIN)) {
                    target.removeStatusEffect(ModStatusEffects.SOUL_STRAIN);

                    return TypedActionResult.success(stack, world.isClient());
                }
            } else {
                if (!world.isClient()) {
                    user.sendMessage(Text.literal("Target is too far away to unchain it (must be within 10 blocks)").formatted(Formatting.RED), true);
                }
                return TypedActionResult.fail(stack);
            }
        }

        if (offhand.isEmpty()) return TypedActionResult.pass(stack);

        if (offhand.isOf(Items.NETHERITE_SWORD)) {
            DamageSource damageSource = new DamageSource(
                    world.getRegistryManager()
                            .get(RegistryKeys.DAMAGE_TYPE)
                            .entryOf(ModDamageTypes.DISOBEDIENCE_DAMAGE));
            target.damage(damageSource, 4.0F);

            user.sendMessage(Text.literal("Dealt 4 damage to " + target.getName().getString()).formatted(Formatting.GOLD), true);
        } else if (offhand.isOf(Items.ENDER_PEARL)) {
            user.teleport(target.getX(), target.getY(), target.getZ());

            user.sendMessage(Text.literal("Teleported to " + target.getName().getString()).formatted(Formatting.GOLD), true);
        } else if (offhand.isOf(Items.BLAZE_POWDER)) {
            target.setOnFireFor(5);

            user.sendMessage(Text.literal("Set " + target.getName().getString() + " on Fire").formatted(Formatting.GOLD), true);
        } else if (offhand.isOf(Items.IRON_BLOCK)) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 30, 10));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 30, 250, false, true));

            user.sendMessage(Text.literal("Stunned " + target.getName().getString()).formatted(Formatting.GOLD), false);
        } else if (offhand.isOf(ModItems.ANCIENT_BONE_ALLOY) || offhand.isOf(ModItems.ANCIENT_BONE_ALLOY_CHUNK)) {
            if (!world.isClient()) {
                target.addStatusEffect(new StatusEffectInstance((StatusEffect) ModStatusEffects.SOUL_STRAIN, 20 * 30, 0, false, false, true));

                ServerWorld serverWorld = (ServerWorld) world;
                EternalDivinityChainsEntity chain = ModEntities.ETERNAL_DIVINITY_CHAINS.create(serverWorld);

                if (chain != null) {
                    chain.setBoundPlayer(target.getUuid());
                    chain.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), 0, 0);
                    serverWorld.spawnEntity(chain);
                }

                serverWorld.spawnParticles(ParticleTypes.SOUL, target.getX(), target.getY() + 1, target.getZ(), 30, 0.5, 0.5, 0.5, 0.01);

                user.sendMessage(Text.literal("Trapped " + target.getName().getString() + " in chains for 30 seconds").formatted(Formatting.GOLD), true);
            }
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
                tooltip.add(Text.literal("Right Click with an Ancient Bone Alloy Ingot or Chunk in your offhand to trap the Signer in chains for 30 seconds.").formatted(Formatting.DARK_AQUA));
            }
            if (debugOne != null && !debugOne.isEmpty() && MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Crouch without an offhand item to remove Soul Strain (within 10 blocks).").formatted(Formatting.GREEN));
            }
            if (debugOne != null && !debugOne.isEmpty() && MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Debug UUID: " + debugOne).formatted(Formatting.DARK_GRAY));
            }
        }

        super.appendTooltip(stack, world, tooltip, context);
    }
}