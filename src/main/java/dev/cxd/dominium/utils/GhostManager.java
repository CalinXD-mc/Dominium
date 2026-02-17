package dev.cxd.dominium.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.cxd.dominium.Dominium;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GhostManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File GHOST_FILE = new File(
            FabricLoader.getInstance().getConfigDir().toFile(),
            "dominium_ghosts.json"
    );

    public static Set<UUID> loadGhosts() {
        if (!GHOST_FILE.exists()) {
            return new HashSet<>();
        }

        try (FileReader reader = new FileReader(GHOST_FILE)) {
            Type type = new TypeToken<HashSet<UUID>>(){}.getType();
            Set<UUID> loaded = GSON.fromJson(reader, type);
            return loaded != null ? loaded : new HashSet<>();
        } catch (IOException e) {
            Dominium.LOGGER.error("Failed to load ghost UUIDs", e);
            return new HashSet<>();
        }
    }

    public static void saveGhosts(Set<UUID> ghosts) {
        try (FileWriter writer = new FileWriter(GHOST_FILE)) {
            GSON.toJson(ghosts, writer);
        } catch (IOException e) {
            Dominium.LOGGER.error("Failed to save ghost UUIDs", e);
        }
    }
}