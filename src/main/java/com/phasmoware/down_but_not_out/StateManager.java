package com.phasmoware.down_but_not_out;

import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.handler.MessageHandler;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import com.phasmoware.down_but_not_out.util.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class StateManager {


    public static void onDeathEventOfDownedPlayer(ServerPlayerEntity player, DamageSource damageSource) {
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;
        serverPlayer.dbno$getBleedOutTimer().reset();
        DownedUtility.removeDownedState(player);
        ServerCrawlUtility.cleanUpForceCrawlEntities((ServerPlayerDuck) player);
        Text bledOutMessage = Text.literal(Constants.BLED_OUT_MSG).formatted(Formatting.RED);
        MessageHandler.sendUpdateMessage(bledOutMessage, player);
    }

    public static void onPlayerDownedEvent(ServerPlayerEntity player, DamageSource damageSource) {
        ServerCrawlUtility.setInvisibleShulkerArmorStandRider((ServerPlayerDuck) player);
        DownedUtility.applyDownedState(player);
        SoundUtility.playDownedSound(player);
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;

        // original damageSource will be used for the death if possible
        serverPlayer.dbno$getBleedOutTimer().setDamageSource(damageSource);

        if (ModConfig.INSTANCE.USE_CUSTOM_DOWNED_TEAMS) {
            TeamUtility.assignTempDownedTeam(player);
            TeamUtility.assignShulkerAndArmorStandToTempDownedTeam(player);
        }

        MessageHandler.broadcastMessageToPlayers(player.getName().getLiteralString() +
                Constants.DOWNED_STATE_MSG, player.getEntityWorld(), Formatting.RED);

        MessageHandler.sendClickableGiveUpMessage(player);
    }

    public static void onPlayerDownedInEmptyServer(ServerPlayerEntity player) {
        Text skippedDownedStateMessage = Text.literal(Constants.SKIPPED_DOWNED_STATE_MSG).formatted(Formatting.RED);
        MessageHandler.sendUpdateMessage(skippedDownedStateMessage, player);
    }

    public static void onBleedOutEvent(ServerPlayerEntity player, DamageSource damageSource) {
        DownedUtility.bleedOut(player, damageSource);
        ServerCrawlUtility.cleanUpForceCrawlEntities((ServerPlayerDuck) player);
    }

    public static void onReviveComplete(ServerPlayerEntity player, ServerPlayerEntity reviver) {
        MessageHandler.broadcastMessageToPlayers(reviver.getName().getLiteralString() +
                Constants.REVIVED_MSG + player.getName().getLiteralString(), player.getEntityWorld(), Formatting.GREEN);
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;
        ReviveUtility.applyRevivedPenalty(serverPlayer);
        SoundUtility.playRevivedSound(player);
        DownedUtility.removeDownedState(player);
        ServerCrawlUtility.cleanUpForceCrawlEntities((ServerPlayerDuck) player);
    }

    public static void onReviveInteractionEvent(ServerPlayerEntity downed, ServerPlayerEntity reviver) {
        ServerPlayerDuck downedPlayer = (ServerPlayerDuck) downed;
        ReviveTimer reviveTimer = downedPlayer.dbno$getReviveTimer();
        reviveTimer.continueRevive(reviver);
    }
}
