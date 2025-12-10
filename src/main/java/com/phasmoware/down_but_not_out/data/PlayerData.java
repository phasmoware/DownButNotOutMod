package com.phasmoware.down_but_not_out.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.phasmoware.down_but_not_out.config.ModConfig;

public record PlayerData(boolean isDowned, long ticksUntilBleedOut) {

    public PlayerData() {
        this(false, ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS);
    }

    public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("is_downed").forGetter(PlayerData::isDowned),
                    Codec.LONG.fieldOf("ticks_until_bleed_out").forGetter(PlayerData::ticksUntilBleedOut)
            ).apply(instance, PlayerData::new)
    );
}
