package items;

import java.io.Serializable;

import charcters.Player;
import items.EquipmentSlot;
import items.ItemRarity;

public abstract class EquipableItem extends Item implements Serializable {

    protected EquipmentSlot equipSlot;
    protected int levelRequirement;

    public EquipableItem(String name, String description, String type, int value, ItemRarity rarity, EquipmentSlot equipSlot, int levelRequirement) {
        super(name, description, type, value, rarity);
        this.equipSlot = equipSlot;
        this.levelRequirement = levelRequirement;
    }

    public EquipmentSlot getEquipSlot() {
        return equipSlot;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public void equip(Player player) {
        System.out.println(player.getName() + " equipped " + getName() + ".");
    }

    public void unequip(Player player) {
        System.out.println(player.getName() + " unequipped " + getName() + ".");
    }

    @Override
    public void use(Player player) {
        // This is the default 'use' behavior for EquipableItems.
        // It now explicitly states that it has no direct 'use' effect,
        // aligning with your request to remove its purpose.
        System.out.println(player.getName() + " attempts to use " + getName() + ". This item has no direct 'use' effect.");
    }

    @Override
    public String getDetailedStats() {
        return super.getDetailedStats() + "\n" +
               "Equip Slot: " + equipSlot.name() + "\n" +
               "Level Requirement: " + levelRequirement;
    }
}