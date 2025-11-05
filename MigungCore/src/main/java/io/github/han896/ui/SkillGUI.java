package io.github.han896.ui;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import io.github.han896.player.PlayerSkills;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.Map;

public class SkillGUI {

    public void openInventory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "내 스킬 목록");

        PlayerData playerData = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
        if (playerData == null) return;

        PlayerSkills playerSkills = playerData.getSkills();

        // 플레이어가 배운 모든 패시브/숙련도 스킬을 가져와서 GUI에 표시
        for (Map.Entry<String, int[]> entry : playerSkills.getSkills().entrySet()) {
            String skillId = entry.getKey();
            gui.addItem(createSkillItem(playerSkills, skillId));
        }

        player.openInventory(gui);
    }

    private ItemStack createSkillItem(PlayerSkills playerSkills, String skillId) {
        int level = playerSkills.getSkillLevel(skillId);
        int exp = playerSkills.getSkillExp(skillId);
        int requiredExp = level * 10;
        String skillName = PlayerSkills.getSkillName(skillId);
        String description = playerSkills.getSkillDescription(skillId);

        Material icon = switch (skillId) {
            case "SWORDSMANSHIP" -> Material.IRON_SWORD;
            case "AXE_MASTERY" -> Material.IRON_AXE;
            case "SPEAR_MASTERY" -> Material.TRIDENT;
            case "BLUNT_MASTERY" -> Material.IRON_SHOVEL;
            default -> Material.BOOK;
        };

        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + "[" + skillName + "]");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + description, "",
                    ChatColor.YELLOW + "레벨: " + level,
                    ChatColor.GREEN + "숙련도: " + exp + " / " + requiredExp, "",
                    ChatColor.AQUA + "클릭하여 상세 정보 보기"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
}