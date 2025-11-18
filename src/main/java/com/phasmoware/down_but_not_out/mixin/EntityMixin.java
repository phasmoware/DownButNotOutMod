package com.phasmoware.down_but_not_out.mixin;

import com.phasmoware.down_but_not_out.duck.PlayerDownButNotOut;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Final
    @Shadow
    protected static TrackedData<EntityPose> POSE;

    @Shadow
    public abstract DataTracker getDataTracker();

    @Inject(method = "setPose", at = @At("HEAD"), cancellable = true)
    void injectSetPose(EntityPose pose, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player) {
            if (((PlayerDownButNotOut) player).downButNotOut$isDowned()) {
                getDataTracker().set(POSE, EntityPose.SWIMMING);
                ci.cancel();
            }
        }
    }
}
