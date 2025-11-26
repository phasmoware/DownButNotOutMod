package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.config.ModConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class SoundUtility {

    public static void playDownedSound(ServerPlayerEntity player) {
        player.getEntityWorld().playSoundFromEntity(null, player, SoundEvents.ENTITY_TURTLE_EGG_BREAK,
                SoundCategory.PLAYERS, ModConfig.INSTANCE.DOWNED_SOUND_VOLUME, Constants.DOWNED_SOUND_PITCH);
    }

    public static void playRevivedSound(ServerPlayerEntity player) {
        player.getEntityWorld().playSoundFromEntity(null, player, SoundEvents.ITEM_TRIDENT_RETURN,
                SoundCategory.PLAYERS, ModConfig.INSTANCE.REVIVED_SOUND_VOLUME, Constants.REVIVED_SOUND_PITCH);
    }

    public static void playHeartBeatSound(ServerPlayerEntity player, float pitch) {
        player.getEntityWorld().playSoundFromEntity(null, player, SoundEvents.ENTITY_WARDEN_HEARTBEAT,
                SoundCategory.PLAYERS, ModConfig.INSTANCE.HEARTBEAT_SOUND_VOLUME, pitch);
    }
}
