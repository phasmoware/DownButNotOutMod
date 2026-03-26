package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class TeamUtility {

    public static boolean isOnTempDownedTeam(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        if (team == null) {
            return false;
        }
        return team.getName().equals(getTempDownedTeamName(player));
    }

    public static String getTempDownedTeamName(ServerPlayer player) {
        return Constants.MOD_ABBREV_PREFIX + player.getStringUUID();
    }

    public static Component getTempDownedTeamDisplayName(ServerPlayer player) {
        return Component.literal(Constants.MOD_ABBREV_PREFIX).append(player.getName());
    }

    public static PlayerTeam getTempDownedTeam(ServerPlayer player) {
        String teamName = getTempDownedTeamName(player);
        Scoreboard scoreboard = player.level().getScoreboard();
        return scoreboard.getPlayerTeam(teamName);
    }

    public static PlayerTeam addTempTeamToScoreboard(ServerPlayer player) {
        Scoreboard scoreboard = player.level().getScoreboard();
        String teamName = getTempDownedTeamName(player);
        Component teamDisplayName = getTempDownedTeamDisplayName(player);
        PlayerTeam team = scoreboard.addPlayerTeam(teamName);
        team.setDisplayName(teamDisplayName);
        team.setAllowFriendlyFire(true);
        team.setSeeFriendlyInvisibles(false);
        team.setCollisionRule(Team.CollisionRule.PUSH_OWN_TEAM);
        team.setColor(ChatFormatting.DARK_RED);
        return team;
    }

    public static void assignTempDownedTeam(ServerPlayer player) {
        // player can only be on one team so we should not overwrite a current team
        // only applies if player is not part of a different team already
        Scoreboard scoreboard = player.level().getScoreboard();
        String teamName = getTempDownedTeamName(player);
        PlayerTeam team = scoreboard.getPlayerTeam(teamName);
        if (player.getTeam() == null) {
            if (team == null) {
                team = addTempTeamToScoreboard(player);
            }
            scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
        }
    }

    public static void removeTempDownedTeam(ServerPlayer player) {
        Scoreboard scoreboard = player.level().getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(getTempDownedTeamName(player));
        if (team != null) {
            scoreboard.removePlayerTeam(team);
        }
    }

    public static void updateTempTeamColor(ServerPlayer player, ChatFormatting color) {
        if (getTempDownedTeam(player) != null) {
            PlayerTeam team = getTempDownedTeam(player);
            if (team != null && !(team.getColor().equals(color))) {
                team.setColor(color);
            }
        }
    }

    public static void updateRevivingTeamColor(ServerPlayer player) {
        updateTempTeamColor(player, ChatFormatting.AQUA);
    }

    public static void updateBleedOutStatusTeamColor(ServerPlayer player, float progress) {
        updateTempTeamColor(player, getProgressColor(progress));
    }

    public static ChatFormatting getProgressColor(float progress) {
        if (progress == 0f) {
            return ChatFormatting.GRAY;
        } else if (progress > 0f && progress <= 0.25f) {
            return ChatFormatting.YELLOW;
        } else if (progress > 0.25f && progress <= 0.5f) {
            return ChatFormatting.GOLD;
        } else if (progress > 0.5f && progress <= 0.75f) {
            return ChatFormatting.RED;
        } else if (progress > 0.75f && progress <= 0.99f) {
            return ChatFormatting.DARK_RED;
        } else {
            return ChatFormatting.GRAY;
        }
    }

    public static void assignShulkerAndArmorStandToTempDownedTeam(ServerPlayer player) {
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;
        Shulker shulker = serverPlayer.dbno$getInvisibleShulkerEntity();
        ArmorStand armorStandEntity = serverPlayer.dbno$getInvisibleArmorStandEntity();
        Scoreboard scoreboard = player.level().getScoreboard();
        PlayerTeam team = getTempDownedTeam(player);
        if (team == null) {
            team = addTempTeamToScoreboard(player);
        }
        if (shulker != null && armorStandEntity != null) {
            scoreboard.addPlayerToTeam(shulker.getScoreboardName(), team);
            scoreboard.addPlayerToTeam(armorStandEntity.getScoreboardName(), team);
        }
    }
}