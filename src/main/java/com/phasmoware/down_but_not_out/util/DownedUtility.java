package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.mixin.ArmorStandEntityAccessor;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.Optional;

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
            Constants.LOGGER.error("Error: Can't apply downed state because player is null!");
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
            Constants.LOGGER.error("Error: Can't remove downed state because player is null!");
        }
    }

    public static ArmorStandEntity spawnInvisibleArmorStand(ServerPlayerEntity player) {
        ArmorStandEntity armorStand = new ArmorStandEntity(EntityType.ARMOR_STAND, player.getEntityWorld());
        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);
        armorStand.setInvulnerable(true);
        armorStand.setSilent(true);
        armorStand.setPosition(player.getEntityPos());

        ((ArmorStandEntityAccessor) armorStand).invokeSetMarker(true);
        ((ArmorStandEntityAccessor) armorStand).invokeSetSmall(true);
        if (ModConfig.INSTANCE.SHOW_REVIVE_TAG_ABOVE_PLAYER) {
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(Text.literal(Constants.CUSTOM_REVIVE_TAG_ABOVE_NAME));
        } else {
            armorStand.setCustomNameVisible(false);
        }
        EntityAttributeInstance armorStandScale = armorStand.getAttributeInstance(EntityAttributes.SCALE);
        armorStandScale.setBaseValue(Constants.MIN_ENTITY_SCALE);
        player.getEntityWorld().spawnEntity(armorStand);
        return armorStand;
    }

    public static ShulkerEntity spawnInvisibleShulker(ServerPlayerEntity player) {
        ShulkerEntity shulkerEntity = new ShulkerEntity(EntityType.SHULKER, player.getEntityWorld());
        shulkerEntity.setPosition(player.getEntityPos());
        shulkerEntity.setInvulnerable(true);
        shulkerEntity.setNoGravity(true);
        shulkerEntity.setAiDisabled(true);
        shulkerEntity.setSilent(true);
        shulkerEntity.setCustomNameVisible(false);

        EntityAttributeInstance attributeInstance = shulkerEntity.getAttributeInstance(EntityAttributes.SCALE);
        attributeInstance.setBaseValue(Constants.MIN_ENTITY_SCALE);

        shulkerEntity.setInvisible(true);
        StatusEffectInstance instance = new StatusEffectInstance(StatusEffects.INVISIBILITY, -1, 0, false, false);
        shulkerEntity.addStatusEffect(instance);
        player.getEntityWorld().spawnEntity(shulkerEntity);

        return shulkerEntity;
    }

    public static void setInvisibleShulkerArmorStandRider(ServerPlayerAPI player) {
        // create Shulker (passenger)
        player.downButNotOut$setInvisibleShulkerEntity(spawnInvisibleShulker((ServerPlayerEntity) player));
        // create ArmorStand (vehicle)
        player.downButNotOut$setInvisibleArmorStandEntity(spawnInvisibleArmorStand((ServerPlayerEntity) player));
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
        Vec3d headPosition = new Vec3d(player.getX(), player.getY(), player.getZ()).offset(Direction.UP, Constants.Y_OFFSET);
        if (serverPlayer.downButNotOut$getInvisibleShulkerEntity().getEntityPos().squaredDistanceTo(headPosition) > 0.1) {
            if (serverPlayer.downButNotOut$getInvisibleArmorStandEntity() != null && !serverPlayer.downButNotOut$getInvisibleArmorStandEntity().isRemoved()) {
                serverPlayer.downButNotOut$getInvisibleArmorStandEntity().setPosition(headPosition.x, headPosition.y, headPosition.z);
            } else if (serverPlayer.downButNotOut$getInvisibleShulkerEntity() == null || serverPlayer.downButNotOut$getInvisibleShulkerEntity().isRemoved()) {
                DownedUtility.setInvisibleShulkerArmorStandRider(serverPlayer);
            }
        }
    }

    public static void applyRevivedPenalty(ServerPlayerAPI player) {
        BleedOutTimer timer = player.downButNotOut$getBleedOutTimer();
        if (ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS > 0 && timer.getTicksUntilBleedOut() > 1) {
            player.downButNotOut$getBleedOutTimer().setReviveCooldownTicks(ModConfig.INSTANCE.REVIVE_PENALTY_COOLDOWN_TICKS);
            timer.setTicksUntilBleedOut(timer.getTicksUntilBleedOut() / ModConfig.INSTANCE.REVIVE_PENALTY_MULTIPLIER);
        }
    }

    public static boolean isLookingAtPlayer(ServerPlayerEntity viewer, ServerPlayerEntity target) {
        if (viewer == target) {
            return false;
        }
        Vec3d eyePos = viewer.getCameraPosVec(1.0f);
        Vec3d lookDir = viewer.getRotationVec(1.0f);
        Vec3d rayEnd = eyePos.add(lookDir.multiply(viewer.getEntityInteractionRange()));
        Box targetBox = target.getBoundingBox().expand(0.1);
        Optional<Vec3d> hit = targetBox.raycast(eyePos, rayEnd);
        return hit.isPresent();
    }
}
