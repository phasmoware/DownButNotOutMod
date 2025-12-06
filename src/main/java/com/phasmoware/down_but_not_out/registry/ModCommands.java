package com.phasmoware.down_but_not_out.registry;


import com.mojang.brigadier.tree.LiteralCommandNode;
import com.phasmoware.down_but_not_out.command.BleedOutCommand;
import com.phasmoware.down_but_not_out.util.Constants;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ModCommands {


    private ModCommands() {
    }

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            // build new nodes
            LiteralCommandNode<ServerCommandSource> bleedOutNode = CommandManager.literal(Constants.COMMAND_STRING).executes(new BleedOutCommand()).build();
            // usage: /bleedout
            dispatcher.getRoot().addChild(bleedOutNode);
        });
    }
}
