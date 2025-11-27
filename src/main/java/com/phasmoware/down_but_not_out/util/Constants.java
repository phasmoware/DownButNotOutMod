package com.phasmoware.down_but_not_out.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String MOD_ID = "down_but_not_out";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final float HEARTS_AFTER_REVIVE = 0.01f;
    public static final String DOWNED_TAG = "Downed";
    public static final double CONE_SIZE = 0.05;
    public static final boolean ADJUST_FOR_DISTANCE = true;
    public static final boolean SEE_THROUGH_TRANSPARENT_BLOCKS = true;
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
    public static final float PLAYER_MAX_HEALTH = 1024.5f;
    public static final double DOWN_FORCE = -0.0784000015258789;
    public static final Vec3d DOWNED_NOT_MOVING =  new Vec3d(0.0D, DOWN_FORCE, 0.0D);
    public static final long UPDATE_MSG_SPAM_COOLDOWN = 15;
    public static final long REPEAT_MSG_SPAM_COOLDOWN = 60;
}
