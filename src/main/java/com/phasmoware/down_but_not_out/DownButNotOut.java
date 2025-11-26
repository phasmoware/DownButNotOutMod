package com.phasmoware.down_but_not_out;

import com.phasmoware.down_but_not_out.command.ModCommands;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.listener.EventCallbackListener;
import com.phasmoware.down_but_not_out.util.Reference;
import net.fabricmc.api.ModInitializer;

public class DownButNotOut implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConfig.init();
        EventCallbackListener.registerEventCallbacks();
        ModCommands.initialize();
        Reference.LOGGER.info(Reference.MOD_ID + " mod initialized");
    }
}
