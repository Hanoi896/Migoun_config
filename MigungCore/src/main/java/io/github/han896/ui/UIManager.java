package io.github.han896.ui;

import io.github.han896.MigungCore;
import io.github.han896.player.CombatMode; // (해결!) CombatMode import 추가
import io.github.han896.player.PlayerData;
import io.github.han896.skill.SkillData;
import io.github.han896.skill.SkillManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UIManager {

    private final Map<UUID, BossBar> bossBars = new HashMap<>();
    private final MigungCore plugin;

    public UIManager(MigungCore plugin) {
        this.plugin = plugin;
        startUpdateTask();
    }

    private void startUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
                    if (data == null) continue;

                    if (!player.isSprinting()) data.getSkills().regenerateStamina();
                    data.getSkills().regenerateMana();
                    updateActionBar(player, data);
                    player.setExp(0);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public void setupPlayerUI(Player player) {
        BossBar bossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
        bossBar.addPlayer(player);
        bossBars.put(player.getUniqueId(), bossBar);
        player.setLevel(0);
    }

    public void clearPlayerUI(Player player) {
        BossBar bossBar = bossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void updateActionBar(Player player, PlayerData data) {
        if (data.getSkills().getCombatMode() == CombatMode.COMBAT) {
            updateCombatBar(player, data);
        } else {
            updateStatusBar(player, data);
        }
    }

    private void updateStatusBar(Player player, PlayerData data) {
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHealth = (maxHealthAttribute != null) ? maxHealthAttribute.getValue() : 20.0;
        player.sendActionBar(
                Component.text("HP ", NamedTextColor.RED)
                        .append(Component.text(String.format("%.0f / %.0f", player.getHealth(), maxHealth), NamedTextColor.WHITE))
                        .append(Component.text(" | MP ", NamedTextColor.BLUE))
                        .append(Component.text(String.format("%.0f / %.0f", data.getSkills().getCurrentMana(), data.getSkills().getMaxMana()), NamedTextColor.WHITE))
                        .append(Component.text(" | SP ", NamedTextColor.GREEN))
                        .append(Component.text(String.format("%.0f / %.0f", data.getSkills().getCurrentStamina(), data.getSkills().getMaxStamina()), NamedTextColor.WHITE))
        );
    }

    private void updateCombatBar(Player player, PlayerData data) {
        Component combatBar = Component.text("");
        SkillManager skillManager = MigungCore.getInstance().getSkillManager();

        int skillBarNum = data.getSkills().getCurrentSkillBar();
        List<String> skillSlots = data.getSkills().getAllSkillSlots().get(skillBarNum);
        int currentSkillSlot = data.getSkills().getCurrentSkillSlot();

        combatBar = combatBar.append(Component.text("Skills " + (skillBarNum + 1) + ": ", NamedTextColor.YELLOW));
        for (int i = 0; i < 8; i++) {
            boolean isUnlocked = true;
            String skillId = skillSlots.get(i);

            if (!isUnlocked) {
                combatBar = combatBar.append(Component.text("[X]", NamedTextColor.DARK_GRAY));
            } else if (skillId == null) {
                combatBar = combatBar.append(Component.text("[ ]", NamedTextColor.GRAY));
            } else {
                if (i == currentSkillSlot) {
                    combatBar = combatBar.append(Component.text("<" + skillId + ">", NamedTextColor.AQUA, TextDecoration.BOLD));
                } else {
                    combatBar = combatBar.append(Component.text("[" + skillId + "]", NamedTextColor.WHITE));
                }
            }
            combatBar = combatBar.append(Component.text(" "));
        }

        combatBar = combatBar.append(Component.text("| ", NamedTextColor.DARK_GRAY));

        int magicBarNum = data.getSkills().getCurrentMagicBar();
        List<String> spellSlots = data.getSkills().getAllSpellSlots().get(magicBarNum);
        int currentSpellSlot = data.getSkills().getCurrentSpellSlot();
        int maxMagicSlots = data.getSkills().getMaxSpellSlots();

        combatBar = combatBar.append(Component.text("Magics " + (magicBarNum + 1) + ": ", NamedTextColor.LIGHT_PURPLE));
        for (int i = 0; i < 8; i++) {
            String spellId = spellSlots.get(i);

            if (i >= maxMagicSlots) {
                combatBar = combatBar.append(Component.text("[X]", NamedTextColor.DARK_GRAY));
            } else if (spellId == null) {
                combatBar = combatBar.append(Component.text("[ ]", NamedTextColor.GRAY));
            } else {
                SkillData skillData = skillManager.getSkillData(spellId);
                if (skillData == null) continue;
                double remainingCooldown = data.getSkills().getRemainingCooldown(spellId, skillData.getCooldown());

                Component spellComponent;
                if (remainingCooldown > 0) {
                    spellComponent = Component.text("[" + skillData.getName() + " " + String.format("%.1f", remainingCooldown) + "s]", NamedTextColor.GRAY);
                } else {
                    if (i == currentSpellSlot) {
                        spellComponent = Component.text("<" + skillData.getName() + ">", NamedTextColor.AQUA, TextDecoration.BOLD);
                    } else {
                        spellComponent = Component.text("[" + skillData.getName() + "]", NamedTextColor.WHITE);
                    }
                }
                combatBar = combatBar.append(spellComponent);
            }
            combatBar = combatBar.append(Component.text(" "));
        }
        player.sendActionBar(combatBar);
    }
}