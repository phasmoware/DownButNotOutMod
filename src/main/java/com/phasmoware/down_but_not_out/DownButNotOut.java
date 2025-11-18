package com.phasmoware.down_but_not_out;

import com.phasmoware.down_but_not_out.duck.PlayerDownButNotOut;
import com.phasmoware.down_but_not_out.timer.BleedOutTimer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


public class DownButNotOut implements ModInitializer {

    public static final String MOD_ID = "down_but_not_out";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final float HEARTS_AFTER_REVIVE = 0.01f;
    public static final String DOWNED_TAG = "Downed";
    public static final double CONE_SIZE = 0.002;
    public static final boolean ADJUST_FOR_DISTANCE = true;
    public static final boolean SEE_THROUGH_TRANSPARENT_BLOCKS = false;
    public static final long TICKS_UNTIL_BLEED_OUT = 200L;
    public static final HashMap<ServerPlayerEntity, BleedOutTimer> bleedOutTimers = new HashMap<>();


    @Override
    public void onInitialize() {
        registerEventCallbacks();
        LOGGER.info(MOD_ID + " mod initialized");
    }

    public void registerEventCallbacks() {
        listenForDownedEvent();
        listenForInteractionWhileDowned();
    }


    private void listenForDownedEvent() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (!(entity instanceof ServerPlayerEntity player)) {
                return true;
            }

            if (((PlayerDownButNotOut)player).downButNotOut$isDowned()) {
                ((PlayerDownButNotOut)player).downButNotOut$removeDowned();
                return true;
            }

            if (player.isInLava() && player.isOnFire()) {
                return true;
            }

            // TODO: if player falls into void
//            if (damageSource.getType().equals(DamageTypes.OUT_OF_WORLD)) {
//                return true;
//            }

            // prevent death and apply downed state instead
            ((PlayerDownButNotOut)player).downButNotOut$applyDowned(damageSource);
            BleedOutTimer timer = new BleedOutTimer(TICKS_UNTIL_BLEED_OUT, player, damageSource);
            timer.register();
            bleedOutTimers.put(player, timer);
            ((PlayerDownButNotOut)player).downButNotOut$setBleedOutTimerInstance(timer);
            return false;
        });
    }


    private void listenForInteractionWhileDowned() {

        // prevent left click block interaction while downed
        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
            if (((PlayerDownButNotOut)playerEntity).downButNotOut$isDowned()) {
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });

        // prevent right click block interaction while downed
        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            if (((PlayerDownButNotOut)playerEntity).downButNotOut$isDowned()) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // prevent left click entity interaction while downed
        AttackEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
            if (((PlayerDownButNotOut)playerEntity).downButNotOut$isDowned()) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // prevent right click entity interaction while downed
        UseEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
            if (((PlayerDownButNotOut)playerEntity).downButNotOut$isDowned()) {
                return ActionResult.CONSUME;
            } else if (entity instanceof ServerPlayerEntity targetPlayer && ((PlayerDownButNotOut)targetPlayer).downButNotOut$isDowned()) {
                ((PlayerDownButNotOut)targetPlayer).downButNotOut$revive();
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






}
