package dev.cxd.dominium.entity;

import dev.cxd.dominium.entity.goals.TamedAttackWithOwnerGoal;
import dev.cxd.dominium.entity.goals.TamedTrackAttackerGoal;
import dev.cxd.dominium.entity.goals.VassalAttackLogicGoal;
import dev.cxd.dominium.entity.goals.VassalDashSlashGoal;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.init.ModSounds;
import dev.cxd.dominium.utils.AdvancementsUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class VassalEntity extends HostileEntity implements TameableHostileEntity {

    protected static final TrackedData<Boolean> DORMANT =
            DataTracker.registerData(VassalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Optional<BlockPos>> DORMANT_POS =
            DataTracker.registerData(VassalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
    public static final TrackedData<Integer> ATTACK_STATE =
            DataTracker.registerData(VassalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> ACTION_STATE =
            DataTracker.registerData(VassalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Direction> DORMANT_DIR =
            DataTracker.registerData(VassalEntity.class, TrackedDataHandlerRegistry.FACING);
    private static final TrackedData<Byte> TAMEABLE =
            DataTracker.registerData(VassalEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Optional<UUID>> OWNER_UUID =
            DataTracker.registerData(VassalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    public int activationTicks = 0;
    public int dashSlashTicks = 0;

    public VassalEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(1.6f);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 160)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 9)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_ARMOR, 12)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 6);
    }


    @Override
    protected void initGoals() {
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(0, new VassalAttackLogicGoal(this));
        this.goalSelector.add(1, new VassalDashSlashGoal(this));
        this.targetSelector.add(1, new TamedTrackAttackerGoal(this));
        this.targetSelector.add(2, new TamedAttackWithOwnerGoal<>(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false,
                livingEntity -> {
                    boolean isFeral = this.getOwnerUuid() == null;

                    if (!isFeral && this.getActionState() != 2) return false;

                    return !livingEntity.equals(this.getOwner())
                            && !(livingEntity instanceof TameableEntity tamed
                            && tamed.getOwner() != null
                            && tamed.getOwner().equals(this.getOwner()))
                            && !(livingEntity instanceof ArmorStandEntity)
                            && !(livingEntity instanceof VassalEntity other && other.isOwner(this.getOwner()))
                            && !(livingEntity instanceof BatEntity)
                            && !(livingEntity instanceof CreeperEntity)
                            && !(livingEntity instanceof PlayerEntity player
                            && player.getUuid().equals(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2")));
                }));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACK_STATE, 0);
        this.dataTracker.startTracking(ACTION_STATE, 0);
        this.dataTracker.startTracking(DORMANT_POS, Optional.empty());
        this.dataTracker.startTracking(DORMANT_DIR, this.getHorizontalFacing());
        this.dataTracker.startTracking(DORMANT, true);
        this.dataTracker.startTracking(TAMEABLE, (byte) 0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.VASSAL_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.VASSAL_DEATH;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!getWorld().isClient()) {
            if (getOwnerUuid() == null
                    && source.getAttacker() instanceof PlayerEntity player
                    && player.isCreative()) {
                return super.damage(source, Float.MAX_VALUE);
            }

            if (source.isIn(DamageTypeTags.IS_EXPLOSION)) {
                amount *= 0.5f;
            }
        }
        return super.damage(source, amount);
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return false;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        if (getDormantPos().isPresent()) {
            nbt.put("DormantPos", NbtHelper.fromBlockPos(getDormantPos().get()));
        }
        nbt.putInt("DormantDir", getDormantDir().getId());
        nbt.putInt("ActionState", getActionState());
        nbt.putInt("AttackState", getAttackState());
        nbt.putInt("activationTicks", activationTicks);
        nbt.putInt("dashSlashTicks", dashSlashTicks);
        nbt.putBoolean("Dormant", this.isDormant());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID ownerUUID;
        if (nbt.containsUuid("Owner")) {
            ownerUUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            ownerUUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (ownerUUID != null) {
            try {
                this.setOwnerUuid(ownerUUID);
                this.setTamed(true);
            } catch (Throwable var4) {
                this.setTamed(false);
            }
        }
        if (nbt.contains("DormantPos")) {
            setDormantPos(NbtHelper.toBlockPos(nbt.getCompound("DormantPos")));
        }
        this.setActionState(nbt.getInt("ActionState"));
        this.setAttackState(nbt.getInt("AttackState"));
        this.setDormantDir(Direction.byId(nbt.getInt("DormantDir")));
        activationTicks = nbt.getInt("activationTicks");
        dashSlashTicks = nbt.getInt("dashSlashTicks");
        this.setDormant(nbt.getBoolean("Dormant"));
    }

    @Override
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    @Override
    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    public void setOwner(PlayerEntity player) {
        this.setTamed(true);
        this.setOwnerUuid(player.getUuid());
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerUuid();
            return uuid == null ? null : this.getWorld().getPlayerByUuid(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    @Override
    public boolean isTamed() {
        return (this.dataTracker.get(TAMEABLE) & 4) != 0;
    }

    @Override
    public void setTamed(boolean tamed) {
        byte b = this.dataTracker.get(TAMEABLE);
        if (tamed) {
            this.dataTracker.set(TAMEABLE, (byte) (b | 4));
        } else {
            this.dataTracker.set(TAMEABLE, (byte) (b & -5));
        }
        this.onTamedChanged();
    }

    public int getAttackState() {
        return this.dataTracker.get(ATTACK_STATE);
    }

    public int attackStartAge = 0;

    public void setAttackState(int state) {
        if (state == 2 && this.dataTracker.get(ATTACK_STATE) != 2) {
            this.attackStartAge = this.age;
        }
        this.dataTracker.set(ATTACK_STATE, state);
    }

    public int getActionState() {
        return this.dataTracker.get(ACTION_STATE);
    }

    public void setActionState(int i) {
        this.dataTracker.set(ACTION_STATE, i);
    }

    public Optional<BlockPos> getDormantPos() {
        return getDataTracker().get(DORMANT_POS);
    }

    public void setDormantPos(BlockPos pos) {
        getDataTracker().set(DORMANT_POS, Optional.of(pos));
    }

    public Direction getDormantDir() {
        return getDataTracker().get(DORMANT_DIR);
    }

    public void setDormantDir(Direction dir) {
        getDataTracker().set(DORMANT_DIR, dir);
    }

    public boolean isDormant() {
        return getDataTracker().get(DORMANT);
    }

    public void setDormant(boolean rest) {
        getDataTracker().set(DORMANT, rest);
    }

    public void reset() {
        this.setTarget(null);
        this.navigation.stop();
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return ModItems.VASSAL_ITEM.getDefaultStack();
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (player.getStackInHand(hand).isEmpty() && player.getUuid().equals(this.getOwnerUuid())) {
            if (player.isSneaking()) {
                if (!player.getAbilities().creativeMode)
                    player.setStackInHand(hand, ModItems.VASSAL_ITEM.getDefaultStack());
                this.remove(RemovalReason.DISCARDED);
                return ActionResult.SUCCESS;
            } else {
                this.cycleActionState(player);
            }
        }
        return super.interactMob(player, hand);
    }

    private void cycleActionState(PlayerEntity player) {
        if (getActionState() == 1) {
            setActionState(2);
            player.sendMessage(Text.translatable("Vassal Death Mode", getWorld().getRegistryKey().getValue().getPath())
                    .setStyle(Style.EMPTY.withColor(Formatting.DARK_RED).withObfuscated(true)
                            .withFont(new Identifier("minecraft", "default"))), true);
        } else if (getActionState() == 0) {
            setActionState(1);
            player.sendMessage(Text.translatable("info.dominium.vassal_activate",
                            getWorld().getRegistryKey().getValue().getPath())
                    .setStyle(Style.EMPTY.withColor(Formatting.AQUA)), true);
        } else if (getActionState() == 2) {
            setActionState(0);
            player.sendMessage(Text.translatable("info.dominium.vassal_deactivate",
                            getWorld().getRegistryKey().getValue().getPath())
                    .setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)), true);
        }
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                 SpawnReason spawnReason, @Nullable EntityData entityData,
                                 @Nullable NbtCompound entityTag) {
        setDormantPos(getBlockPos());
        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getAttackState() == 2 && this.age % 2 == 0) {
            dashSlashTicks++;
        }
        if (dashSlashTicks >= 17) {
            setAttackState(0);
            dashSlashTicks = 0;
        }

        if (age % 5 == 0 && getHealth() < getMaxHealth() && isDormant()) {
            heal(2);
        }

        if (getTarget() != null && (!getTarget().isAlive() || getTarget().getHealth() <= 0)) {
            setTarget(null);
        }

        if (!getWorld().isClient) {
            if (getOwnerUuid() == null) {
                setDormant(false);
                if (getActionState() != 2) setActionState(2);
            } else {
                if (!isDormant()) {
                    if ((this.getTarget() == null || (this.getDormantPos().isPresent()
                            && !this.getDormantPos().get().isWithinDistance(this.getTarget().getBlockPos(), 16)))
                            && forwardSpeed == 0
                            && this.getNavigation().isIdle()
                            && isAtDormantPos()) {
                        setDormant(true);
                        this.playSound(ModSounds.VASSAL_AMBIENT, 1f, 1f);
                        this.updatePositionAndAngles(
                                getDormantPos().get().getX() + 0.5,
                                getDormantPos().get().getY(),
                                getDormantPos().get().getZ() + 0.5,
                                this.getYaw(), this.getPitch());
                    }
                } else if (getTarget() != null
                        && squaredDistanceTo(getTarget()) < 100
                        && dataTracker.get(ACTION_STATE) != 0) {
                    if (activationTicks == 0) {
                        this.playSound(ModSounds.VASSAL_AMBIENT, 1f, 1f);
                    }
                    activationTicks++;
                    setAttackState(1);
                    if (activationTicks > 60) {
                        this.setDormant(false);
                        this.setAttackState(0);
                        this.activationTicks = 0;
                    }
                }
            }
        }


        if (isDormant()) {
            setVelocity(0, getVelocity().y, 0);
            setYaw(getDormantDir().asRotation());
            setBodyYaw(getDormantDir().asRotation());
            setHeadYaw(getDormantDir().asRotation());
            setPitch(0);
        }

        if ((this.getTarget() == null || (this.getDormantPos().isPresent() && !this.getTarget().isAlive()))
                && getNavigation().isIdle()
                && !isAtDormantPos()
                && !isDormant()) {
            updateDormantPos();
        }

        if (this.getTarget() != null && this.getTarget().equals(this.getOwner())) {
            this.setTarget(null);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (getWorld() instanceof ServerWorld server) {
            server.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_IRON_GOLEM_STEP, SoundCategory.HOSTILE, 32.0f, 1.0f);
        }
    }

    @Override
    public void pushAwayFrom(Entity entity) {}

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return !this.isRemoved();
    }

    private boolean isAtDormantPos() {
        Optional<BlockPos> restPos = getDormantPos();
        if (restPos.isPresent()) {
            return restPos.get().isWithinDistance(this.getBlockPos(), 1.6f);
        }
        return false;
    }

    private void updateDormantPos() {
        if (getDormantPos().isPresent()) {
            BlockPos pos = getDormantPos().get();
            this.getNavigation().startMovingTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.7);
        } else {
            setDormantPos(getBlockPos());
        }
    }

    public double getAngleBetweenEntities(Entity first, Entity second) {
        return Math.atan2(second.getZ() - first.getZ(), second.getX() - first.getX()) * (180 / Math.PI) + 90;
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (damageSource.getAttacker() instanceof ServerPlayerEntity killer) {
            AdvancementsUtils.grantAdvancement(killer, "slay_vassal");
        }

        if (getOwnerUuid() != null) {
            if (!getWorld().isClient()) {
                for (int i = 0; i < 3; i++) {
                    net.minecraft.entity.ItemEntity item = new net.minecraft.entity.ItemEntity(
                            getWorld(),
                            getX(), getY() + 1, getZ(),
                            new ItemStack(net.minecraft.item.Items.NETHERITE_INGOT)
                    );

                    double angle = getWorld().getRandom().nextDouble() * Math.PI * 2;
                    double speed = 0.2 + getWorld().getRandom().nextDouble() * 0.3;
                    double upward = 0.2 + getWorld().getRandom().nextDouble() * 0.2;

                    item.setVelocity(
                            Math.cos(angle) * speed,
                            upward,
                            Math.sin(angle) * speed
                    );

                    getWorld().spawnEntity(item);
                }
            }
        }

        super.onDeath(damageSource);
    }
}