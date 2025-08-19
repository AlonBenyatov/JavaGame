package items.equipments;

import java.io.Serializable;
import charcters.Player; // Import Player to interact with player stats
import items.Armor;
import items.ItemRarity;
import items.Weapon;
import items.EquipmentSlot;

//In your WoddenShield class:
public class WoddenShield extends Armor implements Serializable {

 public WoddenShield() {
     super(
         "Wooden Shield",
         "A simple Shield made of Wood. Very basic .",
         "Shield",
         500,
         ItemRarity.COMMON,
         4,                
         EquipmentSlot.OFF_HAND, 
         5                   
     );
 }
}

/**
 * Constructor for an Armor piece.
 * @param name The display name of the armor.
 * @param description A short description of the armor.
 * @param type The specific type of armor (e.g., "Helmet", "Chestplate", "Boots").
 * @param value The gold value.
 * @param rarity The rarity level of the armor. // NEW
 * @param defense The amount of defense this armor provides.
 * @param equipSlot The specific slot where this armor can be equipped.
 * @param levelRequirement The minimum level required to equip.
 */


// i will continue later