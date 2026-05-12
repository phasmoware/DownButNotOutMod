package com.phasmoware.down_but_not_out.handler;

import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class MessageHandler {

    public static void sendClickableGiveUpMessage(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Click or type ").withStyle(style -> style
                .withBold(true)
                .withItalic(true)
                .withColor(ChatFormatting.GRAY))
                .append(Component.literal("/" + Constants.COMMAND_STRING)
                        .withStyle(style -> style
                                .withColor(ChatFormatting.RED)
                                .withUnderlined(true).withBold(true)
                                .withClickEvent(new ClickEvent.RunCommand(Constants.COMMAND_STRING))
                                .withHoverEvent(new HoverEvent.ShowText(Component.literal("Run Command /" + Constants.COMMAND_STRING)))))
                .append(Component.literal(" to give up and respawn")
                        .withStyle(style -> style
                                .withBold(true).
                                withItalic(true)
                                .withColor(ChatFormatting.GRAY))), false); // can't be an overlay with a click event
    }

    public static void sendThrottledUpdateMessage(Component message, ServerPlayer player) {
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;
        if (ModConfig.INSTANCE.USE_OVERLAY_MESSAGES || serverPlayer.dbno$getTicksSinceLastUpdate() > Constants.UPDATE_MSG_SPAM_COOLDOWN) {
            serverPlayer.dbno$setTicksSinceLastUpdate(0);
            sendUpdateMessage(message, player);
        }
    }

    public static void sendUpdateMessage(Component message, ServerPlayer player) {
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;
        if (!message.equals(serverPlayer.dbno$getLastUpdateText()) || serverPlayer.dbno$getTicksSinceLastUpdate() > Constants.REPEAT_MSG_SPAM_COOLDOWN) {
            serverPlayer.dbno$setLastUpdateText(message);
            player.sendSystemMessage(message, ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
        }
    }

    public static void sendErrorMessage(Component message, CommandSourceStack source) {
        source.sendFailure(message);
    }

    public static void broadcastMessageToPlayers(String message, ServerLevel world, ChatFormatting formatting) {
        if (ModConfig.INSTANCE.BROADCAST_PLAYER_DOWNED_NOTIFICATIONS) {
            Component text = Component.literal(message).withStyle(formatting);
            world.getServer().getPlayerList().broadcastSystemMessage(text, ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
        }
    }

    public static void onPlayerDownedInLava(ServerPlayer player) {
        Component skippedDownedStateMessage = Component.literal(Constants.LAVA_PREVENTED_DOWNED_MSG).withStyle(ChatFormatting.RED);
        MessageHandler.sendUpdateMessage(skippedDownedStateMessage, player);
    }

    public static void onPlayerRevivingWithoutEmptyHand(ServerPlayer reviver, ServerPlayer downed) {
        MessageHandler.sendUpdateMessage(Constants.USE_EMPTY_HAND_TO_REVIVE_TEXT, reviver);
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, downed);
    }

    public static void onPlayerRevivingTooFarAway(ServerPlayer reviver, ServerPlayer downed) {
        MessageHandler.sendUpdateMessage(Constants.TOO_FAR_AWAY_TO_REVIVE_TEXT, reviver);
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, downed);
    }

    public static void onPlayerLookingAwayWhileReviving(ServerPlayer reviver, ServerPlayer downed) {
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, reviver);
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, downed);
    }

    public static void onPlayerRevivingFromDifferentTeam(ServerPlayer reviver, ServerPlayer downed) {
        MessageHandler.sendUpdateMessage(Constants.REVIVER_NOT_TEAMMATE_TEXT, reviver);
        MessageHandler.sendUpdateMessage(Constants.REVIVER_NOT_TEAMMATE_TEXT, downed);
    }
}
