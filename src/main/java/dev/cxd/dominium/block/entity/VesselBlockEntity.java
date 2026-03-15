package dev.cxd.dominium.block.entity;

import dev.cxd.dominium.init.ModBlockEntities;
import dev.cxd.dominium.init.ModComponents;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.init.ModStatusEffects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class VesselBlockEntity extends BlockEntity {

    private ItemStack insertedContract = ItemStack.EMPTY;

    public VesselBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VESSEL_BLOCK_ENTITY, pos, state);
    }

    public boolean hasContract() {
        return !insertedContract.isEmpty();
    }

    public ItemStack getContract() {
        return insertedContract;
    }

    public void insertContract(ItemStack stack) {
        this.insertedContract = stack.copy();
        markDirty();
    }

    public ItemStack removeContract() {
        ItemStack contract = insertedContract.copy();
        this.insertedContract = ItemStack.EMPTY;
        markDirty();
        return contract;
    }

    public UUID getSignerUUID() {
        if (insertedContract.isEmpty()) return null;
        String uuidStr = ModComponents.getVesselUuid(insertedContract);
        if (uuidStr == null || uuidStr.isEmpty()) return null;
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, VesselBlockEntity be) {
        if (world.isClient()) return;
        if (world.getTime() % 20 != 0) return;
        if (!state.get(dev.cxd.dominium.block.VesselBlock.LIT)) return;
        if (!be.hasContract()) return;

        UUID signerUUID = be.getSignerUUID();
        if (signerUUID == null) return;

        PlayerEntity signer = world.getPlayerByUuid(signerUUID);
        if (signer == null) return;

        signer.addStatusEffect(new StatusEffectInstance(
                ModStatusEffects.SOUL_DEBT, 40, 0, false, true, true));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!insertedContract.isEmpty()) {
            NbtCompound contractNbt = new NbtCompound();
            insertedContract.writeNbt(contractNbt);
            nbt.put("InsertedContract", contractNbt);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("InsertedContract")) {
            insertedContract = ItemStack.fromNbt(nbt.getCompound("InsertedContract"));
        } else {
            insertedContract = ItemStack.EMPTY;
        }
    }
}