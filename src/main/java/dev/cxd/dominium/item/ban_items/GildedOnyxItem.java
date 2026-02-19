package dev.cxd.dominium.item.ban_items;

import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ScreenParticleEffects;
import dev.cxd.dominium.utils.CanBanPeopleItem;
import dev.cxd.dominium.utils.MarkableItem;
import dev.cxd.dominium.utils.ModRarities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import team.lodestar.lodestone.systems.particle.screen.ScreenParticleHolder;

import java.util.List;

public class GildedOnyxItem extends SwordItem implements CanBanPeopleItem, MarkableItem, ParticleEmitterHandler.ItemParticleSupplier {
    private final ModRarities rarity;

    public GildedOnyxItem(Settings settings, int attackDamage, float attackSpeed, ToolMaterial toolMaterial, ModRarities rarity) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.rarity = rarity;
    }


    @Override
    public Text getName(ItemStack stack) {
        Text baseName = super.getName(stack);

        return baseName.copy().setStyle(Style.EMPTY.withColor(rarity.color));
    }

    @Override
    public void setMark(ItemStack stack, String mark) {
        stack.getOrCreateNbt().putString("Mark", mark);
    }

    @Override
    public String getMark(ItemStack stack) {
        return stack.hasNbt() ? stack.getNbt().getString("Mark") : "";
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String mark = getMark(stack);
        if (!mark.isEmpty()) {
            tooltip.add(Text.literal(mark).formatted(Formatting.GOLD));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void spawnEarlyParticles(ScreenParticleHolder target, World level, float partialTick, ItemStack stack, float x, float y) {
        ScreenParticleEffects.spawnGildedOnyxParticles(target, level, 0.55f, partialTick);
    }
}
