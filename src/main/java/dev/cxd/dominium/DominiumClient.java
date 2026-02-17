package dev.cxd.dominium;

import dev.cxd.dominium.client.entity.eternal_divinity_chains.EternalDivinityChainsModel;
import dev.cxd.dominium.client.entity.eternal_divinity_chains.EternalDivinityChainsRenderer;
import dev.cxd.dominium.client.entity.roofling.RooflingModel;
import dev.cxd.dominium.client.entity.roofling.RooflingRenderer;
import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ModClientPackets;
import dev.cxd.dominium.init.ModBlocks;
import dev.cxd.dominium.init.ModEntities;
import dev.cxd.dominium.init.ModParticles;
import dev.cxd.dominium.packet.GhostSyncPacket;
import dev.cxd.dominium.particle.AxisLockedParticles.XAxisParticle;
import dev.cxd.dominium.particle.AxisLockedParticles.YAxisParticle;
import dev.cxd.dominium.particle.AxisLockedParticles.ZAxisParticle;
import dev.cxd.dominium.particle.ChantDirectionalLockedParticle;
import dev.cxd.dominium.particle.DominicSymbolParticle;
import dev.cxd.dominium.particle.ExplosionBurstParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class DominiumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModClientPackets.initializeClientPackets();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOUL_GLASS, RenderLayer.getTranslucent());

        EntityRendererRegistry.register(ModEntities.ETERNAL_DIVINITY_CHAINS, EternalDivinityChainsRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(EternalDivinityChainsModel.ETERNAL_DIVINITY_CHAINS, EternalDivinityChainsModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.ROOFLING, RooflingRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(RooflingModel.ROOFLING, RooflingModel::getTexturedModelData);

        ParticleFactoryRegistry.getInstance().register(ModParticles.CHANT, ChantDirectionalLockedParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.DOMINIC_SYMBOL, DominicSymbolParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.DOMINIC_BALL, GlowParticle.ElectricSparkFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.X_GILDED_ONYX_PARTICLE, XAxisParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.Y_GILDED_ONYX_PARTICLE, YAxisParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.Z_GILDED_ONYX_PARTICLE, ZAxisParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.GEAR_GILDED_ONYX_PARTICLE, ExplosionBurstParticle.Factory::new);

        ClientPlayNetworking.registerGlobalReceiver(Dominium.GHOST_SYNC_PACKET_ID, (client, handler, buf, responseSender) -> {
            var ghosts = GhostSyncPacket.read(buf);

            client.execute(() -> {
                Dominium.GHOST_UUIDS.clear();
                Dominium.GHOST_UUIDS.addAll(ghosts);
                Dominium.LOGGER.info("Synced {} ghost player(s) from server", ghosts.size());
            });
        });
    }
}
