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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

/**
 * Extension of ItemStack with helper methods for detecting the presence, and
 * displaying, of item information results.
 * 
 * @author Eric Hildebrand
 */
public class DisplayableItem extends ItemStack {
    
    private DisplayFrames plugin;
    
    /**
     * @param plugin
     *            Reference to DisplayFrames plugin instance
     * @param item
     *            ItemStack represented
     */
    public DisplayableItem(DisplayFrames plugin, ItemStack item) {
        super(item);
        this.plugin = plugin;
    }
    
    /**
     * Display an individual line of item information to a user, by type
     * 
     * @param sender
     * @param info
     */
    private void displayInfoTo(CommandSender sender, ItemInfo info) {
        ConfigHelper config = plugin.getConfigHelper();
        String infoString = config.getInfoString(info);
        String c1 = config.getPrimaryColor();
        String c2 = config.getSecondaryColor();
        String c3 = config.getTertiaryColor();
        String glue = c2 + ", " + c3;
        
        switch (info) {
        case AUTHOR:
            if (hasAuthor()) {
                sender.sendMessage(c2
                        + String.format(infoString, c3
                                + ((BookMeta) getItemMeta()).getAuthor()));
            }
            break;
        case DURABILITY:
            if (hasDurability()) {
                sender.sendMessage(c2
                        + String.format(
                                infoString,
                                c3
                                        + (getType().getMaxDurability() - getDurability())
                                        + c2,
                                c3 + (getType().getMaxDurability())) + c2);
            }
            break;
        case ENCHANTMENT:
            if (hasEnchantment()) {
                List<String> enchants = new ArrayList<String>();
                for (Entry<Enchantment, Integer> enchant : getEnchants()) {
                    enchants.add(Localization.getEnchantment(enchant.getKey(),
                            enchant.getValue()));
                }
                sender.sendMessage(c2
                        + String.format(infoString,
                                c3 + StringUtils.join(enchants, glue) + c2));
            }
            break;
        case FOOTER:
        case HEADER:
            sender.sendMessage(c1 + infoString);
            break;
        case LORE:
            if (hasLore()) {
                sender.sendMessage(c2
                        + String.format(
                                infoString,
                                c3
                                        + StringUtils.join(getItemMeta()
                                                .getLore(), glue)));
            }
            break;
        case NAME:
            if (hasItemMeta() && getItemMeta().hasDisplayName()) {
                sender.sendMessage(c1
                        + String.format(infoString, getItemMeta()
                                .getDisplayName() + c1));
            } else {
                sender.sendMessage(c1
                        + String.format(infoString,
                                Localization.getItemName(this) + c1));
            }
            break;
        case POTION_EFFECT:
            if (hasPotionEffect()) {
                List<String> effects = new ArrayList<String>();
                for (PotionEffect effect : getPotionEffects()) {
                    effects.add(Localization.getPotionEffect(effect));
                }
                sender.sendMessage(c2
                        + String.format(infoString,
                                c3 + StringUtils.join(effects, glue) + c2));
            }
            break;
        default:
            break;
        }
    }
    
    /**
     * Display all enabled information about the item to the given user
     * 
     * @param sender
     */
    public void displayTo(CommandSender sender) {
        for (ItemInfo info : ItemInfo.values()) {
            if (plugin.getConfigHelper().isEnabled(info)) {
                displayInfoTo(sender, info);
            }
        }
    }
    
    /**
     * @return List of the item's Enchantments and Enchantment levels
     */
    private List<Entry<Enchantment, Integer>> getEnchants() {
        List<Entry<Enchantment, Integer>> enchants = new ArrayList<Entry<Enchantment, Integer>>();
        if (getEnchantments() != null && !getEnchantments().isEmpty()) {
            for (Entry<Enchantment, Integer> enchant : getEnchantments()
                    .entrySet()) {
                enchants.add(enchant);
            }
        }
        if (hasItemMeta()
                && getItemMeta() instanceof EnchantmentStorageMeta
                && !((EnchantmentStorageMeta) getItemMeta()).getEnchants()
                        .isEmpty()) {
            for (Entry<Enchantment, Integer> enchant : ((EnchantmentStorageMeta) getItemMeta())
                    .getEnchants().entrySet()) {
                enchants.add(enchant);
            }
        }
        return enchants;
    }
    
    /**
     * @return List of the item's PotionEffects
     */
    private List<PotionEffect> getPotionEffects() {
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        if (getType() == Material.POTION) {
            try {
                Potion potion = Potion.fromItemStack(this);
                for (PotionEffect effect : potion.getEffects()) {
                    effects.add(effect);
                }
            } catch (IllegalArgumentException e) {
            }
        }
        if (hasItemMeta() && getItemMeta() instanceof PotionMeta) {
            for (PotionEffect effect : ((PotionMeta) getItemMeta())
                    .getCustomEffects()) {
                effects.add(effect);
            }
        }
        return effects;
    }
    
    /**
     * @return Whether the item has available Author information
     */
    private boolean hasAuthor() {
        return hasItemMeta() && getItemMeta() instanceof BookMeta
                && ((BookMeta) getItemMeta()).hasAuthor();
    }
    
    /**
     * @return Whether the item has available Durability information
     */
    private boolean hasDurability() {
        return getType().getMaxDurability() > 0;
    }
    
    /**
     * @return Whether the item has available Enchantment information
     */
    private boolean hasEnchantment() {
        return (getEnchantments() != null && !getEnchantments().isEmpty())
                || (hasItemMeta()
                        && getItemMeta() instanceof EnchantmentStorageMeta && ((EnchantmentStorageMeta) getItemMeta())
                            .hasEnchants());
    }
    
    /**
     * @return Whether the item has available Lore information
     */
    private boolean hasLore() {
        return hasItemMeta() && getItemMeta().getLore() != null
                && !getItemMeta().getLore().isEmpty();
    }
    
    /**
     * @return Whether the item has available PotionEffect information
     */
    private boolean hasPotionEffect() {
        try {
            if (getType() == Material.POTION
                    && !Potion.fromItemStack(this).getEffects().isEmpty()) {
                return true;
            }
        } catch (IllegalArgumentException e) {
        }
        if (hasItemMeta() && getItemMeta() instanceof PotionMeta
                && ((PotionMeta) getItemMeta()).hasCustomEffects()) {
            return true;
        }
        return false;
    }
}
