package com.phasmoware.down_but_not_out.timer;

import com.phasmoware.down_but_not_out.DownButNotOut;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.duck.PlayerDownButNotOut;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class ReviveTimer implements ServerTickEvents.EndTick {
    private long interactionTicks;
    private long counter;
    private ServerPlayerEntity reviver;
    private ServerPlayerEntity downed;

    public ReviveTimer(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
        this.reviver = reviver;
        this.downed = downed;
    }


    @Override
    public void onEndTick(MinecraftServer minecraftServer) {
        counter++;
        if (isValidReviver(this.reviver, this.downed)) {
            if (this.interactionTicks >= ModConfig.INSTANCE.REVIVE_DURATION_TICKS) {
                DownButNotOut.broadcastMessageToPlayers(reviver.getName().getLiteralString() + DownButNotOut.REVIVED_MSG +
                        downed.getName().getLiteralString(), downed.getEntityWorld(), Formatting.GREEN);
                ((PlayerDownButNotOut)this.downed).downButNotOut$revive();

            } else if ((counter - 10) > interactionTicks) {
                ((PlayerDownButNotOut)this.downed).downButNotOut$cancelReviving(this);
            }
        }
    }

    public void register() {
        ServerTickEvents.END_SERVER_TICK.register(this);
    }

    public boolean isValidReviver(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
        if (reviver == null || downed == null) {
            return false;
        }
        if (reviver == downed) {
            return false;
        }
        if (((PlayerDownButNotOut)reviver).downButNotOut$isDowned()) {
            return false;
        }
        if (!((PlayerDownButNotOut)downed).downButNotOut$isDowned()) {
            return false;
        }
        /*if (reviver.isUsingItem()) {
            return false;
        }*/
        /*if (!downed.isEntityLookingAtMe(reviver, DownButNotOut.CONE_SIZE, DownButNotOut.ADJUST_FOR_DISTANCE, DownButNotOut.SEE_THROUGH_TRANSPARENT_BLOCKS)) {
            return false;
        }*/

        return true;
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
}
