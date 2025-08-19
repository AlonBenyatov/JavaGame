package items;

import java.io.Serializable;

import charcters.Player;
import items.ItemRarity; // Import the ItemRarity enum

public abstract class QuestItem extends Item implements Serializable {

    protected String questId;
    protected boolean isKeyItem;

    /**
     * Constructor for QuestItem.
     * @param name The display name of the item.
     * @param description A short description of the item.
     * @param value The gold value (often 0 for quest items).
     * @param rarity The rarity level of the quest item. // NEW
     * @param questId The ID of the quest associated with this item.
     * @param isKeyItem True if this item is crucial and cannot be removed from inventory.
     */
    public QuestItem(String name, String description, int value, ItemRarity rarity, String questId, boolean isKeyItem) {
        super(name, description, "Quest", value, rarity); // Pass rarity to Item's constructor
        this.questId = questId;
        this.isKeyItem = isKeyItem;
    }

    public String getQuestId() {
        return questId;
    }

    public boolean isKeyItem() {
        return isKeyItem;
    }

    @Override
    public abstract void use(Player player);

    @Override
    public String getDetailedStats() {
        return super.getDetailedStats() + "\n" + // Calls Item's getDetailedStats() which now includes rarity
               "Related Quest ID: " + questId + "\n" +
               "Key Item: " + (isKeyItem ? "Yes" : "No");
    }
}