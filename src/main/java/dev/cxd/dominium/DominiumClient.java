package dev.cxd.dominium;

import dev.cxd.dominium.client.entity.eternal_divinity_chains.EternalDivinityChainsModel;
import dev.cxd.dominium.client.entity.eternal_divinity_chains.EternalDivinityChainsRenderer;
import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ModClientPackets;
import dev.cxd.dominium.init.ModBlocks;
import dev.cxd.dominium.init.ModComponents;
import dev.cxd.dominium.init.ModEntities;
import dev.cxd.dominium.init.ModParticles;
import dev.cxd.dominium.item.signable.SoulboundContractSigned;
import dev.cxd.dominium.packet.GhostSyncPacket;
import dev.cxd.dominium.particle.ChantDirectionalLockedParticle;
import dev.cxd.dominium.particle.DominicSymbolParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class DominiumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModClientPackets.initializeClientPackets();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOUL_GLASS, RenderLayer.getTranslucent());

        EntityRendererRegistry.register(ModEntities.ETERNAL_DIVINITY_CHAINS, EternalDivinityChainsRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(EternalDivinityChainsModel.ETERNAL_DIVINITY_CHAINS, EternalDivinityChainsModel::getTexturedModelData);

        ParticleFactoryRegistry.getInstance().register(ModParticles.CHANT, ChantDirectionalLockedParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.DOMINIC_SYMBOL, DominicSymbolParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.DOMINIC_BALL, GlowParticle.ElectricSparkFactory::new);

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
