package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.data.PlayerData;
import com.phasmoware.down_but_not_out.registry.ModAttachments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;


public class DownedUtility {

    public static void bleedOut(ServerPlayerEntity player, DamageSource damageSource) {
        player.setInvulnerable(false);
        // clear status effects to ensure that they are not invulnerable to the damage source type
        player.clearStatusEffects();
        if (damageSource != null) {
            player.damage(player.getEntityWorld(), damageSource, 0.1f); // should not survive
        } else {
            // if damage source is null (because player logged out or because server restarted) try setting wither DamageSource
            player.damage(player.getEntityWorld(), player.getEntityWorld().getDamageSources().wither(), 0.1f);
        }
        if (!player.isDead()) {
            // if player somehow survived with temporary health or something, fall back to kill command
            player.kill(player.getEntityWorld());
        }
    }

    public static PlayerData getPlayerData(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(ModAttachments.PLAYER_DATA);
    }

    public static void savePlayerData(ServerPlayerEntity player, boolean isDowned, long ticksUntilBleedOut) {
        PlayerData playerData = new PlayerData(isDowned, ticksUntilBleedOut);
        if (player != null) {
            player.setAttached(ModAttachments.PLAYER_DATA, playerData);
        }
    }

    public static void savePlayerData(ServerPlayerEntity player, boolean isDowned) {
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

    public static boolean isDowned(ServerPlayerEntity player) {
        if (player == null) {
            return false;
        }
        PlayerData data = getPlayerData(player);
        return data.isDowned();
    }

    public static boolean isDowned(PlayerEntity player) {
        if (player == null) {
            return false;
        }
        return isDowned((ServerPlayerEntity) player);
    }

    public static boolean isDowned(LivingEntity player) {
        if (player == null) {
            return false;
        }
        if (!(player instanceof PlayerEntity playerEntity)) {
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

    public static void applyDownedState(ServerPlayerEntity player) {
        if (player != null) {
            player.setHealth(Constants.HEARTS_WHILE_DOWNED);
            player.addCommandTag(Constants.DOWNED_TAG);
            savePlayerData(player,true);
            player.setInvulnerable(true);
            if (ModConfig.INSTANCE.ALLOW_CHANGE_GAME_MODE) {
                player.changeGameMode(GameMode.ADVENTURE);
            }
            if (ModConfig.INSTANCE.DOWNED_PLAYERS_HAVE_GLOW_EFFECT) {
                player.setGlowing(true);
            }
            if (ModConfig.INSTANCE.DOWNED_PLAYERS_HAVE_BLINDNESS_EFFECT) {
                StatusEffectInstance darkness = new StatusEffectInstance(StatusEffects.DARKNESS, -1, 0, false, false);
                player.addStatusEffect(darkness);
            }
            if (player.getVehicle() != null) {
                player.stopRiding();
            }
            EntityAttributeInstance moveSpeed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            moveSpeed.setBaseValue(ModConfig.INSTANCE.DOWNED_MOVE_SPEED);
        } else {
            Constants.LOGGER.error(Constants.PLAYER_IS_NULL_ON_APPLY_DOWNED_STATE_ERROR);
        }
    }

    public static void removeDownedState(ServerPlayerEntity player) {
        if (player != null) {
            player.removeCommandTag(Constants.DOWNED_TAG);
            savePlayerData(player, false, ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS);
            player.setInvulnerable(false);
            if (ModConfig.INSTANCE.ALLOW_CHANGE_GAME_MODE) {
                player.changeGameMode(GameMode.SURVIVAL);
            }
            player.setGlowing(false);
            EntityAttributeInstance moveSpeed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            moveSpeed.setBaseValue(Constants.BASE_MOVE_SPEED);
            player.removeStatusEffect(StatusEffects.DARKNESS);
            player.removeStatusEffect(StatusEffects.SLOWNESS);
            TeamUtility.removeTempDownedTeam(player);
        } else {
            Constants.LOGGER.error(Constants.PLAYER_IS_NULL_ON_REMOVE_DOWNED_STATE_ERROR);
        }
    }
}
