package io.github.han896.quest;

import io.github.han896.MigungCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestManager {
    private final Map<String, Quest> quests = new HashMap<>();

    public QuestManager(MigungCore plugin) {
        File questsFile = new File(plugin.getDataFolder(), "quests.yml");
        if (!questsFile.exists()) {
            plugin.saveResource("quests.yml", false);
        }
        loadQuests(questsFile);
    }

    private void loadQuests(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;

            String name = section.getString("name", "이름 없는 퀘스트");
            List<QuestStep> steps = new ArrayList<>();
            ConfigurationSection stepsSection = section.getConfigurationSection("steps");
            if (stepsSection != null) {
                for (String stepKey : stepsSection.getKeys(false)) {
                    ConfigurationSection stepSection = stepsSection.getConfigurationSection(stepKey);
                    if (stepSection != null) {
                        steps.add(new QuestStep(
                                stepSection.getString("type", ""),
                                stepSection.getString("target", ""),
                                stepSection.getInt("amount", 0),
                                stepSection.getString("description", "")
                        ));
                    }
                }
            }
            quests.put(key.toUpperCase(), new Quest(key.toUpperCase(), name, steps));
        }
    }

    public Quest getQuest(String id) {
        return quests.get(id.toUpperCase());
    }
}