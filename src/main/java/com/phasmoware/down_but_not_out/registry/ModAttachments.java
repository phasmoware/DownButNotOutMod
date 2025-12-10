package com.phasmoware.down_but_not_out.registry;

import com.phasmoware.down_but_not_out.data.PlayerData;
import com.phasmoware.down_but_not_out.util.Constants;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class ModAttachments {
    public static final AttachmentType<PlayerData> PLAYER_DATA = AttachmentRegistry.create(
            Identifier.of(Constants.MOD_ID, "player_data"),
            builder -> builder
                    .initializer(PlayerData::new)
                    .persistent(PlayerData.CODEC)
                    .copyOnDeath()
    );

    public static void init() {}
}
