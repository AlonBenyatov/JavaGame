package items;

import java.io.Serializable;

import charcters.Player;
import items.ItemRarity;

public abstract class Item implements Serializable{

    protected String name;
    protected String description;
    protected String type;
    protected int value;
    protected ItemRarity rarity;

    public Item(String name, String description, String type, int value, ItemRarity rarity) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
        this.rarity = rarity;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public abstract void use(Player player);

    /**
     * Provides a detailed string representation of the item's stats and properties.
     * This method is now concrete and provides the common details for all items.
     * Subclasses will override this method and typically call super.getDetailedStats()
     * to include these common details before adding their specific properties.
     * @return A formatted string with the item's details.
     */
    public String getDetailedStats() {
        return "Name: " + name + "\n" +
               "Description: " + description + "\n" +
               "Type: " + type + "\n" +
               "Value: " + value + " Gold\n" +
               "Rarity: " + rarity.name().replace("_", " "); // Nicer display for rarity
    }

    /**
     * Overrides the default toString() method to provide a user-friendly display
     * for items in lists, showing the name, gold value, and rarity.
     * This is what will be seen directly in the JList in the shop.
     * For example: "Wooden Shortsword (5 Gold, Common)"
     * @return A string representation of the item, including its name, value, and rarity.
     */
    @Override
    public String toString() {
        return name + " (" + value + " Gold, " + rarity.name().replace("_", " ") + ")";
    }
}