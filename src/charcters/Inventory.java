package charcters;

import items.Item;
import items.EquipableItem;
import items.EquipmentSlot;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The `Inventory` class manages a player's items, equipped gear, and gold.
 * It handles the state changes related to adding, removing, equipping, and unequipping items.
 * The actual effects of items (e.g., stat changes, healing) are managed by the `Player` class.
 */
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Item> items; // All items the player owns
    private Map<EquipmentSlot, EquipableItem> equippedItems; // Items currently equipped
    private int gold;

    /**
     * Constructs a new Inventory, initializing empty lists for items and equipped gear, and setting gold to zero.
     */
    public Inventory() {
        this.items = new ArrayList<>();
        this.equippedItems = new HashMap<>();
        this.gold = 0;
    }

    // --- Gold Management ---
    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        if (amount > 0) {
            this.gold += amount;
            System.out.println("DEBUG: Added " + amount + " gold. Total: " + this.gold);
        }
    }

    public boolean deductGold(int amount) {
        if (amount > 0 && this.gold >= amount) {
            this.gold -= amount;
            System.out.println("DEBUG: Deducted " + amount + " gold. Total: " + this.gold);
            return true;
        }
        System.out.println("DEBUG: Not enough gold to deduct " + amount + ". Current: " + this.gold);
        return false;
    }

    // --- General Item Management (adding/removing from main inventory list) ---
    public void addItem(Item item) {
        if (item != null) {
            this.items.add(item);
            System.out.println("DEBUG: Added " + item.getName() + " to inventory.");
        }
    }

    public boolean removeItem(Item item) {
        if (item != null && this.items.remove(item)) {
            System.out.println("DEBUG: Removed " + item.getName() + " from inventory.");
            return true;
        }
        System.out.println("DEBUG: Failed to remove " + (item != null ? item.getName() : "null item") + " from inventory (not found).");
        return false;
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items); // Return a copy to prevent external modification
    }

    // --- Equipped Items Management (getting equipped items) ---
    public Map<EquipmentSlot, EquipableItem> getEquippedItems() {
        return new HashMap<>(equippedItems); // Return a copy
    }

    public EquipableItem getEquippedItemInSlot(EquipmentSlot slot) {
        return equippedItems.get(slot);
    }

    /**
     * Handles the internal inventory logic for equipping an item.
     * If an item is already in the slot, it's moved back to the general inventory.
     * The item to be equipped is removed from the general inventory and placed in the equipped slot.
     * Stat application is handled by the Player class.
     * @param item The EquipableItem to equip.
     * @return The item that was unequipped (if any), or null if no item was unequipped.
     */
    public EquipableItem equipItem(EquipableItem item) {
        if (item == null) {
            return null;
        }

        EquipableItem oldItem = null;
        // Check if there's an item already in the slot
        if (equippedItems.containsKey(item.getEquipSlot())) {
            oldItem = equippedItems.get(item.getEquipSlot());
            addItem(oldItem); // Move old item back to inventory
            System.out.println("DEBUG: Unequipped " + oldItem.getName() + " from " + item.getEquipSlot().name() + " slot and added back to inventory.");
        }

        equippedItems.put(item.getEquipSlot(), item); // Equip the new item
        removeItem(item); // Remove from general inventory list
        System.out.println("DEBUG: Equipped " + item.getName() + " in " + item.getEquipSlot().name() + " slot.");
        return oldItem;
    }

    /**
     * Handles the internal inventory logic for unequipping an item from a specific slot.
     * The item is moved from the equipped slot back to the general inventory.
     * Stat removal is handled by the Player class.
     * @param slot The EquipmentSlot from which to unequip.
     * @return The item that was unequipped, or null if no item was in that slot.
     */
    public EquipableItem unequipItem(EquipmentSlot slot) {
        EquipableItem unequippedItem = equippedItems.remove(slot);
        if (unequippedItem != null) {
            addItem(unequippedItem); // Add back to general inventory list
            System.out.println("DEBUG: Unequipped " + unequippedItem.getName() + " from " + slot.name() + " slot and added back to inventory.");
        } else {
            System.out.println("DEBUG: No item found to unequip in " + slot.name() + " slot.");
        }
        return unequippedItem;
    }
}