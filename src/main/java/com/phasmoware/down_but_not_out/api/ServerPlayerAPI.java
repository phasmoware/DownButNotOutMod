package com.phasmoware.down_but_not_out.api;

import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public interface ServerPlayerAPI {

    void downButNotOut$startReviving(ReviveTimer reviveTimer, ServerPlayerEntity reviver);

    void downButNotOut$cancelReviving(ReviveTimer reviveTimer);

    boolean downButNotOut$isBeingRevived();

    BleedOutTimer downButNotOut$getBleedOutTimer();

    void downButNotOut$setBleedOutTimer(BleedOutTimer bleedOutTimer);

    ReviveTimer downButNotOut$getReviveTimer();

    void downButNotOut$setReviveTimer(ReviveTimer reviveTimer);

    ShulkerEntity downButNotOut$getInvisibleShulkerEntity();

    void downButNotOut$setInvisibleShulkerEntity(ShulkerEntity shulkerEntity);

    ArmorStandEntity downButNotOut$getInvisibleArmorStandEntity();

    void downButNotOut$setInvisibleArmorStandEntity(ArmorStandEntity armorStandEntity);

    Text downButNotOut$getLastText();

    void downButNotOut$setLastText(Text lastText);

}
