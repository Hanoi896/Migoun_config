package io.github.han896.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PlayerStats {
    private final UUID playerId;
    private final PlayerData playerData; // 다른 데이터에 접근하기 위한 '상사' 참조
    private int level = 1;
    private int exp = 0;
    private int expToNextLevel;
    private int statPoints = 0;
    private final Map<String, Integer> stats = new HashMap<>();

    public PlayerStats(UUID playerId, PlayerData playerData) {
        this.playerId = playerId;
        this.playerData = playerData;
        initRandomStats();
        calculateExpToNextLevel();
    }

    public void initRandomStats() {
        stats.put("STR", 0);
        stats.put("AGI", 0);
        stats.put("INT", 0);
        stats.put("VIT", 0);
        stats.put("WIS", 0);
        Random random = new Random();
        String[] statNames = {"STR", "AGI", "INT", "VIT", "WIS"};
        for (int i = 0; i < 30; i++) {
            String randomStat = statNames[random.nextInt(statNames.length)];
            stats.put(randomStat, stats.get(randomStat) + 1);
        }
    }

    public void addMainExp(int amount) {
        this.exp += amount;
        Player player = Bukkit.getPlayer(playerId);
        // 전투 모드가 아닐 때만 경험치 바를 표시 (PlayerSkills의 정보 필요)
        if (player != null && playerData.getSkills().getCombatMode() == CombatMode.NONE) {
            player.sendActionBar(
                    Component.text("경험치 +" + amount, NamedTextColor.DARK_AQUA)
                            .append(Component.text(" (" + this.exp + "/" + this.expToNextLevel + ")", NamedTextColor.AQUA))
            );
        }
        if (this.exp >= this.expToNextLevel) {
            mainLevelUp();
        }
    }

    private void mainLevelUp() {
        this.exp -= this.expToNextLevel;
        this.level++;
        calculateExpToNextLevel();
        addStatPoints(3);
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            final Title title = Title.title(
                    Component.text("LEVEL UP!", NamedTextColor.GOLD, TextDecoration.BOLD),
                    Component.text("당신은 " + this.level + "레벨이 되었습니다.", NamedTextColor.YELLOW),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3500), Duration.ofMillis(1000))
            );
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            player.sendMessage(ChatColor.AQUA + "분배할 수 있는 스탯 포인트를 3 획득했습니다! (/스탯)");
        }
    }

    private void calculateExpToNextLevel() {
        this.expToNextLevel = (int) (Math.pow(this.level, 1.2) * 100);
    }

    // --- Getter/Setter 메소드 ---
    public int getStat(String statName) {
        return stats.getOrDefault(statName.toUpperCase(), 0);
    }

    public void setStat(String statName, int value) {
        stats.put(statName.toUpperCase(), value);
    }

    public int getStatPoints() {
        return statPoints;
    }

    public void addStatPoints(int amount) {
        this.statPoints += amount;
    }

    public int getLevel() {
        return level;
    }
}