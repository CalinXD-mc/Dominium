package dev.cxd.dominium.item;

import dev.cxd.dominium.entity.VassalEntity;
import dev.cxd.dominium.init.ModEntities;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class VassalItem extends Item {
    public VassalItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        BlockPos pos = ctx.getBlockPos();
        if (ctx.getWorld().getBlockState(pos.offset(ctx.getSide())).isAir() && ctx.getWorld().getBlockState(pos.offset(ctx.getSide()).offset(Direction.UP)).isAir() && ctx.getWorld().getBlockState(pos.offset(ctx.getSide()).offset(Direction.UP, 2)).isAir() || ctx.getWorld().getBlockState(pos.offset(ctx.getSide())).getBlock().equals(Blocks.WATER) && ctx.getWorld().getBlockState(pos.offset(ctx.getSide()).offset(Direction.UP)).getBlock().equals(Blocks.WATER) && ctx.getWorld().getBlockState(pos.offset(ctx.getSide()).offset(Direction.UP, 2)).getBlock().equals(Blocks.WATER)) {
            VassalEntity mould = new VassalEntity(ModEntities.VASSAL, ctx.getWorld());
            mould.refreshPositionAndAngles(pos.offset(ctx.getSide()), 0.0F, 0.0F);
            mould.setDormantDir(Direction.fromRotation(ctx.getPlayerYaw()).getOpposite());
            mould.setDormantPos(pos.offset(ctx.getSide()));
            mould.setActionState(0);

            assert player != null;

            mould.setOwner(player);
            ctx.getWorld().spawnEntity(mould);
            ctx.getStack().decrement(1);
        }

        return super.useOnBlock(ctx);
    }
}
