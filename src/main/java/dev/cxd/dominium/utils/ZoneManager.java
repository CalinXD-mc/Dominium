package dev.cxd.dominium.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ZoneManager {
    private static final Map<UUID, NullZone> ZONES = new HashMap<>();
    private static final Map<UUID, StoredEnchantments> STORED_ENCHANTS = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path saveFile;

    public static void initialize(MinecraftServer server) {
        saveFile = server.getSavePath(WorldSavePath.ROOT)
                .resolve("dominium_no_enchant_zones.json");
        loadZones();
        if (!Files.exists(saveFile)) {
            saveZones();
        }
    }

    public static void shutdown() {
        saveZones();
    }

    private static void loadZones() {
        if (saveFile == null || !Files.exists(saveFile)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(saveFile)) {
            Type type = new TypeToken<List<SerializedZone>>(){}.getType();
            List<SerializedZone> serializedZones = GSON.fromJson(reader, type);

            if (serializedZones != null) {
                for (SerializedZone sz : serializedZones) {
                    Box box = new Box(sz.minX, sz.minY, sz.minZ, sz.maxX, sz.maxY, sz.maxZ);
                    ZONES.put(sz.id, new NullZone(box, sz.dimension));
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load no-enchant zones: " + e.getMessage());
        }
    }

    private static void saveZones() {
        if (saveFile == null) {
            return;
        }

        try {
            Files.createDirectories(saveFile.getParent());

            List<SerializedZone> serializedZones = new ArrayList<>();
            for (Map.Entry<UUID, NullZone> entry : ZONES.entrySet()) {
                NullZone zone = entry.getValue();
                serializedZones.add(new SerializedZone(
                        entry.getKey(),
                        zone.box.minX, zone.box.minY, zone.box.minZ,
                        zone.box.maxX, zone.box.maxY, zone.box.maxZ,
                        zone.dimension
                ));
            }

            try (Writer writer = Files.newBufferedWriter(saveFile)) {
                GSON.toJson(serializedZones, writer);
            }
        } catch (Exception e) {
            System.err.println("Failed to save no-enchant zones: " + e.getMessage());
        }
    }

    public static void addZone(UUID id, Box box, String dimension) {
        ZONES.put(id, new NullZone(box, dimension));
        saveZones();
    }

    public static UUID removeZoneAt(BlockPos pos) {
        for (Map.Entry<UUID, NullZone> entry : ZONES.entrySet()) {
            if (entry.getValue().box.contains(pos.toCenterPos())) {
                ZONES.remove(entry.getKey());
                saveZones();
                return entry.getKey();
            }
        }
        return null;
    }

    public static Map<UUID, NullZone> getZones() {
        return new HashMap<>(ZONES);
    }

    public static void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            boolean inZone = isInAnyZone(player);
            UUID playerId = player.getUuid();

            if (inZone && !STORED_ENCHANTS.containsKey(playerId)) {
                storeAndRemoveEnchantments(player);
            } else if (!inZone && STORED_ENCHANTS.containsKey(playerId)) {
                restoreEnchantments(player);
            }
        }
    }

    private static boolean isInAnyZone(ServerPlayerEntity player) {
        String dimension = player.getEntityWorld().getRegistryKey().getValue().toString();
        for (NullZone zone : ZONES.values()) {
            if (zone.dimension.equals(dimension) && zone.box.contains(player.getPos())) {
                return true;
            }
        }
        return false;
    }

    private static void storeAndRemoveEnchantments(ServerPlayerEntity player) {
        StoredEnchantments stored = new StoredEnchantments();

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            Map<Enchantment, Integer> enchants = EnchantmentHelper.get(stack);
            if (enchants.isEmpty()) continue;

            stored.enchantments.put(
                    i,
                    new EnchantmentData(new HashMap<>(enchants), stack.getItem())
            );

            EnchantmentHelper.set(Collections.emptyMap(), stack);
        }

        if (!stored.enchantments.isEmpty()) {
            STORED_ENCHANTS.put(player.getUuid(), stored);
        }
    }


    private static void restoreEnchantments(ServerPlayerEntity player) {
        StoredEnchantments stored = STORED_ENCHANTS.remove(player.getUuid());
        if (stored == null) return;

        List<EnchantmentData> toRestore =
                new ArrayList<>(stored.enchantments.values());

        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.isEmpty()) continue;
            if (!EnchantmentHelper.get(stack).isEmpty()) continue;

            for (Iterator<EnchantmentData> it = toRestore.iterator(); it.hasNext();) {
                EnchantmentData data = it.next();

                if (stack.getItem() == data.originalItem) {
                    try {
                        EnchantmentHelper.set(data.enchantmentMap, stack);
                        it.remove();
                        break;
                    } catch (Exception e) {
                        player.sendMessage(
                                Text.literal("§6Warning: Could not restore enchantments to " +
                                        stack.getName().getString()),
                                false
                        );
                    }
                }
            }
        }

        if (!toRestore.isEmpty()) {
            player.sendMessage(
                    Text.literal("§c⚠ Warning: " + toRestore.size() +
                            " enchanted item(s) could not be restored"),
                    false
            );
        }
    }

    public static class NullZone {
        public final Box box;
        public final String dimension;

        public NullZone(Box box, String dimension) {
            this.box = box;
            this.dimension = dimension;
        }
    }

    private static class StoredEnchantments {
        final Map<Integer, EnchantmentData> enchantments = new HashMap<>();
    }

    private static class EnchantmentData {
        final Map<Enchantment, Integer> enchantmentMap;
        final net.minecraft.item.Item originalItem;

        EnchantmentData(Map<Enchantment, Integer> enchantmentMap,
                        net.minecraft.item.Item originalItem) {
            this.enchantmentMap = enchantmentMap;
            this.originalItem = originalItem;
        }
    }

    private static class SerializedZone {
        UUID id;
        double minX, minY, minZ;
        double maxX, maxY, maxZ;
        String dimension;

        SerializedZone(UUID id, double minX, double minY, double minZ,
                       double maxX, double maxY, double maxZ, String dimension) {
            this.id = id;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            this.dimension = dimension;
        }
    }
}

