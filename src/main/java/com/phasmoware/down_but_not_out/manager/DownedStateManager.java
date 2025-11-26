package com.phasmoware.down_but_not_out.manager;

import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.handler.SendMessageHandler;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import com.phasmoware.down_but_not_out.util.DownedUtility;
import com.phasmoware.down_but_not_out.util.Reference;
import com.phasmoware.down_but_not_out.util.SoundUtility;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DownedStateManager {

    public static void onDeathEventOfDownedPlayer(ServerPlayerEntity player, DamageSource damageSource) {
        ServerPlayerAPI serverPlayer = (ServerPlayerAPI) player;
        if (serverPlayer.downButNotOut$getBleedOutTimer() != null) {
            serverPlayer.downButNotOut$getBleedOutTimer().setPlayer(null);
            serverPlayer.downButNotOut$setBleedOutTimer(null);
        }
        DownedUtility.removeDownedState(player);
        DownedUtility.cleanUpInvisibleEntities((ServerPlayerAPI) player);
        player.sendMessage(Text.literal(Reference.BLED_OUT_MSG).formatted(Formatting.RED),
                ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
    }

    public static void onPlayerDownedEvent(ServerPlayerEntity player, DamageSource damageSource) {
        DownedUtility.applyDownedState(player);
        SoundUtility.playDownedSound(player);
        ServerPlayerAPI serverPlayer = (ServerPlayerAPI) player;

        // set a bleed out timer (original damageSource will be used for the death)
        // message and statistics
        if (serverPlayer.downButNotOut$getBleedOutTimer() == null) {
           serverPlayer.downButNotOut$setBleedOutTimer(new BleedOutTimer(ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS, player, damageSource));
            serverPlayer.downButNotOut$getBleedOutTimer().register();
        }

        DownedUtility.setInvisibleShulkerArmorStandRider((ServerPlayerAPI) player, player.getEntityWorld());

        SendMessageHandler.broadcastMessageToPlayers(player.getName().getLiteralString() + Reference.DOWNED_STATE_MSG,
                player.getEntityWorld(), Formatting.RED);

        player.sendMessage(Text.literal("[Click here to give up] or use command /bleedout").setStyle(Style.EMPTY.withUnderline(true)
                .withBold(true).withClickEvent(new ClickEvent.RunCommand("bleedout"))), false);
    }

    public static void onBleedOutEvent(ServerPlayerEntity player, DamageSource damageSource) {
        DownedUtility.bleedOut(player, damageSource);
        DownedUtility.cleanUpInvisibleEntities((ServerPlayerAPI) player);
    }

    public static void onReviveEvent(ServerPlayerEntity player, ServerPlayerEntity reviver) {
        SendMessageHandler.broadcastMessageToPlayers(reviver.getName().getLiteralString() + Reference.REVIVED_MSG +
                player.getName().getLiteralString(), player.getEntityWorld(), Formatting.GREEN);
        ServerPlayerAPI serverPlayer = (ServerPlayerAPI) player;
        serverPlayer.downButNotOut$cancelReviving(serverPlayer.downButNotOut$getReviveTimer());
        serverPlayer.downButNotOut$getBleedOutTimer().setTicksUntilBleedOut(ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS);
        SoundUtility.playRevivedSound(player);
        DownedUtility.removeDownedState(player);
        DownedUtility.cleanUpInvisibleEntities((ServerPlayerAPI) player);
    }

    public static void onReviveInteractionEvent(ServerPlayerEntity downed, ServerPlayerEntity reviver) {
        ReviveTimer reviveTimer = ((ServerPlayerAPI) downed).downButNotOut$getReviveTimer();
        if (!DownedUtility.playerIsGettingRevivedBy(downed, reviver)) {
            if (reviveTimer == null) {
                reviveTimer = new ReviveTimer(reviver, downed);
                reviveTimer.register();
                ((ServerPlayerAPI) downed).downButNotOut$startReviving(reviveTimer, reviver);
                reviveTimer.startReviveInteraction();
            } else if (reviveTimer.getReviver() != null
                    && !(reviveTimer.getReviver().equals(reviver))) {
                reviveTimer.reset(reviver);
                ((ServerPlayerAPI) downed).downButNotOut$cancelReviving(reviveTimer);
                ((ServerPlayerAPI) downed).downButNotOut$startReviving(reviveTimer, reviver);
                reviveTimer.startReviveInteraction();
            }
        } else if (DownedUtility.playerIsGettingRevivedBy(downed, reviver)) {
            reviveTimer.startReviveInteraction();
        }
    }
}
