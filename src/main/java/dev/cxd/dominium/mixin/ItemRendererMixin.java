package dev.cxd.dominium.mixin;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.init.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @ At(value = "HEAD"), argsOnly = true)
    public BakedModel useItem(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModItems.DOMINIC_ORB) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.GROUND) {
            return ((ItemRendererAccessor) this).item$getModels().getModelManager().getModel(new ModelIdentifier(Dominium.MOD_ID, "dominic_orb_handheld", "inventory"));
        }
        if (stack.isOf(ModItems.SOUL_ORB) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.GROUND) {
            return ((ItemRendererAccessor) this).item$getModels().getModelManager().getModel(new ModelIdentifier(Dominium.MOD_ID, "soul_orb_handheld", "inventory"));
        }
        if (stack.isOf(ModItems.SOULTRAP_SPETUM) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.GROUND) {
            return ((ItemRendererAccessor) this).item$getModels().getModelManager().getModel(new ModelIdentifier(Dominium.MOD_ID, "soultrap_spetum_handheld", "inventory"));
        }
        return value;
    }
}