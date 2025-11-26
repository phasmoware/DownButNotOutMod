package com.phasmoware.down_but_not_out.handler;

import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import com.phasmoware.down_but_not_out.util.DownedUtility;
import com.phasmoware.down_but_not_out.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import static com.phasmoware.down_but_not_out.util.DownedUtility.isDowned;

public class EventCallbackHandler {
    public static boolean onAllowDeathEvent(LivingEntity entity, DamageSource damageSource, float damageAmount) {

        // if mod disabled in config, allow death like normal
        if (!ModConfig.INSTANCE.MOD_ENABLED) {
            return true;
        }

        if (!(entity instanceof ServerPlayerEntity player)) {
            return true;
        }

        if (isDowned(player)) {
            ((ServerPlayerAPI) player).downButNotOut$removeDowned();
            player.sendMessage(Text.literal(Reference.BLED_OUT_MSG).formatted(Formatting.RED),
                    ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
            return true;
        }

        if (player.isInLava() && player.isOnFire() && !ModConfig.INSTANCE.ALLOW_DOWNED_STATE_IN_LAVA) {
            return true;
        }

        if (ModConfig.INSTANCE.SKIP_DOWNED_STATE_IF_NO_OTHER_PLAYERS_ONLINE
                && player.getEntityWorld().getServer().getCurrentPlayerCount() <= 1) {
            player.sendMessage(Text.literal(Reference.SKIPPED_DOWNED_STATE_MSG).formatted(Formatting.RED),
                    ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
            return true;
        }

        // else prevent death and apply downed state instead
        ((ServerPlayerAPI) player).downButNotOut$applyDowned(damageSource);
        SendMessageHandler.broadcastMessageToPlayers(player.getName().getLiteralString() + Reference.DOWNED_STATE_MSG,
                player.getEntityWorld(), Formatting.RED);
        player.sendMessage(Text.literal("[Click here to give up]").setStyle(Style.EMPTY.withUnderline(true)
                .withBold(true).withClickEvent(new ClickEvent.RunCommand("bleedout"))), false);
        return false;
    }

    public static ActionResult onConsumeDownedAction(PlayerEntity playerEntity) {
        if (isDowned(playerEntity)) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    public static ActionResult onReviveDownedInteraction(PlayerEntity playerEntity, World world, Hand hand, Entity entity, EntityHitResult entityHitResult) {
        if (isDowned(playerEntity)) {
            return ActionResult.CONSUME;
        } else if (entity instanceof ServerPlayerEntity targetPlayer
                && (isDowned(entity))) {

            ReviveTimer reviveTimer = ((ServerPlayerAPI) targetPlayer).downButNotOut$getReviveTimer();
            if (!((ServerPlayerAPI) targetPlayer)
                    .downButNotOut$isBeingRevivedBy((ServerPlayerEntity) playerEntity)) {
                if (reviveTimer == null) {
                    reviveTimer = new ReviveTimer((ServerPlayerEntity) playerEntity, targetPlayer);
                    reviveTimer.register();
                    ((ServerPlayerAPI) targetPlayer).downButNotOut$startReviving(reviveTimer,
                            (ServerPlayerEntity) playerEntity);
                    reviveTimer.startReviveInteraction();
                } else if (reviveTimer.getReviver() != null
                        && !(reviveTimer.getReviver().equals(playerEntity))) {
                    reviveTimer.reset((ServerPlayerEntity) playerEntity);
                    ((ServerPlayerAPI) targetPlayer).downButNotOut$cancelReviving(reviveTimer);
                    ((ServerPlayerAPI) targetPlayer).downButNotOut$startReviving(reviveTimer,
                            (ServerPlayerEntity) playerEntity);
                    reviveTimer.startReviveInteraction();
                }
            } else if (((ServerPlayerAPI) targetPlayer)
                    .downButNotOut$isBeingRevivedBy((ServerPlayerEntity) playerEntity)) {
                reviveTimer.startReviveInteraction();
            }
        }
        return ActionResult.PASS;
    }

    public static void onCleanUpEvent(PlayerEntity playerEntity) {
        DownedUtility.cleanUpInvisibleEntities((ServerPlayerAPI)  playerEntity);
    }
}
