package dev.cxd.dominium.init;

import dev.cxd.dominium.block.entity.IdolBlockEntity;
import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.item.necklaces.EtherealNecklaceItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.UUID;

public class ModEvents {
    public static void initialize() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(entity instanceof PlayerEntity target)) return ActionResult.PASS;

            if (player.isCreative()) return ActionResult.PASS;

            for (IdolBlockEntity idol : IdolBlockEntity.getActiveIdols()) {
                if (idol.currentForbiddenUuids.contains(player.getUuid()) && idol.getPlacerUuid() != null) {
                    if (target.getUuid().equals(idol.getPlacerUuid())) {
                        player.sendMessage(Text.literal("You are bound by contract and cannot attack this player!")
                                .formatted(Formatting.RED), true);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity player) {
                World world = player.getWorld();
                BlockPos pos = player.getBlockPos();
                Block block = world.getBlockState(pos.down()).getBlock();

                if (block == Blocks.SOUL_SAND || block == Blocks.SOUL_SOIL) {
                    int count = 2 + world.getRandom().nextInt(4); // 2–5 inclusive
                    ItemStack stack = new ItemStack(ModItems.SOUL_ORB, count);

                    world.spawnEntity(new net.minecraft.entity.ItemEntity(
                            world,
                            player.getX(), player.getY(), player.getZ(),
                            stack
                    ));
                }
            }
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if(ModConfig.shouldSpectatorsInteractWithBlocks) {
                if (player.isSpectator()) {
                    BlockPos pos = hitResult.getBlockPos();
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block instanceof ButtonBlock ||
                            block instanceof DoorBlock ||
                            block instanceof TrapdoorBlock ||
                            block instanceof FenceGateBlock ||
                            block instanceof LeverBlock ||
                            block instanceof BellBlock ||
                            block instanceof NoteBlock) {

                        ActionResult result = state.onUse(world, player, hand, hitResult);
                        return result.isAccepted() ? ActionResult.SUCCESS : ActionResult.PASS;
                    }
                }
            }
            return ActionResult.PASS;
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (world.getRegistryKey() != World.NETHER
                        || player.getY() < ModConfig.NETHER_ROOF_HEIGHT
                        || world.isSkyVisible(player.getBlockPos())) continue;

                boolean wearingEthereal = TrinketsApi.getTrinketComponent(player)
                        .map(tc -> tc.isEquipped(stack -> stack.getItem() instanceof EtherealNecklaceItem))
                        .orElse(false);

                if (!wearingEthereal) {
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.DARKNESS,
                            200,
                            0,
                            true,
                            false
                    ));
                }
            }
        });
    }
}
