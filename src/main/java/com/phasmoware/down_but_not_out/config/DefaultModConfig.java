package com.phasmoware.down_but_not_out.config;

public class DefaultModConfig {
    public boolean MOD_ENABLED = true;
    public boolean SKIP_DOWNED_STATE_IF_NO_OTHER_PLAYERS_ONLINE = true;
    public boolean REVIVING_REQUIRES_EMPTY_HAND = true;
    public boolean ALLOW_DOWNED_STATE_IN_LAVA = false;
    public boolean USE_OVERLAY_MESSAGES = true;
    public boolean DOWNED_PLAYERS_HAVE_GLOW_EFFECT = true;
    public boolean DOWNED_PLAYERS_HAVE_BLINDNESS_EFFECT = true;
    public long BLEEDING_OUT_DURATION_TICKS = 900L;
    public long REVIVE_DURATION_TICKS = 60L;
    public float HEARTBEAT_SOUND_VOLUME = 1.0F;
    public float REVIVED_SOUND_VOLUME = 2.0F;
    public float DOWNED_SOUND_VOLUME = 2.0F;
    public float DOWNED_MOVE_SPEED = 0.01F;
}
