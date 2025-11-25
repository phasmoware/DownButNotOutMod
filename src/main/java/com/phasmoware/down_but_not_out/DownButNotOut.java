package com.phasmoware.down_but_not_out;

import com.phasmoware.down_but_not_out.command.ModCommands;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.duck.PlayerDownButNotOut;
import com.phasmoware.down_but_not_out.timer.ReviveTimer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DownButNotOut implements ModInitializer {

    public static final String MOD_ID = "down_but_not_out";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final float HEARTS_AFTER_REVIVE = 0.01f;
    public static final String DOWNED_TAG = "Downed";
    public static final double CONE_SIZE = 0.002;
    public static final boolean ADJUST_FOR_DISTANCE = true;
    public static final boolean SEE_THROUGH_TRANSPARENT_BLOCKS = false;
    public static final String SKIPPED_DOWNED_STATE_MSG = "No one was available to revive you...";
    public static final String DOWNED_STATE_MSG = " is down, give them a hand to revive them";
    public static final String REVIVED_MSG = " has revived ";
    public static final String BLED_OUT_MSG = "You bled out...";
    public static final float BASE_MOVE_SPEED = 0.1F;
    public static final float DOWNED_SOUND_PITCH = 1.2F;
    public static final float REVIVED_SOUND_PITCH = 0.6F;
    public static final double SHULKER_ENTITY_SCALE = 0.65d;



    @Override
    public void onInitialize() {
        ModConfig.init();
        registerEventCallbacks();
        ModCommands.initialize();
        LOGGER.info(MOD_ID + " mod initialized");
    }

    public void registerEventCallbacks() {
        listenForDownedEvent();
        listenForInteractionWhileDowned();
        listenForDownedDisconnect();
    }


    private void listenForDownedEvent() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            // if mod disabled in config, allow death like normal
            if (!ModConfig.INSTANCE.MOD_ENABLED) {
                return true;
            }

            if (!(entity instanceof ServerPlayerEntity player)) {
                return true;
            }

            if (((PlayerDownButNotOut)player).downButNotOut$isDowned()) {
                ((PlayerDownButNotOut)player).downButNotOut$removeDowned();
                player.sendMessage(Text.literal(BLED_OUT_MSG).formatted(Formatting.RED), ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
                return true;
            }

            if (player.isInLava() && player.isOnFire() && !ModConfig.INSTANCE.ALLOW_DOWNED_STATE_IN_LAVA) {
                return true;
            }

            if (ModConfig.INSTANCE.SKIP_DOWNED_STATE_IF_NO_OTHER_PLAYERS_ONLINE && player.getEntityWorld().getServer().getCurrentPlayerCount() <= 1) {
                player.sendMessage(Text.literal(SKIPPED_DOWNED_STATE_MSG).formatted(Formatting.RED), ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
                return true;
            }

            // else prevent death and apply downed state instead
            ((PlayerDownButNotOut)player).downButNotOut$applyDowned(damageSource);
            broadcastMessageToPlayers(player.getName().getLiteralString() + DOWNED_STATE_MSG,
                    player.getEntityWorld(), Formatting.RED);
            player.sendMessage(Text.literal("[Click here to give up]").setStyle(Style.EMPTY.withUnderline(true).withBold(true).withClickEvent(new ClickEvent.RunCommand("bleedout"))), false);
            return false;
        });
    }


    private void listenForInteractionWhileDowned() {

        // prevent left click block interaction while downed
        AttackBlockCallback.EVENT.register(
                (playerEntity, world, hand, blockPos, direction) -> {
                    if (((PlayerDownButNotOut)playerEntity).downButNotOut$isDowned()) {
                        return ActionResult.CONSUME;
                    }
                    return ActionResult.PASS;
        });

        // prevent right click block interaction while downed
        UseBlockCallback.EVENT.register(
                (playerEntity, world, hand, blockHitResult) -> {
                    if (((PlayerDownButNotOut)playerEntity).downButNotOut$isDowned()) {
                        return ActionResult.FAIL;
                    }
                    return ActionResult.PASS;
        });

        // prevent left click entity interaction while downed
        AttackEntityCallback.EVENT.register(
                (playerEntity, world, hand, entity, entityHitResult) -> {
                    if (((PlayerDownButNotOut)playerEntity).downButNotOut$isDowned()) {
                        return ActionResult.FAIL;
                    }
                    return ActionResult.PASS;
        });

        // prevent right click entity interaction while downed and listen for revive on downed player
        UseEntityCallback.EVENT.register(
            (playerEntity, world, hand, entity, entityHitResult) -> {
                if (((PlayerDownButNotOut)playerEntity).downButNotOut$isDowned()) {
                    return ActionResult.CONSUME;
                } else if (entity instanceof ServerPlayerEntity targetPlayer
                                                    && ((PlayerDownButNotOut)targetPlayer).downButNotOut$isDowned()) {

                    ReviveTimer reviveTimer = ((PlayerDownButNotOut) targetPlayer).downButNotOut$getReviveTimer();
                    if (!((PlayerDownButNotOut) targetPlayer).downButNotOut$isBeingRevivedBy((ServerPlayerEntity) playerEntity)) {
                        if (reviveTimer == null) {
                            reviveTimer = new ReviveTimer((ServerPlayerEntity) playerEntity, targetPlayer);
                            reviveTimer.register();
                            ((PlayerDownButNotOut) targetPlayer).downButNotOut$startReviving(reviveTimer, (ServerPlayerEntity) playerEntity);
                            reviveTimer.incrementInteractionTicks();
                        } else if (reviveTimer.getReviver() != null && !(reviveTimer.getReviver().equals(playerEntity))) {
                            reviveTimer.reset((ServerPlayerEntity) playerEntity);
                            ((PlayerDownButNotOut) targetPlayer).downButNotOut$cancelReviving(reviveTimer);
                            ((PlayerDownButNotOut) targetPlayer).downButNotOut$startReviving(reviveTimer, (ServerPlayerEntity) playerEntity);
                            reviveTimer.incrementInteractionTicks();
                        }
                    } else if (((PlayerDownButNotOut) targetPlayer).downButNotOut$isBeingRevivedBy((ServerPlayerEntity) playerEntity)) {
                        reviveTimer.incrementInteractionTicks();
                        Text msgToReviver = Text.literal("Hold to Revive: " + reviveTimer.getCurrentProgressPercent() + "%").formatted(Formatting.BLUE);
                        playerEntity.sendMessage(msgToReviver, ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
                        Text msgToDowned = Text.literal("Reviving: " + reviveTimer.getCurrentProgressPercent() + "%").formatted(Formatting.BLUE);
                        targetPlayer.sendMessage(msgToDowned, ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
                    }
                }
                return ActionResult.PASS;
        });

        // prevent right click item interaction while downed
        UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
            if (((PlayerDownButNotOut)playerEntity).downButNotOut$isDowned()) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }

    private void listenForDownedDisconnect() {
        ServerPlayerEvents.LEAVE.register(
                playerEntity -> {
                    if (((PlayerDownButNotOut)playerEntity).downButNotOut$isDowned()) {
                        ((PlayerDownButNotOut)playerEntity).downButNotOut$getInvisibleShulkerEntity().remove(Entity.RemovalReason.DISCARDED);;
                    }
                });
    }

    public static void broadcastMessageToPlayers(String message, ServerWorld world, Formatting formatting) {
        Text text = Text.literal(message).formatted(formatting);
        world.getServer().getPlayerManager().broadcast(text, ModConfig.INSTANCE.USE_OVERLAY_MESSAGES);
    }
}
