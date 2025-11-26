package com.phasmoware.down_but_not_out.handler;

import com.phasmoware.down_but_not_out.api.ServerPlayerAPI;
import com.phasmoware.down_but_not_out.config.ModConfig;
import com.phasmoware.down_but_not_out.manager.DownedStateManager;
import com.phasmoware.down_but_not_out.util.DownedUtility;
import com.phasmoware.down_but_not_out.util.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
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
            DownedStateManager.onDeathEventOfDownedPlayer(player, damageSource);
            return true;
        }

        if (player.isInLava() && player.isOnFire() && !ModConfig.INSTANCE.ALLOW_DOWNED_STATE_IN_LAVA) {
            return true;
        }

        if (ModConfig.INSTANCE.SKIP_DOWNED_STATE_IF_NO_OTHER_PLAYERS_ONLINE
                && player.getEntityWorld().getServer().getCurrentPlayerCount() <= 1) {
            Text skippedDownedStateMessage = Text.literal(Constants.SKIPPED_DOWNED_STATE_MSG).formatted(Formatting.RED);
            MessageHandler.sendUpdateMessage(skippedDownedStateMessage, player);
            return true;
        }

        // else prevent death and apply downed state instead
        DownedStateManager.onPlayerDownedEvent(player, damageSource);
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
        } else if (entity instanceof ServerPlayerEntity downed
                && (isDowned(entity))) {
            ServerPlayerEntity reviver = (ServerPlayerEntity) playerEntity;
            DownedStateManager.onReviveInteractionEvent(downed, reviver);
        }
        return ActionResult.PASS;
    }

    public static void onCleanUpEvent(PlayerEntity playerEntity) {
        DownedUtility.cleanUpInvisibleEntities((ServerPlayerAPI)  playerEntity);
    }
}
