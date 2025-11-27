package com.phasmoware.down_but_not_out.timer;

import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.handler.MessageHandler;
import com.phasmoware.down_but_not_out.manager.DownedStateManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReviveTimer {
    private boolean interactionActive = false;
    private long reviveProgressTicks;
    @Nullable
    private ServerPlayerEntity reviver;
    @NotNull
    private final ServerPlayerEntity downed;

    public ReviveTimer(@Nullable ServerPlayerEntity reviver, @NotNull ServerPlayerEntity downed) {
        this.reviver = reviver;
        this.downed = downed;
    }


    public void tick() {
        if (DownedStateManager.isValidReviver(reviver, downed)) {
            interactionActive = true;
            reviveProgressTicks++;
            updateReviveProgress();
            if (this.reviveProgressTicks >= ModConfig.INSTANCE.REVIVE_DURATION_TICKS) {
                reviveProgressTicks = 0;
                DownedStateManager.onReviveComplete(downed, reviver);
                stopReviveInteraction();
            }
        } else if (interactionActive || reviver != null) {
            stopReviveInteraction();
        } else {
            decrementReviveProgress();
        }
    }


    private void updateReviveProgress() {
        Text msgToReviver = Text.literal("Hold to Revive: " + getCurrentProgressPercent() + "%").formatted(Formatting.BLUE);
        MessageHandler.sendThrottledUpdateMessage(msgToReviver, reviver);
        Text msgToDowned = Text.literal("Reviving: " + getCurrentProgressPercent() + "%").formatted(Formatting.BLUE);
        MessageHandler.sendThrottledUpdateMessage(msgToDowned, downed);
    }

    private void decrementReviveProgress() {
        if (reviveProgressTicks > 0) {
            reviveProgressTicks--;
        }
    }

    public void stopReviveInteraction() {
        if (this.reviver != null) {
            this.reviver = null;
        }
        if (this.interactionActive) {
            this.interactionActive = false;
        }
    }

    public void continueRevive(ServerPlayerEntity reviver) {
        if (this.reviver != reviver) {
            this.reviver = reviver;
        }
        if (!interactionActive) {
            interactionActive = true;
        }
    }

    public boolean isInteractionActive() {
        return interactionActive;
    }

    public int getCurrentProgressPercent() {
        return (int) ((((float) this.reviveProgressTicks / (float) ModConfig.INSTANCE.REVIVE_DURATION_TICKS)) * 100);
    }

    public @Nullable ServerPlayerEntity getReviver() {
        return reviver;
    }
}
