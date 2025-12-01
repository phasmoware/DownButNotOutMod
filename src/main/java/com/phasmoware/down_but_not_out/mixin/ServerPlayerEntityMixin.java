package com.phasmoware.down_but_not_out.mixin;

import com.mojang.authlib.GameProfile;
import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import com.phasmoware.down_but_not_out.util.DownedUtility;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
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
    private final BleedOutTimer bleedOutTimer = new BleedOutTimer((ServerPlayerEntity) (Object) this);

    @Unique
    private final ReviveTimer reviveTimer = new ReviveTimer(null, (ServerPlayerEntity) (Object) this);

    @Unique
    private ShulkerEntity invisibleShulkerEntity;

    @Unique
    private ArmorStandEntity invisibleArmorStandEntity;

    @Unique
    private Text lastUpdateText;

    @Unique
    public long ticksSinceLastUpdate;

    @Shadow
    public abstract ServerWorld getEntityWorld();

    @Shadow
    @Final
    private MinecraftServer server;

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void injectTickPlayer(CallbackInfo ci) {
        ticksSinceLastUpdate++;
        reviveTimer.tick();
        bleedOutTimer.tick();
        if (isDowned(this)) {
            // keep an invisible ShulkerEntity riding an ArmorStandEntity at player's head to force crawling pose
            // (server side workaround)
            DownedUtility.forceCrawlPose(this);
        }
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

    @Override
    public Text downButNotOut$getLastUpdateText() {
        return this.lastUpdateText;
    }

    @Override
    public void downButNotOut$setLastUpdateText(Text lastText) {
        this.lastUpdateText = lastText;
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
