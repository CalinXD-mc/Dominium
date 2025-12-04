package dev.cxd.dominium.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.cxd.dominium.init.ModItems;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MarkerCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("dominium:marker")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("mark", StringArgumentType.greedyString())
                                .executes(MarkerCommand::setMark)))
                .then(CommandManager.literal("get")
                        .executes(MarkerCommand::getMark))
                .then(CommandManager.literal("clear")
                        .executes(MarkerCommand::clearMark)));
    }

    private static int setMark(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ItemStack heldItem = player.getMainHandStack();

        if (heldItem.getItem() != ModItems.ITEM_MARKER) {
            context.getSource().sendError(Text.literal("You must be holding an Item Marker!"));
            return 0;
        }

        String mark = StringArgumentType.getString(context, "mark");

        // Store the mark in NBT
        NbtCompound nbt = heldItem.getOrCreateNbt();
        nbt.putString("Mark", mark);

        context.getSource().sendFeedback(() -> Text.literal("Set marker text to: \"" + mark + "\""), false);
        return 1;
    }

    private static int getMark(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ItemStack heldItem = player.getMainHandStack();

        if (heldItem.getItem() != ModItems.ITEM_MARKER) {
            context.getSource().sendError(Text.literal("You must be holding an Item Marker!"));
            return 0;
        }

        NbtCompound nbt = heldItem.getNbt();
        if (nbt == null || !nbt.contains("Mark")) {
            context.getSource().sendFeedback(() -> Text.literal("This marker has no text set (default: \" \")"), false);
            return 0;
        }

        String mark = nbt.getString("Mark");
        context.getSource().sendFeedback(() -> Text.literal("Current marker text: \"" + mark + "\""), false);
        return 1;
    }

    private static int clearMark(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ItemStack heldItem = player.getMainHandStack();

        if (heldItem.getItem() != ModItems.ITEM_MARKER) {
            context.getSource().sendError(Text.literal("You must be holding an Item Marker!"));
            return 0;
        }

        NbtCompound nbt = heldItem.getNbt();
        if (nbt != null) {
            nbt.remove("Mark");
        }

        context.getSource().sendFeedback(() -> Text.literal("Cleared marker text (reset to default: \" \")"), false);
        return 1;
    }
}