package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.mixin.ArmorStandEntityAccessor;
import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ServerCrawlUtility {

    public static ArmorStandEntity spawnInvisibleArmorStand(ServerPlayerEntity player) {
        ArmorStandEntity armorStand = new ArmorStandEntity(EntityType.ARMOR_STAND, player.getEntityWorld());
        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);
        armorStand.setInvulnerable(true);
        armorStand.setSilent(true);
        armorStand.setPosition(player.getEntityPos());
        if (ModConfig.INSTANCE.SHOW_REVIVE_TAG_ABOVE_PLAYER) {
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(Text.literal(Constants.CUSTOM_REVIVE_TAG_ABOVE_NAME));

        } else {
            armorStand.setCustomNameVisible(false);
        }
        EntityAttributeInstance armorStandScale = armorStand.getAttributeInstance(EntityAttributes.SCALE);
        if (armorStandScale != null) {
            armorStandScale.setBaseValue(Constants.MIN_ENTITY_SCALE);
        }
        ArmorStandEntityAccessor accessor = (ArmorStandEntityAccessor) armorStand;
        accessor.invokeSetMarker(true);
        accessor.invokeSetSmall(true);
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
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(Constants.MIN_ENTITY_SCALE);
        }
        shulkerEntity.setInvisible(true);
        StatusEffectInstance instance = new StatusEffectInstance(StatusEffects.INVISIBILITY, -1, 0, false, false);
        shulkerEntity.addStatusEffect(instance);
        player.getEntityWorld().spawnEntity(shulkerEntity);

        return shulkerEntity;
    }

    public static void setInvisibleShulkerArmorStandRider(ServerPlayerDuck player) {
        // spawns and saves invisible Shulker (passenger)
        player.dbno$setInvisibleShulkerEntity(spawnInvisibleShulker((ServerPlayerEntity) player));
        // spawns and saves ArmorStand (vehicle)
        player.dbno$setInvisibleArmorStandEntity(spawnInvisibleArmorStand((ServerPlayerEntity) player));
        // spawn and mount Shulker on top of ArmorStand
        player.dbno$getInvisibleShulkerEntity().startRiding(player.dbno$getInvisibleArmorStandEntity(), true, true);
    }

    public static void cleanUpForceCrawlEntities(ServerPlayerDuck player) {
        if (player.dbno$getInvisibleShulkerEntity() != null) {
            player.dbno$getInvisibleShulkerEntity().remove(Entity.RemovalReason.DISCARDED);
            player.dbno$setInvisibleShulkerEntity(null);
        }
        if (player.dbno$getInvisibleArmorStandEntity() != null) {
            player.dbno$getInvisibleArmorStandEntity().remove(Entity.RemovalReason.DISCARDED);
            player.dbno$setInvisibleArmorStandEntity(null);
        }
    }

    public static void forceCrawlPose(ServerPlayerDuck serverPlayer) {
        ServerPlayerEntity player = (ServerPlayerEntity) serverPlayer;
        Vec3d headPosition = player.getEntityPos().offset(Direction.UP, Constants.Y_OFFSET);
        if (serverPlayer.dbno$getInvisibleShulkerEntity().getEntityPos().squaredDistanceTo(headPosition) > 0.01) {
            if (serverPlayer.dbno$getInvisibleArmorStandEntity() != null && !serverPlayer.dbno$getInvisibleArmorStandEntity().isRemoved()) {
                serverPlayer.dbno$getInvisibleArmorStandEntity().setPosition(headPosition.x, headPosition.y, headPosition.z);
            } else if (serverPlayer.dbno$getInvisibleShulkerEntity() == null || serverPlayer.dbno$getInvisibleShulkerEntity().isRemoved()) {
                setInvisibleShulkerArmorStandRider(serverPlayer);
            }
        }
    }
}
