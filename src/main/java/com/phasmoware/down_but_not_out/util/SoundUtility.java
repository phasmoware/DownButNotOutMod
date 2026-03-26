package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.config.ModConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class SoundUtility {

    public static void playDownedSound(ServerPlayer player) {
        player.level().playSound(
                null,
                player,
                SoundEvents.TURTLE_EGG_BREAK,
                SoundSource.PLAYERS,
                ModConfig.INSTANCE.DOWNED_SOUND_VOLUME,
                Constants.DOWNED_SOUND_PITCH);
    }

    public static void playRevivedSound(ServerPlayer player) {
        player.level().playSound(
                null,
                player,
                SoundEvents.TRIDENT_RETURN,
                SoundSource.PLAYERS,
                ModConfig.INSTANCE.REVIVED_SOUND_VOLUME,
                Constants.REVIVED_SOUND_PITCH);
    }

    public static void playHeartBeatSound(ServerPlayer player, float pitch) {
        player.level().playSound(
                null,
                player,
                SoundEvents.WARDEN_HEARTBEAT,
                SoundSource.PLAYERS,
                ModConfig.INSTANCE.HEARTBEAT_SOUND_VOLUME,
                pitch);
    }
}
