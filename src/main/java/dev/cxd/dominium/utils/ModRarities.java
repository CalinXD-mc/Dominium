package dev.cxd.dominium.utils;

import net.minecraft.util.Formatting;

import net.minecraft.text.TextColor;

public enum ModRarities {
    CONTRACT(TextColor.fromRgb(0x5BE3E8)),
    SOULBOUND_CONTRACT(TextColor.fromRgb(0xD8AE58)),
    DOMINIC(TextColor.fromRgb(0xF8D16D)),
    FIXED_DOMINIC(TextColor.fromRgb(0xF7E2AD)),
    DOMINIC_EFFIGY(TextColor.fromRgb(0xA2A2A2)),
    UNDEAD(TextColor.fromRgb(0x3F7F47)),
    ARACHNID(TextColor.fromRgb(0x820000)),
    ETHEREAL(TextColor.fromRgb(0x7FFFFF));

    public final TextColor color;

    ModRarities(TextColor color) {
        this.color = color;
    }
}
