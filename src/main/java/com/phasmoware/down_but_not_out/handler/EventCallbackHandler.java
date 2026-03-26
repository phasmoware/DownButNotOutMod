package com.phasmoware.down_but_not_out.handler;

import com.phasmoware.down_but_not_out.data.PlayerData;
import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.StateManager;
import com.phasmoware.down_but_not_out.registry.ModAttachments;
import com.phasmoware.down_but_not_out.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import static com.phasmoware.down_but_not_out.util.DownedUtility.isDowned;

public class EventCallbackHandler {
    public static boolean onAllowDeathEvent(LivingEntity entity, DamageSource damageSource, float damageAmount) {

        // if mod disabled in config, allow death like normal
        if (!ModConfig.INSTANCE.MOD_ENABLED) {
            return true;
        }
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }
        if (isDowned(player)) {
            StateManager.onDeathEventOfDownedPlayer(player, damageSource);
            return true;
        }
        if (player.isInLava() && player.isOnFire() && !ModConfig.INSTANCE.ALLOW_DOWNED_STATE_IN_LAVA) {
            MessageHandler.onPlayerDownedInLava(player);
            return true;
        }
        if (ModConfig.INSTANCE.SKIP_DOWNED_STATE_IF_NO_OTHER_PLAYERS_ONLINE && player.level().getServer().getPlayerCount() <= 1) {
            StateManager.onPlayerDownedInEmptyServer(player);
            return true;
        }
        // else prevent death and apply downed state instead
        StateManager.onPlayerDownedEvent(player, damageSource);
        return false;
    }

    public static InteractionResult onConsumeDownedAction(Player playerEntity) {
        if (isDowned(playerEntity)) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onConsumeDownedItemAction(Player playerEntity, Level world, InteractionHand hand) {
        if (isDowned(playerEntity) && playerEntity.isHolding(Items.TOTEM_OF_UNDYING)) {
            StateManager.onBleedOutEvent((ServerPlayer) playerEntity, null);
            return InteractionResult.FAIL;
        } else if (isDowned(playerEntity)) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onReviveDownedInteraction(Player playerEntity, Level world, InteractionHand hand, Entity entity, EntityHitResult entityHitResult) {
        if (isDowned(playerEntity)) {
            return InteractionResult.CONSUME;
        } else if (entity instanceof ServerPlayer downed && (isDowned(entity))) {
            ServerPlayer reviver = (ServerPlayer) playerEntity;
            StateManager.onReviveInteractionEvent(downed, reviver);
        }
        return InteractionResult.PASS;
    }

    public static void onCleanUpEvent(Player playerEntity) {
        ServerCrawlUtility.cleanUpForceCrawlEntities((ServerPlayerDuck) playerEntity);
        TeamUtility.removeTempDownedTeam((ServerPlayer) playerEntity);
    }

    public static void onPlayerDisconnect(Player playerEntity) {
        ServerPlayer serverPlayerEntity = (ServerPlayer) playerEntity;
        PlayerData playerData = serverPlayerEntity.getAttached(ModAttachments.PLAYER_DATA);
        if (playerData != null && isDowned(playerEntity)) {
            ServerPlayerDuck serverPlayerDuck = (ServerPlayerDuck) serverPlayerEntity;
            DownedUtility.savePlayerData(serverPlayerEntity, true, serverPlayerDuck.dbno$getBleedOutTimer().getTicksUntilBleedOut());
        }
        onCleanUpEvent(playerEntity);
    }

    public static void onPlayerJoinWhileDowned(ServerPlayer serverPlayer) {
        if (isDowned(serverPlayer)) {
            ServerPlayerDuck serverPlayerDuck = (ServerPlayerDuck) serverPlayer;
            StateManager.onPlayerDownedEvent(serverPlayer, null);
            serverPlayerDuck.dbno$getBleedOutTimer().setTicksUntilBleedOut(DownedUtility.getPlayerData(serverPlayer).ticksUntilBleedOut());
            ReviveUtility.applyRevivedPenalty(serverPlayerDuck);
        }
    }

    public static void onEntityChangeWorld(Entity originalEntity, Entity newEntity, ServerLevel origin, ServerLevel destination) {
        if (newEntity instanceof Shulker || newEntity instanceof ArmorStand) {
            if (newEntity.getTags().contains(Constants.DOWNED_TAG)) {
                newEntity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
            }
        }
    }
}
