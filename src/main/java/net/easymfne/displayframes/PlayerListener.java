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

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The class that monitors and reacts to server events.
 * 
 * @author Eric Hildebrand
 */
public class PlayerListener implements Listener {
    
    /** Distance limit for range of experimental middle-click cloning */
    private final int CLICK_RANGE = 10;
    
    private DisplayFrames plugin = null;
    
    /**
     * Instantiate by getting a reference to the plugin instance and registering
     * each of the defined EventHandlers.
     * 
     * @param plugin
     *            Reference to DisplayFrames plugin instance
     */
    public PlayerListener(DisplayFrames plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Unregister all registered EventHandlers, preventing further reactions.
     */
    public void close() {
        HandlerList.unregisterAll(this);
    }
    
    /**
     * Check to see if two Locations are within the same Block
     * 
     * @param location1
     * @param location2
     * @return
     */
    private boolean inSameBlock(Location location1, Location location2) {
        return location1.getWorld() == location2.getWorld()
                && location1.getBlockX() == location2.getBlockX()
                && location1.getBlockY() == location2.getBlockY()
                && location1.getBlockZ() == location2.getBlockZ();
    }
    
    /**
     * Detect right-clicks on ItemFrames and display their contents' information
     * unless the player is sneaking or the ItemFrame is empty.
     * 
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void onFrameInteract(PlayerInteractEntityEvent event) {
        if (!Perms.isUser(event.getPlayer())
                || !(event.getRightClicked() instanceof ItemFrame)) {
            return;
        }
        ItemFrame frame = (ItemFrame) event.getRightClicked();
        if (!event.getPlayer().isSneaking() && frame.getItem() != null
                && frame.getItem().getType() != Material.AIR) {
            event.setCancelled(true);
            new DisplayableItem(plugin, frame.getItem()).displayTo(event
                    .getPlayer());
        }
    }
    
    /**
     * Experimental: Detect creative-mode middle-clicks on nonempty ItemFrames
     * and copy the contents into the player's hotbar. Requires the player to
     * not have a similar item already in their hotbar to fire this event.
     * 
     * NOTE: Uses deprecated LivingEntity.getLastTwoTargetBlocks() method, but
     * will fail safely if the method is not found.
     * 
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void onFrameMiddleclick(InventoryClickEvent event) {
        if (!plugin.getConfigHelper().isCloningEnabled()) {
            return;
        }
        try {
            if (event.getWhoClicked() instanceof Player
                    && event.getClick() == ClickType.CREATIVE
                    && event.getAction() == InventoryAction.PLACE_ALL) {
                Player player = (Player) event.getWhoClicked();
                List<Block> blocks = player.getLastTwoTargetBlocks(null,
                        CLICK_RANGE);
                if (blocks != null && blocks.size() == 2) {
                    BlockFace face = blocks.get(0).getFace(blocks.get(1));
                    for (Entity entity : blocks.get(0).getLocation().getChunk()
                            .getEntities()) {
                        if (entity instanceof ItemFrame
                                && inSameBlock(entity.getLocation(), blocks
                                        .get(0).getLocation())
                                && ((ItemFrame) entity).getAttachedFace() == face) {
                            ItemStack item = ((ItemFrame) entity).getItem();
                            if (item == null
                                    || item.getType().equals(Material.AIR)) {
                                return;
                            }
                            for (int i = 0; i < 9; i++) {
                                if (player.getInventory().getItem(i) == null
                                        || player.getInventory().getItem(i)
                                                .getType().equals(Material.AIR)) {
                                    player.getInventory().setItem(i,
                                            item.clone());
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                            player.setItemInHand(item.clone());
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        } catch (NoSuchMethodError e) {
            plugin.fancyLog(
                    Level.WARNING,
                    "The server version running does not"
                            + "contain the required method for ItemFrame item cloning");
        }
    }
    
}
