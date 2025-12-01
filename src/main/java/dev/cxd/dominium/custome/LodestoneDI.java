package dev.cxd.dominium.custome;

import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.init.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;

public class LodestoneDI {
    public static class ParticleSpawnerItem extends Item {
        public ParticleSpawnerItem(Settings settings) {
            super(settings);
        }

        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            World world = context.getWorld();
            PlayerEntity player = context.getPlayer();
            BlockPos blockPos = context.getBlockPos();
            Hand hand = context.getHand();

            if (!world.isClient() && player != null) {
                EternalDivinityChainsEntity chains =
                        new EternalDivinityChainsEntity(ModEntities.ETERNAL_DIVINITY_CHAINS, world);

                // set position first
                chains.updatePosition(
                        blockPos.getX() + 0.5,
                        blockPos.getY(),
                        blockPos.getZ() + 0.5
                );

                // spawn into world FIRST
                world.spawnEntity(chains);

                // then bind player
                chains.setBoundPlayer(player.getUuid());

                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    }
}

//                Vec3d originalPosition = Vec3d.ofCenter(blockPos);
//
//                Color startColor = new Color(248, 209, 109);
//                Color endColor = new Color(211, 149, 77);
//
//                // Use the data-only packet class
//                ParticleSpawnPacketData packetData = new ParticleSpawnPacketData(
//                        originalPosition, startColor.getRGB(), endColor.getRGB(), "spetum_kill_particle"
//                );
//
//                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
//                packetData.toBytes(buf);
//
//                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
//                ServerPlayNetworking.send(serverPlayer, ModPackets.PARTICLE_SPAWN_ID, buf);
//
//                return ActionResult.SUCCESS;