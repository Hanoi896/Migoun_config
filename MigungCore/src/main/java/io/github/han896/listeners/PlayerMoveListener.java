package io.github.han896.listeners;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // (해결!) 최신 Paper API 방식으로 위치 변경을 확인합니다.
        // 이전 위치와 새로운 위치의 x, y, z 좌표가 모두 동일하면(즉, 머리만 돌린 경우) 무시합니다.
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ())) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (player.isSprinting()) {
            PlayerData data = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
            if (data == null) return;

            // 0.2초(4틱)마다 1의 스태미나를 소모 -> 초당 5
            if (!data.consumeStamina(1.0)) {
                player.setSprinting(false);
            }
        }
    }
}