package io.github.han896.ui;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import io.github.han896.player.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.Collections;

public class StatsGUI {
    public void openInventory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "내 정보");
        PlayerData playerData = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
        if (playerData == null) return;

        PlayerStats playerStats = playerData.getStats();

        // 스탯 아이템 생성
        gui.setItem(10, createStatItem(Material.IRON_SWORD, "힘 (STR)", playerStats.getStat("STR"), "물리 공격력에 영향을 줍니다."));
        gui.setItem(11, createStatItem(Material.FEATHER, "민첩 (AGI)", playerStats.getStat("AGI"), "공격 속도와 회피율에 영향을 줍니다."));
        gui.setItem(12, createStatItem(Material.BOOK, "지능 (INT)", playerStats.getStat("INT"), "마법 공격력에 영향을 줍니다."));
        gui.setItem(14, createStatItem(Material.APPLE, "체력 (VIT)", playerStats.getStat("VIT"), "최대 체력에 영향을 줍니다."));
        gui.setItem(15, createStatItem(Material.EXPERIENCE_BOTTLE, "지혜 (WIS)", playerStats.getStat("WIS"), "최대 마나와 마나 회복량에 영향을 줍니다."));

        // 남은 스탯 포인트 정보
        ItemStack pointsItem = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta pointsMeta = pointsItem.getItemMeta();
        if (pointsMeta != null) {
            pointsMeta.setDisplayName(ChatColor.AQUA + "남은 스탯 포인트");
            pointsMeta.setLore(Collections.singletonList(ChatColor.WHITE + "" + playerStats.getStatPoints() + " 포인트"));
            pointsItem.setItemMeta(pointsMeta);
        }
        gui.setItem(16, pointsItem);

        player.openInventory(gui);
    }

    private ItemStack createStatItem(Material material, String name, int value, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + name);
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + description, "",
                    ChatColor.YELLOW + "현재 수치: " + value, "",
                    ChatColor.GREEN + "클릭하여 포인트 분배"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
}