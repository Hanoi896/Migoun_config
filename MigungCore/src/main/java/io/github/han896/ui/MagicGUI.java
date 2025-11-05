package io.github.han896.ui;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import io.github.han896.skill.SkillData;
import io.github.han896.skill.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MagicGUI {

    // TODO: 페이지 정보를 저장하는 변수 (나중에 GUIClickListener와 연동)
    private int currentPage = 0;

    public void open(Player player) {
        PlayerData data = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        int magicBarNum = data.getSkills().getCurrentMagicBar();
        Inventory gui = Bukkit.createInventory(null, 54, "마법 관리 (바 " + (magicBarNum + 1) + ")");

        // --- 배경 및 구분선 아이템 ---
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i == 17 || i == 18 || i == 26 || i == 27 || i > 44) {
                gui.setItem(i, border);
            }
        }

        // --- 상단: 마법 퀵슬롯 바 ---
        int maxSlots = data.getSkills().getMaxSpellSlots();
        List<String> spellSlots = data.getSkills().getAllSpellSlots().get(magicBarNum);
        SkillManager skillManager = MigungCore.getInstance().getSkillManager();

        for (int i = 0; i < 8; i++) {
            ItemStack slotItem;
            if (i >= maxSlots) { // 잠긴 슬롯
                slotItem = new ItemStack(Material.BARRIER);
                ItemMeta meta = slotItem.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.DARK_RED + "잠긴 슬롯");
                    meta.setLore(Collections.singletonList(ChatColor.GRAY + "지혜(WIS) 스탯을 올려 해금하세요."));
                    slotItem.setItemMeta(meta);
                }
            } else {
                String spellId = spellSlots.get(i);
                if (spellId == null) { // 비어있는 슬롯
                    slotItem = new ItemStack(Material.GLASS_BOTTLE);
                    ItemMeta meta = slotItem.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ChatColor.GREEN + "비어있는 슬롯 " + (i + 1));
                        meta.setLore(Collections.singletonList(ChatColor.GRAY + "아래에서 마법을 선택하여 등록하세요."));
                        slotItem.setItemMeta(meta);
                    }
                } else { // 등록된 마법
                    SkillData skillData = skillManager.getSkillData(spellId);
                    slotItem = new ItemStack(skillData != null ? skillData.getIcon() : Material.ENCHANTED_BOOK);
                    ItemMeta meta = slotItem.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ChatColor.AQUA + (skillData != null ? skillData.getName() : spellId));
                        meta.setLore(Collections.singletonList(ChatColor.DARK_AQUA + "우클릭하여 장착 해제"));
                        slotItem.setItemMeta(meta);
                    }
                }
            }
            gui.setItem(18 + i, slotItem); // 중앙 8칸에 배치
        }

        // --- 하단: 배운 마법 목록 ---
        List<String> learnedSpells = data.getSkills().getLearnedSpells();
        int itemsPerPage = 14; // 2줄 * 7칸
        int startIndex = currentPage * itemsPerPage;

        for (int i = 0; i < itemsPerPage; i++) {
            int learnedIndex = startIndex + i;
            if (learnedIndex < learnedSpells.size()) {
                String spellId = learnedSpells.get(learnedIndex);
                SkillData skillData = skillManager.getSkillData(spellId);
                if (skillData == null) continue;

                ItemStack spellItem = new ItemStack(skillData.getIcon());
                ItemMeta meta = spellItem.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.LIGHT_PURPLE + skillData.getName());
                    meta.setLore(Arrays.asList(
                            ChatColor.GRAY + skillData.getDescription(),
                            "",
                            ChatColor.YELLOW + "마나 소모: " + skillData.getManaCost(),
                            ChatColor.YELLOW + "쿨타임: " + skillData.getCooldown() + "초",
                            "",
                            ChatColor.GREEN + "클릭하여 장착할 슬롯을 선택하세요."
                    ));
                    spellItem.setItemMeta(meta);
                }
                gui.setItem(31 + i, spellItem);
            }
        }

        // --- 네비게이션 버튼 ---
        ItemStack prevPage = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prevPage.getItemMeta();
        if (prevMeta != null) {
            prevMeta.setDisplayName(ChatColor.YELLOW + "<-- 이전 페이지");
            prevPage.setItemMeta(prevMeta);
        }
        gui.setItem(45, prevPage);

        ItemStack nextPage = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextPage.getItemMeta();
        if (nextMeta != null) {
            nextMeta.setDisplayName(ChatColor.YELLOW + "다음 페이지 -->");
            nextPage.setItemMeta(nextMeta);
        }
        gui.setItem(53, nextPage);

        player.openInventory(gui);
    }
}