package com.phasmoware.down_but_not_out.registry;

import com.phasmoware.down_but_not_out.handler.EventCallbackHandler;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.*;


public class ModEvents {

    public static void init() {
        registerServerCleanUpEvent();
        registerAllowDeathEvent();
        registerInteractionEventsWhileDowned();
        registerReviveInteractionEvent();
        registerServerPlayerDisconnectEvent();
        registerServerPlayerJoinEvent();
    }

    private static void registerServerCleanUpEvent() {
        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
                server.getPlayerManager().getPlayerList().forEach(EventCallbackHandler::onCleanUpEvent));
    }

    private static void registerAllowDeathEvent() {
        ServerLivingEntityEvents.ALLOW_DEATH.register(EventCallbackHandler::onAllowDeathEvent);
    }

    private static void registerInteractionEventsWhileDowned() {

        // prevent left click block interaction while downed
        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) ->
                EventCallbackHandler.onConsumeDownedAction(playerEntity));

        // prevent right click block interaction while downed
        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) ->
                EventCallbackHandler.onConsumeDownedAction(playerEntity));

        // prevent left click entity interaction while downed
        AttackEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) ->
                EventCallbackHandler.onConsumeDownedAction(playerEntity));

        // prevent right click item interaction while downed
        UseItemCallback.EVENT.register((playerEntity, world, hand) ->
                EventCallbackHandler.onConsumeDownedAction(playerEntity));
    }

    private static void registerReviveInteractionEvent() {
        UseEntityCallback.EVENT.register(EventCallbackHandler::onReviveDownedInteraction);
    }

    private static void registerServerPlayerDisconnectEvent() {
        ServerPlayerEvents.LEAVE.register(EventCallbackHandler::onPlayerDisconnect);
    }

    private static void registerServerPlayerJoinEvent() {
        ServerPlayerEvents.JOIN.register(EventCallbackHandler::onPlayerJoinWhileDowned);
    }
}
