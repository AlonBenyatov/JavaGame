package items;

import charcters.Player;
import items.ItemRarity;

public class CraftItem extends Item {

    protected String materialType;
    protected int craftingValue;

    public CraftItem(String name, String description, int value, ItemRarity rarity, String materialType, int craftingValue) {
        super(name, description, "Crafting Material", value, rarity);
        this.materialType = materialType;
        this.craftingValue = craftingValue;
    }

    public String getMaterialType() {
        return materialType;
    }

    public int getCraftingValue() {
        return craftingValue;
    }

    @Override
    public void use(Player player) {
        // Updated to reflect that this item has no direct 'use' effect,
        // aligning with your request for no consumables/no purpose for 'use'.
        System.out.println(player.getName() + " inspects " + getName() + ". This crafting material has no direct 'use' effect.");
    }

    @Override
    public String getDetailedStats() {
        return super.getDetailedStats() + "\n" +
               "Material Type: " + materialType + "\n" +
               "Crafting Value: " + craftingValue;
    }
}