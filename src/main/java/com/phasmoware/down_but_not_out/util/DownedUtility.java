package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.config.ModConfig;
import net.minecraft.entity.*;
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
        if (damageSource != null) {
            player.damage(player.getEntityWorld(), damageSource, player.getHealth()); // should not survive
            if (!player.isDead()) {
                player.kill(player.getEntityWorld());
            }
        } else {
            player.kill(player.getEntityWorld());
        }
    }

    public static boolean isDowned(ServerPlayerEntity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Constants.DOWNED_TAG);
    }

    public static boolean isDowned(PlayerEntity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Constants.DOWNED_TAG);
    }

    public static boolean isDowned(LivingEntity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Constants.DOWNED_TAG);
    }

    public static boolean isDowned(Entity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Constants.DOWNED_TAG);
    }

    public static void applyDownedState(ServerPlayerEntity player) {
        if (player != null) {
            player.setHealth(Constants.HEARTS_WHILE_DOWNED);
            player.addCommandTag(Constants.DOWNED_TAG);
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
            EntityAttributeInstance moveSpeed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            moveSpeed.setBaseValue(ModConfig.INSTANCE.DOWNED_MOVE_SPEED);
        } else {
            Constants.LOGGER.error(Constants.PLAYER_IS_NULL_ON_APPLY_DOWNED_STATE_ERROR);
        }
    }

    public static void removeDownedState(ServerPlayerEntity player) {
        if (player != null) {
            player.removeCommandTag(Constants.DOWNED_TAG);
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
