package com.phasmoware.down_but_not_out.handler;

import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.config.ModConfig;
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
                .withColor(Formatting.DARK_GRAY)
        ).append(Text.literal("[HERE]").styled(style -> style
                .withColor(Formatting.AQUA)
                .withUnderline(true)
                .withBold(true)
                .withClickEvent(new ClickEvent.RunCommand("bleedout"))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Run Command /bleedout")))
        )).append(Text.literal(" or type /bleedout to give up and respawn").styled(style -> style
                .withBold(true)
                .withItalic(true)
                .withColor(Formatting.DARK_GRAY)
        )), false); // can't be an overlay with a click event
    }

    public static void sendUpdateMessage(Text message, ServerPlayerEntity player) {
        ServerPlayerAPI serverPlayer = (ServerPlayerAPI) player;
        if (!message.equals(serverPlayer.downButNotOut$getLastText())) {
            serverPlayer.downButNotOut$setLastText(message);
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
