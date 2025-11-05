package io.github.han896.player;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import java.util.UUID;

public class StatUpdateManager {

    private static final UUID STR_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
    private static final UUID VIT_MODIFIER_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-2345-67890abcdef1");
    private static final UUID AGI_MODIFIER_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-3456-7890abcdef12");

    public static void updateStats(Player player, PlayerData data) {
        if (player == null || data == null) return;

        // PlayerStats 전문가에게 스탯 정보를 요청합니다.
        applyAttackDamageModifier(player, data.getStats().getStat("STR"));
        applySpeedModifiers(player, data.getStats().getStat("AGI"));
        applyMaxHealthModifier(player, data.getStats().getStat("VIT"));

        // PlayerSkills 전문가에게 마나/스태미나 업데이트를 요청합니다.
        data.getSkills().updateMaxMana();
        data.getSkills().updateMaxStamina();
    }

    private static void applyAttackDamageModifier(Player player, int strength) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attribute != null) {
            removeModifier(attribute, STR_MODIFIER_UUID);
            double strengthBonus = strength * 0.2;
            AttributeModifier modifier = new AttributeModifier(STR_MODIFIER_UUID, "MigungCore STR", strengthBonus, AttributeModifier.Operation.ADD_NUMBER);
            attribute.addModifier(modifier);
        }
    }

    private static void applySpeedModifiers(Player player, int agility) {
        float newWalkSpeed = 0.2f + (agility * 0.001f);
        player.setWalkSpeed(Math.min(newWalkSpeed, 1.0f));
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute != null) {
            removeModifier(attribute, AGI_MODIFIER_UUID);
            double agilityBonus = agility * 0.01;
            AttributeModifier modifier = new AttributeModifier(AGI_MODIFIER_UUID, "MigungCore AGI", agilityBonus, AttributeModifier.Operation.ADD_NUMBER);
            attribute.addModifier(modifier);
        }
    }

    private static void applyMaxHealthModifier(Player player, int vitality) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(8.0);
            removeModifier(attribute, VIT_MODIFIER_UUID);
            double vitalityBonus = vitality * 0.5;
            AttributeModifier modifier = new AttributeModifier(VIT_MODIFIER_UUID, "MigungCore VIT", vitalityBonus, AttributeModifier.Operation.ADD_NUMBER);
            attribute.addModifier(modifier);
            if (player.getHealth() > attribute.getValue()) {
                player.setHealth(attribute.getValue());
            }
        }
    }

    private static void removeModifier(AttributeInstance attribute, UUID uuid) {
        attribute.getModifiers().stream()
                .filter(modifier -> modifier.getUniqueId().equals(uuid))
                .forEach(attribute::removeModifier);
    }
}