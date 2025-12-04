package com.phasmoware.down_but_not_out.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String MOD_ID = "down_but_not_out";
    public static final String MOD_ABBREV_PREFIX = "dbno_";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final float HEARTS_WHILE_DOWNED = 0.01f;
    public static final String DOWNED_TAG = "Downed";
    public static final String SKIPPED_DOWNED_STATE_MSG = "No one was available to revive you...";
    public static final String LAVA_PREVENTED_DOWNED_MSG = "The lava prevented you from being downed...";
    public static final String DOWNED_STATE_MSG = " is down, give them a hand to revive them";
    public static final String REVIVED_MSG = " has revived ";
    public static final String BLED_OUT_MSG = "You bled out...";
    public static final Text REVIVE_CANCELED_TEXT = Text.literal("Revive canceled!").formatted(Formatting.RED);
    public static final float BASE_MOVE_SPEED = 0.1F;
    public static final float DOWNED_SOUND_PITCH = 1.2F;
    public static final float REVIVED_SOUND_PITCH = 0.6F;
    public static final double MIN_ENTITY_SCALE = 0.0625d;
    public static final double Y_OFFSET = 0.95;
    public static final long UPDATE_MSG_SPAM_COOLDOWN = 15;
    public static final long REPEAT_MSG_SPAM_COOLDOWN = 60;
    public static final String CUSTOM_REVIVE_TAG_ABOVE_NAME = "◥REVIVE◤";
}
