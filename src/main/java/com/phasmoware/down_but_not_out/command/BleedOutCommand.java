package com.phasmoware.down_but_not_out.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.handler.MessageHandler;
import com.phasmoware.down_but_not_out.util.Constants;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class BleedOutCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            MessageHandler.sendErrorMessage(Text.literal("You must be a player to use this command."), source);
            return 1;
        }
        if (!(player.getCommandTags().contains(Constants.DOWNED_TAG))) {
            MessageHandler.sendErrorMessage(Text.literal("You are not in a downed state."), source);
            return 1;
        }
        if (((ServerPlayerAPI)player).downButNotOut$getBleedOutTimer() == null) {
            MessageHandler.sendErrorMessage(Text.literal("No BleedOut Timer has been set."), source);
            return 1;
        }

        // set ticks until bleed out to 1 to bleed out next tick
        ((ServerPlayerAPI)player).downButNotOut$getBleedOutTimer().setTicksUntilBleedOut(1);
        return 0;
    }
}
