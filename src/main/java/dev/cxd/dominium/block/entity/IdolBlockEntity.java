package dev.cxd.dominium.block.entity;

import dev.cxd.dominium.block.IdolBlock;
import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.init.ModBlockEntities;
import dev.cxd.dominium.init.ModComponents;
import dev.cxd.dominium.item.signable.ContractSigned;
import dev.cxd.dominium.item.signable.SoulboundContractSigned;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class IdolBlockEntity extends BlockEntity {
    private UUID placerUuid;
    private static final Set<IdolBlockEntity> ACTIVE_IDOLS = Collections.newSetFromMap(new WeakHashMap<>());
    public Set<UUID> currentForbiddenUuids = Collections.emptySet();

    public IdolBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IDOL_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, IdolBlockEntity be) {
        if (world.isClient) return;
        if (be.placerUuid == null) return;

        ServerWorld serverWorld = (ServerWorld)world;
        ServerPlayerEntity owner = serverWorld.getServer().getPlayerManager().getPlayer(be.placerUuid);
        if (owner == null) return;

        if (world.getTime() % 20 != 0) return;

        Set<UUID> forbiddenUuids = new HashSet<>();
        for (int i = 0; i < owner.getInventory().size(); i++) {
            ItemStack stack = owner.getInventory().getStack(i);
            if (stack.getItem() instanceof ContractSigned || stack.getItem() instanceof SoulboundContractSigned) {
                String uuidStr = ModComponents.getVesselUuid(stack);
                if (uuidStr != null && !uuidStr.isEmpty()) {
                    forbiddenUuids.add(UUID.fromString(uuidStr));
                }
            }
        }

        forbiddenUuids.removeIf(uuid -> {
            ServerPlayerEntity p = serverWorld.getServer().getPlayerManager().getPlayer(uuid);
            if (p == null) return true;
            double dx = p.getX() - pos.getX();
            double dy = p.getY() - pos.getY();
            double dz = p.getZ() - pos.getZ();
            return dx * dx + dy * dy + dz * dz > ModConfig.IDOL_PROTECTION_RADIUS * ModConfig.IDOL_PROTECTION_RADIUS;
        });

        be.currentForbiddenUuids = forbiddenUuids;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (!world.isClient) {
            ACTIVE_IDOLS.add(this);
        }
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        ACTIVE_IDOLS.remove(this);
    }

    public static Set<IdolBlockEntity> getActiveIdols() {
        return ACTIVE_IDOLS;
    }

    public void setPlacerUuid(UUID uuid) {
        this.placerUuid = uuid;
        markDirty();
    }

    public UUID getPlacerUuid() {
        return this.placerUuid;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.containsUuid("Placer")) {
            this.placerUuid = nbt.getUuid("Placer");
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (placerUuid != null) {
            nbt.putUuid("Placer", placerUuid);
        }
    }
}