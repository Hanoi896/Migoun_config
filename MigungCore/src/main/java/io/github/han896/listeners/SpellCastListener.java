package io.github.han896.listeners;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import io.github.han896.skill.SkillManager;
import io.github.han896.util.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpellCastListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData data = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
        // 전투 모드가 아니면 아무것도 하지 않음
        if (data == null || data.getCombatMode() != PlayerData.CombatMode.COMBAT) return;

        Action action = event.getAction();
        // 좌클릭 또는 우클릭일 때만 실행
        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK &&
                action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // 1. 특수 무기 스킬 우선 처리 (변경 없음)
        String itemSkillTag = ItemFactory.getTag(itemInHand, "skill_id");
        if (itemSkillTag != null) {
            // TODO: 아이템 고유 스킬 시전 로직
            player.sendMessage("아이템 고유 스킬 [" + itemSkillTag + "] 발동!");
            event.setCancelled(true);
            return;
        }

        // 2. 마법봉(괭이) 또는 맨손일 경우 -> 마법 시전
        boolean isWand = itemInHand.getType().name().endsWith("_HOE");
        boolean isBareHand = itemInHand.getType() == Material.AIR;

        if (isWand || isBareHand) {
            event.setCancelled(true); // 기본 행동 방지

            int barNum = data.getCurrentMagicBar();
            int slotNum = data.getCurrentSpellSlot(); // TODO: 현재 선택된 슬롯 번호 가져오기
            String spellId = data.getAllSpellSlots().get(barNum).get(slotNum);

            if (spellId == null) {
                player.sendActionBar(Component.text("비어있는 마법 슬롯입니다.", NamedTextColor.RED));
                return;
            }

            boolean isCharged = (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
            MigungCore.getInstance().getSkillManager().castSkill(player, spellId, isBareHand, isCharged);
        }

        // 3. 그 외 일반 무기(검, 도끼 등)일 경우 -> 액티브 스킬 시전
        else {
            // TODO: 여기에 액티브 스킬 시전 로직 구현
            // 예: /내스킬 GUI에서 등록한 '회전베기' 같은 스킬 발동
        }
    }
}