package com.phasmoware.down_but_not_out.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import com.phasmoware.down_but_not_out.handler.MessageHandler;
import com.phasmoware.down_but_not_out.util.DownedUtility;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class BleedOutCommand implements Command<ServerCommandSource> {

    public static final String MUST_BE_A_PLAYER_TO_USE_THIS_COMMAND = "You must be a player to use this command.";
    public static final String NOT_IN_A_DOWNED_STATE = "You are not in a downed state.";
    public static final String BLEED_OUT_TIMER_NOT_SET = "Error: No BleedOut Timer has been set.";

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            MessageHandler.sendErrorMessage(Text.literal(MUST_BE_A_PLAYER_TO_USE_THIS_COMMAND), source);
            return 1;
        }
        if (!(DownedUtility.isDowned(player))) {
            MessageHandler.sendErrorMessage(Text.literal(NOT_IN_A_DOWNED_STATE), source);
            return 1;
        }
        if (((ServerPlayerDuck) player).dbno$getBleedOutTimer() == null) {
            MessageHandler.sendErrorMessage(Text.literal(BLEED_OUT_TIMER_NOT_SET), source);
            return 1;
        }

        // set ticks until bleed out to 1 to bleed out next tick
        ((ServerPlayerDuck) player).dbno$getBleedOutTimer().setTicksUntilBleedOut(1);
        return 0;
    }
}
