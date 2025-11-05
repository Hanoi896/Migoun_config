package io.github.han896.skill;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import io.github.han896.skill.type.Skill;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillManager {

    private final Map<String, SkillData> skillDataMap = new HashMap<>();
    private final Map<String, Skill> skillExecutorMap = new HashMap<>();
    private final MigungCore plugin;

    public SkillManager(MigungCore plugin) {
        this.plugin = plugin;
        File skillsFolder = new File(plugin.getDataFolder(), "skills");
        if (!skillsFolder.exists()) {
            skillsFolder.mkdirs();
        }

        // skills 폴더 안의 모든 .yml 파일을 로드합니다.
        File[] skillFiles = skillsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (skillFiles != null) {
            for (File file : skillFiles) {
                loadSkillsFromFile(file);
            }
        }
    }

    private void loadSkillsFromFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;

            String name = section.getString("name", "이름 없는 스킬");
            String description = section.getString("description", "");
            Material icon = Material.getMaterial(section.getString("icon", "BOOK"));
            double manaCost = section.getDouble("mana_cost", 0);
            double cooldown = section.getDouble("cooldown", 0);
            List<String> effects = section.getStringList("effects");

            SkillData data = new SkillData(key.toUpperCase(), name, description, icon, manaCost, cooldown, effects);
            skillDataMap.put(key.toUpperCase(), data);
            registerSkillExecutor(key.toUpperCase(), effects);
        }
    }

    private void registerSkillExecutor(String skillId, List<String> effects) {
        Skill executor = (caster, skillData) -> {
            for (String effect : effects) {
                String[] parts = effect.split(":", 2);
                String type = parts[0].toUpperCase();
                String args = (parts.length > 1) ? parts[1] : "";

                // TODO: 더 많은 effect 타입과 args 파싱 로직 추가
                if (type.equals("PROJECTILE")) {
                    if (args.startsWith("FIREBALL")) {
                        Vector direction = caster.getEyeLocation().getDirection();
                        caster.launchProjectile(SmallFireball.class, direction.multiply(1.5));
                    }
                } else if (type.equals("SOUND")) {
                    caster.playSound(caster.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.2f);
                }
            }
        };
        skillExecutorMap.put(skillId, executor);
    }

    public void castSkill(Player player, String skillId, boolean isBareHand, boolean isCharged) {
        SkillData skillData = skillDataMap.get(skillId.toUpperCase());
        Skill executor = skillExecutorMap.get(skillId.toUpperCase());
        PlayerData playerData = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);

        if (skillData != null && executor != null && playerData != null) {
            double remainingCooldown = playerData.getSkills().getRemainingCooldown(skillData.getId(), skillData.getCooldown());
            if (remainingCooldown > 0) {
                player.sendActionBar(Component.text(skillData.getName() + " 재사용 대기 중: " + String.format("%.1f초", remainingCooldown), NamedTextColor.RED));
                return;
            }

            if (!playerData.getSkills().consumeMana(skillData.getManaCost())) {
                player.sendActionBar(Component.text("마나가 부족합니다.", NamedTextColor.BLUE));
                return;
            }

            if (isBareHand) {
                player.sendMessage(ChatColor.GRAY + "마법을 집중합니다...");
                new BukkitRunnable() {
                    int chargeTime = 0;
                    @Override
                    public void run() {
                        if (chargeTime >= 20) {
                            executor.execute(player, skillData);
                            playerData.getSkills().setCooldown(skillData.getId());
                            this.cancel();
                            return;
                        }
                        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation().add(0, 1, 0), 5);
                        chargeTime++;
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            } else {
                executor.execute(player, skillData);
                playerData.getSkills().setCooldown(skillData.getId());
            }
        }
    }

    public SkillData getSkillData(String skillId) {
        return skillDataMap.get(skillId.toUpperCase());
    }
}