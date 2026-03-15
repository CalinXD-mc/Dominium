package dev.cxd.dominium.client.lodestone_dark_magic_stuff;

import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.init.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class ModClientPackets {
    public static void initializeClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.PARTICLE_SPAWN_ID, (client, handler, buf, responseSender) -> {
            ParticleSpawnPacketData data = new ParticleSpawnPacketData(buf);
            ParticleSpawnClientHandler.handle(client, data);
        });

        ServerPlayNetworking.registerGlobalReceiver(ModPackets.SOULBOUND_ACTION_ID, (server, player, handler, buf, responseSender) -> {
            int action = buf.readInt();
            int slot = buf.readInt();

            server.execute(() -> {
                ItemStack stack = player.getInventory().getStack(slot);
                if (!stack.isOf(ModItems.SOULBOUND_CONTRACT_SIGNED)) return;

                String uuidStr = ModComponents.getVesselUuid(stack);
                if (uuidStr == null || uuidStr.isEmpty()) return;

                UUID targetUUID = UUID.fromString(uuidStr);
                PlayerEntity target = server.getPlayerManager().getPlayer(targetUUID);
                if (target == null) {
                    player.sendMessage(Text.literal("Target is not online.").formatted(Formatting.RED), true);
                    return;
                }

                ServerWorld serverWorld = (ServerWorld) player.getWorld();

                switch (action) {
                    case 0 -> {
                        DamageSource damageSource = new DamageSource(
                                serverWorld.getRegistryManager()
                                        .get(RegistryKeys.DAMAGE_TYPE)
                                        .entryOf(ModDamageTypes.DISOBEDIENCE_DAMAGE));
                        target.damage(damageSource, 4.0F);
                        player.sendMessage(Text.literal("Dealt 4 damage to " + target.getName().getString()).formatted(Formatting.GOLD), true);
                    }
                    case 1 -> {
                        player.teleport(target.getX(), target.getY(), target.getZ());
                        player.sendMessage(Text.literal("Teleported to " + target.getName().getString()).formatted(Formatting.GOLD), true);
                    }
                    case 2 -> {
                        target.setOnFireFor(5);
                        player.sendMessage(Text.literal("Set " + target.getName().getString() + " on fire").formatted(Formatting.GOLD), true);
                    }
                    case 3 -> {
                        target.addStatusEffect(new StatusEffectInstance(
                                (StatusEffect) ModStatusEffects.SOUL_STRAIN, 20 * 30, 0, false, false, true));

                        EternalDivinityChainsEntity chain = ModEntities.ETERNAL_DIVINITY_CHAINS.create(serverWorld);
                        if (chain != null) {
                            chain.setBoundPlayer(target.getUuid());
                            chain.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), 0, 0);
                            serverWorld.spawnEntity(chain);
                        }

                        serverWorld.spawnParticles(ParticleTypes.SOUL,
                                target.getX(), target.getY() + 1, target.getZ(),
                                30, 0.5, 0.5, 0.5, 0.01);

                        player.sendMessage(Text.literal("Trapped " + target.getName().getString() + " in chains").formatted(Formatting.GOLD), true);
                    }
                    case 4 -> {
                        target.removeStatusEffect(ModStatusEffects.SOUL_STRAIN);
                        player.sendMessage(Text.literal("Removed Soul Strain from " + target.getName().getString()).formatted(Formatting.GREEN), true);
                    }
                    case 5 -> {
                        String signerUuidStr = ModComponents.getVesselUuid(stack);
                        if (signerUuidStr == null || signerUuidStr.isEmpty()) {
                            player.sendMessage(Text.literal("This contract has no signer.").formatted(Formatting.RED), true);
                            return;
                        }

                        UUID signerUUID = UUID.fromString(signerUuidStr);
                        ServerPlayerEntity signer = server.getPlayerManager().getPlayer(signerUUID);
                        if (signer == null) {
                            player.sendMessage(Text.literal("The signer is not online.").formatted(Formatting.RED), true);
                            return;
                        }

                        ItemStack offhand = player.getOffHandStack();
                        if (!offhand.isOf(ModItems.CONTRACT) || ModComponents.getContractSigned(offhand) != 0) {
                            player.sendMessage(Text.literal("You need an unsigned contract in your offhand.").formatted(Formatting.RED), true);
                            return;
                        }

                        ItemStack signedStack = new ItemStack(ModItems.CONTRACT_SIGNED);
                        ModComponents.setVesselUuid(signedStack, signerUUID.toString());
                        ModComponents.setContractSigned(signedStack, 1);
                        ModComponents.setPlayerNameForSoulOwning(signedStack, signer.getName().getString());

                        player.setStackInHand(net.minecraft.util.Hand.OFF_HAND, signedStack);
                        player.sendMessage(Text.literal("Signed a contract for " + signer.getName().getString() + ".").formatted(Formatting.GOLD), true);
                    }
                }
            });
        });
    }
}
