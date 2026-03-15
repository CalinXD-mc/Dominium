package dev.cxd.dominium.utils;

import dev.cxd.dominium.Dominium;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdvancementsUtils {

    private static final Map<String, Identifier> CACHE = new HashMap<>();

    public static void grantAdvancement(ServerPlayerEntity player, String path) {
        Identifier id = CACHE.computeIfAbsent(path, p -> new Identifier(Dominium.MOD_ID, p));

        Advancement adv = Objects.requireNonNull(player.getServer())
                .getAdvancementLoader()
                .get(id);
        if (adv == null) return;

        AdvancementProgress progress = player.getAdvancementTracker().getProgress(adv);
        if (progress.isDone()) return;

        progress.getUnobtainedCriteria().forEach(c ->
                player.getAdvancementTracker().grantCriterion(adv, c));
    }
}
