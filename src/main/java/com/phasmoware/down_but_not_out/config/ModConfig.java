package com.phasmoware.down_but_not_out.config;

public final class ModConfig {
    public static ModConfig INSTANCE;

    // default values set here
    public boolean MOD_ENABLED = true;
    public boolean SKIP_DOWNED_STATE_IF_NO_OTHER_PLAYERS_ONLINE = true;
    public boolean REVIVING_REQUIRES_EMPTY_HAND = true;
    public boolean ALLOW_DOWNED_STATE_IN_LAVA = false;
    public boolean USE_OVERLAY_MESSAGES = true;
    public boolean DOWNED_PLAYERS_HAVE_GLOW_EFFECT = true;
    public boolean DOWNED_PLAYERS_HAVE_BLINDNESS_EFFECT = true;
    public boolean USE_CUSTOM_DOWNED_TEAMS = true;
    public boolean ALLOW_CHANGE_GAME_MODE = true;
    public boolean SHOW_REVIVE_TAG_ABOVE_PLAYER = true;
    public long REVIVE_PENALTY_COOLDOWN_TICKS = 600L;
    public int REVIVE_PENALTY_MULTIPLIER = 4;
    public long BLEEDING_OUT_DURATION_TICKS = 900L;
    public long REVIVE_DURATION_TICKS = 60L;
    public float HEARTBEAT_SOUND_VOLUME = 1.0F;
    public float REVIVED_SOUND_VOLUME = 2.5F;
    public float DOWNED_SOUND_VOLUME = 1.0F;
    public float DOWNED_MOVE_SPEED = 0.01F;

    public ModConfig() {
    }


    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = ConfigUtility.loadConfig();
        }
    }

}
