package dev.cxd.dominium.entity.goals;

import dev.cxd.dominium.entity.VassalEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class VassalDashSlashGoal extends Goal {
    private final VassalEntity mould;
    private int scrunkly;
    private double targetX;
    private double targetY;
    private double targetZ;

    public VassalDashSlashGoal(VassalEntity entity) {
        this.mould = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    public boolean canStart() {
        LivingEntity target = this.mould.getTarget();
        return target != null && target.isAlive() && !this.mould.isDormant();
    }

    public void start() {
        this.scrunkly = 0;
    }

    public void stop() {
        this.mould.getNavigation().stop();
    }

    public void tick() {
        LivingEntity target = this.mould.getTarget();
        if (target == null) return;

        double distance = this.mould.squaredDistanceTo(this.targetX, this.targetY, this.targetZ);
        if (--this.scrunkly <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
                || target.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0)
                || this.mould.getNavigation().isIdle()) {
            this.targetX = target.getX();
            this.targetY = target.getY();
            this.targetZ = target.getZ();
            this.scrunkly = 4 + this.mould.getRandom().nextInt(6);
            if (distance > 1024.0) {
                this.scrunkly += 10;
            } else if (distance > 256.0) {
                this.scrunkly += 5;
            }

            if (!this.mould.getNavigation().startMovingTo(target, 0.5)) {
                this.scrunkly += 15;
            }
        }

        distance = this.mould.squaredDistanceTo(this.targetX, this.targetY, this.targetZ);
        double heightDiff = target.getY() - this.mould.getY();

        boolean facingTarget = Math.abs(MathHelper.wrapDegrees(
                this.mould.getAngleBetweenEntities(target, this.mould) - (double) this.mould.getYaw())) < 35.0;

        if (heightDiff >= -1.0 && heightDiff <= 8.0 && facingTarget) {
            if (heightDiff > 2.5 && this.mould.isOnGround()) {
                double horizontalDist = Math.sqrt(
                        (target.getX() - this.mould.getX()) * (target.getX() - this.mould.getX()) +
                                (target.getZ() - this.mould.getZ()) * (target.getZ() - this.mould.getZ())
                );

                double jumpY = Math.min(0.5 + heightDiff * 0.12, 0.9);
                double jumpXZ = horizontalDist > 0.1 ? Math.min(0.4, 1.5 / horizontalDist) : 0.25;

                Vec3d dir = new Vec3d(
                        target.getX() - this.mould.getX(),
                        0,
                        target.getZ() - this.mould.getZ()
                ).normalize().multiply(jumpXZ);
                this.mould.setVelocity(dir.x, jumpY, dir.z);
                this.mould.velocityModified = true;
            }

            this.mould.setAttackState(2);
            this.mould.dashSlashTicks = 0;
        }
    }
}