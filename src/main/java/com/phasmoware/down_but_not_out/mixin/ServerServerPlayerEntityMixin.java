package com.phasmoware.down_but_not_out.mixin;

import com.mojang.authlib.GameProfile;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import com.phasmoware.down_but_not_out.util.DownedUtility;
import com.phasmoware.down_but_not_out.util.SoundUtility;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ShulkerEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
                DownedUtility.setInvisibleShulkerArmorStandRider(this, getEntityWorld());
            }
        }
    }

    @Override
    public void downButNotOut$bleedOut(DamageSource damageSource) {
        DownedUtility.bleedOut((ServerPlayerEntity) (Object) this, damageSource);
        DownedUtility.cleanUpInvisibleEntities(this);
    }

    @Override
    public void downButNotOut$applyDowned(DamageSource damageSource) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        DownedUtility.applyDownedState(player);

        SoundUtility.playDownedSound(player);

        // set a bleed out timer (original damageSource will be used for the death)
        // message and statistics
        if (this.bleedOutTimer == null) {
            this.bleedOutTimer = new BleedOutTimer(ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS, player, damageSource);
            this.bleedOutTimer.register();
        }

        DownedUtility.setInvisibleShulkerArmorStandRider(this, getEntityWorld());
    }

    @Override
    public void downButNotOut$removeDowned() {
        if (this.bleedOutTimer != null) {
            this.bleedOutTimer.setPlayer(null);
            this.bleedOutTimer = null;
        }
        DownedUtility.removeDownedState((ServerPlayerEntity)  (Object) this);
        DownedUtility.cleanUpInvisibleEntities(this);
    }


    @Override
    public void downButNotOut$revive() {
        this.downButNotOut$cancelReviving(this.reviveTimer);
        this.bleedOutTimer.setTicksUntilBleedOut(ModConfig.INSTANCE.BLEEDING_OUT_DURATION_TICKS);
        SoundUtility.playRevivedSound((ServerPlayerEntity) (Object) this);

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
    public void downButNotOut$setInvisibleShulkerEntity(ShulkerEntity shulkerEntity) {
        this.invisibleShulkerEntity = shulkerEntity;
    }

    @Override
    public ArmorStandEntity downButNotOut$getInvisibleArmorStandEntity() {
        return this.invisibleArmorStandEntity;
    }

    @Override
    public void downButNotOut$setInvisibleArmorStandEntity(ArmorStandEntity armorStandEntity) {
        this.invisibleArmorStandEntity = armorStandEntity;
    }

}
