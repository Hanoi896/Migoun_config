package io.github.han896.skill;

import org.bukkit.Material;
import java.util.List;

public class SkillData {
    private final String id;
    private final String name;
    private final String description;
    private final Material icon;
    private final double manaCost;
    private final double cooldown;
    private final List<String> effects;

    public SkillData(String id, String name, String description, Material icon, double manaCost, double cooldown, List<String> effects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.effects = effects;
    }

    // --- Getter 메소드들 ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Material getIcon() { return icon; }
    public double getManaCost() { return manaCost; }
    public double getCooldown() { return cooldown; }
    public List<String> getEffects() { return effects; }
}