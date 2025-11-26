package com.phasmoware.down_but_not_out.handler;

import com.phasmoware.down_but_not_out.config.ModConfig;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SendMessageHandler {

    public static void broadcastMessageToPlayers(String message, ServerWorld world, Formatting formatting) {
        Text text = Text.literal(message).formatted(formatting);
        world.getServer().getPlayerManager().broadcast(text, ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
    }
}
