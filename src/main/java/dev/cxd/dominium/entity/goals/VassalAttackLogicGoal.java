package dev.cxd.dominium.entity.goals;

import dev.cxd.dominium.entity.VassalEntity;
import dev.cxd.dominium.init.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class VassalAttackLogicGoal extends Goal {
    private final VassalEntity mould;

    public VassalAttackLogicGoal(VassalEntity entity) {
        this.mould = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        return this.mould.getAttackState() == 2
                && !this.mould.isDormant()
                && this.mould.getTarget() != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.mould.getAttackState() == 2
                && !this.mould.isDormant()
                && this.mould.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mould.getTarget();
        if (target == null) return;

        int ticks = this.mould.dashSlashTicks;
        this.mould.lookAtEntity(target, 80.0F, 80.0F);

        if (ticks == 2) {
            this.mould.playSound(ModSounds.VASSAL_ATTACK, 1.0F, 0.8F);
            List<LivingEntity> nearbyEntities = this.mould.getWorld().getEntitiesByClass(
                    LivingEntity.class,
                    this.mould.getBoundingBox().expand(3.5, 1.0, 3.5),
                    livingEntity -> livingEntity != this.mould
                            && livingEntity != this.mould.getOwner()
                            && !(livingEntity instanceof VassalEntity other
                            && other.getOwner() == this.mould.getOwner())
            );
            for (LivingEntity entity : nearbyEntities) {
                entity.setVelocity(entity.getVelocity().x, 0.6, entity.getVelocity().z);
                entity.velocityModified = true;
                disableShield(entity);
                this.mould.tryAttack(entity);
            }
        }

        if (ticks == 9 || ticks == 12 || ticks == 15) {
            Vec3d current = this.mould.getVelocity();
            Vec3d toward = new Vec3d(
                    target.getX() - this.mould.getX(),
                    0.0,
                    target.getZ() - this.mould.getZ()
            ).normalize().multiply(1.0).add(current);
            this.mould.setVelocity(toward.x, 0.0, toward.z);
        }

        if (ticks == 10 || ticks == 13 || ticks == 15) {
            this.mould.playSound(ModSounds.VASSAL_ATTACK, 1.0F, 1.0F);
            List<LivingEntity> entities = this.mould.getWorld().getEntitiesByClass(
                    LivingEntity.class,
                    this.mould.getBoundingBox().expand(4.0, 4.0, 4.0),
                    livingEntity -> {
                        if (livingEntity == this.mould || livingEntity == this.mould.getOwner()) {
                            return false;
                        }
                        if (livingEntity instanceof VassalEntity other
                                && other.getOwner() == this.mould.getOwner()) {
                            return false;
                        }
                        return this.mould.distanceTo(livingEntity) <= 4.0F + livingEntity.getWidth() / 2.0F
                                && livingEntity.getY() <= this.mould.getY() + 5.0;
                    }
            );
            for (LivingEntity entity : entities) {
                Vec3d knockDir = entity.getPos().subtract(this.mould.getPos()).normalize().negate();
                entity.takeKnockback(1.0, knockDir.x, knockDir.z);
                disableShield(entity);
                this.mould.tryAttack(entity);
            }
        }
    }

    private void disableShield(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            player.disableShield(true);
        }
    }
}