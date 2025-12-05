package com.phasmoware.down_but_not_out.timer;

import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.StateManager;
import com.phasmoware.down_but_not_out.util.Constants;
import com.phasmoware.down_but_not_out.util.SoundUtility;
import com.phasmoware.down_but_not_out.util.TeamUtility;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.phasmoware.down_but_not_out.util.DownedUtility.isDowned;

public class BleedOutTimer {

    @NotNull
    private final ServerPlayerEntity player;
    private long ticksUntilBleedOut;
    @Nullable
    private DamageSource damageSource;
    private long reviveCooldownTicks;
    private long heartbeatCooldownTicks;

    public BleedOutTimer(@NotNull ServerPlayerEntity player) {
        this.ticksUntilBleedOut = ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS;
        this.player = player;
    }


    public void tick() {
        if (isDowned(player)) {
            tickHeartbeats();
            player.setHealth(Constants.HEARTS_WHILE_DOWNED);
            if (playerIsNotGettingRevived()) {
                TeamUtility.updateBleedOutStatusTeamColor(player, getCurrentProgress());
                if (this.ticksUntilBleedOut > 0L && playerIsNotGettingRevived()) {
                    --this.ticksUntilBleedOut;
                    if (this.ticksUntilBleedOut == 0L) {
                        StateManager.onBleedOutEvent(player, damageSource);
                        reset();
                    }
                }
            }
        } else {
            tickReviveCooldown();
        }
    }

    // starts downed at 0.0 progress and ends at bleed out at 1f progress
    // for infinite downed ticks (-1f) just return 1f progress
    private float getCurrentProgress() {
        if (ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS < 0L) {
            return 1f;
        }
        return 1f - (this.ticksUntilBleedOut / (float) ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS);
    }

    private void tickHeartbeats() {
        // disabled if Max Heartbeat volume is set to less than or equal to 0
        if (ModConfig.INSTANCE.HEARTBEAT_SOUND_VOLUME > 0) {
            float currentProgress = getCurrentProgress();

            // interval between heartbeats in ticks grows larger towards the end of the bleed out progress
            int nextHeartbeatInterval = (int) (Constants.MIN_HEARTBEAT_INTERVAL + (Constants.MAX_HEARTBEAT_INTERVAL * currentProgress));

            if (heartbeatCooldownTicks <= 0) {
                playHeartbeatSound();
                heartbeatCooldownTicks = nextHeartbeatInterval;
            } else {
                heartbeatCooldownTicks--;
            }
        }
    }

    private void tickReviveCooldown() {
        if (reviveCooldownTicks > 0) {
            reviveCooldownTicks--;
        } else if (reviveCooldownTicks == 0 && this.ticksUntilBleedOut != ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS) {
            this.ticksUntilBleedOut = ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS;
        }
    }

    private void playHeartbeatSound() {
        // pitch slows down heartbeat as bleed out progresses
        float pitch = Math.max(0f, 1f - getCurrentProgress());
        SoundUtility.playHeartBeatSound(this.player, pitch);
    }

    public void reset() {
        this.ticksUntilBleedOut = ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS;
        this.damageSource = null;
    }

    public long getTicksUntilBleedOut() {
        return ticksUntilBleedOut;
    }

    public void setTicksUntilBleedOut(long ticksUntilBleedOut) {
        this.ticksUntilBleedOut = ticksUntilBleedOut;
    }

    public void setDamageSource(@Nullable DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    public void setReviveCooldownTicks(long reviveCooldownTicks) {
        this.reviveCooldownTicks = reviveCooldownTicks;
    }

    private boolean playerIsNotGettingRevived() {
        return !((ServerPlayerDuck) player).dbno$getReviveTimer().isInteractionActive();
    }
}
