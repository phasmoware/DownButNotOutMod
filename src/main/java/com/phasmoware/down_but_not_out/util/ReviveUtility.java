package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.handler.MessageHandler;
import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import java.util.Optional;

import static com.phasmoware.down_but_not_out.util.DownedUtility.isDowned;

public class ReviveUtility {
    public static void applyRevivedPenalty(ServerPlayerDuck player) {
        BleedOutTimer timer = player.dbno$getBleedOutTimer();
        if (ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS > 0 && timer.getTicksUntilBleedOut() > 1) {
            player.dbno$getBleedOutTimer().setReviveCooldownTicks(ModConfig.INSTANCE.REVIVE_PENALTY_COOLDOWN_TICKS);
            timer.setTicksUntilBleedOut(timer.getTicksUntilBleedOut() / ModConfig.INSTANCE.REVIVE_PENALTY_MULTIPLIER);
        }
    }

    public static boolean isLookingAtPlayer(ServerPlayerEntity viewer, ServerPlayerEntity target) {
        if (viewer == target) {
            return false;
        }
        Vec3d eyePos = viewer.getCameraPosVec(Constants.MIN_TICK_PROGRESS);
        Vec3d lookDir = viewer.getRotationVec(Constants.MIN_TICK_PROGRESS);
        Vec3d rayEnd = eyePos.add(lookDir.multiply(viewer.getEntityInteractionRange()));
        // expand by a bit for stability
        Box targetBox = target.getBoundingBox().expand(0.1);
        Optional<Vec3d> hit = targetBox.raycast(eyePos, rayEnd);
        return hit.isPresent();
    }

    public static boolean checkValidReviver(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
        if (reviver == null || downed == null) {
            return false;
        }
        if (reviver == downed) {
            return false;
        }
        if (isDowned(reviver)) {
            return false;
        }
        if (!isDowned(downed)) {
            return false;
        }
        if (ModConfig.INSTANCE.RESTRICT_REVIVE_TO_TEAMMATES_OR_TEAMLESS) {
            if ((reviver.getScoreboardTeam() != null && !TeamUtility.isOnTempDownedTeam(downed)) && (!reviver.getScoreboardTeam().equals(downed.getScoreboardTeam()))) {
                MessageHandler.onPlayerRevivingFromDifferentTeam(reviver, downed);
                return false;
            }
        }
        if (!(reviver.getMainHandStack().isEmpty() && ModConfig.INSTANCE.REVIVING_REQUIRES_EMPTY_HAND)) {
            MessageHandler.onPlayerRevivingWithoutEmptyHand(reviver, downed);
            return false;
        }
        if ((reviver.squaredDistanceTo(downed) > reviver.getBlockInteractionRange())) {
            MessageHandler.onPlayerRevivingTooFarAway(reviver, downed);
            return false;
        }
        if (!ReviveUtility.isLookingAtPlayer(reviver, downed)) {
            MessageHandler.onPlayerLookingAwayWhileReviving(reviver, downed);
            return false;
        }
        return true;
    }
}
