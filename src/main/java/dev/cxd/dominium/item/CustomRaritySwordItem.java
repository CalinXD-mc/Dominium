package dev.cxd.dominium.item;

import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class CustomRaritySwordItem extends SwordItem {
    private final ModRarities rarity;

    public CustomRaritySwordItem(Settings settings, int attackDamage, float attackSpeed, ToolMaterial toolMaterial, ModRarities rarity) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.rarity = rarity;
    }


    @Override
    public Text getName(ItemStack stack) {
        Text baseName = super.getName(stack);

        return baseName.copy().setStyle(Style.EMPTY.withColor(rarity.color));
    }
}
