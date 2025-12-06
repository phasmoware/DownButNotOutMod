package com.phasmoware.down_but_not_out.util;

import com.phasmoware.down_but_not_out.mixinterface.ServerPlayerDuck;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TeamUtility {

    public static boolean isOnTempDownedTeam(ServerPlayerEntity player) {
        Team team = player.getScoreboardTeam();
        if (team == null) {
            return false;
        }
        return team.getName().equals(getTempDownedTeamName(player));
    }

    public static String getTempDownedTeamName(ServerPlayerEntity player) {
        return Constants.MOD_ABBREV_PREFIX + player.getUuidAsString();
    }

    public static Text getTempDownedTeamDisplayName(ServerPlayerEntity player) {
        return Text.literal(Constants.MOD_ABBREV_PREFIX).append(player.getName());
    }

    public static void assignTempDownedTeam(ServerPlayerEntity player) {
        // player can only be on one team so we should not overwrite a current team
        // only applies if player is not part of a different team already
        if (player.getScoreboardTeam() == null) {
            Scoreboard scoreboard = player.getEntityWorld().getScoreboard();
            String teamName = getTempDownedTeamName(player);
            Text teamDisplayName = getTempDownedTeamDisplayName(player);

            if (scoreboard.getTeam(teamName) == null) {
                Team team = scoreboard.addTeam(teamName);
                team.setDisplayName(teamDisplayName);
                team.setFriendlyFireAllowed(true);
                team.setShowFriendlyInvisibles(false);
                team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OWN_TEAM);
                team.setColor(Formatting.DARK_RED);
            }

            Team team = scoreboard.getTeam(teamName);
            if (team != null) {
                scoreboard.addScoreHolderToTeam(player.getNameForScoreboard(), team);
            }
        }
    }

    public static void removeTempDownedTeam(ServerPlayerEntity player) {
        Scoreboard scoreboard = player.getEntityWorld().getScoreboard();
        Team team = scoreboard.getTeam(getTempDownedTeamName(player));
        if (team != null) {
            scoreboard.removeTeam(team);
        }
    }

    public static void updateTempTeamColor(ServerPlayerEntity player, Formatting color) {
        if (isOnTempDownedTeam(player)) {
            Team team = player.getScoreboardTeam();
            if (team != null && !(team.getColor().equals(color))) {
                team.setColor(color);
            }
        }
    }

    public static void updateRevivingTeamColor(ServerPlayerEntity player) {
        updateTempTeamColor(player, Formatting.AQUA);
    }

    public static void updateBleedOutStatusTeamColor(ServerPlayerEntity player, float progress) {
        updateTempTeamColor(player, getProgressColor(progress));
    }

    public static Formatting getProgressColor(float progress) {
        if (progress == 0f) {
            return Formatting.GRAY;
        } else if (progress > 0f && progress <= 0.25f) {
            return Formatting.YELLOW;
        } else if (progress > 0.25f && progress <= 0.5f) {
            return Formatting.GOLD;
        } else if (progress > 0.5f && progress <= 0.75f) {
            return Formatting.RED;
        } else if (progress > 0.75f && progress <= 0.99f) {
            return Formatting.DARK_RED;
        } else {
            return Formatting.GRAY;
        }
    }

    public static void assignShulkerAndArmorStandToTempDownedTeam(ServerPlayerEntity player) {
        ServerPlayerDuck serverPlayer = (ServerPlayerDuck) player;
        ShulkerEntity shulker = serverPlayer.dbno$getInvisibleShulkerEntity();
        ArmorStandEntity armorStandEntity = serverPlayer.dbno$getInvisibleArmorStandEntity();
        Scoreboard scoreboard = player.getEntityWorld().getScoreboard();
        Team team = player.getScoreboardTeam();
        scoreboard.addScoreHolderToTeam(shulker.getNameForScoreboard(), team);
        scoreboard.addScoreHolderToTeam(armorStandEntity.getNameForScoreboard(), team);
    }
}