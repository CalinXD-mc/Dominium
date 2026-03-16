package dev.cxd.dominium;

import dev.cxd.dominium.client.entity.eternal_divinity_chains.EternalDivinityChainsModel;
import dev.cxd.dominium.client.entity.eternal_divinity_chains.EternalDivinityChainsRenderer;
import dev.cxd.dominium.client.entity.roofling.RooflingModel;
import dev.cxd.dominium.client.entity.roofling.RooflingRenderer;
import dev.cxd.dominium.client.entity.vassal.VassalModel;
import dev.cxd.dominium.client.entity.vassal.VassalRenderer;
import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ModClientPackets;
import dev.cxd.dominium.client.screen.SoulboundContractScreen;
import dev.cxd.dominium.init.*;
import dev.cxd.dominium.packet.GhostSyncPacket;
import dev.cxd.dominium.particle.AxisLockedParticles.XAxisParticle;
import dev.cxd.dominium.particle.AxisLockedParticles.YAxisParticle;
import dev.cxd.dominium.particle.AxisLockedParticles.ZAxisParticle;
import dev.cxd.dominium.particle.ChantDirectionalLockedParticle;
import dev.cxd.dominium.particle.DominicSymbolParticle;
import dev.cxd.dominium.particle.ExplosionBurstParticle;
import dev.cxd.dominium.particle.ObeliskParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class DominiumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModClientPackets.initializeClientPackets();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOUL_GLASS, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OBELISK, RenderLayer.getCutout());

        EntityRendererRegistry.register(ModEntities.ETERNAL_DIVINITY_CHAINS, EternalDivinityChainsRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(EternalDivinityChainsModel.ETERNAL_DIVINITY_CHAINS, EternalDivinityChainsModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.ROOFLING, RooflingRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(RooflingModel.ROOFLING, RooflingModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.VASSAL, VassalRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(VassalModel.VASSAL, VassalModel::getTexturedModelData);

        ParticleFactoryRegistry.getInstance().register(ModParticles.CHANT, ChantDirectionalLockedParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.DOMINIC_SYMBOL, DominicSymbolParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.DOMINIC_BALL, GlowParticle.ElectricSparkFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.X_GILDED_ONYX_PARTICLE, XAxisParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.Y_GILDED_ONYX_PARTICLE, YAxisParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.Z_GILDED_ONYX_PARTICLE, ZAxisParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.GEAR_GILDED_ONYX_PARTICLE, ExplosionBurstParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.OBELISK_PARTICLE, ObeliskParticle.Factory::new);

        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OPEN_SOULBOUND_SCREEN_ID, (client, handler, buf, responseSender) -> {
            int slot = buf.readInt();
            client.execute(() -> client.setScreen(new SoulboundContractScreen(slot)));
        });

        ClientPlayNetworking.registerGlobalReceiver(Dominium.GHOST_SYNC_PACKET_ID, (client, handler, buf, responseSender) -> {
            var ghosts = GhostSyncPacket.read(buf);

            client.execute(() -> {
                Dominium.GHOST_UUIDS.clear();
                Dominium.GHOST_UUIDS.addAll(ghosts);
                Dominium.LOGGER.info("Synced {} ghost player(s) from server", ghosts.size());
            });
        });


        MinecraftClient mc = MinecraftClient.getInstance();

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {

            if (mc.player == null || ModClientPackets.lastVesselPos == null) return;
            if (!mc.player.hasStatusEffect(ModStatusEffects.SOUL_DEBT)) {
                ModClientPackets.lastVesselPos = null;
                return;
            }

            Vec3d playerPos = mc.player.getPos();
            Vec3d toVessel = ModClientPackets.lastVesselPos.subtract(playerPos).normalize();
            Vec3d lookDir = mc.player.getRotationVec(tickDelta);
            double dot = lookDir.dotProduct(toVessel);

            int screenW = mc.getWindow().getScaledWidth();
            int screenH = mc.getWindow().getScaledHeight();

            if (dot > 0.975) {
                drawContext.drawCenteredTextWithShadow(
                        mc.textRenderer,
                        Text.literal("§9You feel something affecting your soul in this direction..."),
                        screenW / 2,
                        screenH - screenH / 6,
                        0x88FFFFFF
                );
            }

            if (ModClientPackets.claimOwnerName != null) {
                if (System.currentTimeMillis() > ModClientPackets.claimPresenceUntil) {
                    ModClientPackets.claimOwnerName = null;
                } else {
                    int sw = mc.getWindow().getScaledWidth();
                    drawContext.drawCenteredTextWithShadow(
                            mc.textRenderer,
                            Text.literal("§3Claimed by §b" + ModClientPackets.claimOwnerName),
                            sw / 2,
                            10,
                            0xFFFFFF
                    );
                }
            }
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (ModClientPackets.claimRenderCenter == null) return;
            if (System.currentTimeMillis() > ModClientPackets.claimRenderUntil) {
                ModClientPackets.claimRenderCenter = null;
                return;
            }

            BlockPos center = ModClientPackets.claimRenderCenter;
            int r = ModClientPackets.claimRadius;
            int h = ModClientPackets.claimHeight;

            net.minecraft.client.render.Camera camera = context.camera();
            net.minecraft.client.util.math.MatrixStack matrices = context.matrixStack();
            matrices.push();
            matrices.translate(
                    center.getX() + 0.5 - camera.getPos().x,
                    center.getY() + 0.5 - camera.getPos().y,
                    center.getZ() + 0.5 - camera.getPos().z
            );

            net.minecraft.client.render.WorldRenderer.drawBox(
                    matrices,
                    context.consumers().getBuffer(net.minecraft.client.render.RenderLayer.LINES),
                    -r - 0.5, -h - 0.5, -r - 0.5,
                    r + 0.5,  h + 0.5,  r + 0.5,
                    0.0f, 0.8f, 1.0f, 0.5f
            );

            matrices.pop();
        });
    }
}
