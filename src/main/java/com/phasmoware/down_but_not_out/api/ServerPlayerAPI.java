package com.phasmoware.down_but_not_out.api;

import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.text.Text;

public interface ServerPlayerAPI {

    BleedOutTimer downButNotOut$getBleedOutTimer();

    ReviveTimer downButNotOut$getReviveTimer();

    ShulkerEntity downButNotOut$getInvisibleShulkerEntity();

    void downButNotOut$setInvisibleShulkerEntity(ShulkerEntity shulkerEntity);

    ArmorStandEntity downButNotOut$getInvisibleArmorStandEntity();

    void downButNotOut$setInvisibleArmorStandEntity(ArmorStandEntity armorStandEntity);

    Text downButNotOut$getLastUpdateText();

    void downButNotOut$setLastUpdateText(Text lastUpdateText);

    long downButNotOut$getTicksSinceLastUpdate();

    void downButNotOut$setTicksSinceLastUpdate(long ticksSinceLastUpdate);
}
