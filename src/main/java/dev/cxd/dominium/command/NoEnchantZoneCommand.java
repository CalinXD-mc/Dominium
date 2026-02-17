package dev.cxd.dominium.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.cxd.dominium.utils.ZoneManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Map;
import java.util.UUID;

public class NoEnchantZoneCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("dominium:no_enchant_zone")
                .then(CommandManager.literal("create")
                        .then(CommandManager.argument("center", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("corner", BlockPosArgumentType.blockPos())
                                        .executes(NoEnchantZoneCommand::createZone))))
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("center", BlockPosArgumentType.blockPos())
                                .executes(NoEnchantZoneCommand::removeZone)))
                .then(CommandManager.literal("list")
                        .executes(NoEnchantZoneCommand::listZones)));
    }

    private static int createZone(CommandContext<ServerCommandSource> context) {
        try {
            BlockPos center = BlockPosArgumentType.getBlockPos(context, "center");
            BlockPos corner = BlockPosArgumentType.getBlockPos(context, "corner");
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

            UUID zoneId = UUID.randomUUID();
            Box box = new Box(center.getX(), center.getY(), center.getZ(), corner.getX(), corner.getY(), corner.getZ());
            String dimension = player.getEntityWorld().getRegistryKey().getValue().toString();

            ZoneManager.addZone(zoneId, box, dimension);

            context.getSource().sendFeedback(() ->
                    Text.literal("Created enchantment null zone with ID: " + zoneId), false);
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Error creating zone: " + e.getMessage()));
            return 0;
        }
    }

    private static int removeZone(CommandContext<ServerCommandSource> context) {
        BlockPos center = BlockPosArgumentType.getBlockPos(context, "center");

        UUID removed = ZoneManager.removeZoneAt(center);

        if (removed != null) {
            context.getSource().sendFeedback(() ->
                    Text.literal("Removed enchantment null zone"), false);
            return 1;
        }

        context.getSource().sendError(Text.literal("No zone found at that position"));
        return 0;
    }

    private static int listZones(CommandContext<ServerCommandSource> context) {
        Map<UUID, ZoneManager.NullZone> zones = ZoneManager.getZones();

        if (zones.isEmpty()) {
            context.getSource().sendFeedback(() ->
                    Text.literal("No active enchantment null zones"), false);
        } else {
            context.getSource().sendFeedback(() ->
                    Text.literal("Active zones: " + zones.size()), false);
            for (Map.Entry<UUID, ZoneManager.NullZone> entry : zones.entrySet()) {
                Box box = entry.getValue().box;
                context.getSource().sendFeedback(() ->
                        Text.literal("- " + entry.getKey() + " at " +
                                String.format("%.1f,%.1f,%.1f to %.1f,%.1f,%.1f",
                                        box.minX, box.minY, box.minZ,
                                        box.maxX, box.maxY, box.maxZ)), false);
            }
        }
        return 1;
    }
}
