package com.phasmoware.down_but_not_out;

import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.handler.MessageHandler;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import com.phasmoware.down_but_not_out.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class StateManager {


    public static void onDeathEventOfDownedPlayer(ServerPlayer player, DamageSource damageSource) {
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;
        serverPlayer.dbno$getBleedOutTimer().reset();
        DownedUtility.removeDownedState(player);
        ServerCrawlUtility.cleanUpForceCrawlEntities((ServerPlayerDuck) player);
        Component bledOutMessage = Component.literal(Constants.BLED_OUT_MSG).withStyle(ChatFormatting.RED);
        MessageHandler.sendUpdateMessage(bledOutMessage, player);
    }

    public static void onPlayerDownedEvent(ServerPlayer player, DamageSource damageSource) {
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

        MessageHandler.broadcastMessageToPlayers(player.getName().tryCollapseToString() +
                Constants.DOWNED_STATE_MSG, player.level(), ChatFormatting.RED);

        MessageHandler.sendClickableGiveUpMessage(player);
    }

    public static void onPlayerDownedInEmptyServer(ServerPlayer player) {
        Component skippedDownedStateMessage = Component.literal(Constants.SKIPPED_DOWNED_STATE_MSG).withStyle(ChatFormatting.RED);
        MessageHandler.sendUpdateMessage(skippedDownedStateMessage, player);
    }

    public static void onBleedOutEvent(ServerPlayer player, DamageSource damageSource) {
        DownedUtility.bleedOut(player, damageSource);
        ServerCrawlUtility.cleanUpForceCrawlEntities((ServerPlayerDuck) player);
    }

    public static void onReviveComplete(ServerPlayer player, ServerPlayer reviver) {
        MessageHandler.broadcastMessageToPlayers(reviver.getName().tryCollapseToString() +
                Constants.REVIVED_MSG + player.getName().tryCollapseToString(), player.level(), ChatFormatting.GREEN);
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;
        ReviveUtility.applyRevivedPenalty(serverPlayer);
        SoundUtility.playRevivedSound(player);
        DownedUtility.removeDownedState(player);
        ServerCrawlUtility.cleanUpForceCrawlEntities((ServerPlayerDuck) player);
    }

    public static void onReviveInteractionEvent(ServerPlayer downed, ServerPlayer reviver) {
        ServerPlayerDuck downedPlayer = (ServerPlayerDuck) downed;
        ReviveTimer reviveTimer = downedPlayer.dbno$getReviveTimer();
        reviveTimer.continueRevive(reviver);
    }
}
