package com.phasmoware.down_but_not_out.mixin;

import com.mojang.authlib.GameProfile;
import com.phasmoware.down_but_not_out.duck.PlayerDownButNotOut;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import com.phasmoware.down_but_not_out.DownButNotOut;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements PlayerDownButNotOut {

    @Unique
    private BleedOutTimer bleedOutTimer;

    @Unique
    private ReviveTimer reviveTimer;

    @Unique
    private ShulkerEntity invisibleShulkerEntity;

    @Unique
    private boolean isBeingRevived;

    @Shadow
    public abstract ServerWorld getEntityWorld();

    @SuppressWarnings("UnusedReturnValue")
    @Shadow
    public abstract boolean changeGameMode(GameMode gameMode);


    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }


    @Inject(method = "tick()V", at = @At("TAIL"))
    private void injectTickPlayer(CallbackInfo ci) {
        if (downButNotOut$isDowned()) {
            if (this.bleedOutTimer == null) {
                downButNotOut$applyDowned(null);
            }
            forceCrawlPose();
        }
    }

    // keep an invisible shulker entity at player's head to force crawling pose (server side workaround)
    @Unique
    private void forceCrawlPose() {
        Vec3d headPosition = new Vec3d(this.getX(), this.getY(), this.getZ()).offset(Direction.UP, 1.0);
        if (!this.isInFluid()) {
            this.invisibleShulkerEntity.setPosition(headPosition.x, headPosition.y, headPosition.z);
        }
    }

    @Unique
    private void createInvisibleShulkerBox() {
        ShulkerEntity shulkerEntity = new ShulkerEntity(EntityType.SHULKER, this.getEntityWorld());
        this.invisibleShulkerEntity = shulkerEntity;
        shulkerEntity.setInvulnerable(true);
        shulkerEntity.setNoGravity(true);
        shulkerEntity.setAiDisabled(true);
        shulkerEntity.setSilent(true);
        shulkerEntity.setCustomNameVisible(false);
        shulkerEntity.setDespawnCounter((int)DownButNotOut.TICKS_UNTIL_BLEED_OUT * 2);
        EntityAttributeInstance attributeInstance = shulkerEntity.getAttributeInstance(EntityAttributes.SCALE);
        attributeInstance.setBaseValue(DownButNotOut.SHULKER_ENTITY_SCALE);
        StatusEffectInstance instance =
                new StatusEffectInstance(StatusEffects.INVISIBILITY, -1, 0, false, false);
        shulkerEntity.addStatusEffect(instance);
        this.getEntityWorld().spawnEntity(this.invisibleShulkerEntity);
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
            DownButNotOut.LOGGER.info(this.getName() + "'s DamageSource is NULL on bleed out");
            this.invisibleShulkerEntity.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void downButNotOut$applyDowned(DamageSource damageSource) {
        this.setHealth(DownButNotOut.HEARTS_AFTER_REVIVE);
        this.addCommandTag(DownButNotOut.DOWNED_TAG);
        this.setInvulnerable(true);
        this.changeGameMode(GameMode.ADVENTURE);
        this.setGlowing(true);
        StatusEffectInstance darkness =
                new StatusEffectInstance(StatusEffects.DARKNESS, -1, 0, false, false);
        this.addStatusEffect(darkness);
        EntityAttributeInstance moveSpeed = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        this.getEntityWorld().playSoundFromEntity(null, this, SoundEvents.ENTITY_TURTLE_EGG_BREAK,
                SoundCategory.PLAYERS, DownButNotOut.DOWNED_SOUND_VOLUME, DownButNotOut.DOWNED_SOUND_PITCH);
        moveSpeed.setBaseValue(DownButNotOut.DOWNED_MOVE_SPEED);

        // set a bleed out timer. The original damageSource will be used for the death message and statistics
        if (this.bleedOutTimer == null) {
            this.bleedOutTimer = new BleedOutTimer(DownButNotOut.TICKS_UNTIL_BLEED_OUT, (ServerPlayerEntity) (Object) this, damageSource);
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
        this.removeCommandTag(DownButNotOut.DOWNED_TAG);
        this.setInvulnerable(false);
        this.changeGameMode(GameMode.SURVIVAL);
        this.setGlowing(false);
        EntityAttributeInstance moveSpeed = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        moveSpeed.setBaseValue(DownButNotOut.BASE_MOVE_SPEED);
        this.removeStatusEffect(StatusEffects.DARKNESS);
        this.invisibleShulkerEntity.remove(RemovalReason.DISCARDED);
    }

    @Override
    public boolean downButNotOut$isDowned() {
        return this.getCommandTags().contains(DownButNotOut.DOWNED_TAG);
    }

    @Override
    public void downButNotOut$revive() {
        this.downButNotOut$cancelReviving(this.reviveTimer);
        this.bleedOutTimer.setTicksUntilBleedOut(DownButNotOut.TICKS_UNTIL_BLEED_OUT);
        this.getEntityWorld().playSoundFromEntity(null, this, SoundEvents.ITEM_TRIDENT_RETURN,
                SoundCategory.PLAYERS, DownButNotOut.REVIVED_SOUND_VOLUME, DownButNotOut.REVIVED_SOUND_PITCH);
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

}
