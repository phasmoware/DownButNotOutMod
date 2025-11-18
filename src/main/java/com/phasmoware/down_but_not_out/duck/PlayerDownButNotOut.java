package com.phasmoware.down_but_not_out.duck;

import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import net.minecraft.entity.damage.DamageSource;

public interface PlayerDownButNotOut {


    void downButNotOut$bleedOut(DamageSource damageSource);

    void downButNotOut$applyDowned(DamageSource damageSource);

    void downButNotOut$removeDowned();

    boolean downButNotOut$isDowned();

    void downButNotOut$revive();

    BleedOutTimer downButNotOut$getBleedOutTimer();

    void downButNotOut$setBleedOutTimerInstance(BleedOutTimer bleedOutTimerInstance);

}
