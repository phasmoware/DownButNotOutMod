package com.phasmoware.down_but_not_out.mixin;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStand.class)
public interface ArmorStandEntityAccessor {
    @Invoker("setMarker")
    void invokeSetMarker(boolean marker);

    @Invoker("setSmall")
    void invokeSetSmall(boolean small);
}
