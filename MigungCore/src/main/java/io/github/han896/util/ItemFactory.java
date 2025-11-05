package io.github.han896.util;

import io.github.han896.MigungCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set; // Set import 추가

public class ItemFactory {
    private final Map<String, ItemStack> itemPrototypes = new HashMap<>();
    private final MigungCore plugin;

    public ItemFactory(MigungCore plugin) {
        this.plugin = plugin;
        File itemsFile = new File(plugin.getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            plugin.saveResource("items.yml", false);
        }
        loadItemsFromFile(itemsFile);
    }

    private void loadItemsFromFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;

            Material material = Material.getMaterial(section.getString("material", "PAPER").toUpperCase());
            if (material == null) {
                plugin.getLogger().warning("알 수 없는 Material: " + section.getString("material") + " (in " + key + ")");
                material = Material.PAPER;
            }
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            if (section.contains("display_name")) {
                meta.displayName(Component.text(section.getString("display_name").replace('&', '§')).decoration(TextDecoration.ITALIC, false));
            }
            if (section.contains("lore")) {
                List<Component> lore = new ArrayList<>();
                for (String line : section.getStringList("lore")) {
                    lore.add(Component.text(line.replace('&', '§')).decoration(TextDecoration.ITALIC, false));
                }
                meta.lore(lore);
            }

            if (section.contains("nbt_tag")) {
                String[] tagParts = section.getString("nbt_tag").split(":", 2);
                if (tagParts.length == 2) {
                    NamespacedKey nbtKey = new NamespacedKey(plugin, tagParts[0]);
                    meta.getPersistentDataContainer().set(nbtKey, PersistentDataType.STRING, tagParts[1]);
                }
            }
            item.setItemMeta(meta);
            itemPrototypes.put(key.toUpperCase(), item);
        }
    }

    public ItemStack createItem(String id) {
        ItemStack prototype = itemPrototypes.get(id.toUpperCase());
        return (prototype != null) ? prototype.clone() : null;
    }

    // (해결!) 탭 자동 완성을 위한 새로운 메소드
    public Set<String> getItemIds() {
        return itemPrototypes.keySet();
    }

    public static String getTag(ItemStack item, String key) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        NamespacedKey nbtKey = new NamespacedKey(MigungCore.getInstance(), key);
        if (meta.getPersistentDataContainer().has(nbtKey, PersistentDataType.STRING)) {
            return meta.getPersistentDataContainer().get(nbtKey, PersistentDataType.STRING);
        }
        return null;
    }
}