package items;

import java.io.Serializable;

import charcters.Player;
import items.EquipmentSlot;
import items.ItemRarity; // Import the ItemRarity enum

public class Weapon extends EquipableItem implements Serializable {

    private int damage;
    private double attackSpeedModifier;

    /**
     * Constructor for a Weapon.
     * Weapons are equipped in the WEAPON slot.
     * @param name The display name of the weapon.
     * @param description A short description of the weapon.
     * @param type The specific type of weapon (e.g., "Sword", "Axe", "Bow").
     * @param value The gold value.
     * @param rarity The rarity level of the weapon. // NEW
     * @param damage The base damage the weapon deals.
     * @param attackSpeedModifier A multiplier for the player's attack speed (1.0 is no change).
     * @param levelRequirement The minimum level required to equip.
     */
    public Weapon(String name, String description, String type, int value, ItemRarity rarity, int damage, double attackSpeedModifier, int levelRequirement) {
        super(name, description, type, value, rarity, EquipmentSlot.WEAPON, levelRequirement); 
        this.damage = damage;
        this.attackSpeedModifier = attackSpeedModifier;
    }

    public int getDamage() {
        return damage;
    }

    public double getAttackSpeedModifier() {
        return attackSpeedModifier;
    }

    @Override
    public void equip(Player player) {
        super.equip(player);
        System.out.println(getName() + " equipped. Damage +" + damage + ", Attack Speed Modifier: " + attackSpeedModifier);
        player.recalculateDerivedStats();
    }

    @Override
    public void unequip(Player player) {
        super.unequip(player);
        System.out.println(getName() + " unequipped. Damage -" + damage + ", Attack Speed Modifier removed.");
        player.recalculateDerivedStats();
    }

    @Override
    public String getDetailedStats() {
        return super.getDetailedStats() + "\n" +
               "Damage: " + damage + "\n" +
               "Attack Speed Modifier: " + String.format("%.2f", attackSpeedModifier);
    }
}