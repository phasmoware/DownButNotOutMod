package com.phasmoware.down_but_not_out.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class DownedUtility {

    public static boolean isDowned(ServerPlayerEntity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Reference.DOWNED_TAG);
    }

    public static boolean isDowned(PlayerEntity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Reference.DOWNED_TAG);
    }

    public static boolean isDowned(LivingEntity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Reference.DOWNED_TAG);
    }

    public static boolean isDowned(Entity player) {
        if (player == null) {
            return false;
        }
        return player.getCommandTags().contains(Reference.DOWNED_TAG);
    }
}
