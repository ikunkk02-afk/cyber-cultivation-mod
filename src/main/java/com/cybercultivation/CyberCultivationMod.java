package com.cybercultivation;

import com.cybercultivation.block.ModBlocks;
import com.cybercultivation.command.CultivationCommand;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.effect.ModEffects;
import com.cybercultivation.entity.ModEntities;
import com.cybercultivation.flysword.FlyingSwordHandler;
import com.cybercultivation.handler.AttackEffectHandler;
import com.cybercultivation.item.DaoEquipmentHandler;
import com.cybercultivation.item.ModItems;
import com.cybercultivation.loot.ModLootTableModifiers;
import com.cybercultivation.meditation.MeditationHandler;
import com.cybercultivation.network.*;
import com.cybercultivation.particle.ModParticles;
import com.cybercultivation.villager.ModTrades;
import com.cybercultivation.villager.ModVillagers;
import com.cybercultivation.worldgen.ModEntitySpawns;
import com.cybercultivation.worldgen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CyberCultivationMod implements ModInitializer {
    public static final String MOD_ID = "cyber-cultivation-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Cyber Cultivation Mod initializing...");

        ModEffects.registerModEffects();
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        ModEntities.registerModEntities();
        ModVillagers.registerModVillagers();
        ModTrades.registerTrades();
        ModLootTableModifiers.register();
        ModWorldGeneration.register();
        ModEntitySpawns.register();
        DaoEquipmentHandler.register();
        AttackEffectHandler.register();
        ModParticles.register();

        PayloadTypeRegistry.playS2C().register(QiSyncPayload.TYPE, QiSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerAnimationSyncPayload.TYPE, PlayerAnimationSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(AttackEffectPayload.TYPE, AttackEffectPayload.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(ToggleMeditationPayload.TYPE, ToggleMeditationPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleFlyingSwordPayload.TYPE, ToggleFlyingSwordPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestCultivationInfoPayload.TYPE, RequestCultivationInfoPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestAptitudeInfoPayload.TYPE, RequestAptitudeInfoPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ToggleMeditationPayload.TYPE,
                (payload, context) -> CultivationActions.toggleMeditation(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(ToggleFlyingSwordPayload.TYPE,
                (payload, context) -> CultivationActions.toggleFlyingSword(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(RequestCultivationInfoPayload.TYPE,
                (payload, context) -> CultivationActions.sendCultivationInfo(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(RequestAptitudeInfoPayload.TYPE,
                (payload, context) -> CultivationActions.sendAptitudeInfo(context.player()));

        CommandRegistrationCallback.EVENT.register(CultivationCommand::register);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            MeditationHandler.onServerTick(server);
            FlyingSwordHandler.onServerTick(server);
            DaoEquipmentHandler.onServerTick(server);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerQiManager.syncToClient(handler.player);
            DaoEquipmentHandler.enforceArmorRestrictions(handler.player);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            FlyingSwordHandler.exitForDisconnect(handler.player);
        });

        // Clear cached player data when the server stops so that
        // switching worlds / reloading doesn't leak old state.
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            PlayerQiManager.getAllData().clear();
        });

        LOGGER.info("Cyber Cultivation Mod initialized!");
    }
}
