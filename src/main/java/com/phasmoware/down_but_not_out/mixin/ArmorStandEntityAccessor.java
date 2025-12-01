package com.phasmoware.down_but_not_out.mixin;

import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandEntityAccessor {
    @Invoker("setMarker")
    void invokeSetMarker(boolean marker);

    @Invoker("setSmall")
    void invokeSetSmall(boolean small);
}
