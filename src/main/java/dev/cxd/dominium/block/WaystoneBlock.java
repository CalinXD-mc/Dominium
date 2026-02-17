package dev.cxd.dominium.block;

import dev.cxd.dominium.init.ModComponents;
import dev.cxd.dominium.init.ModParticles;
import dev.cxd.dominium.init.ModSounds;
import dev.cxd.dominium.item.signable.ContractSigned;
import dev.cxd.dominium.item.signable.SoulboundContractSigned;
import dev.cxd.dominium.item.signable.WandererTabletSigned;
import dev.cxd.dominium.utils.DelayedTaskScheduler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.UUID;

public class WaystoneBlock extends Block {

    public static final VoxelShape TOP_SHAPE;
    public static final VoxelShape MIDDLE_SHAPE;
    public static final VoxelShape BASE_SHAPE;

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand activeHand, BlockHitResult hit) {
        Hand hand = activeHand;
        ItemStack held = player.getStackInHand(hand);

        if (!(held.getItem() instanceof ContractSigned || held.getItem() instanceof WandererTabletSigned || held.getItem() instanceof SoulboundContractSigned))
            return ActionResult.PASS;

        String uuidStr = ModComponents.getVesselUuid(held);
        if (uuidStr == null || uuidStr.isEmpty()) return ActionResult.FAIL;

        UUID uuid = UUID.fromString(uuidStr);

        if (world instanceof ServerWorld serverWorld) {

            MinecraftServer server = serverWorld.getServer();
            ServerPlayerEntity targetPlayer = server.getPlayerManager().getPlayer(uuid);
            if (targetPlayer == null) return ActionResult.FAIL;

            if (held.getItem() instanceof ContractSigned) {
                if (player.experienceLevel < 5) {
                    player.sendMessage(Text.literal("You do not have enough XP levels!").formatted(Formatting.RED), true);
                    if (player instanceof ServerPlayerEntity svPlayer)
                        svPlayer.setExperienceLevel(targetPlayer.experienceLevel - 5);
                    return ActionResult.FAIL;
                }
                player.setStackInHand(hand, ItemStack.EMPTY);
            }

            if (held.getItem() instanceof WandererTabletSigned) {
                if (!targetPlayer.isSneaking()) {
                    player.sendMessage(Text.literal(targetPlayer.getName().getString() + " must be sneaking to be teleported!").formatted(Formatting.RED), true);
                    targetPlayer.sendMessage(Text.literal("Someone tried to Teleport you but, You must be sneaking to be teleported!").formatted(Formatting.RED), true);
                    return ActionResult.FAIL;
                }

                if (targetPlayer.experienceLevel < 10) {
                    player.sendMessage(Text.literal(targetPlayer.getName().getString() + " does not have enough XP levels!").formatted(Formatting.RED), true);
                    targetPlayer.sendMessage(Text.literal("You do not have enough XP levels!").formatted(Formatting.RED), true);
                    targetPlayer.setExperienceLevel(targetPlayer.experienceLevel - 10);
                    return ActionResult.FAIL;
                }
            }

            player.getWorld().playSound(null, BlockPos.ofFloored(player.getPos()),
                    ModSounds.WAYSTONE_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.0f);

            targetPlayer.getWorld().playSound(null, BlockPos.ofFloored(player.getPos()),
                    ModSounds.WAYSTONE_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.0f);

            serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    20, 0.3, 0.5, 0.3, 0.01);

            targetPlayer.getServerWorld().spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    20, 0.3, 0.5, 0.3, 0.01);

            targetPlayer.getServerWorld().spawnParticles(ModParticles.CHANT,
                    targetPlayer.getX(), targetPlayer.getY() - 0.45, targetPlayer.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0);

            targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 4));
            targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 7));

            DelayedTaskScheduler.schedule(server, 100, () -> {
                server.execute(() -> {
                    targetPlayer.teleport(serverWorld,
                            pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                            targetPlayer.getYaw(), targetPlayer.getPitch());

                    serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                            pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                            20, 0.3, 0.5, 0.3, 0.01);

                    targetPlayer.getServerWorld().spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                            targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                            20, 0.3, 0.5, 0.3, 0.01);

                    player.getWorld().playSound(null, BlockPos.ofFloored(player.getPos()),
                            ModSounds.WAYSTONE_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    targetPlayer.getWorld().playSound(null, BlockPos.ofFloored(player.getPos()),
                            ModSounds.WAYSTONE_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                });
            });
        }

        return ActionResult.SUCCESS;
    }



    public WaystoneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BASE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BASE_SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return BASE_SHAPE;
    }

    static {
        TOP_SHAPE = Block.createCuboidShape((double)2.0F, (double)11.0F, (double)2.0F, (double)14.0F, (double)16.0F, (double)14.0F);
        MIDDLE_SHAPE = Block.createCuboidShape((double)3.0F, (double)0.0F, (double)3.0F, (double)13.0F, (double)11.0F, (double)13.0F);
        BASE_SHAPE = VoxelShapes.union(TOP_SHAPE, MIDDLE_SHAPE);
    }
}
