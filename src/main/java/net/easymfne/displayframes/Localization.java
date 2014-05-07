/*
 * This file is part of the DisplayFrames plugin by EasyMFnE.
 * 
 * DisplayFrames is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * 
 * DisplayFrames is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for details.
 * 
 * You should have received a copy of the GNU General Public License v3 along
 * with DisplayFrames. If not, see <http://www.gnu.org/licenses/>.
 */
package net.easymfne.displayframes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Utility class with static methods for fetching localized names for items and
 * other content. Can be used to create new localization file templates as well.
 * 
 * @author Eric Hildebrand
 */
public class Localization {
    
    private static final String LOCALE_FOLDER = File.separator + "locale"
            + File.separator;
    private static final String LOCALE_EXTENSION = ".lang";
    private static final String ENCHANTED_APPLE_COLOR = ChatColor.AQUA
            .toString();
    
    private static YamlConfiguration locale;
    
    /**
     * Take an enum name string (e.g. "DIAMOND_SWORD") and transform it into a
     * more displayable format (e.g. "Diamond Sword"). This method is typically
     * used as a fallback when no localized name for an object could be found.
     * 
     * @param enumString
     *            String to transform
     * @return Nicely formatted string
     */
    protected static String formatEnumString(String enumString) {
        if (enumString == null) {
            return null;
        }
        if (enumString.length() == 0) {
            return "";
        }
        String[] parts = enumString.split("_");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].substring(0, 1).toUpperCase()
                    + parts[i].substring(1).toLowerCase();
        }
        return StringUtils.join(parts, " ");
    }
    
    /**
     * Process a raw tick count into a nicely formatted display string
     * 
     * @param ticks
     * @return
     */
    protected static String formatTime(int ticks) {
        return String.format("(%d:%02d)", ticks / 1200, (ticks % 1200) / 20);
    }
    
    /**
     * Get a display String representing an Enchantment and its level.
     * 
     * @param enchant
     * @param level
     * @return
     */
    protected static String getEnchantment(Enchantment enchant, int level) {
        return locale.getString("enchantmentName." + enchant.getName(),
                formatEnumString(enchant.getName()))
                + " "
                + locale.getString("enchantmentLevel." + level, "" + level);
    }
    
    /**
     * Get a display String representing an ItemStack's name based upon its
     * Material and durability values. Handles special cases, or passes the item
     * along to getItemNameNormal() and gets its result.
     * 
     * @param item
     * @return
     */
    protected static String getItemName(ItemStack item) {
        switch (item.getType()) {
        case POTION:
            String potionName = getPotionName(item);
            if (potionName != null) {
                return potionName;
            }
            break;
        case GOLDEN_APPLE:
            if (item.getDurability() != 1) {
                break;
            }
            return ENCHANTED_APPLE_COLOR + getItemNameNormal(item);
        case MONSTER_EGG:
            return getItemNameNormal(item)
                    + locale.getString("eggNames." + item.getDurability(), "");
        case SKULL_ITEM:
            if (item.getDurability() != 3) {
                break;
            }
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta != null && meta.hasOwner()) {
                return String.format(getItemNameNormal(item), meta.getOwner());
            }
            return getItemNameNormal(new ItemStack(Material.SKULL_ITEM, 1,
                    (short) -1));
        case GOLD_RECORD:
        case GREEN_RECORD:
        case RECORD_3:
        case RECORD_4:
        case RECORD_5:
        case RECORD_6:
        case RECORD_7:
        case RECORD_8:
        case RECORD_9:
        case RECORD_10:
        case RECORD_11:
        case RECORD_12:
            if (!locale.contains("itemName." + item.getType())) {
                break;
            }
            return locale.getString("itemExtra.record", formatEnumString(item
                    .getType().toString()));
        default:
            break;
        }
        return getItemNameNormal(item);
    }
    
    /**
     * Get a display String representing an ItemStack's name based upon its
     * Material and durability values.
     * 
     * @param item
     * @return
     */
    private static String getItemNameNormal(ItemStack item) {
        /* Defined with specific damage value */
        if (locale.contains("itemName." + item.getType().toString() + ":"
                + item.getDurability())) {
            return locale.getString("itemName." + item.getType().toString()
                    + ":" + item.getDurability());
        }
        /* Defined generically */
        else if (locale.contains("itemName." + item.getType())) {
            return locale.getString("itemName." + item.getType());
        }
        /* Undefined */
        else {
            return formatEnumString(item.getType().toString());
        }
    }
    
    /**
     * @return Current locale name for display
     */
    protected static String getLocaleName() {
        return locale.getString("language.name", "Unknown") + " ("
                + locale.getString("language.code", "N/A") + ")";
    }
    
    /**
     * Get a display String representing a PotionEffect, including its duration.
     * 
     * @param effect
     * @return
     */
    protected static String getPotionEffect(PotionEffect effect) {
        String level;
        if (locale.contains("potionLevel." + effect.getAmplifier())) {
            level = " "
                    + locale.getString("potionLevel." + effect.getAmplifier());
        } else if (effect.getAmplifier() != 0) {
            level = " " + effect.getAmplifier();
        } else {
            level = "";
        }
        return locale.getString("potionEffect." + effect.getType().getName(),
                formatEnumString(effect.getType().getName()))
                + level
                + (effect.getDuration() > 19 ? " "
                        + Localization.formatTime(effect.getDuration()) : "");
    }
    
    /**
     * Get a display String representing a Potion, using its effects and custom
     * effects. Utilized privately by getItemName() in the case that the item is
     * a potion.
     * 
     * @param item
     *            Potion item
     * @return
     */
    private static String getPotionName(ItemStack item) {
        try {
            Potion potion = Potion.fromItemStack(item);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            
            if (meta.hasCustomEffects()
                    && !meta.getCustomEffects().isEmpty()
                    && locale.contains("potionName."
                            + meta.getCustomEffects().get(0))) {
                return locale.getString("potionName."
                        + meta.getCustomEffects().get(0));
            } else if (potion.getEffects() != null
                    && !potion.getEffects().isEmpty()
                    && locale.contains("potionName."
                            + potion.getEffects().iterator().next())) {
                return locale.getString("potionName."
                        + potion.getEffects().iterator().next());
            } else {
                return null;
            }
        } catch (IllegalArgumentException e) {
        } catch (ClassCastException e) {
        } catch (NullPointerException e) {
        }
        return null;
    }
    
    /**
     * Attempt to load a selected locale file by name. If the file cannot be
     * read, fall back to default 'en_US' locale. If that file also cannot be
     * read, use a blank locale (thus all display names come from Bukkit).
     * 
     * @param plugin
     *            DisplayFrames plugin instance
     * @param localeName
     *            Name of desired locale
     */
    protected static void load(DisplayFrames plugin, String localeName) {
        locale = new YamlConfiguration();
        File localeFile = new File(plugin.getDataFolder().getAbsolutePath()
                + LOCALE_FOLDER + localeName + LOCALE_EXTENSION);
        if (!localeFile.exists()) {
            try {
                plugin.saveResource("locale/" + localeName + LOCALE_EXTENSION,
                        false);
            } catch (IllegalArgumentException e) {
                plugin.fancyLog(Level.WARNING, "Locale '" + localeName
                        + "' is not included with this plugin.");
            }
        }
        try {
            locale.load(localeFile);
            plugin.fancyLog("Loaded strings from: " + localeName);
            return;
        } catch (FileNotFoundException e) {
            plugin.fancyLog(Level.WARNING, "Locale file not found for: "
                    + localeName);
        } catch (IOException e) {
            plugin.fancyLog(Level.WARNING, "Locale file readable for: "
                    + localeName);
        } catch (InvalidConfigurationException e) {
            plugin.fancyLog(Level.WARNING, "Locale file has syntax errors: "
                    + localeName);
            e.printStackTrace();
        }
        plugin.fancyLog(Level.WARNING, "Falling back to Bukkit names");
    }
    
    /**
     * Generate a new locale file template using all Material, Enchantment, and
     * PotionEffectType enum values and their respective display names as
     * returned by {@link #formatEnumString(String) formatEnumString()}.
     * 
     * @param plugin
     *            DisplayFrames instance
     * @return Whether a new template was saved
     */
    protected static boolean makeNewTemplate(DisplayFrames plugin) {
        YamlConfiguration newTemplate = new YamlConfiguration();
        newTemplate.set("_comment_", new String[] {
                "To configure a display name for an item with a specific data",
                "value, add a new node in the format",
                "  'ITEM_NAME:#': 'Display Name' (e.g. 'WOOL:0': 'White Wool')",
                "If an item with a data value is not explicitly defined, it",
        "will use the general display name from the Material." });
        for (Material material : Material.values()) {
            newTemplate.set("item." + material.name(),
                    formatEnumString(material.name()));
        }
        for (Enchantment enchantment : Enchantment.values()) {
            newTemplate.set("enchantment." + enchantment.getName(),
                    formatEnumString(enchantment.getName()));
        }
        for (PotionEffectType potionEffect : PotionEffectType.values()) {
            if (potionEffect != null) {
                newTemplate.set("potionEffect." + potionEffect.getName(),
                        formatEnumString(potionEffect.getName()));
            }
        }
        File localeFile = new File(plugin.getDataFolder().getAbsolutePath()
                + LOCALE_FOLDER + "new" + LOCALE_EXTENSION);
        try {
            newTemplate.save(localeFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
}
