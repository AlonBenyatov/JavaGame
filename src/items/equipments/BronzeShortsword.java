package items.equipments; // Correct package for specific equipable items

import java.io.Serializable;

import charcters.Player; // Import Player to interact with player stats
import items.EquipableItem; // Import the base EquipableItem class
import items.EquipmentSlot; // Import the EquipmentSlot enum
import items.ItemRarity; // Import the ItemRarity enum
import items.Weapon;

public class BronzeShortsword extends Weapon implements Serializable { // Changed to extend Weapon directly

    // The BASE_DAMAGE should ideally be defined in the Weapon class,
    // or passed to the Weapon constructor. For this example, I'll pass it.
    // private static final int BASE_DAMAGE = 5; // No longer needed here if passed to super

    /**
     * Constructs a new WoodenShortsword.
     * Initializes it with predefined stats appropriate for a basic starting weapon.
     */
    public BronzeShortsword() {
        // Corrected super() call to match Weapon constructor signature:
        // public Weapon(String name, String description, String type, int value, ItemRarity rarity, int damage, double attackSpeedModifier, int levelRequirement)
        super(
            "bronze Shortsword",
            "A simple shortsword made of bronze. Better than punching that's for sure.",
            "Shortsword", // Specific type, e.g., "Shortsword", "Axe", "Bow"
            250,           // Gold value
            ItemRarity.COMMON, // Rarity: This is the new ItemRarity enum value
            3,           // Base Damage: BASE_DAMAGE for this weapon
            1.0,         // Attack Speed Modifier (1.0 means no change)
            3            // Level requirement
        );
    }
}     


// i will continue later