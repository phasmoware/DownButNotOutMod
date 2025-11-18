package com.phasmoware.down_but_not_out.mixin;

import com.phasmoware.down_but_not_out.duck.PlayerDownButNotOut;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "canChangeIntoPose", at = @At("HEAD"), cancellable = true)
    void injectCanChangeIntoPose(EntityPose pose, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (((PlayerDownButNotOut) player).downButNotOut$isDowned()) {
            if (pose.equals(EntityPose.STANDING)) {
                cir.setReturnValue(false);
                cir.cancel();
            } else if (pose.equals(EntityPose.SWIMMING)) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "getExpectedPose", at = @At("HEAD"), cancellable = true)
    void injectGetExpectedPose(CallbackInfoReturnable<EntityPose> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (((PlayerDownButNotOut) player).downButNotOut$isDowned()) {
            cir.setReturnValue(EntityPose.SWIMMING);
            cir.cancel();
        }
    }

    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    void injectUpdatePose(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (((PlayerDownButNotOut) player).downButNotOut$isDowned()) {
            player.setPose(EntityPose.SWIMMING);
            ci.cancel();
        }
    }
}
