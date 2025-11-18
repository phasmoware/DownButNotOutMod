package com.phasmoware.down_but_not_out.timer;

import com.phasmoware.down_but_not_out.DownButNotOut;
import com.phasmoware.down_but_not_out.duck.PlayerDownButNotOut;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class BleedOutTimer implements ServerTickEvents.EndTick {

    private long ticksUntilBleedOut;
    private ServerPlayerEntity player;
    private DamageSource damageSource;

    public BleedOutTimer(long ticksUntilBleedOut, ServerPlayerEntity player, DamageSource damageSource) {
        this.setTicksUntilBleedOut(ticksUntilBleedOut);
        this.player = player;
        this.damageSource = damageSource;
    }

    @Override
    public void onEndTick(MinecraftServer minecraftServer) {
        if (this.player != null) {
            if (((PlayerDownButNotOut)player).downButNotOut$isDowned() && this.ticksUntilBleedOut > 0L) {
                --ticksUntilBleedOut;
                if (ticksUntilBleedOut <= 0L) {
                    ((PlayerDownButNotOut)player).downButNotOut$bleedOut(damageSource);
                }
            }
        } else {
            DownButNotOut.LOGGER.info("BleedOutTimer player is null!");
        }
    }

    public void register() {
        ServerTickEvents.END_SERVER_TICK.register(this);
    }

    public long getTicksUntilBleedOut() {
        return ticksUntilBleedOut;
    }

    public void setTicksUntilBleedOut(long ticksUntilBleedOut) {
        this.ticksUntilBleedOut = ticksUntilBleedOut;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(ServerPlayerEntity player) {
        this.player = player;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public void setDamageSource(DamageSource damageSource) {
        this.damageSource = damageSource;
    }
}
