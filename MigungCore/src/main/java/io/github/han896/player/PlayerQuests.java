package io.github.han896.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerQuests {
    private final UUID playerId;
    private final PlayerData playerData;
    private final Map<String, Integer> activeQuests = new HashMap<>();
    private final Map<String, Integer> questProgress = new HashMap<>();

    public PlayerQuests(UUID playerId, PlayerData playerData) {
        this.playerId = playerId;
        this.playerData = playerData;
    }

    public Set<String> getActiveQuests() { return activeQuests.keySet(); }
    public int getQuestStep(String questId) { return activeQuests.getOrDefault(questId, 0); }
    public int getQuestProgress(String questId, int step) { return questProgress.getOrDefault(questId + "_" + step, 0); }
    public void acceptQuest(String questId) { activeQuests.put(questId, 1); }
    public void incrementQuestProgress(String questId, int step) {
        String key = questId + "_" + step;
        int currentProgress = questProgress.getOrDefault(key, 0);
        questProgress.put(key, currentProgress + 1);
    }
}