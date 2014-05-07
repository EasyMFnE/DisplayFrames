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

import org.bukkit.ChatColor;

/**
 * Configuration helper class, with methods for accessing the configuration.
 * 
 * @author Eric Hildebrand
 */
public class ConfigHelper {
    
    private DisplayFrames plugin = null;
    
    /**
     * Instantiate the class and give it a reference back to the plugin itself.
     * 
     * @param plugin
     *            The DisplayFrames plugin
     */
    public ConfigHelper(DisplayFrames plugin) {
        this.plugin = plugin;
    }
    
    /**
     * @param info
     *            Item information type
     * @return Display String template for the information
     */
    public String getInfoString(ItemInfo info) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                .getString("string." + info));
    }
    
    /**
     * @return Configured locale file name. Defaults to 'en_US' if undefined.
     */
    public String getLocaleName() {
        return plugin.getConfig().getString("locale", "en_US");
    }
    
    /**
     * @return Primary color, used in display header, footer, and title
     */
    public String getPrimaryColor() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                .getString("color.primary", "&6"));
    }
    
    /**
     * @return Secondary color, used in information category and glue
     */
    public String getSecondaryColor() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                .getString("color.secondary", "&e"));
    }
    
    /**
     * @return Tertiary color, used in information results
     */
    public String getTertiaryColor() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                .getString("color.tertiary", "&f"));
    }
    
    /**
     * @return Whether experimental Cloning feature is enabled
     */
    public boolean isCloningEnabled() {
        return plugin.getConfig().getBoolean("cloning.enabled", false);
    }
    
    /**
     * @param info
     *            Item information type
     * @return Whether the information type is enabled
     */
    public boolean isEnabled(ItemInfo info) {
        return plugin.getConfig().getBoolean("enabled." + info, false);
    }
    
}
