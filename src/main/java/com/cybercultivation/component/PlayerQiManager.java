package com.cybercultivation.component;

import com.cybercultivation.network.QiSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerQiManager {
    private static final Map<UUID, PlayerQiData> playerDataMap = new ConcurrentHashMap<>();

    public static PlayerQiData getOrCreate(Player player) {
        return getOrCreate(player.getUUID());
    }

    public static PlayerQiData getOrCreate(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, k -> new PlayerQiData());
    }

    public static PlayerQiData get(Player player) {
        return playerDataMap.get(player.getUUID());
    }

    public static void setData(UUID uuid, int currentQi, int maxQi, boolean meditating) {
        PlayerQiData data = getOrCreate(uuid);
        data.setMaxQi(maxQi);
        data.setCurrentQi(currentQi);
        data.setMeditating(meditating);
    }

    public static void remove(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    public static Map<UUID, PlayerQiData> getAllData() {
        return playerDataMap;
    }

    public static void syncToClient(ServerPlayer player) {
        PlayerQiData data = getOrCreate(player);
        ServerPlayNetworking.send(player, new QiSyncPayload(
                data.getCurrentQi(),
                data.getMaxQi(),
                data.getSelectedPath(),
                data.getMainDiscipline(),
                data.getSubDisciplines(),
                data.getElement(),
                data.isFlyingSword(),
                data.getFlyingSwordItemId(),
                data.isMeditating()
        ));
    }

    /**
     * Broadcast this player's animation state (meditating / flyingSword) to
     * all tracking clients so they can drive player animations for remote players.
     */
    public static void broadcastAnimationState(ServerPlayer player) {
        PlayerQiData data = getOrCreate(player);
        if (data == null) return;
        var payload = new com.cybercultivation.network.PlayerAnimationSyncPayload(
                player.getId(),
                data.isMeditating(),
                data.isFlyingSword(),
                data.getFlyingSwordItemId()
        );
        for (ServerPlayer p : player.getServer().getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(p, payload);
        }
    }
}
