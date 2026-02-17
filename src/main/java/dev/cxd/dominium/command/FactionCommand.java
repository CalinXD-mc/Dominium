package dev.cxd.dominium.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.cxd.dominium.utils.FactionManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class FactionCommand {

    private static final SuggestionProvider<ServerCommandSource> FACTION_SUGGESTIONS = (context, builder) -> {
        return CommandSource.suggestMatching(FactionManager.getAllFactions(), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        // Main command with full name
        var factionCommand = CommandManager.literal("dominium:faction")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("create")
                        .then(CommandManager.argument("faction_name", StringArgumentType.string())
                                .executes(FactionCommand::createFaction)))
                .then(CommandManager.literal("invite")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(FactionCommand::inviteToFaction)))
                .then(CommandManager.literal("accept")
                        .executes(FactionCommand::acceptInvite))
                .then(CommandManager.literal("leave")
                        .executes(FactionCommand::leaveFaction))
                .then(CommandManager.literal("kick")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(FactionCommand::kickPlayer)))
                .then(CommandManager.literal("rename")
                        .then(CommandManager.argument("new_name", StringArgumentType.string())
                                .executes(FactionCommand::renameFaction)))
                .then(CommandManager.literal("pvp")
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(FactionCommand::togglePvp)))
                .then(CommandManager.literal("message")
                        .then(CommandManager.argument("message", StringArgumentType.greedyString())
                                .executes(FactionCommand::sendFactionMessage)))
                .then(CommandManager.literal("disband")
                        .executes(FactionCommand::disbandFaction))
                .then(CommandManager.literal("list")
                        .executes(FactionCommand::listFactions))
                .then(CommandManager.literal("info")
                        .then(CommandManager.argument("faction_name", StringArgumentType.string())
                                .suggests(FACTION_SUGGESTIONS)
                                .executes(FactionCommand::factionInfo)));

        dispatcher.register(factionCommand);

        // Register alias
        dispatcher.register(CommandManager.literal("faction")
                .requires(source -> source.hasPermissionLevel(2))
                .redirect(dispatcher.register(factionCommand)));
    }

    private static int createFaction(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String factionName = StringArgumentType.getString(context, "faction_name");
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        UUID playerUuid = player.getUuid();

        if (FactionManager.playerHasFaction(playerUuid)) {
            context.getSource().sendError(Text.literal("You are already in a faction!"));
            return 0;
        }

        if (FactionManager.factionExists(factionName)) {
            context.getSource().sendError(Text.literal("Faction '" + factionName + "' already exists!"));
            return 0;
        }

        FactionManager.createFaction(factionName, playerUuid);
        context.getSource().sendFeedback(() -> Text.literal("Created faction '" + factionName + "' successfully!"), true);
        return 1;
    }

    private static int inviteToFaction(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity inviter = context.getSource().getPlayerOrThrow();
        ServerPlayerEntity invitee = EntityArgumentType.getPlayer(context, "player");
        UUID inviterUuid = inviter.getUuid();
        UUID inviteeUuid = invitee.getUuid();

        String factionName = FactionManager.getPlayerFaction(inviterUuid);
        if (factionName == null) {
            context.getSource().sendError(Text.literal("You are not in a faction!"));
            return 0;
        }

        if (!FactionManager.isOwner(factionName, inviterUuid)) {
            context.getSource().sendError(Text.literal("Only the faction owner can invite players!"));
            return 0;
        }

        if (FactionManager.playerHasFaction(inviteeUuid)) {
            context.getSource().sendError(Text.literal(invitee.getName().getString() + " is already in a faction!"));
            return 0;
        }

        FactionManager.invitePlayer(factionName, inviteeUuid);
        context.getSource().sendFeedback(() -> Text.literal("Invited " + invitee.getName().getString() + " to faction '" + factionName + "'"), true);
        invitee.sendMessage(Text.literal("You have been invited to faction '" + factionName + "'! Use /faction accept to join."));
        return 1;
    }

    private static int acceptInvite(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        UUID playerUuid = player.getUuid();

        if (FactionManager.playerHasFaction(playerUuid)) {
            context.getSource().sendError(Text.literal("You are already in a faction!"));
            return 0;
        }

        String factionName = FactionManager.getPendingInvite(playerUuid);
        if (factionName == null) {
            context.getSource().sendError(Text.literal("You have no pending faction invites!"));
            return 0;
        }

        FactionManager.acceptInvite(playerUuid);
        context.getSource().sendFeedback(() -> Text.literal("You have joined faction '" + factionName + "'!"), false);

        // Notify faction members
        for (UUID memberUuid : FactionManager.getFactionMembers(factionName)) {
            ServerPlayerEntity member = context.getSource().getServer().getPlayerManager().getPlayer(memberUuid);
            if (member != null && !member.getUuid().equals(playerUuid)) {
                member.sendMessage(Text.literal(player.getName().getString() + " has joined the faction!"));
            }
        }

        return 1;
    }

    private static int leaveFaction(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        UUID playerUuid = player.getUuid();

        String factionName = FactionManager.getPlayerFaction(playerUuid);
        if (factionName == null) {
            context.getSource().sendError(Text.literal("You are not in a faction!"));
            return 0;
        }

        if (FactionManager.isOwner(factionName, playerUuid)) {
            context.getSource().sendError(Text.literal("You cannot leave as the faction owner! Transfer ownership or disband the faction."));
            return 0;
        }

        FactionManager.removePlayer(factionName, playerUuid);
        context.getSource().sendFeedback(() -> Text.literal("You have left faction '" + factionName + "'"), false);

        for (UUID memberUuid : FactionManager.getFactionMembers(factionName)) {
            ServerPlayerEntity member = context.getSource().getServer().getPlayerManager().getPlayer(memberUuid);
            if (member != null) {
                member.sendMessage(Text.literal(player.getName().getString() + " has left the faction."));
            }
        }

        return 1;
    }

    private static int kickPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity kicker = context.getSource().getPlayerOrThrow();
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        UUID kickerUuid = kicker.getUuid();
        UUID targetUuid = target.getUuid();

        String factionName = FactionManager.getPlayerFaction(kickerUuid);
        if (factionName == null) {
            context.getSource().sendError(Text.literal("You are not in a faction!"));
            return 0;
        }

        if (!FactionManager.isOwner(factionName, kickerUuid)) {
            context.getSource().sendError(Text.literal("Only the faction owner can kick players!"));
            return 0;
        }

        if (kickerUuid.equals(targetUuid)) {
            context.getSource().sendError(Text.literal("You cannot kick yourself!"));
            return 0;
        }

        String targetFaction = FactionManager.getPlayerFaction(targetUuid);
        if (!factionName.equals(targetFaction)) {
            context.getSource().sendError(Text.literal(target.getName().getString() + " is not in your faction!"));
            return 0;
        }

        FactionManager.removePlayer(factionName, targetUuid);
        context.getSource().sendFeedback(() -> Text.literal("Kicked " + target.getName().getString() + " from the faction"), true);
        target.sendMessage(Text.literal("You have been kicked from faction '" + factionName + "'"));

        return 1;
    }

    private static int renameFaction(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        UUID playerUuid = player.getUuid();
        String newName = StringArgumentType.getString(context, "new_name");

        String factionName = FactionManager.getPlayerFaction(playerUuid);
        if (factionName == null) {
            context.getSource().sendError(Text.literal("You are not in a faction!"));
            return 0;
        }

        if (!FactionManager.isOwner(factionName, playerUuid)) {
            context.getSource().sendError(Text.literal("Only the faction owner can rename the faction!"));
            return 0;
        }

        if (FactionManager.factionExists(newName)) {
            context.getSource().sendError(Text.literal("Faction '" + newName + "' already exists!"));
            return 0;
        }

        String oldName = factionName;
        FactionManager.renameFaction(factionName, newName);
        context.getSource().sendFeedback(() -> Text.literal("Renamed faction from '" + oldName + "' to '" + newName + "'"), true);

        for (UUID memberUuid : FactionManager.getFactionMembers(newName)) {
            ServerPlayerEntity member = context.getSource().getServer().getPlayerManager().getPlayer(memberUuid);
            if (member != null && !member.getUuid().equals(playerUuid)) {
                member.sendMessage(Text.literal("Your faction has been renamed to '" + newName + "'"));
            }
        }

        return 1;
    }

    private static int togglePvp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        UUID playerUuid = player.getUuid();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");

        String factionName = FactionManager.getPlayerFaction(playerUuid);
        if (factionName == null) {
            context.getSource().sendError(Text.literal("You are not in a faction!"));
            return 0;
        }

        if (!FactionManager.isOwner(factionName, playerUuid)) {
            context.getSource().sendError(Text.literal("Only the faction owner can toggle PVP!"));
            return 0;
        }

        FactionManager.setPvp(factionName, enabled);
        context.getSource().sendFeedback(() -> Text.literal("Faction PVP has been " + (enabled ? "enabled" : "disabled") + "!"), true);

        // Notify all faction members
        for (UUID memberUuid : FactionManager.getFactionMembers(factionName)) {
            ServerPlayerEntity member = context.getSource().getServer().getPlayerManager().getPlayer(memberUuid);
            if (member != null && !member.getUuid().equals(playerUuid)) {
                member.sendMessage(Text.literal("Faction PVP has been " + (enabled ? "enabled" : "disabled") + " by the faction owner."));
            }
        }

        return 1;
    }

    private static int sendFactionMessage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sender = context.getSource().getPlayerOrThrow();
        UUID senderUuid = sender.getUuid();
        String message = StringArgumentType.getString(context, "message");

        String factionName = FactionManager.getPlayerFaction(senderUuid);
        if (factionName == null) {
            context.getSource().sendError(Text.literal("You are not in a faction!"));
            return 0;
        }

        String colorStr = FactionManager.getFactionConfig(factionName, "factionColor");
        Formatting color = colorStr != null ? Formatting.byName(colorStr) : Formatting.WHITE;
        if (color == null) color = Formatting.WHITE;

        Text formattedMessage = Text.literal("[")
                .append(Text.literal(factionName).formatted(color))
                .append("] ")
                .append(Text.literal("<" + sender.getName().getString() + "> "))
                .append(Text.literal(message));

        for (UUID memberUuid : FactionManager.getFactionMembers(factionName)) {
            ServerPlayerEntity member = context.getSource().getServer().getPlayerManager().getPlayer(memberUuid);
            if (member != null) {
                member.sendMessage(formattedMessage);
            }
        }

        return 1;
    }

    private static int disbandFaction(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        UUID playerUuid = player.getUuid();

        String factionName = FactionManager.getPlayerFaction(playerUuid);
        if (factionName == null) {
            context.getSource().sendError(Text.literal("You are not in a faction!"));
            return 0;
        }

        if (!FactionManager.isOwner(factionName, playerUuid)) {
            context.getSource().sendError(Text.literal("Only the faction owner can disband the faction!"));
            return 0;
        }

        // Notify all members
        for (UUID memberUuid : FactionManager.getFactionMembers(factionName)) {
            ServerPlayerEntity member = context.getSource().getServer().getPlayerManager().getPlayer(memberUuid);
            if (member != null && !member.getUuid().equals(playerUuid)) {
                member.sendMessage(Text.literal("Your faction '" + factionName + "' has been disbanded."));
            }
        }

        FactionManager.disbandFaction(factionName);
        context.getSource().sendFeedback(() -> Text.literal("Disbanded faction '" + factionName + "' successfully!"), true);
        return 1;
    }

    private static int listFactions(CommandContext<ServerCommandSource> context) {
        var factions = FactionManager.getAllFactions();

        if (factions.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.literal("No factions exist."), false);
            return 0;
        }

        context.getSource().sendFeedback(() -> Text.literal("Factions (" + factions.size() + "):"), false);

        for (String factionName : factions) {
            int memberCount = FactionManager.getFactionMemberCount(factionName);
            String colorStr = FactionManager.getFactionConfig(factionName, "factionColor");
            Formatting color = colorStr != null ? Formatting.byName(colorStr) : Formatting.WHITE;
            if (color == null) color = Formatting.WHITE;

            Formatting finalColor = color;
            context.getSource().sendFeedback(() -> Text.literal("  - ")
                    .append(Text.literal(factionName).formatted(finalColor))
                    .append(Text.literal(" (" + memberCount + " members)")), false);
        }

        return factions.size();
    }

    private static int factionInfo(CommandContext<ServerCommandSource> context) {
        String factionName = StringArgumentType.getString(context, "faction_name");

        if (!FactionManager.factionExists(factionName)) {
            context.getSource().sendError(Text.literal("Faction '" + factionName + "' does not exist!"));
            return 0;
        }

        var members = FactionManager.getFactionMembers(factionName);
        UUID owner = FactionManager.getFactionOwner(factionName);

        String colorStr = FactionManager.getFactionConfig(factionName, "factionColor");
        Formatting color = colorStr != null ? Formatting.byName(colorStr) : Formatting.WHITE;
        if (color == null) color = Formatting.WHITE;

        boolean pvpEnabled = FactionManager.isPVPAllowed(factionName);

        Formatting finalColor = color;
        context.getSource().sendFeedback(() -> Text.literal("Faction: ").append(Text.literal(factionName).formatted(finalColor)), false);

        ServerPlayerEntity ownerPlayer = context.getSource().getServer().getPlayerManager().getPlayer(owner);
        String ownerName = ownerPlayer != null ? ownerPlayer.getName().getString() : "Offline/Unknown";
        context.getSource().sendFeedback(() -> Text.literal("Owner: " + ownerName), false);
        context.getSource().sendFeedback(() -> Text.literal("PVP: " + (pvpEnabled ? "Enabled" : "Disabled")), false);

        context.getSource().sendFeedback(() -> Text.literal("Members (" + members.size() + "):"), false);

        for (UUID memberUuid : members) {
            ServerPlayerEntity member = context.getSource().getServer().getPlayerManager().getPlayer(memberUuid);
            String memberName = member != null ? member.getName().getString() : "Offline/Unknown";
            context.getSource().sendFeedback(() -> Text.literal("  - " + memberName), false);
        }

        return 1;
    }
}