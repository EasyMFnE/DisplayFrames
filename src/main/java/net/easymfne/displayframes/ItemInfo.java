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

/**
 * Simple enum representing the type of information about an item the plugin can
 * parse and process.
 * 
 * @author Eric Hildebrand
 */
public enum ItemInfo {
    HEADER, NAME, AUTHOR, DURABILITY, ENCHANTMENT, POTION_EFFECT, LORE, FOOTER;
    
    /**
     * @return Lowercase name for configuration use
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
}
