package io.github.han896.listeners;

import io.github.han896.MigungCore;
import io.github.han896.player.CombatMode; // (해결!) CombatMode의 새로운 경로 import
import io.github.han896.player.PlayerData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class CombatModeListener implements Listener {

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        PlayerData data = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        if (event.isSneaking()) {
            data.getSkills().setCombatMode(CombatMode.COMBAT);
        } else {
            data.getSkills().setCombatMode(CombatMode.NONE);
        }
        MigungCore.getInstance().getUiManager().updateActionBar(player, data);
    }

    @EventHandler
    public void onPlayerScroll(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PlayerData data = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
        if (data == null || data.getSkills().getCombatMode() != CombatMode.COMBAT) {
            return;
        }

        event.setCancelled(true);
        int previous = event.getPreviousSlot();
        int next = event.getNewSlot();

        if (next > previous || (previous == 8 && next == 0)) { // 휠 아래로
            data.getSkills().cycleNextMagicBar();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.8f);
        } else if (next < previous || (previous == 0 && next == 8)) { // 휠 위로
            data.getSkills().cycleNextSkillBar();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.5f);
        }

        MigungCore.getInstance().getUiManager().updateActionBar(player, data);
    }
}