package dev.cxd.dominium.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.packet.GhostSyncPacket;
import dev.cxd.dominium.utils.GhostManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GhostCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("dominium:ghost")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(GhostCommand::addGhost)))
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(GhostCommand::removeGhost)))
                .then(CommandManager.literal("list")
                        .executes(GhostCommand::listGhosts))
                .then(CommandManager.literal("clear")
                        .executes(GhostCommand::clearGhosts)));
    }

    private static int addGhost(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        UUID playerUuid = player.getUuid();

        if (Dominium.GHOST_UUIDS.contains(playerUuid)) {
            context.getSource().sendError(Text.literal("Player " + player.getName().getString() + " is already a ghost!"));
            return 0;
        }

        Dominium.GHOST_UUIDS.add(playerUuid);
        GhostManager.saveGhosts(Dominium.GHOST_UUIDS);

        GhostSyncPacket.sendToAll(context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendFeedback(() -> Text.literal("Added " + player.getName().getString() + " as a ghost. UUID: " + playerUuid), true);
        return 1;
    }

    private static int removeGhost(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        UUID playerUuid = player.getUuid();

        if (!Dominium.GHOST_UUIDS.contains(playerUuid)) {
            context.getSource().sendError(Text.literal("Player " + player.getName().getString() + " is not a ghost!"));
            return 0;
        }

        Dominium.GHOST_UUIDS.remove(playerUuid);
        GhostManager.saveGhosts(Dominium.GHOST_UUIDS);

        GhostSyncPacket.sendToAll(context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendFeedback(() -> Text.literal("Removed " + player.getName().getString() + " from ghosts. UUID: " + playerUuid), true);
        return 1;
    }

    private static int clearGhosts(CommandContext<ServerCommandSource> context) {
        int count = Dominium.GHOST_UUIDS.size();
        Dominium.GHOST_UUIDS.clear();
        GhostManager.saveGhosts(Dominium.GHOST_UUIDS);

        GhostSyncPacket.sendToAll(context.getSource().getServer().getPlayerManager().getPlayerList());

        context.getSource().sendFeedback(() -> Text.literal("Cleared " + count + " ghost player(s)."), true);
        return count;
    }

    private static int listGhosts(CommandContext<ServerCommandSource> context) {
        if (Dominium.GHOST_UUIDS.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.literal("No ghost players registered."), false);
            return 0;
        }

        context.getSource().sendFeedback(() -> Text.literal("Ghost Players (" + Dominium.GHOST_UUIDS.size() + "):"), false);

        for (UUID uuid : Dominium.GHOST_UUIDS) {
            ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(uuid);
            String playerName = player != null ? player.getName().getString() : "Offline/Unknown";
            context.getSource().sendFeedback(() -> Text.literal("  - " + playerName + " (" + uuid + ")"), false);
        }

        return Dominium.GHOST_UUIDS.size();
    }
}