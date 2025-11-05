package io.github.han896.listeners;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import io.github.han896.player.StatUpdateManager;
import io.github.han896.ui.MagicGUI;
import io.github.han896.ui.SkillDetailGUI;
import io.github.han896.ui.StatsGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIClickListener implements Listener {

    private final Map<UUID, String> isEquipping = new HashMap<>();

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!(event.getWhoClicked() instanceof Player player)) return;

        PlayerData data = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        // --- 내 정보 (스탯) GUI 처리 ---
        if (title.equals("내 정보")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            ClickType clickType = event.getClick();
            int pointsToSpend = 0;
            if (clickType.isLeftClick()) pointsToSpend = 1;
            else if (clickType.isRightClick()) pointsToSpend = 5;

            if (pointsToSpend > 0 && data.getStats().getStatPoints() >= pointsToSpend) {
                String statToUpgrade = switch (clickedItem.getType()) {
                    case IRON_SWORD -> "STR"; case FEATHER -> "AGI";
                    case BOOK -> "INT"; case APPLE -> "VIT";
                    case EXPERIENCE_BOTTLE -> "WIS"; default -> null;
                };
                if (statToUpgrade != null) {
                    data.getStats().addStatPoints(-pointsToSpend);
                    data.getStats().setStat(statToUpgrade, data.getStats().getStat(statToUpgrade) + pointsToSpend);
                    StatUpdateManager.updateStats(player, data);
                    MigungCore.getInstance().getUiManager().updateActionBar(player, data);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
                    new StatsGUI().openInventory(player);
                }
            }
            return;
        }

        // --- 스킬 관련 GUI 처리 ---
        if (title.equals("내 스킬 목록") || title.startsWith("스킬 정보:")) {
            event.setCancelled(true);
            if (title.equals("내 스킬 목록")) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
                String skillId = switch (clickedItem.getType()) {
                    case IRON_SWORD -> "SWORDSMANSHIP";
                    case IRON_AXE -> "AXE_MASTERY";
                    case TRIDENT -> "SPEAR_MASTERY";
                    case IRON_SHOVEL -> "BLUNT_MASTERY";
                    default -> null;
                };
                if (skillId != null) {
                    new SkillDetailGUI().openInventory(player, skillId);
                }
            }
            return;
        }

        // --- 마법 관리 GUI 처리 ---
        if (title.startsWith("마법 관리")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            int clickedSlot = event.getSlot();

            // 하단 (배운 마법 목록) 클릭
            if (clickedSlot >= 28 && clickedSlot <= 53) {
                String spellId = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                isEquipping.put(player.getUniqueId(), spellId);
                player.sendMessage(ChatColor.AQUA + "[" + spellId + "] 마법을 선택했습니다. 장착할 상단 슬롯을 클릭하세요.");
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                return;
            }

            // 상단 (마법 슬롯) 클릭
            if (clickedSlot >= 10 && clickedSlot <= 17) {
                int barNum = data.getSkills().getCurrentMagicBar();
                int slotIndex = clickedSlot - 10;

                if (event.getClick() == ClickType.RIGHT) {
                    data.getSkills().getAllSpellSlots().get(barNum).set(slotIndex, null);
                    player.sendMessage(ChatColor.YELLOW + (slotIndex + 1) + "번 슬롯의 마법을 해제했습니다.");
                    new MagicGUI().open(player);
                    return;
                }

                if (isEquipping.containsKey(player.getUniqueId())) {
                    String spellToEquip = isEquipping.get(player.getUniqueId());
                    data.getSkills().getAllSpellSlots().get(barNum).set(slotIndex, spellToEquip);
                    player.sendMessage(ChatColor.GREEN + "[" + spellToEquip + "] 마법을 " + (slotIndex + 1) + "번 슬롯에 등록했습니다.");
                    isEquipping.remove(player.getUniqueId());
                    new MagicGUI().open(player);
                }
            }
        }
    }
}