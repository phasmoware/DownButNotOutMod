package com.phasmoware.down_but_not_out.handler;

import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerEntityDuck;
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
        player.sendMessage(Text.literal("Click ").styled(style -> style
                .withBold(true)
                .withItalic(true)
                .withColor(Formatting.DARK_GRAY))
                .append(Text.literal("[HERE]")
                        .styled(style -> style
                                .withColor(Formatting.AQUA)
                                .withUnderline(true).withBold(true)
                                .withClickEvent(new ClickEvent.RunCommand("bleedout"))
                                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Run Command /bleedout")))))
                .append(Text.literal(" or type /bleedout to give up and respawn")
                        .styled(style -> style
                                .withBold(true).
                                withItalic(true)
                                .withColor(Formatting.DARK_GRAY))), false); // can't be an overlay with a click event
    }

    public static void sendThrottledUpdateMessage(Text message, ServerPlayerEntity player) {
        ServerPlayerEntityDuck serverPlayer = (ServerPlayerEntityDuck) player;
        if (ModConfig.INSTANCE.USE_OVERLAY_MESSAGES || serverPlayer.downButNotOut$getTicksSinceLastUpdate() > Constants.UPDATE_MSG_SPAM_COOLDOWN) {
            serverPlayer.downButNotOut$setTicksSinceLastUpdate(0);
            sendUpdateMessage(message, player);
        }
    }

    public static void sendUpdateMessage(Text message, ServerPlayerEntity player) {
        ServerPlayerEntityDuck serverPlayer = (ServerPlayerEntityDuck) player;
        if (!message.equals(serverPlayer.downButNotOut$getLastUpdateText()) || serverPlayer.downButNotOut$getTicksSinceLastUpdate() > Constants.REPEAT_MSG_SPAM_COOLDOWN) {
            serverPlayer.downButNotOut$setLastUpdateText(message);
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
}
