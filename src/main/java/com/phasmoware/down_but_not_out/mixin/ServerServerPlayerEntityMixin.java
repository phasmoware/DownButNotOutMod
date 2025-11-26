package com.phasmoware.down_but_not_out.mixin;

import com.mojang.authlib.GameProfile;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import com.phasmoware.down_but_not_out.util.Reference;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ShulkerEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.phasmoware.down_but_not_out.util.DownedUtility.isDowned;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerAPI {

    @Unique
    private BleedOutTimer bleedOutTimer;

    @Unique
    private ReviveTimer reviveTimer;

    @Unique
    private ShulkerEntity invisibleShulkerEntity;

    @Unique
    private ArmorStandEntity invisibleArmorStandEntity;

    @Unique
    private boolean isBeingRevived;

    @Shadow
    public abstract ServerWorld getEntityWorld();

    @SuppressWarnings("UnusedReturnValue")
    @Shadow
    public abstract boolean changeGameMode(GameMode gameMode);

    public ServerServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void injectTickPlayer(CallbackInfo ci) {
        if (isDowned(this)) {
            if (this.bleedOutTimer == null) {
                downButNotOut$applyDowned(null);
            }
            forceCrawlPose();
        }
    }

    // keep an invisible ShulkerEntity riding an ArmorStandEntity at player's head to force crawling pose
    // (server side workaround)
    @Unique
    private void forceCrawlPose() {
        this.setPose(EntityPose.SWIMMING);
        Vec3d headPosition = new Vec3d(this.getX(), this.getY(), this.getZ()).offset(Direction.UP, 1);
        if (!this.isInFluid()) {
            if (this.invisibleArmorStandEntity != null && !this.invisibleArmorStandEntity.isRemoved()) {
                this.invisibleArmorStandEntity.setPosition(headPosition.x, headPosition.y, headPosition.z);
                this.invisibleArmorStandEntity.setVelocity(Vec3d.ZERO);
                this.invisibleArmorStandEntity.fallDistance = 0;
            } else if (this.invisibleShulkerEntity == null || this.invisibleShulkerEntity.isRemoved()) {
                createInvisibleShulkerBox();
            }
        }
    }

    @Unique
    private void createInvisibleShulkerBox() {
        // create ArmorStand (vehicle)
        ArmorStandEntity armorStand = new ArmorStandEntity(EntityType.ARMOR_STAND, this.getEntityWorld());
        this.invisibleArmorStandEntity = armorStand;
        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);
        armorStand.setInvulnerable(true);
        armorStand.setSilent(true);
        armorStand.setCustomNameVisible(false);
        ((ArmorStandEntityAccessor) armorStand).invokeSetMarker(true);
        ((ArmorStandEntityAccessor) armorStand).invokeSetSmall(true);
        EntityAttributeInstance armorStandScale = armorStand.getAttributeInstance(EntityAttributes.SCALE);
        armorStandScale.setBaseValue(Reference.MIN_ENTITY_SCALE);


        // create Shulker (passenger)
        ShulkerEntity shulkerEntity = new ShulkerEntity(EntityType.SHULKER, this.getEntityWorld());
        this.invisibleShulkerEntity = shulkerEntity;
        shulkerEntity.setInvulnerable(true);
        shulkerEntity.setNoGravity(true);
        shulkerEntity.setAiDisabled(true);
        shulkerEntity.setSilent(true);
        shulkerEntity.setCustomNameVisible(false);

        EntityAttributeInstance attributeInstance = shulkerEntity.getAttributeInstance(EntityAttributes.SCALE);
        attributeInstance.setBaseValue(Reference.MIN_ENTITY_SCALE);

        StatusEffectInstance instance = new StatusEffectInstance(StatusEffects.INVISIBILITY, -1, 0, false, false);
        shulkerEntity.addStatusEffect(instance);

        // spawn and mount
        this.getEntityWorld().spawnEntity(armorStand);
        this.getEntityWorld().spawnEntity(shulkerEntity);
        shulkerEntity.startRiding(armorStand, true, true);
    }

    @Override
    public void downButNotOut$bleedOut(DamageSource damageSource) {
        this.setInvulnerable(false);
        if (damageSource != null) {
            this.damage(this.getEntityWorld(), damageSource, this.getMaxHealth() * 255.0F); // should not survive
            if (!this.isDead()) {
                this.kill(this.getEntityWorld());
            }
        } else {
            this.kill(this.getEntityWorld());
            Reference.LOGGER.info(this.getName() + "'s DamageSource is NULL on bleed out");
            downButNotOut$cleanupDownedEntities();
        }
    }

    @Override
    public void downButNotOut$applyDowned(DamageSource damageSource) {
        this.setHealth(Reference.HEARTS_AFTER_REVIVE);
        this.addCommandTag(Reference.DOWNED_TAG);
        this.setInvulnerable(true);
        this.changeGameMode(GameMode.ADVENTURE);
        if (ModConfig.INSTANCE.DOWNED_PLAYERS_HAVE_GLOW_EFFECT) {
            this.setGlowing(true);
        }
        if (ModConfig.INSTANCE.DOWNED_PLAYERS_HAVE_BLINDNESS_EFFECT) {
            StatusEffectInstance darkness = new StatusEffectInstance(StatusEffects.DARKNESS, -1, 0, false, false);
            this.addStatusEffect(darkness);
        }
        EntityAttributeInstance moveSpeed = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        moveSpeed.setBaseValue(ModConfig.INSTANCE.DOWNED_MOVE_SPEED);

        this.getEntityWorld().playSoundFromEntity(null, this, SoundEvents.ENTITY_TURTLE_EGG_BREAK,
                SoundCategory.PLAYERS, ModConfig.INSTANCE.DOWNED_SOUND_VOLUME, Reference.DOWNED_SOUND_PITCH);

        // set a bleed out timer (original damageSource will be used for the death)
        // message and statistics
        if (this.bleedOutTimer == null) {
            this.bleedOutTimer = new BleedOutTimer(ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS,
                    (ServerPlayerEntity) (Object) this, damageSource);
            this.bleedOutTimer.register();
        }

        createInvisibleShulkerBox();
    }

    @Override
    public void downButNotOut$removeDowned() {
        if (this.bleedOutTimer != null) {
            this.bleedOutTimer.setPlayer(null);
            this.bleedOutTimer = null;
        }
        this.removeCommandTag(Reference.DOWNED_TAG);
        this.setInvulnerable(false);
        this.changeGameMode(GameMode.SURVIVAL);
        this.setGlowing(false);
        EntityAttributeInstance moveSpeed = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        moveSpeed.setBaseValue(Reference.BASE_MOVE_SPEED);
        this.removeStatusEffect(StatusEffects.DARKNESS);
        this.removeStatusEffect(StatusEffects.SLOWNESS);
        downButNotOut$cleanupDownedEntities();
    }


    @Override
    public void downButNotOut$revive() {
        this.downButNotOut$cancelReviving(this.reviveTimer);
        this.bleedOutTimer.setTicksUntilBleedOut(ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS);
        this.getEntityWorld().playSoundFromEntity(null, this, SoundEvents.ITEM_TRIDENT_RETURN,
                SoundCategory.PLAYERS, ModConfig.INSTANCE.REVIVED_SOUND_VOLUME, Reference.REVIVED_SOUND_PITCH);
        this.downButNotOut$removeDowned();
    }

    @Override
    public boolean downButNotOut$isBeingRevivedBy(ServerPlayerEntity reviver) {
        if (this.reviveTimer == null) {
            return false;
        }
        if (reviver == null) {
            return false;
        }
        if (!(this.reviveTimer.getReviver().equals(reviver))) {
            return false;
        }
        if (!(this.reviveTimer.isValidReviver(reviver, (ServerPlayerEntity) (Object) this))) {
            return false;
        }
        return (this.isBeingRevived);
    }

    @Override
    public boolean downButNotOut$isBeingRevived() {
        return (this.isBeingRevived);
    }

    @Override
    public void downButNotOut$startReviving(ReviveTimer reviveTimer, ServerPlayerEntity reviver) {
        this.reviveTimer = reviveTimer;
        this.reviveTimer.reset(reviver);
        isBeingRevived = true;
    }

    @Override
    public void downButNotOut$cancelReviving(ReviveTimer reviveTimer) {
        reviveTimer.reset(null);
        this.reviveTimer = null;
        isBeingRevived = false;
    }

    @Override
    public BleedOutTimer downButNotOut$getBleedOutTimer() {
        return this.bleedOutTimer;
    }

    @Override
    public ReviveTimer downButNotOut$getReviveTimer() {
        return this.reviveTimer;
    }

    @Override
    public ShulkerEntity downButNotOut$getInvisibleShulkerEntity() {
        return this.invisibleShulkerEntity;
    }

    @Override
    public void downButNotOut$cleanupDownedEntities() {
        if (this.invisibleShulkerEntity != null) {
            this.invisibleShulkerEntity.remove(RemovalReason.DISCARDED);
            this.invisibleShulkerEntity = null;
        }
        if (this.invisibleArmorStandEntity != null) {
            this.invisibleArmorStandEntity.remove(RemovalReason.DISCARDED);
            this.invisibleArmorStandEntity = null;
        }
    }

}
