package com.phasmoware.down_but_not_out.command;


import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ModCommands {


    private ModCommands() {
    }

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            //Make some new nodes
            LiteralCommandNode<ServerCommandSource> killNode = CommandManager
                    .literal("bleedout")
                    .executes(new BleedOutCommand())
                    .build();

            //usage: /bleedout
            dispatcher.getRoot().addChild(killNode);
        });
    }

}
