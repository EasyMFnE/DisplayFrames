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

import org.bukkit.permissions.Permissible;

/**
 * This method provides a static way to check user permissions. Methods are
 * self-explanatory.
 * 
 * @author Eric Hildebrand
 */
public class Perms {
    
    /**
     * Whether or not a user is an Admin of the plugin, as denoted by the
     * permission 'displayframes.admin'
     * 
     * @param p
     * @return
     */
    public static boolean isAdmin(Permissible p) {
        return p.hasPermission("displayframes.admin");
    }
    
    /**
     * Whether or not a user can use the plugin's features, as denoted by the
     * permission 'displayframes.use'
     * 
     * @param p
     * @return
     */
    public static boolean isUser(Permissible p) {
        return p.hasPermission("displayframes.use");
    }
    
}
