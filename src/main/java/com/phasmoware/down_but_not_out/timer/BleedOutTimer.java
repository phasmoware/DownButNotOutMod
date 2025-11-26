package com.phasmoware.down_but_not_out.timer;

import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.manager.DownedStateManager;
import com.phasmoware.down_but_not_out.util.SoundUtility;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.phasmoware.down_but_not_out.util.DownedUtility.isDowned;

public class BleedOutTimer implements ServerTickEvents.EndTick {

    private long ticksUntilBleedOut;
    private ServerPlayerEntity player;
    private DamageSource damageSource;
    private final long totalTicks;
    private long heartbeatCooldownTicks;

    public BleedOutTimer(long ticksUntilBleedOut, ServerPlayerEntity player, DamageSource damageSource) {
        this.setTicksUntilBleedOut(ticksUntilBleedOut);
        this.totalTicks = ticksUntilBleedOut;
        this.player = player;
        this.damageSource = damageSource;
    }

    @Override
    public void onEndTick(MinecraftServer minecraftServer) {
        if (this.player != null) {
            if (isDowned(player)) {
                tickHeartbeats();
                if (!((ServerPlayerAPI) player).downButNotOut$isBeingRevived()) {
                    if (this.ticksUntilBleedOut > 0L) {
                        --this.ticksUntilBleedOut;
                        if (this.ticksUntilBleedOut == 0L) {
                            DownedStateManager.onBleedOutEvent(player, damageSource);
                        }
                    }
                }
            }
        } else {
            this.damageSource = null;
        }
    }

    // starts downed at 0.0 progress and ends at bleed out at 1.0 progress
    // for infinite downed ticks (-1)
    private float getCurrentProgress() {
        if (totalTicks < 0L) {
            return 1f;
        }

        return 1f - (this.ticksUntilBleedOut / (float) this.totalTicks);
    }

    private void tickHeartbeats() {
        // disabled if Max Heartbeat volume is set to less than 0
        if (ModConfig.INSTANCE.HEARTBEAT_SOUND_VOLUME > 0) {
            float currentProgress = getCurrentProgress();

            // interval of 5 ticks min and 100 ticks max between heartbeats
            // interval grows larger towards the end of the progress
            int nextHeartbeatInterval = (int) (5 + (95 * currentProgress));

            if (heartbeatCooldownTicks <= 0) {
                playHeartbeatSound();
                heartbeatCooldownTicks = nextHeartbeatInterval;
            } else {
                heartbeatCooldownTicks--;
            }
        }
    }

    private void playHeartbeatSound() {
        // pitch slows down heartbeat as bleed out timer progresses
        float pitch = Math.max(0f, 1f - getCurrentProgress());
        SoundUtility.playHeartBeatSound(this.player, pitch);
    }

    public void register() {
        ServerTickEvents.END_SERVER_TICK.register(this);
    }

    public long getTicksUntilBleedOut() {
        return this.ticksUntilBleedOut;
    }

    public void setTicksUntilBleedOut(long ticksUntilBleedOut) {
        this.ticksUntilBleedOut = ticksUntilBleedOut;
    }

    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    public void setPlayer(ServerPlayerEntity player) {
        this.player = player;
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public void setDamageSource(DamageSource damageSource) {
        this.damageSource = damageSource;
    }
}
