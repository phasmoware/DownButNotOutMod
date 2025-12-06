package com.phasmoware.down_but_not_out;

import com.phasmoware.down_but_not_out.registry.ModCommands;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.registry.ModEvents;
import com.phasmoware.down_but_not_out.util.Constants;
import net.fabricmc.api.ModInitializer;

public class DownButNotOut implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConfig.init();
        ModEvents.init();
        ModCommands.init();
        Constants.LOGGER.info(Constants.MOD_INITIALIZED);
    }
}
