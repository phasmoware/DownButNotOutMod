package com.phasmoware.down_but_not_out.timer;

import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.handler.MessageHandler;
import com.phasmoware.down_but_not_out.manager.DownedStateManager;
import com.phasmoware.down_but_not_out.util.Constants;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.phasmoware.down_but_not_out.util.DownedUtility.isDowned;

public class ReviveTimer {
    private boolean interactionActive = false;
    private long interactionTicks;
    private long counter;
    private ServerPlayerEntity reviver;
    private ServerPlayerEntity downed;

    public ReviveTimer(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
        this.reviver = reviver;
        this.downed = downed;
    }


    public void tick() {
        if (reviver != null && downed != null) {
            counter++;
            if (interactionActive) {
                updateReviveProgress();
                incrementInteractionTicks();
            }
            if (isValidReviver(this.reviver, this.downed)) {
                if (this.interactionTicks >= ModConfig.INSTANCE.REVIVE_DURATION_TICKS) {

                    DownedStateManager.onReviveEvent(downed, reviver);

                } else if ((counter - 10) > interactionTicks) {
                    ((ServerPlayerAPI)this.downed).downButNotOut$cancelReviving(this);
                }
            }
        }
    }


    public boolean isValidReviver(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
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
        if (!(reviver.getMainHandStack().isEmpty() && ModConfig.INSTANCE.REVIVING_REQUIRES_EMPTY_HAND)) {
            DownedStateManager.onPlayerRevivingWithoutEmptyHand(reviver, downed);
            return false;
        }
        if ((reviver.squaredDistanceTo(downed) > reviver.getBlockInteractionRange())) {
            DownedStateManager.onPlayerRevivingTooFarAway(reviver, downed);

            return false;
        }
        if (!downed.isEntityLookingAtMe(reviver, Constants.CONE_SIZE, Constants.ADJUST_FOR_DISTANCE, Constants.SEE_THROUGH_TRANSPARENT_BLOCKS, new double[]{downed.getY(), downed.getEyeY()})) {
            DownedStateManager.onPlayerLookingAwayWhileReviving(reviver, downed);
            return false;
        }
        return true;
    }

    private void updateReviveProgress() {
        Text msgToReviver = Text.literal("Hold to Revive: " + getCurrentProgressPercent() + "%").formatted(Formatting.BLUE);
        MessageHandler.sendThrottledUpdateMessage(msgToReviver, reviver);
        Text msgToDowned = Text.literal("Reviving: " + getCurrentProgressPercent() + "%").formatted(Formatting.BLUE);
        MessageHandler.sendThrottledUpdateMessage(msgToDowned, downed);
    }

    public void reset(ServerPlayerEntity reviver) {
        this.reviver = reviver;
        interactionTicks = 0;
    }

    public int getCurrentProgressPercent() {
        return (int) ((((float) this.interactionTicks / (float) ModConfig.INSTANCE.REVIVE_DURATION_TICKS)) * 100);
    }

    public long getInteractionTicks() {
        return interactionTicks;
    }

    public void setInteractionTicks(long interactionTicks) {
        this.interactionTicks = interactionTicks;
    }

    public void incrementInteractionTicks() {
        if (isValidReviver(this.reviver, this.downed)) {
            this.interactionTicks++;
        } else {
            this.interactionActive = false;
        }
    }

    public ServerPlayerEntity getReviver() {
        return reviver;
    }

    public void setReviver(ServerPlayerEntity reviver) {
        this.reviver = reviver;
    }

    public ServerPlayerEntity getDowned() {
        return downed;
    }

    public void setDowned(ServerPlayerEntity downed) {
        this.downed = downed;
    }

    public void startReviveInteraction() {
        this.interactionActive = true;
    }
}
