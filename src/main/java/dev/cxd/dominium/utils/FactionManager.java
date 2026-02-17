package dev.cxd.dominium.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.cxd.dominium.Dominium;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class FactionManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FACTION_FILE = new File("config/dominium_factions.json");
    private static Map<String, Faction> factions = new HashMap<>();
    private static Map<UUID, String> playerInvites = new HashMap<>();

    public static class Faction {
        public String name;
        public UUID owner;
        public Set<UUID> members;
        public Map<String, String> config;

        public Faction(String name, UUID owner) {
            this.name = name;
            this.owner = owner;
            this.members = new HashSet<>();
            this.members.add(owner);
            this.config = new HashMap<>();
            this.config.put("glowingFactionMembers", "false");
            this.config.put("allowPVP", "false");
            this.config.put("factionColor", "white");
        }
    }

    public static void loadFactions() {
        if (!FACTION_FILE.exists()) {
            factions = new HashMap<>();
            return;
        }

        try (FileReader reader = new FileReader(FACTION_FILE)) {
            Type type = new TypeToken<Map<String, Faction>>(){}.getType();
            factions = GSON.fromJson(reader, type);
            if (factions == null) {
                factions = new HashMap<>();
            }
            Dominium.LOGGER.info("Loaded {} factions", factions.size());
        } catch (IOException e) {
            Dominium.LOGGER.error("Failed to load factions", e);
            factions = new HashMap<>();
        }
    }

    public static void saveFactions() {
        try {
            FACTION_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(FACTION_FILE)) {
                GSON.toJson(factions, writer);
            }
            Dominium.LOGGER.info("Saved {} factions", factions.size());
        } catch (IOException e) {
            Dominium.LOGGER.error("Failed to save factions", e);
        }
    }

    public static boolean factionExists(String factionName) {
        return factions.containsKey(factionName);
    }

    public static void createFaction(String factionName, UUID owner) {
        Faction faction = new Faction(factionName, owner);
        factions.put(factionName, faction);
        saveFactions();
    }

    public static void disbandFaction(String factionName) {
        factions.remove(factionName);
        saveFactions();
    }

    public static void renameFaction(String oldName, String newName) {
        Faction faction = factions.remove(oldName);
        if (faction != null) {
            faction.name = newName;
            factions.put(newName, faction);
            saveFactions();
        }
    }

    public static boolean playerHasFaction(UUID playerUuid) {
        for (Faction faction : factions.values()) {
            if (faction.members.contains(playerUuid)) {
                return true;
            }
        }
        return false;
    }

    public static String getPlayerFaction(UUID playerUuid) {
        for (Faction faction : factions.values()) {
            if (faction.members.contains(playerUuid)) {
                return faction.name;
            }
        }
        return null;
    }

    public static boolean isOwner(String factionName, UUID playerUuid) {
        Faction faction = factions.get(factionName);
        return faction != null && faction.owner.equals(playerUuid);
    }

    public static void invitePlayer(String factionName, UUID playerUuid) {
        playerInvites.put(playerUuid, factionName);
    }

    public static String getPendingInvite(UUID playerUuid) {
        return playerInvites.get(playerUuid);
    }

    public static boolean hasInvite(UUID playerUuid, String factionName) {
        return factionName.equals(playerInvites.get(playerUuid));
    }

    public static void acceptInvite(UUID playerUuid) {
        String factionName = playerInvites.remove(playerUuid);
        if (factionName != null) {
            Faction faction = factions.get(factionName);
            if (faction != null) {
                faction.members.add(playerUuid);
                saveFactions();
            }
        }
    }

    public static void removePlayer(String factionName, UUID playerUuid) {
        Faction faction = factions.get(factionName);
        if (faction != null) {
            faction.members.remove(playerUuid);
            saveFactions();
        }
    }

    public static void configureFaction(String factionName, String option, String value) {
        Faction faction = factions.get(factionName);
        if (faction != null) {
            faction.config.put(option, value);
            saveFactions();
        }
    }

    public static Set<String> getAllFactions() {
        return factions.keySet();
    }

    public static int getFactionMemberCount(String factionName) {
        Faction faction = factions.get(factionName);
        return faction != null ? faction.members.size() : 0;
    }

    public static Set<UUID> getFactionMembers(String factionName) {
        Faction faction = factions.get(factionName);
        return faction != null ? new HashSet<>(faction.members) : new HashSet<>();
    }

    public static UUID getFactionOwner(String factionName) {
        Faction faction = factions.get(factionName);
        return faction != null ? faction.owner : null;
    }

    public static String getFactionConfig(String factionName, String option) {
        Faction faction = factions.get(factionName);
        return faction != null ? faction.config.get(option) : null;
    }

    public static boolean areSameFaction(UUID player1, UUID player2) {
        String faction1 = getPlayerFaction(player1);
        String faction2 = getPlayerFaction(player2);
        return faction1 != null && faction1.equals(faction2);
    }

    public static boolean isPVPAllowed(String factionName) {
        String allowPVP = getFactionConfig(factionName, "allowPVP");
        return "true".equals(allowPVP);
    }

    public static void setPvp(String factionName, boolean enabled) {
        Faction faction = factions.get(factionName);
        if (faction != null) {
            faction.config.put("allowPVP", String.valueOf(enabled));
            saveFactions();
        }
    }
}