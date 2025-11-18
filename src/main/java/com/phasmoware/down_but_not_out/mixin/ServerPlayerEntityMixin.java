package com.phasmoware.down_but_not_out.mixin;

import com.mojang.authlib.GameProfile;
import com.phasmoware.down_but_not_out.duck.PlayerDownButNotOut;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

    @Shadow
    public abstract ServerWorld getEntityWorld();

    @Shadow
    public abstract boolean changeGameMode(GameMode gameMode);


    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }


    @Inject(method = "tick()V", at = @At("TAIL"))
    private void injectTickPlayer(CallbackInfo ci) {
        if (downButNotOut$isDowned()) {
            this.setPose(EntityPose.DYING);
            if (this.bleedOutTimer == null) {
                bleedOutTimer = new BleedOutTimer(DownButNotOut.TICKS_UNTIL_BLEED_OUT, (ServerPlayerEntity) (Object) this, null);
                bleedOutTimer.register();
            }
        }
    }


    @Override
    public void downButNotOut$bleedOut(DamageSource damageSource) {
            this.setInvulnerable(false);
        if (damageSource != null) {
            this.damage(this.getEntityWorld(), damageSource, 100f);
        } else {
            DownButNotOut.LOGGER.debug(this.getName() + " failed to preserve DamageSource on bleed out! DamageSource: null");
            this.kill(this.getEntityWorld());
        }
    }

    @Override
    public void downButNotOut$applyDowned(DamageSource damageSource) {
        this.setHealth(DownButNotOut.HEARTS_AFTER_REVIVE);
        this.addCommandTag(DownButNotOut.DOWNED_TAG);
        this.setInvulnerable(true);
        this.changeGameMode(GameMode.ADVENTURE);
        this.setGlowing(true);
    }

    @Override
    public void downButNotOut$removeDowned() {
        this.removeCommandTag(DownButNotOut.DOWNED_TAG);
        this.setInvulnerable(false);
        this.changeGameMode(GameMode.SURVIVAL);
        this.setGlowing(false);
    }

    @Override
    public boolean downButNotOut$isDowned() {
        return this.getCommandTags().contains(DownButNotOut.DOWNED_TAG);
    }

    @Override
    public void downButNotOut$revive() {
        this.downButNotOut$removeDowned();
    }

    @Override
    public BleedOutTimer downButNotOut$getBleedOutTimer() {
        return this.bleedOutTimer;
    }

    @Override
    public void downButNotOut$setBleedOutTimerInstance(BleedOutTimer bleedOutTimerInstance) {
        this.bleedOutTimer = bleedOutTimerInstance;
    }

}
