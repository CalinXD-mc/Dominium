package dev.cxd.dominium.utils;

import net.minecraft.item.ItemStack;

public interface MarkableItem {
    void setMark(ItemStack stack, String mark);
    String getMark(ItemStack stack);
}
