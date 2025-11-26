package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.mixin.ArmorStandEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public class DownedUtility {

    public static void bleedOut(ServerPlayerEntity player, DamageSource damageSource) {
        player.setInvulnerable(false);
        if (damageSource != null) {
            player.damage(player.getEntityWorld(), damageSource, Reference.PLAYER_MAX_HEALTH); // should not survive
            if (!player.isDead()) {
                player.kill(player.getEntityWorld());
            }
        } else {
            player.kill(player.getEntityWorld());
            Reference.LOGGER.warn(player.getName() + "'s DamageSource is NULL on bleed out");
        }
    }

    public static boolean isDowned(ServerPlayerEntity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Reference.DOWNED_TAG);
    }

    public static boolean isDowned(PlayerEntity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Reference.DOWNED_TAG);
    }

    public static boolean isDowned(LivingEntity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Reference.DOWNED_TAG);
    }

    public static boolean isDowned(Entity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Reference.DOWNED_TAG);
    }

    public static void applyDownedState(ServerPlayerEntity player) {
        if (player != null) {
            player.setHealth(Reference.HEARTS_AFTER_REVIVE);
            player.addCommandTag(Reference.DOWNED_TAG);
            player.setInvulnerable(true);
            player.changeGameMode(GameMode.ADVENTURE);
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
            Reference.LOGGER.error("Error: Can't apply downed state because player is null!");
        }
    }

    public static void removeDownedState(ServerPlayerEntity player) {
        if (player != null) {
            player.removeCommandTag(Reference.DOWNED_TAG);
            player.setInvulnerable(false);
            player.changeGameMode(GameMode.SURVIVAL);
            player.setGlowing(false);
            EntityAttributeInstance moveSpeed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            moveSpeed.setBaseValue(Reference.BASE_MOVE_SPEED);
            player.removeStatusEffect(StatusEffects.DARKNESS);
            player.removeStatusEffect(StatusEffects.SLOWNESS);
        } else {
            Reference.LOGGER.error("Error: Can't remove downed state because player is null!");
        }
    }

    public static ArmorStandEntity spawnInvisibleArmorStand(ServerWorld world) {
        ArmorStandEntity armorStand = new ArmorStandEntity(EntityType.ARMOR_STAND, world);
        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);
        armorStand.setInvulnerable(true);
        armorStand.setSilent(true);
        armorStand.setCustomNameVisible(false);
        ((ArmorStandEntityAccessor) armorStand).invokeSetMarker(true);
        ((ArmorStandEntityAccessor) armorStand).invokeSetSmall(true);
        EntityAttributeInstance armorStandScale = armorStand.getAttributeInstance(EntityAttributes.SCALE);
        armorStandScale.setBaseValue(Reference.MIN_ENTITY_SCALE);
        world.spawnEntity(armorStand);
        return armorStand;
    }

    public static ShulkerEntity spawnInvisibleShulker(ServerWorld world) {
        ShulkerEntity shulkerEntity = new ShulkerEntity(EntityType.SHULKER, world);
        shulkerEntity.setInvulnerable(true);
        shulkerEntity.setNoGravity(true);
        shulkerEntity.setAiDisabled(true);
        shulkerEntity.setSilent(true);
        shulkerEntity.setCustomNameVisible(false);

        EntityAttributeInstance attributeInstance = shulkerEntity.getAttributeInstance(EntityAttributes.SCALE);
        attributeInstance.setBaseValue(Reference.MIN_ENTITY_SCALE);

        StatusEffectInstance instance = new StatusEffectInstance(StatusEffects.INVISIBILITY, -1, 0, false, false);
        shulkerEntity.addStatusEffect(instance);
        world.spawnEntity(shulkerEntity);
        return shulkerEntity;
    }

    public static void setInvisibleShulkerArmorStandRider(ServerPlayerAPI player, ServerWorld world) {
        // create ArmorStand (vehicle)
        player.downButNotOut$setInvisibleArmorStandEntity(spawnInvisibleArmorStand(world));
        // create Shulker (passenger)
        player.downButNotOut$setInvisibleShulkerEntity(spawnInvisibleShulker(world));
        // spawn and mount
        player.downButNotOut$getInvisibleShulkerEntity().startRiding(player.downButNotOut$getInvisibleArmorStandEntity(), true, true);
    }

    public static void cleanUpInvisibleEntities(ServerPlayerAPI player) {
        if (player.downButNotOut$getInvisibleShulkerEntity() != null) {
            player.downButNotOut$getInvisibleShulkerEntity().remove(Entity.RemovalReason.DISCARDED);
            player.downButNotOut$setInvisibleShulkerEntity(null);
        }
        if (player.downButNotOut$getInvisibleArmorStandEntity() != null) {
            player.downButNotOut$getInvisibleArmorStandEntity().remove(Entity.RemovalReason.DISCARDED);
            player.downButNotOut$setInvisibleArmorStandEntity(null);
        }
    }

    public static void forceCrawlPose(ServerPlayerAPI serverPlayer) {
        ServerPlayerEntity player = (ServerPlayerEntity) serverPlayer;
        player.setPose(EntityPose.SWIMMING);
        Vec3d headPosition = new Vec3d(player.getX(), player.getY(), player.getZ()).offset(Direction.UP, 1);
        if (!player.isInFluid()) {
            if (serverPlayer.downButNotOut$getInvisibleArmorStandEntity() != null && !serverPlayer.downButNotOut$getInvisibleArmorStandEntity().isRemoved()) {
                serverPlayer.downButNotOut$getInvisibleArmorStandEntity().setPosition(headPosition.x, headPosition.y, headPosition.z);
            } else if (serverPlayer.downButNotOut$getInvisibleShulkerEntity() == null || serverPlayer.downButNotOut$getInvisibleShulkerEntity().isRemoved()) {
                DownedUtility.setInvisibleShulkerArmorStandRider(serverPlayer, player.getEntityWorld());
            }
        }
    }

    public static boolean playerIsGettingRevivedBy(ServerPlayerEntity downed, ServerPlayerEntity reviver) {
        ServerPlayerAPI downedPlayer = (ServerPlayerAPI) downed;

        if (downedPlayer.downButNotOut$getReviveTimer() == null) {
            return false;
        }
        if (reviver == null) {
            return false;
        }
        if (!(downedPlayer.downButNotOut$getReviveTimer().getReviver().equals(reviver))) {
            return false;
        }
        if (!(downedPlayer.downButNotOut$getReviveTimer().isValidReviver(reviver, downed))) {
            return false;
        }
        return (downedPlayer.downButNotOut$isBeingRevived());
    }
}
