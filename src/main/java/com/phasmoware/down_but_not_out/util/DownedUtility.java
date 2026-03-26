package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.data.PlayerData;
import com.phasmoware.down_but_not_out.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;


public class DownedUtility {

    public static void bleedOut(ServerPlayer player, DamageSource damageSource) {
        player.setInvulnerable(false);
        // clear status effects to ensure that they are not invulnerable to the damage source type
        player.removeAllEffects();
        if (damageSource != null) {
            player.hurtServer(player.level(), damageSource, 0.5f); // should not survive unless player has totem of undying
        } else {
            // if damage source is null (because player logged out or because server restarted) try setting wither DamageSource
            player.hurtServer(player.level(), player.level().damageSources().wither(), 0.5f);
        }
    }

    public static PlayerData getPlayerData(ServerPlayer player) {
        return player.getAttachedOrCreate(ModAttachments.PLAYER_DATA);
    }

    public static void savePlayerData(ServerPlayer player, boolean isDowned, long ticksUntilBleedOut) {
        PlayerData playerData = new PlayerData(isDowned, ticksUntilBleedOut);
        if (player != null) {
            player.setAttached(ModAttachments.PLAYER_DATA, playerData);
        }
    }

    public static void savePlayerData(ServerPlayer player, boolean isDowned) {
        PlayerData playerData = null;
        if (player != null) {
            playerData = getPlayerData(player);
        }
        if (playerData != null) {
            savePlayerData(player, isDowned, playerData.ticksUntilBleedOut());
        } else {
            savePlayerData(player, isDowned, ModConfig.INSTANCE.REVIVE_DURATION_TICKS);
        }
    }

    public static boolean isDowned(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        PlayerData data = getPlayerData(player);
        return data.isDowned();
    }

    public static boolean isDowned(Player player) {
        if (player == null) {
            return false;
        }
        return isDowned((ServerPlayer) player);
    }

    public static boolean isDowned(LivingEntity player) {
        if (player == null) {
            return false;
        }
        if (!(player instanceof Player playerEntity)) {
            return false;
        }
        return isDowned(playerEntity);
    }

    public static boolean isDowned(Entity player) {
        if (player == null) {
            return false;
        }
        if (!(player instanceof LivingEntity livingEntity)) {
            return false;
        }
        return isDowned(livingEntity);
    }

    public static void applyDownedState(ServerPlayer player) {
        if (player != null) {
            player.setHealth(Constants.HEARTS_WHILE_DOWNED);
            player.addTag(Constants.DOWNED_TAG);
            savePlayerData(player,true);
            player.setInvulnerable(true);
            if (ModConfig.INSTANCE.ALLOW_CHANGE_GAME_MODE) {
                player.setGameMode(GameType.ADVENTURE);
            }
            if (ModConfig.INSTANCE.DOWNED_PLAYERS_HAVE_GLOW_EFFECT) {
                player.setGlowingTag(true);
            }
            if (ModConfig.INSTANCE.DOWNED_PLAYERS_HAVE_BLINDNESS_EFFECT) {
                MobEffectInstance darkness = new MobEffectInstance(MobEffects.DARKNESS, -1, 0, false, false);
                player.addEffect(darkness);
            }
            if (player.getVehicle() != null) {
                player.stopRiding();
            }
            AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (moveSpeed != null) {
                moveSpeed.setBaseValue(ModConfig.INSTANCE.DOWNED_MOVE_SPEED);
            }
        } else {
            Constants.LOGGER.error(Constants.PLAYER_IS_NULL_ON_APPLY_DOWNED_STATE_ERROR);
        }
    }

    public static void removeDownedState(ServerPlayer player) {
        if (player != null) {
            player.removeTag(Constants.DOWNED_TAG);
            savePlayerData(player, false, ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS);
            player.setInvulnerable(false);
            if (ModConfig.INSTANCE.ALLOW_CHANGE_GAME_MODE) {
                player.setGameMode(GameType.SURVIVAL);
            }
            player.setGlowingTag(false);
            AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (moveSpeed != null) {
                moveSpeed.setBaseValue(Constants.BASE_MOVE_SPEED);
            }
            player.removeEffect(MobEffects.DARKNESS);
            player.removeEffect(MobEffects.SLOWNESS);
            TeamUtility.removeTempDownedTeam(player);
        } else {
            Constants.LOGGER.error(Constants.PLAYER_IS_NULL_ON_REMOVE_DOWNED_STATE_ERROR);
        }
    }
}
