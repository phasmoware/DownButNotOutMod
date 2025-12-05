package com.phasmoware.down_but_not_out.handler;

import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.util.Constants;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MessageHandler {

    public static void sendClickableGiveUpMessage(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("Click or type ").styled(style -> style
                .withBold(true)
                .withItalic(true)
                .withColor(Formatting.GRAY))
                .append(Text.literal("/" + Constants.COMMAND_STRING)
                        .styled(style -> style
                                .withColor(Formatting.RED)
                                .withUnderline(true).withBold(true)
                                .withClickEvent(new ClickEvent.RunCommand(Constants.COMMAND_STRING))
                                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Run Command /" + Constants.COMMAND_STRING)))))
                .append(Text.literal(" to give up and respawn")
                        .styled(style -> style
                                .withBold(true).
                                withItalic(true)
                                .withColor(Formatting.GRAY))), false); // can't be an overlay with a click event
    }

    public static void sendThrottledUpdateMessage(Text message, ServerPlayerEntity player) {
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;
        if (ModConfig.INSTANCE.USE_OVERLAY_MESSAGES || serverPlayer.dbno$getTicksSinceLastUpdate() > Constants.UPDATE_MSG_SPAM_COOLDOWN) {
            serverPlayer.dbno$setTicksSinceLastUpdate(0);
            sendUpdateMessage(message, player);
        }
    }

    public static void sendUpdateMessage(Text message, ServerPlayerEntity player) {
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;
        if (!message.equals(serverPlayer.dbno$getLastUpdateText()) || serverPlayer.dbno$getTicksSinceLastUpdate() > Constants.REPEAT_MSG_SPAM_COOLDOWN) {
            serverPlayer.dbno$setLastUpdateText(message);
            player.sendMessage(message, ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
        }
    }

    public static void sendErrorMessage(Text message, ServerCommandSource source) {
        source.sendError(message);
    }

    public static void broadcastMessageToPlayers(String message, ServerWorld world, Formatting formatting) {
        Text text = Text.literal(message).formatted(formatting);
        world.getServer().getPlayerManager().broadcast(text, ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
    }

    public static void onPlayerDownedInLava(ServerPlayerEntity player) {
        Text skippedDownedStateMessage = Text.literal(Constants.LAVA_PREVENTED_DOWNED_MSG).formatted(Formatting.RED);
        MessageHandler.sendUpdateMessage(skippedDownedStateMessage, player);
    }

    public static void onPlayerRevivingWithoutEmptyHand(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
        MessageHandler.sendUpdateMessage(Constants.USE_EMPTY_HAND_TO_REVIVE_TEXT, reviver);
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, downed);
    }

    public static void onPlayerRevivingTooFarAway(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
        MessageHandler.sendUpdateMessage(Constants.TOO_FAR_AWAY_TO_REVIVE_TEXT, reviver);
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, downed);
    }

    public static void onPlayerLookingAwayWhileReviving(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, reviver);
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, downed);
    }
}
