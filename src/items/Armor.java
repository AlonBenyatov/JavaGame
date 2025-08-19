package items;

import java.io.Serializable;

import charcters.Player;
import items.EquipmentSlot;
import items.ItemRarity; // Import the ItemRarity enum

public class Armor extends EquipableItem implements Serializable {

    private int defense;

    /**
     * Constructor for an Armor piece.
     * @param name The display name of the armor.
     * @param description A short description of the armor.
     * @param type The specific type of armor (e.g., "Helmet", "Chestplate", "Boots").
     * @param value The gold value.
     * @param rarity The rarity level of the armor.
     * @param defense The amount of defense this armor provides.
     * @param slot The specific slot where this armor can be equipped.
     * @param levelRequirement The minimum level required to equip.
     */
    public Armor(String name, String description, String type, int value, ItemRarity rarity, int defense, EquipmentSlot slot, int levelRequirement)
    {
        // Pass the arguments that EquipableItem expects: name, description, type, value, rarity, THEN slot, THEN levelRequirement
        super(name, description, type, value, rarity, slot, levelRequirement);
        this.defense = defense; // Initialize the 'defense' field specific to Armor
    }

    public int getDefense() {
        return defense;
    }

    @Override
    public void equip(Player player) {
        super.equip(player);
        System.out.println(getName() + " equipped. Defense +" + defense + ".");
        player.recalculateDerivedStats();
    }

    @Override
    public void unequip(Player player) {
        super.unequip(player);
        System.out.println(getName() + " unequipped. Defense -" + defense + " removed.");
        player.recalculateDerivedStats();
    }

    @Override
    public String getDetailedStats() {
        return super.getDetailedStats() + "\n" +
                "Defense: " + defense;
    }

    @Override
    public void use(Player player) {
        // As discussed, this override might not be needed if EquipableItem's 'use' is sufficient.
        // If you keep it, you might want to call super.use(player); or simply remove it.
        // super.use(player); // Option: call parent's use method
        System.out.println(player.getName() + " attempts to use " + getName() + ". This armor has no direct 'use' effect.");
    }
}