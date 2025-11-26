package com.phasmoware.down_but_not_out.mixin;

import com.mojang.authlib.GameProfile;
import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.manager.DownedStateManager;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import com.phasmoware.down_but_not_out.util.DownedUtility;
import com.phasmoware.down_but_not_out.util.Constants;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.phasmoware.down_but_not_out.util.DownedUtility.isDowned;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerAPI {

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

    @Unique
    private Text lastUpdateText;

    @Unique
    public long ticksSinceLastUpdate;

    @Shadow
    public abstract ServerWorld getEntityWorld();

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void injectTickPlayer(CallbackInfo ci) {
        ticksSinceLastUpdate++;
        if (isDowned(this)) {
            // if player has command tag but not in downed state
            if (this.bleedOutTimer == null) {
                DownedStateManager.onPlayerDownedEvent((ServerPlayerEntity) (Object) this, null);
            } else {
                bleedOutTimer.tick();
            }
            if (reviveTimer != null) {
                reviveTimer.tick();
            }

            // keep an invisible ShulkerEntity riding an ArmorStandEntity at player's head to force crawling pose
            // (server side workaround)
            if (!this.getVelocity().equals(Constants.DOWNED_NOT_MOVING)) {
                DownedUtility.forceCrawlPose(this);
            }
        }
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
    public void downButNotOut$setBleedOutTimer(BleedOutTimer bleedOutTimer) {
        this.bleedOutTimer = bleedOutTimer;
    }

    @Override
    public void downButNotOut$setReviveTimer(ReviveTimer reviveTimer) {
        this.reviveTimer = reviveTimer;
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

    @Override
    public Text downButNotOut$getLastUpdateText() {
        return this.lastUpdateText;
    }

    @Override
    public void downButNotOut$setLastUpdateText(Text lastText) {
        this.lastUpdateText = lastText;
    }

    @Override
    protected EntityEquipment createEquipment() {
        return super.createEquipment();
    }

    @Override
    public long downButNotOut$getTicksSinceLastUpdate() {
        return this.ticksSinceLastUpdate;
    }

    @Override
    public void downButNotOut$setTicksSinceLastUpdate(long ticksSinceLastUpdate) {
        this.ticksSinceLastUpdate = ticksSinceLastUpdate;
    }
}
