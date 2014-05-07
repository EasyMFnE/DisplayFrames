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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * The class that handles the "/displayframes" command for the plugin.
 * 
 * @author Eric Hildebrand
 */
public class DisplayFramesCommand implements CommandExecutor {
    
    private DisplayFrames plugin = null;
    
    /**
     * Instantiate by getting a reference to the plugin instance and registering
     * this class to handle the '/displayframes' command.
     * 
     * @param plugin
     *            Reference to DisplayFrames plugin instance
     */
    public DisplayFramesCommand(DisplayFrames plugin) {
        this.plugin = plugin;
        plugin.getCommand("displayframes").setExecutor(this);
    }
    
    /**
     * Release the '/displayframes' command from its ties to this class.
     */
    public void close() {
        plugin.getCommand("displayframes").setExecutor(null);
    }
    
    /**
     * This method handles user commands to reload the plugin configuration and
     * save new localization file templates.
     * 
     * Usage: "/displayframes <reload,template>"
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        /* "/displayframes reload" */
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reload();
            sender.sendMessage("Configuration reloaded from disk.");
            return true;
        }
        
        /* "/displayframes template" */
        if (args.length == 1 && args[0].equalsIgnoreCase("template")) {
            if (Localization.makeNewTemplate(plugin)) {
                sender.sendMessage("Localization template saved (new.lang).");
            } else {
                sender.sendMessage("Localization template could not be saved.");
            }
            return true;
        }
        
        return false;
    }
    
}
