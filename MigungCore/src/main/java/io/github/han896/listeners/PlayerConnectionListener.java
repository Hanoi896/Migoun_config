package io.github.han896.listeners;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import io.github.han896.player.StatUpdateManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MigungCore.getInstance().getPlayerDataManager().loadPlayerData(player);

        PlayerData data = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
        StatUpdateManager.updateStats(player, data); // 접속 시 스탯 적용

        // (새로운 기능!) UIManager를 통해 package io.github.han896.ui;
        //
        //import io.github.han896.MigungCore;
        //import io.github.han896.player.PlayerData;
        //import net.kyori.adventure.text.Component;
        //import net.kyori.adventure.text.format.NamedTextColor;
        //import net.kyori.adventure.text.format.TextDecoration;
        //import org.bukkit.Bukkit;
        //import org.bukkit.attribute.Attribute;
        //import org.bukkit.attribute.AttributeInstance;
        //import org.bukkit.boss.BarColor;
        //import org.bukkit.boss.BarStyle;
        //import org.bukkit.boss.BossBar;
        //import org.bukkit.entity.Player;
        //import org.bukkit.scheduler.BukkitRunnable;
        //import java.util.HashMap;
        //import java.util.List;
        //import java.util.Map;
        //import java.util.UUID;
        //
        //public class UIManager {
        //
        //    private final Map<UUID, BossBar> bossBars = new HashMap<>();
        //
        //    public UIManager(MigungCore plugin) {
        //        new BukkitRunnable() {
        //            @Override
        //            public void run() {
        //                for (Player player : Bukkit.getOnlinePlayers()) {
        //                    PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        //                    if (data == null) continue;
        //
        //                    if (!player.isSprinting()) {
        //                        data.regenerateStamina();
        //                    }
        //                    data.regenerateMana();
        //
        //                    updateActionBar(player, data);
        //                    updateBossBar(player, data); // BossBar 업데이트 로직 추가
        //                }
        //            }
        //        }.runTaskTimer(plugin, 0L, 20L);
        //    }
        //
        //    // --- 새로 추가된 메서드 ---
        //
        //    /**
        //     * 플레이어 접속 시 BossBar를 생성하고 등록합니다.
        //     */
        //    public void setupPlayerUI(Player player) {
        //        // BossBar 생성: 초기 제목은 비워두고, 색상은 BLUE, 스타일은 SOLID로 설정
        //        BossBar bossBar = Bukkit.createBossBar(
        //                "",
        //                BarColor.BLUE,
        //                BarStyle.SOLID
        //        );
        //        bossBar.addPlayer(player);
        //        bossBars.put(player.getUniqueId(), bossBar);
        //    }
        //
        //    /**
        //     * 플레이어 종료 시 BossBar를 제거하고 맵에서 해제합니다.
        //     */
        //    public void clearPlayerUI(Player player) {
        //        BossBar bossBar = bossBars.remove(player.getUniqueId());
        //        if (bossBar != null) {
        //            bossBar.removeAll(); // 플레이어에게서 BossBar를 제거
        //        }
        //    }
        //
        //    /**
        //     * BossBar의 내용을 업데이트합니다. (20틱마다 실행됨)
        //     */
        //    public void updateBossBar(Player player, PlayerData data) {
        //        BossBar bossBar = bossBars.get(player.getUniqueId());
        //        if (bossBar == null) return;
        //
        //        // 경험치(EXP)와 레벨 정보로 BossBar를 채웁니다.
        //        String title = String.format("§eLv. %d §fEXP: %.0f/%.0f",
        //            data.getLevel(), data.getExp(), data.getRequiredExpForNextLevel());
        //
        //        bossBar.setTitle(title);
        //
        //        double progress = data.getExp() / data.getRequiredExpForNextLevel();
        //        bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        //    }
        //
        //    // --- 기존 메서드 ---
        //
        //    public void updateActionBar(Player player, PlayerData data) {
        //        if (data.isMagicMode()) {
        //            updateMagicBar(player, data);
        //        } else {
        //            updateStatusBar(player, data);
        //        }
        //    }
        //
        //    private void updateStatusBar(Player player, PlayerData data) {
        //        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        //        double maxHealth = (maxHealthAttribute != null) ? maxHealthAttribute.getValue() : 20.0;
        //
        //        player.sendActionBar(
        //                Component.text("HP ", NamedTextColor.RED)
        //                        .append(Component.text(String.format("%.0f / %.0f", player.getHealth(), maxHealth), NamedTextColor.WHITE))
        //                        .append(Component.text(" | MP ", NamedTextColor.BLUE))
        //                        .append(Component.text(String.format("%.0f / %.0f", data.getCurrentMana(), data.getMaxMana()), NamedTextColor.WHITE))
        //                        .append(Component.text(" | SP ", NamedTextColor.GREEN))
        //                        .append(Component.text(String.format("%.0f / %.0f", data.getCurrentStamina(), data.getMaxStamina()), NamedTextColor.WHITE))
        //        );
        //    }
        //
        //    private void updateMagicBar(Player player, PlayerData data) {
        //        Component magicBar = Component.text("");
        //        int maxSlots = data.getMaxSpellSlots();
        //        int currentSlot = data.getCurrentSpellSlot();
        //        List<String> spellSlots = data.getSpellSlots();
        //
        //        for (int i = 0; i < 4; i++) {
        //            if (i > 0) magicBar = magicBar.append(Component.text(" ", NamedTextColor.GRAY));
        //
        //            if (i >= maxSlots) {
        //                magicBar = magicBar.append(Component.text("[잠김]", NamedTextColor.DARK_GRAY));
        //            } else {
        //                String spellId = spellSlots.get(i);
        //                if (spellId == null) {
        //                    magicBar = magicBar.append(Component.text("[비어있음]", NamedTextColor.GRAY));
        //                } else {
        //                    if (i == currentSlot) {
        //                        magicBar = magicBar.append(Component.text("<" + spellId + ">", NamedTextColor.AQUA, TextDecoration.BOLD));
        //                    } else {
        //                        magicBar = magicBar.append(Component.text("[" + spellId + "]", NamedTextColor.WHITE));
        //                    }
        //                }
        //            }
        //        }
        //        player.sendActionBar(magicBar);
        //    }
        //}UI를 설정합니다.
        MigungCore.getInstance().getUiManager().setupPlayerUI(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // ... (데이터 언로드는 동일)
        MigungCore.getInstance().getPlayerDataManager().unloadPlayerData(event.getPlayer());

        // (새로운 기능!) UIManager를 통해 UI를 정리합니다.
        MigungCore.getInstance().getUiManager().clearPlayerUI(event.getPlayer());
    }
}