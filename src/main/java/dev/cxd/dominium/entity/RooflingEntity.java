package dev.cxd.dominium.entity;

import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.init.ModSounds;
import dev.cxd.dominium.init.ModStatusEffects;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RooflingEntity extends HostileEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();

    private boolean isFrozen = false;

    public RooflingEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()
                && this.getWorld().getDimension().piglinSafe()
                && this.getY() <= ModConfig.NETHER_ROOF_HEIGHT - 1) {
            this.discard();
        }

        this.isFrozen = isPlayerLookingAt();

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
        }
    }

    @Override
    public void tickMovement() {
        if (isFrozen) {
            this.setVelocity(Vec3d.ZERO);
            this.velocityModified = true;
            this.bodyYaw = this.prevBodyYaw;
            this.headYaw = this.prevHeadYaw;
            this.setMovementSpeed(0.0F);
            return;
        }
        super.tickMovement();
    }

    @Override
    protected void mobTick() {
        if (isFrozen) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0F;
            this.forwardSpeed = 0.0F;
            return;
        }
        super.mobTick();
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (isFrozen) {
            return false;
        }
        return super.canTarget(target);
    }

    private void setupAnimationStates() {
        if (this.isMoving()) {
            this.walkAnimationState.start(this.age);
            this.idleAnimationState.stop();
        } else {
            this.walkAnimationState.stop();
            this.idleAnimationState.start(this.age);
        }
    }

    private boolean isMoving() {return this.getVelocity().horizontalLengthSquared() > 1.0E-6;}

    @Override
    public void setTarget(LivingEntity target) {
        LivingEntity previousTarget = this.getTarget();
        super.setTarget(target);

        if (target instanceof PlayerEntity && previousTarget != target) {
            this.playSound(SoundEvents.AMBIENT_CAVE.value(), 1.0F, 0.8F);
        }
    }

    private boolean isPlayerLookingAt() {
        PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 16.0);

        if (nearestPlayer == null) {
            return false;
        }

        Vec3d playerLook = nearestPlayer.getRotationVec(1.0F);

        Vec3d toEntity = this.getEyePos().subtract(nearestPlayer.getEyePos()).normalize();

        double dotProduct = playerLook.dotProduct(toEntity);

        return dotProduct > 0.995;
    }

    @Override
    public boolean tryAttack(net.minecraft.entity.Entity target) {
        if (isFrozen) return false;

        boolean attacked = super.tryAttack(target);

        if (!attacked) return false;

        if (target instanceof PlayerEntity player) {
            boolean hasEtherealNecklace =
                    dev.emi.trinkets.api.TrinketsApi.getTrinketComponent(player)
                            .map(comp -> comp.isEquipped(
                                    stack -> stack.getItem() instanceof dev.cxd.dominium.item.necklaces.EtherealNecklaceItem
                            ))
                            .orElse(false);

            if (hasEtherealNecklace) {
                player.kill();
                return true;
            }
        }

        if (target instanceof LivingEntity livingTarget) {
            livingTarget.addStatusEffect(
                    new StatusEffectInstance(ModStatusEffects.PHASING, 100, 0)
            );
        }

        return true;
    }


    @Override protected SoundEvent getAmbientSound() {return ModSounds.ROOFLING_AMBIENBT;}

    @Override protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) {return ModSounds.ROOFLING_AMBIENBT;}

    @Override protected SoundEvent getDeathSound() {return ModSounds.ROOFLING_AMBIENBT;}

    @Override public boolean shouldRenderName() {return false;}

    @Override public Text getCustomName() {return null;}

    @Override public boolean hasCustomName() {return false;}

    @Override public void setCustomName(Text name) {}

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0F)
                .add(EntityAttributes.GENERIC_ARMOR, 5.0F)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 200.0F)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0F);
    }

    @Override public boolean isCustomNameVisible() { return false; }
}