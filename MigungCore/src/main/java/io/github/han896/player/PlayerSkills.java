package io.github.han896.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerSkills {
    private final UUID playerId;
    private final PlayerData playerData;

    private final Map<String, int[]> skills = new HashMap<>();
    private final Map<String, String> skillDescriptions = new HashMap<>();
    private double currentMana;
    private double maxMana;
    private double currentStamina;
    private double maxStamina;
    private CombatMode combatMode = CombatMode.NONE;
    private int currentSkillBar = 0;
    private int currentMagicBar = 0;
    private final List<List<String>> skillSlots = new ArrayList<>();
    private final List<List<String>> spellSlots = new ArrayList<>();
    private final List<String> learnedActives = new ArrayList<>();
    private final List<String> learnedSpells = new ArrayList<>();
    private final Map<String, Long> skillCooldowns = new ConcurrentHashMap<>();

    private static final Map<String, String> SKILL_ID_TO_NAME = Map.of(
            "SWORDSMANSHIP", "검술", "AXE_MASTERY", "도끼술",
            "SPEAR_MASTERY", "창술", "BLUNT_MASTERY", "둔기술"
    );

    public PlayerSkills(UUID playerId, PlayerData playerData) {
        this.playerId = playerId;
        this.playerData = playerData;

        updateMaxMana();
        updateMaxStamina();
        this.currentMana = this.maxMana;
        this.currentStamina = this.maxStamina;

        for (int i = 0; i < 3; i++) {
            skillSlots.add(new ArrayList<>(Collections.nCopies(8, null)));
            spellSlots.add(new ArrayList<>(Collections.nCopies(8, null)));
        }
        learnedActives.add("SPIN_ATTACK");
        learnedSpells.add("FIREBALL");
        learnedSpells.add("ICE_BOLT");
        learnedSpells.add("HEAL");
    }

    public boolean learnSkill(String skillId) {
        if (skills.containsKey(skillId)) return false;
        skills.put(skillId, new int[]{1, 0});
        skillDescriptions.put(skillId, getSkillName(skillId) + "의 기본 숙련도입니다.");
        return true;
    }

    public void addSkillExp(String skillId, int amount) {
        if (!skills.containsKey(skillId)) return;
        int[] data = skills.get(skillId);
        int sLevel = data[0], sExp = data[1], sReqExp = sLevel * 10;
        sExp += amount;
        if (sExp >= sReqExp) {
            sExp -= sReqExp;
            sLevel++;
            skillLevelUp(skillId, sLevel);
        }
        skills.put(skillId, new int[]{sLevel, sExp});
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            player.sendMessage(ChatColor.GRAY + "[" + getSkillName(skillId) + "] 경험치 +" + amount + " (" + sExp + "/" + (sLevel * 10) + ")");
        }
    }

    private void skillLevelUp(String skillId, int newLevel) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            final Title title = Title.title(
                    Component.text("스킬 레벨 업!", NamedTextColor.AQUA),
                    Component.text("[" + getSkillName(skillId) + "] 스킬이 " + newLevel + "레벨이 되었습니다.", NamedTextColor.WHITE),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
            );
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        }
    }

    public void updateMaxMana() { this.maxMana = 100 + (playerData.getStats().getStat("WIS") * 5); }
    public boolean consumeMana(double amount) {
        if (this.currentMana >= amount) { this.currentMana -= amount; return true; }
        return false;
    }
    public void regenerateMana() { this.currentMana = Math.min(this.maxMana, this.currentMana + (1 + (playerData.getStats().getStat("WIS") / 5.0))); }

    public void updateMaxStamina() { this.maxStamina = 100 + (playerData.getStats().getStat("VIT") * 2); }
    public boolean consumeStamina(double amount) {
        if (this.currentStamina >= amount) { this.currentStamina -= amount; return true; }
        return false;
    }
    public void regenerateStamina() { this.currentStamina = Math.min(this.maxStamina, this.currentStamina + 2.5); }

    public int getMaxSpellSlots() { return Math.min(8, 1 + (playerData.getStats().getStat("WIS") / 10)); }
    public double getRemainingCooldown(String skillId, double baseCooldown) {
        double finalCooldown = baseCooldown * (1 - Math.min(0.5, playerData.getStats().getStat("WIS") * 0.002));
        if (!skillCooldowns.containsKey(skillId)) return 0;
        long elapsedTime = System.currentTimeMillis() - skillCooldowns.get(skillId);
        long finalCooldownMillis = (long) (finalCooldown * 1000);
        if (elapsedTime >= finalCooldownMillis) return 0;
        return (finalCooldownMillis - elapsedTime) / 1000.0;
    }
    public void setCooldown(String skillId) { skillCooldowns.put(skillId, System.currentTimeMillis()); }

    public CombatMode getCombatMode() { return combatMode; }
    public void setCombatMode(CombatMode mode) { this.combatMode = mode; }
    public int getCurrentSkillBar() { return currentSkillBar; }
    public void cycleNextSkillBar() { this.currentSkillBar = (this.currentSkillBar + 1) % skillSlots.size(); }
    public int getCurrentMagicBar() { return currentMagicBar; }
    public void cycleNextMagicBar() { this.currentMagicBar = (this.currentMagicBar + 1) % spellSlots.size(); }

    public List<List<String>> getAllSkillSlots() { return skillSlots; }
    public List<List<String>> getAllSpellSlots() { return spellSlots; }
    public List<String> getLearnedActives() { return learnedActives; }
    public List<String> getLearnedSpells() { return learnedSpells; }
    public int getCurrentSkillSlot() { return 0; } // TODO
    public int getCurrentSpellSlot() { return 0; } // TODO
    public Map<String, int[]> getSkills() { return skills; }
    public static String getSkillName(String skillId) { return SKILL_ID_TO_NAME.getOrDefault(skillId, skillId); }
    public String getSkillDescription(String skillId) { return skillDescriptions.getOrDefault(skillId, "설명이 없는 스킬입니다."); }
    public int getSkillLevel(String skillId) { return skills.getOrDefault(skillId, new int[]{0, 0})[0]; }
    public int getSkillExp(String skillId) { return skills.getOrDefault(skillId, new int[]{0, 0})[1]; }
    public double getCurrentMana() { return currentMana; }
    public double getMaxMana() { return maxMana; }
    public double getCurrentStamina() { return currentStamina; }
    public double getMaxStamina() { return maxStamina; }
}