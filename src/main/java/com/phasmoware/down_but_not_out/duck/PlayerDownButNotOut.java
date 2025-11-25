package com.phasmoware.down_but_not_out.duck;

import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerDownButNotOut {


    void downButNotOut$bleedOut(DamageSource damageSource);

    void downButNotOut$applyDowned(DamageSource damageSource);

    void downButNotOut$removeDowned();

    boolean downButNotOut$isDowned();

    void downButNotOut$revive();

    void downButNotOut$startReviving(ReviveTimer reviveTimer, ServerPlayerEntity reviver);

    void downButNotOut$cancelReviving(ReviveTimer reviveTimer);

    boolean downButNotOut$isBeingRevivedBy(ServerPlayerEntity reviver);

    boolean downButNotOut$isBeingRevived();

    BleedOutTimer downButNotOut$getBleedOutTimer();

    ReviveTimer downButNotOut$getReviveTimer();

    ShulkerEntity downButNotOut$getInvisibleShulkerEntity();

}
