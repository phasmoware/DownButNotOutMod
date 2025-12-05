package com.phasmoware.down_but_not_out.manager;

import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerEntityDuck;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.handler.MessageHandler;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import com.phasmoware.down_but_not_out.util.Constants;
import com.phasmoware.down_but_not_out.util.DownedUtility;
import com.phasmoware.down_but_not_out.util.SoundUtility;
import com.phasmoware.down_but_not_out.util.TeamUtility;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.phasmoware.down_but_not_out.util.DownedUtility.isDowned;

public class DownedStateManager {


    public static void onDeathEventOfDownedPlayer(ServerPlayerEntity player, DamageSource damageSource) {
        ServerPlayerEntityDuck serverPlayer = (ServerPlayerEntityDuck) player;
        serverPlayer.downButNotOut$getBleedOutTimer().reset();
        DownedUtility.removeDownedState(player);
        DownedUtility.cleanUpInvisibleEntities((ServerPlayerEntityDuck) player);
        Text bledOutMessage = Text.literal(Constants.BLED_OUT_MSG).formatted(Formatting.RED);
        MessageHandler.sendUpdateMessage(bledOutMessage, player);
    }

    public static void onPlayerDownedEvent(ServerPlayerEntity player, DamageSource damageSource) {
        DownedUtility.applyDownedState(player);
        SoundUtility.playDownedSound(player);
        ServerPlayerEntityDuck serverPlayer = (ServerPlayerEntityDuck) player;

        // original damageSource will be used for the death if possible
        serverPlayer.downButNotOut$getBleedOutTimer().setDamageSource(damageSource);

        DownedUtility.setInvisibleShulkerArmorStandRider((ServerPlayerEntityDuck) player);

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
        DownedUtility.cleanUpInvisibleEntities((ServerPlayerEntityDuck) player);
    }

    public static void onReviveComplete(ServerPlayerEntity player, ServerPlayerEntity reviver) {
        MessageHandler.broadcastMessageToPlayers(reviver.getName().getLiteralString() +
                Constants.REVIVED_MSG + player.getName().getLiteralString(), player.getEntityWorld(), Formatting.GREEN);
        ServerPlayerEntityDuck serverPlayer = (ServerPlayerEntityDuck) player;
        DownedUtility.applyRevivedPenalty(serverPlayer);
        SoundUtility.playRevivedSound(player);
        DownedUtility.removeDownedState(player);
        DownedUtility.cleanUpInvisibleEntities((ServerPlayerEntityDuck) player);
    }

    public static void onReviveInteractionEvent(ServerPlayerEntity downed, ServerPlayerEntity reviver) {
        ServerPlayerEntityDuck downedPlayer = (ServerPlayerEntityDuck) downed;
        ReviveTimer reviveTimer = downedPlayer.downButNotOut$getReviveTimer();
        reviveTimer.continueRevive(reviver);
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
        if (!(reviver.getMainHandStack().isEmpty() && ModConfig.INSTANCE.REVIVING_REQUIRES_EMPTY_HAND)) {
            onPlayerRevivingWithoutEmptyHand(reviver, downed);
            return false;
        }
        if ((reviver.squaredDistanceTo(downed) > reviver.getBlockInteractionRange())) {
            onPlayerRevivingTooFarAway(reviver, downed);
            return false;
        }
        if (!DownedUtility.isLookingAtPlayer(reviver, downed)) {
            onPlayerLookingAwayWhileReviving(reviver, downed);
            return false;
        }
        return true;
    }


    public static void onPlayerDownedInLava(ServerPlayerEntity player) {
        Text skippedDownedStateMessage = Text.literal(Constants.LAVA_PREVENTED_DOWNED_MSG).formatted(Formatting.RED);
        MessageHandler.sendUpdateMessage(skippedDownedStateMessage, player);
    }

    public static void onPlayerRevivingWithoutEmptyHand(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
        Text msgToReviver = Text.literal("Use a free hand to revive them!").formatted(Formatting.RED);
        MessageHandler.sendUpdateMessage(msgToReviver, reviver);
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, downed);
    }

    public static void onPlayerRevivingTooFarAway(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
        Text msgToReviver = Text.literal("Too far away to revive them!").formatted(Formatting.RED);
        MessageHandler.sendUpdateMessage(msgToReviver, reviver);
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, downed);
    }

    public static void onPlayerLookingAwayWhileReviving(ServerPlayerEntity reviver, ServerPlayerEntity downed) {
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, reviver);
        MessageHandler.sendUpdateMessage(Constants.REVIVE_CANCELED_TEXT, downed);
    }


}
