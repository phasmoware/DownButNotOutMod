package com.phasmoware.down_but_not_out.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String MOD_ID = "down_but_not_out";
    public static final String MOD_INITIALIZED = MOD_ID + " mod initialized";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String MOD_ABBREV_PREFIX = "dbno_";
    public static final String DOWNED_TAG = "Downed";
    public static final String SKIPPED_DOWNED_STATE_MSG = "No one was available to revive you...";
    public static final String LAVA_PREVENTED_DOWNED_MSG = "The lava prevented you from being downed...";
    public static final String DOWNED_STATE_MSG = " is down, give them a hand to revive them";
    public static final String REVIVED_MSG = " has revived ";
    public static final String BLED_OUT_MSG = "You bled out...";
    public static final String CUSTOM_REVIVE_TAG_ABOVE_NAME = "◥REVIVE◤";
    public static final String COMMAND_STRING = "bleedout";
    public static final String PLAYER_IS_NULL_ON_APPLY_DOWNED_STATE_ERROR = "Error: Can't change downed state because player is null!";
    public static final String PLAYER_IS_NULL_ON_REMOVE_DOWNED_STATE_ERROR = "Error: Can't change downed state because player is null!";
    public static final Text REVIVE_CANCELED_TEXT = Text.literal("Revive canceled!").formatted(Formatting.RED);
    public static final Text USE_EMPTY_HAND_TO_REVIVE_TEXT = Text.literal("Use a free hand to revive them!").formatted(Formatting.RED);
    public static final Text REVIVER_NOT_TEAMMATE_TEXT = Text.literal("Wrong team!").formatted(Formatting.RED);
    public static final Text TOO_FAR_AWAY_TO_REVIVE_TEXT = Text.literal("Too far away to revive them!").formatted(Formatting.RED);
    public static final float HEARTS_WHILE_DOWNED = 0.000001f;
    public static final float BASE_MOVE_SPEED = 0.1f;
    public static final float DOWNED_SOUND_PITCH = 1.2f;
    public static final float REVIVED_SOUND_PITCH = 0.6f;
    public static final float MIN_TICK_PROGRESS = 1.0f;
    public static final double MIN_ENTITY_SCALE = 0.0625d;
    public static final double Y_OFFSET = 0.86;
    public static final long UPDATE_MSG_SPAM_COOLDOWN = 15;
    public static final long REPEAT_MSG_SPAM_COOLDOWN = 60;
    public static final int MIN_HEARTBEAT_INTERVAL = 10;
    public static final int MAX_HEARTBEAT_INTERVAL = 100;
    public static final int NON_HEALING_FOOD_LEVEL = 17;
}
