package com.phasmoware.down_but_not_out.mixin;

import com.mojang.authlib.GameProfile;
import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import com.phasmoware.down_but_not_out.util.ServerCrawlUtility;
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
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerDuck {

    @Unique
    private final BleedOutTimer bleedOutTimer = new BleedOutTimer((ServerPlayerEntity) (Object) this);

    @Unique
    private final ReviveTimer reviveTimer = new ReviveTimer(null, (ServerPlayerEntity) (Object) this);
    @Unique
    public long ticksSinceLastUpdate;
    @Unique
    private ShulkerEntity invisibleShulkerEntity;
    @Unique
    private ArmorStandEntity invisibleArmorStandEntity;
    @Unique
    private Text lastUpdateText;

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract ServerWorld getEntityWorld();

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void injectTickPlayer(CallbackInfo ci) {
        ticksSinceLastUpdate++;
        reviveTimer.tick();
        bleedOutTimer.tick();
        if (isDowned(this)) {
            ServerCrawlUtility.forceCrawlPose(this);
        }
    }

    @Override
    public BleedOutTimer dbno$getBleedOutTimer() {
        return this.bleedOutTimer;
    }

    @Override
    public ReviveTimer dbno$getReviveTimer() {
        return this.reviveTimer;
    }

    @Override
    public ShulkerEntity dbno$getInvisibleShulkerEntity() {
        return this.invisibleShulkerEntity;
    }

    @Override
    public void dbno$setInvisibleShulkerEntity(ShulkerEntity shulkerEntity) {
        this.invisibleShulkerEntity = shulkerEntity;
    }

    @Override
    public ArmorStandEntity dbno$getInvisibleArmorStandEntity() {
        return this.invisibleArmorStandEntity;
    }

    @Override
    public void dbno$setInvisibleArmorStandEntity(ArmorStandEntity armorStandEntity) {
        this.invisibleArmorStandEntity = armorStandEntity;
    }

    @Override
    public Text dbno$getLastUpdateText() {
        return this.lastUpdateText;
    }

    @Override
    public void dbno$setLastUpdateText(Text lastText) {
        this.lastUpdateText = lastText;
    }

    @Override
    public long dbno$getTicksSinceLastUpdate() {
        return this.ticksSinceLastUpdate;
    }

    @Override
    public void dbno$setTicksSinceLastUpdate(long ticksSinceLastUpdate) {
        this.ticksSinceLastUpdate = ticksSinceLastUpdate;
    }
}
