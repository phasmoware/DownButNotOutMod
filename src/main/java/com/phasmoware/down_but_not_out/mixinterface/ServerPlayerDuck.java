package com.phasmoware.down_but_not_out.mixinterface;

import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.text.Text;

public interface ServerPlayerDuck {

    BleedOutTimer dbno$getBleedOutTimer();

    ReviveTimer dbno$getReviveTimer();

    ShulkerEntity dbno$getInvisibleShulkerEntity();

    void dbno$setInvisibleShulkerEntity(ShulkerEntity shulkerEntity);

    ArmorStandEntity dbno$getInvisibleArmorStandEntity();

    void dbno$setInvisibleArmorStandEntity(ArmorStandEntity armorStandEntity);

    Text dbno$getLastUpdateText();

    void dbno$setLastUpdateText(Text lastUpdateText);

    long dbno$getTicksSinceLastUpdate();

    void dbno$setTicksSinceLastUpdate(long ticksSinceLastUpdate);
}
