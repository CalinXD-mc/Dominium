package dev.cxd.dominium.block.entity;

import dev.cxd.dominium.block.ObeliskBlock;
import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ParticleSpawnPacketData;
import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.init.ModBlockEntities;
import dev.cxd.dominium.init.ModPackets;
import dev.cxd.dominium.init.ModParticles;
import dev.cxd.dominium.utils.FactionManager;
import dev.cxd.dominium.utils.ZoneManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ObeliskBlockEntity extends BlockEntity {

    public static final int OBELISK_PROTECTION_RADIUS = ModConfig.OBELISK_PROTECTION_RADIUS;
    public static final int OBELISK_PROTECTION_HEIGHT = ModConfig.OBELISK_PROTECTION_HEIGHT;
    private static final int RESTORE_TICKS = 100;

    private UUID ownerUUID = null;

    private final Map<BlockPos, Long> pendingRestoreTimes = new HashMap<>();
    private final Map<BlockPos, BlockState> pendingRestoreStates = new HashMap<>();
    private final Map<BlockPos, Long> pendingRemovals = new HashMap<>();

    private final Map<BlockPos, Integer> breakingIds = new HashMap<>();
    private int nextBreakingId = 9000;

    private final Map<BlockPos, long[]> activelyMining = new HashMap<>();
    private final Map<BlockPos, UUID> miningPlayers = new HashMap<>();

    private boolean noEnchantZoneEnabled = false;

    public ObeliskBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OBELISK_BLOCK_ENTITY, pos, state);
    }

    public boolean isNoEnchantZoneEnabled() {
        return noEnchantZoneEnabled;
    }

    public void setNoEnchantZoneEnabled(boolean enabled, World world) {
        this.noEnchantZoneEnabled = enabled;
        markDirty();

        if (world instanceof ServerWorld serverWorld) {
            String dimension = serverWorld.getRegistryKey().getValue().toString();
            if (enabled) {
                ZoneManager.addZone(getZoneId(), getClaimBox(), dimension, pos);
            } else {
                ZoneManager.removeZone(getZoneId());
            }
        }
    }

    private UUID getZoneId() {
        return UUID.nameUUIDFromBytes(
                (pos.getX() + "," + pos.getY() + "," + pos.getZ()).getBytes()
        );
    }

    private net.minecraft.util.math.Box getClaimBox() {
        return new net.minecraft.util.math.Box(
                pos.getX() - OBELISK_PROTECTION_RADIUS, pos.getY() - OBELISK_PROTECTION_HEIGHT, pos.getZ() - OBELISK_PROTECTION_RADIUS,
                pos.getX() + OBELISK_PROTECTION_RADIUS, pos.getY() + OBELISK_PROTECTION_HEIGHT, pos.getZ() + OBELISK_PROTECTION_RADIUS
        );
    }

    public void setOwner(UUID uuid) {
        this.ownerUUID = uuid;
        markDirty();
    }

    public UUID getOwner() {
        return ownerUUID;
    }

    public boolean isOwner(UUID uuid) {
        return ownerUUID != null && ownerUUID.equals(uuid);
    }

    public boolean isAllowed(UUID uuid) {
        if (ownerUUID == null) return true;
        if (ownerUUID.equals(uuid)) return true;
        String ownerFaction = FactionManager.getPlayerFaction(ownerUUID);
        if (ownerFaction == null) return false;
        return FactionManager.areSameFaction(ownerUUID, uuid);
    }

    public boolean isInClaimRange(BlockPos target) {
        return Math.abs(target.getX() - pos.getX()) <= OBELISK_PROTECTION_RADIUS &&
                Math.abs(target.getY() - pos.getY()) <= OBELISK_PROTECTION_HEIGHT &&
                Math.abs(target.getZ() - pos.getZ()) <= OBELISK_PROTECTION_RADIUS;
    }

    public void onUnauthorizedBreak(World world, BlockPos brokenPos, BlockState brokenState) {
        BlockPos immutable = brokenPos.toImmutable();

        if (pendingRemovals.containsKey(immutable)) {
            pendingRemovals.remove(immutable);
            breakingIds.remove(immutable);
            sendCrackStage(world, immutable, -1);
            markDirty();
            return;
        }

        if (pendingRestoreTimes.containsKey(immutable)) return;

        pendingRestoreTimes.put(immutable, world.getTime());
        pendingRestoreStates.put(immutable, brokenState);
        breakingIds.put(immutable, nextBreakingId++);

        spawnOutlineParticles(world, brokenPos);
        spawnEdgeWaveParticles(world, brokenPos);
        markDirty();
    }

    public void onUnauthorizedPlace(World world, BlockPos placedPos, BlockState previousState) {
        BlockPos immutable = placedPos.toImmutable();
        if (pendingRemovals.containsKey(immutable)) return;
        if (pendingRestoreTimes.containsKey(immutable)) return;

        BlockState current = world.getBlockState(immutable);

        if (previousState.isAir()) {
            pendingRemovals.put(immutable, world.getTime());
            breakingIds.put(immutable, nextBreakingId++);
            spawnOutlineParticles(world, placedPos);
            spawnEdgeWaveParticles(world, placedPos);
            markDirty();
            return;
        }

        if (previousState.isReplaceable()) {
            if (previousState.getBlock().getDefaultState().isAir() ||
                    !previousState.getFluidState().isEmpty()) {
                pendingRemovals.put(immutable, world.getTime());
                breakingIds.put(immutable, nextBreakingId++);
            } else {
                pendingRestoreTimes.put(immutable, world.getTime());
                pendingRestoreStates.put(immutable, previousState);
                breakingIds.put(immutable, nextBreakingId++);
            }
            spawnOutlineParticles(world, placedPos);
            spawnEdgeWaveParticles(world, placedPos);
            markDirty();
            return;
        }

        pendingRestoreTimes.put(immutable, world.getTime());
        pendingRestoreStates.put(immutable, previousState);
        breakingIds.put(immutable, nextBreakingId++);
        spawnOutlineParticles(world, placedPos);
        spawnEdgeWaveParticles(world, placedPos);
        markDirty();
    }

    private void spawnOutlineParticles(World world, BlockPos target) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Color startColor = new Color(0x23C0C6);
        Color endColor = new Color(0x13AFB3);

        ParticleSpawnPacketData packetData = new ParticleSpawnPacketData(
                target.toCenterPos(),
                startColor.getRGB(),
                endColor.getRGB(),
                "block_outline_obelisk"
        );

        PacketByteBuf bufMult = PacketByteBufs.create();
        packetData.toBytes(bufMult);

        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            ServerPlayNetworking.send(player, ModPackets.PARTICLE_SPAWN_ID, bufMult);
        }
    }

    private void spawnEdgeWaveParticles(World world, BlockPos target) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        double[][] edges = {
                {-0.5, -0.5, -0.5,   0.5, -0.5, -0.5},
                { 0.5, -0.5, -0.5,   0.5, -0.5,  0.5},
                { 0.5, -0.5,  0.5,  -0.5, -0.5,  0.5},
                {-0.5, -0.5,  0.5,  -0.5, -0.5, -0.5},
                {-0.5,  0.5, -0.5,   0.5,  0.5, -0.5},
                { 0.5,  0.5, -0.5,   0.5,  0.5,  0.5},
                { 0.5,  0.5,  0.5,  -0.5,  0.5,  0.5},
                {-0.5,  0.5,  0.5,  -0.5,  0.5, -0.5},
                {-0.5, -0.5, -0.5,  -0.5,  0.5, -0.5},
                { 0.5, -0.5, -0.5,   0.5,  0.5, -0.5},
                { 0.5, -0.5,  0.5,   0.5,  0.5,  0.5},
                {-0.5, -0.5,  0.5,  -0.5,  0.5,  0.5},
        };

        double cx = target.getX() + 0.5;
        double cy = target.getY() + 0.5;
        double cz = target.getZ() + 0.5;

        java.util.Random random = new java.util.Random();

        for (double[] edge : edges) {
            double x1 = edge[0], y1 = edge[1], z1 = edge[2];
            double x2 = edge[3], y2 = edge[4], z2 = edge[5];

            int count = 3 + random.nextInt(5);
            double[] ts = new double[count];
            for (int i = 0; i < count; i++) {
                ts[i] = random.nextDouble();
            }
            java.util.Arrays.sort(ts);

            for (double t : ts) {
                double px = cx + x1 + t * (x2 - x1);
                double py = cy + y1 + t * (y2 - y1);
                double pz = cz + z1 + t * (z2 - z1);

                serverWorld.spawnParticles(
                        ModParticles.OBELISK_PARTICLE,
                        px, py, pz,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }
    }

    private void sendCrackStage(World world, BlockPos target, int stage) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        int id = breakingIds.getOrDefault(target.toImmutable(), -1);
        if (id == -1) return;
        serverWorld.setBlockBreakingInfo(id, target, stage);
    }

    private boolean zoneRegistered = false;

    public static void tick(World world, BlockPos pos, BlockState state, ObeliskBlockEntity be) {
        if (world.isClient()) return;

        if (!be.zoneRegistered) {
            be.zoneRegistered = true;
            if (be.noEnchantZoneEnabled && world instanceof ServerWorld serverWorld) {
                String dimension = serverWorld.getRegistryKey().getValue().toString();
                ZoneManager.addZone(be.getZoneId(), be.getClaimBox(), dimension, be.pos);
            }
        }

        if (world.getTime() % 10 != 0) return;

        long now = world.getTime();

        Iterator<Map.Entry<BlockPos, Long>> restoreIter = be.pendingRestoreTimes.entrySet().iterator();
        while (restoreIter.hasNext()) {
            Map.Entry<BlockPos, Long> entry = restoreIter.next();
            BlockPos brokenPos = entry.getKey();
            long brokenAt = entry.getValue();
            long elapsed = now - brokenAt;

            if (elapsed >= RESTORE_TICKS) {
                be.sendCrackStage(world, brokenPos, -1);

                BlockState expected = be.pendingRestoreStates.get(brokenPos);
                if (expected != null) {
                    BlockState current = world.getBlockState(brokenPos);
                    if (!current.isAir() && !current.equals(expected)) {
                        world.setBlockState(brokenPos, Blocks.AIR.getDefaultState(),
                                Block.NOTIFY_ALL | Block.SKIP_DROPS);
                    }
                    world.setBlockState(brokenPos, expected, 3);
                }

                restoreIter.remove();
                be.pendingRestoreStates.remove(brokenPos);
                be.breakingIds.remove(brokenPos);
                be.markDirty();
            } else {
                int stage = (int) ((elapsed / (float) RESTORE_TICKS) * 9);
                be.sendCrackStage(world, brokenPos, stage);
                be.spawnOutlineParticles(world, brokenPos);
            }
        }

        Iterator<Map.Entry<BlockPos, Long>> removeIter = be.pendingRemovals.entrySet().iterator();
        while (removeIter.hasNext()) {
            Map.Entry<BlockPos, Long> entry = removeIter.next();
            BlockPos placedPos = entry.getKey();
            long placedAt = entry.getValue();
            long elapsed = now - placedAt;

            if (elapsed >= RESTORE_TICKS) {
                be.sendCrackStage(world, placedPos, -1);

                BlockState current = world.getBlockState(placedPos);
                if (!current.isAir() && !(current.getBlock() instanceof ObeliskBlock)) {
                    world.syncWorldEvent(2001, placedPos, Block.getRawIdFromState(current));
                    Block.dropStacks(current, world, placedPos, null);
                    world.setBlockState(placedPos, Blocks.AIR.getDefaultState(),
                            Block.NOTIFY_ALL | Block.SKIP_DROPS);
                }

                removeIter.remove();
                be.breakingIds.remove(placedPos);
                be.markDirty();
            } else {
                int stage = (int) ((elapsed / (float) RESTORE_TICKS) * 9);
                be.sendCrackStage(world, placedPos, stage);
                be.spawnOutlineParticles(world, placedPos);
            }
        }
    }

    public void startMining(World world, BlockPos blockPos, UUID playerUuid) {
        BlockPos immutable = blockPos.toImmutable();

        if (activelyMining.containsKey(immutable)) return;

        int id = nextBreakingId++;
        activelyMining.put(immutable, new long[]{world.getTime(), id});
        miningPlayers.put(immutable, playerUuid);
        breakingIds.put(immutable, id);
    }

    public void stopMining(World world, BlockPos blockPos) {
        BlockPos immutable = blockPos.toImmutable();
        sendCrackStage(world, immutable, -1);
        activelyMining.remove(immutable);
        miningPlayers.remove(immutable);
        breakingIds.remove(immutable);
    }

    public void stopMiningIfStale(World world, UUID playerUuid, @Nullable BlockPos currentTarget) {
        Iterator<Map.Entry<BlockPos, UUID>> iter = miningPlayers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<BlockPos, UUID> entry = iter.next();
            if (entry.getValue().equals(playerUuid)) {
                BlockPos miningPos = entry.getKey();
                if (!miningPos.equals(currentTarget)) {
                    sendCrackStage(world, miningPos, -1);
                    activelyMining.remove(miningPos);
                    breakingIds.remove(miningPos);
                    iter.remove();
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        NbtList restoreList = new NbtList();
        for (Map.Entry<BlockPos, Long> entry : pendingRestoreTimes.entrySet()) {
            BlockState state = pendingRestoreStates.get(entry.getKey());
            if (state == null) continue;
            NbtCompound c = new NbtCompound();
            c.putInt("x", entry.getKey().getX());
            c.putInt("y", entry.getKey().getY());
            c.putInt("z", entry.getKey().getZ());
            c.putLong("time", entry.getValue());
            c.put("state", NbtHelper.fromBlockState(state));
            restoreList.add(c);
        }
        nbt.put("PendingRestores", restoreList);

        NbtList removalList = new NbtList();
        for (Map.Entry<BlockPos, Long> entry : pendingRemovals.entrySet()) {
            NbtCompound c = new NbtCompound();
            c.putInt("x", entry.getKey().getX());
            c.putInt("y", entry.getKey().getY());
            c.putInt("z", entry.getKey().getZ());
            c.putLong("time", entry.getValue());
            removalList.add(c);
        }
        nbt.put("PendingRemovals", removalList);
        if (ownerUUID != null) nbt.putUuid("OwnerUUID", ownerUUID);
        nbt.putBoolean("NoEnchantZone", noEnchantZoneEnabled);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        NbtList restoreList = nbt.getList("PendingRestores", 10);
        for (int i = 0; i < restoreList.size(); i++) {
            NbtCompound c = restoreList.getCompound(i);
            BlockPos p = new BlockPos(c.getInt("x"), c.getInt("y"), c.getInt("z"));
            long time = c.getLong("time");
            BlockState state = NbtHelper.toBlockState(
                    world != null ? world.createCommandRegistryWrapper(net.minecraft.registry.RegistryKeys.BLOCK) : null,
                    c.getCompound("state")
            );
            pendingRestoreTimes.put(p, time);
            pendingRestoreStates.put(p, state);
            breakingIds.put(p, nextBreakingId++);
        }

        NbtList removalList = nbt.getList("PendingRemovals", 10);
        for (int i = 0; i < removalList.size(); i++) {
            NbtCompound c = removalList.getCompound(i);
            BlockPos p = new BlockPos(c.getInt("x"), c.getInt("y"), c.getInt("z"));
            long time = c.getLong("time");
            pendingRemovals.put(p, time);
            breakingIds.put(p, nextBreakingId++);
        }
        if (nbt.containsUuid("OwnerUUID")) ownerUUID = nbt.getUuid("OwnerUUID");
        noEnchantZoneEnabled = nbt.getBoolean("NoEnchantZone");
    }
}