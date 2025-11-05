package io.github.han896.listeners;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import io.github.han896.quest.Quest;
import io.github.han896.quest.QuestManager;
import io.github.han896.quest.QuestStep;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            PlayerData data = MigungCore.getInstance().getPlayerDataManager().getPlayerData(killer);
            if (data == null) return;

            data.getStats().addMainExp(15);

            QuestManager questManager = MigungCore.getInstance().getQuestManager();
            String mobId = event.getEntityType().name();

            for (String questId : data.getQuests().getActiveQuests()) {
                Quest quest = questManager.getQuest(questId);
                if (quest == null) continue;
                int currentStepNum = data.getQuests().getQuestStep(questId);
                if (currentStepNum <= 0 || currentStepNum > quest.getSteps().size()) continue;

                QuestStep currentStep = quest.getSteps().get(currentStepNum - 1);
                if ("KILL".equalsIgnoreCase(currentStep.getType()) && currentStep.getTarget().equalsIgnoreCase(mobId)) {
                    data.getQuests().incrementQuestProgress(questId, currentStepNum);
                    int progress = data.getQuests().getQuestProgress(questId, currentStepNum);
                    killer.sendMessage(ChatColor.GOLD + "[퀘스트] " + currentStep.getTarget() + " 처치 (" + progress + "/" + currentStep.getAmount() + ")");
                }
            }
        }
    }
}