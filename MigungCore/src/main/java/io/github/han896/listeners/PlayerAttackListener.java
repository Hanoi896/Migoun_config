package io.github.han896.listeners;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.time.Duration;

public class PlayerAttackListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        Player player = null;
        if (event.getDamager() instanceof Player p) {
            player = p;
        } else if (event.getDamager() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player p) {
                player = p;
            }
        }
        if (player == null) return;

        PlayerData data = MigungCore.getInstance().getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (data.getSkills().getCombatMode() == PlayerData.CombatMode.COMBAT) {
            boolean isWand = itemInHand.getType().name().endsWith("_HOE");
            boolean isBareHand = itemInHand.getType() == Material.AIR;
            if (isWand || isBareHand) {
                return;
            }
        }

        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (!data.getSkills().consumeStamina(1.0)) {
            event.setCancelled(true);
            player.sendActionBar(Component.text("스태미나가 부족하여 공격할 수 없습니다!", NamedTextColor.RED));
            return;
        }

        String weaponType = itemInHand.getType().name();
        String skillIdToLevelUp = null;
        if (weaponType.endsWith("_SWORD")) skillIdToLevelUp = "SWORDSMANSHIP";
        else if (weaponType.endsWith("_AXE")) skillIdToLevelUp = "AXE_MASTERY";
        else if (itemInHand.getType() == Material.TRIDENT) skillIdToLevelUp = "SPEAR_MASTERY";
        else if (weaponType.endsWith("_SHOVEL")) skillIdToLevelUp = "BLUNT_MASTERY";

        if (skillIdToLevelUp != null) {
            if (data.getSkills().learnSkill(skillIdToLevelUp)) {
                String skillName = PlayerData.getSkillName(skillIdToLevelUp);
                final Title title = Title.title(
                        Component.text("새로운 스킬 획득!", NamedTextColor.GREEN),
                        Component.text("[" + skillName + "]", NamedTextColor.WHITE),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
                );
                player.showTitle(title);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.8f);
            }
            data.getSkills().addSkillExp(skillIdToLevelUp, 1);
        }

        double finalDamage = event.getDamage();
        double skillBonus = 0;

        if (skillIdToLevelUp != null) {
            int skillLevel = data.getSkills().getSkillLevel(skillIdToLevelUp);
            skillBonus = skillLevel * 0.5;
            finalDamage += skillBonus;
        }

        event.setDamage(finalDamage);

        int strength = data.getStats().getStat("STR");
        double statBonus = strength * 0.2;

        player.sendActionBar(
                Component.text("데미지: " + String.format("%.1f", finalDamage), NamedTextColor.GRAY)
                        .append(Component.text(" (힘+", NamedTextColor.RED))
                        .append(Component.text(String.format("%.1f", statBonus), NamedTextColor.RED))
                        .append(Component.text(" | 스킬+", NamedTextColor.AQUA))
                        .append(Component.text(String.format("%.1f", skillBonus), NamedTextColor.AQUA))
                        .append(Component.text(")", NamedTextColor.GRAY))
        );
    }
}