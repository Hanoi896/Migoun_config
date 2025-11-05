package io.github.han896.ui;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkillDetailGUI {

    public void openInventory(Player player, String skillId) {
        PlayerData data = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
        if (data == null) {
            player.sendMessage(ChatColor.RED + "플레이어 데이터를 불러올 수 없습니다.");
            return;
        }

        String guiTitle = "";
        ItemStack infoItem = new ItemStack(Material.AIR);
        List<ItemStack> proficiencyBar = new ArrayList<>();
        ItemStack userItem = new ItemStack(Material.AIR);

        // 스킬 ID에 따라 GUI 내용 생성
        String skillName = data.getSkills().getSkillName(skillId);
        if (skillName != null) {
            guiTitle = "스킬 정보: [" + skillName + "]";
            int level = data.getSkills().getSkillLevel(skillId);
            int exp = data.getSkills().getSkillExp(skillId);
            int requiredExp = level * 10;
            String description = data.getSkills().getSkillDescription(skillId);

            // 1. 스킬 정보 아이템
            infoItem = new ItemStack(getSkillIcon(skillId)); // 아이콘을 가져오는 헬퍼 메소드 사용
            ItemMeta infoMeta = infoItem.getItemMeta();
            if (infoMeta != null) {
                infoMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "[" + skillName + "]");
                infoMeta.setLore(Collections.singletonList(ChatColor.GRAY + description));
                infoItem.setItemMeta(infoMeta);
            }

            // 2. 숙련도 바 아이템
            double percentage = (requiredExp == 0) ? 1.0 : (double) exp / requiredExp;
            for (int i = 0; i < 9; i++) {
                ItemStack barItem;
                if (percentage > (double) i / 9) {
                    barItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                    ItemMeta barMeta = barItem.getItemMeta();
                    if (barMeta != null) {
                        barMeta.setDisplayName(ChatColor.GREEN + "숙련도: " + String.format("%.1f%%", percentage * 100));
                        barItem.setItemMeta(barMeta);
                    }
                } else {
                    barItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta barMeta = barItem.getItemMeta();
                    if (barMeta != null) {
                        barMeta.setDisplayName(ChatColor.GRAY + " ");
                        barItem.setItemMeta(barMeta);
                    }
                }
                proficiencyBar.add(barItem);
            }

            // 3. 스킬 사용자 정보 아이템
            userItem = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta userMeta = userItem.getItemMeta();
            if (userMeta != null) {
                userMeta.setDisplayName(ChatColor.AQUA + "스킬 사용자");
                userMeta.setLore(Collections.singletonList(ChatColor.WHITE + player.getName()));
                userItem.setItemMeta(userMeta);
            }
        } else {
            return; // 알 수 없는 스킬 ID일 경우 GUI를 열지 않음
        }

        // GUI 생성 및 배경 채우기
        Inventory gui = Bukkit.createInventory(null, 54, guiTitle);
        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bgMeta = background.getItemMeta();
        if (bgMeta != null) {
            bgMeta.setDisplayName(" ");
            background.setItemMeta(bgMeta);
        }
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }

        // 아이템 배치
        gui.setItem(13, infoItem);
        gui.setItem(22, userItem);

        for (int i = 0; i < 9; i++) {
            gui.setItem(36 + i, proficiencyBar.get(i));
        }

        player.openInventory(gui);
    }

    // 스킬 ID에 따라 적절한 아이콘을 반환하는 헬퍼 메소드
    private Material getSkillIcon(String skillId) {
        return switch (skillId.toUpperCase()) {
            case "SWORDSMANSHIP" -> Material.IRON_SWORD;
            case "AXE_MASTERY" -> Material.IRON_AXE;
            case "SPEAR_MASTERY" -> Material.TRIDENT;
            case "BLUNT_MASTERY" -> Material.IRON_SHOVEL;
            default -> Material.BOOK;
        };
    }
}