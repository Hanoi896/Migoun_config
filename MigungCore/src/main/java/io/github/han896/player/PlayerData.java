package io.github.han896.player;

import java.util.UUID;

public class PlayerData {
    private final UUID playerId;
    private final PlayerStats stats;
    private final PlayerSkills skills;
    private final PlayerQuests quests;

    public PlayerData(UUID playerId, String displayName) {
        this.playerId = playerId;
        this.stats = new PlayerStats(playerId, this);
        this.skills = new PlayerSkills(playerId, this);
        this.quests = new PlayerQuests(playerId, this);
    }

    public PlayerStats getStats() { return stats; }
    public PlayerSkills getSkills() { return skills; }
    public PlayerQuests getQuests() { return quests; }
    public UUID getPlayerId() { return playerId; }
}