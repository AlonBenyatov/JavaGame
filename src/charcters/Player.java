package charcters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import items.Item;
import items.EquipableItem;
import items.EquipmentSlot; // Import the EquipmentSlot enum
import items.Weapon; // Added for weapon damage calculation
import items.Armor; // Added for armor calculation

/**
 * The `Player` class represents the main character controlled by the user.
 * It manages the player's core statistics, health, experience, level progression,
 * inventory, and derived combat attributes.
 * Implements `Serializable` to allow saving and loading of player data.
 * Implements `CharctersGeneralMethods` to conform to the common combatant interface,
 * enabling seamless interaction with combat systems.
 */
public class Player implements Serializable, CharctersGeneralMethods {
    private static final long serialVersionUID = 1L; // Unique ID for serialization for saving/loading

    // These fields hold the player's current attributes.
    private String name;
    private int level;
    private int currentexperience;
    private int ExpToLvlUp;
    private int strength;
    private int intelligence;
    private int luck;
    private int constitution;
    private int charisma;
    private int currentHp;
    private int maxHp;
    private String currentClass;
    private int dexterity;
    private int gold;
    private int armor;
    private double dodge;
    private double attackSpeed;
    private double parry;
    private int attackDmg;
    private double critChance;
    private double critDmg;
    private int unallocatedStatPoints;
    private List<Item> inventory;
    private Map<EquipmentSlot, EquipableItem> equippedItems = new HashMap<>();

    /**
     * Constructs a new `Player` instance with a given name.
     * Initializes the player with base stats (Strength, Dexterity, etc.),
     * starts them at level 1 with default health, and an empty inventory.
     * Derived combat statistics (like dodge, attack speed, crit chance, and attack damage)
     * are calculated immediately upon construction using the `CombatCalculator`.
     *
     * @param name The name chosen for the player character.
     */
    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.currentexperience = 0;
        this.strength = 5;
        this.dexterity = 5;
        this.intelligence = 5;
        this.luck = 5;
        this.constitution = 5;
        this.charisma = 5;
        this.maxHp = calculateMaxHP(); // Calculate max HP based on initial stats
        this.currentHp = this.maxHp;   // Start with full HP
        this.currentClass = "Adventurer"; // Default starting class
        this.gold = 0;
        this.armor = 0;

        // Initialize derived combat stats using CombatCalculator methods
        recalculateDerivedStats(); // This sets dodge, attackSpeed, parry, critChance, critDmg, and attackDmg

        calculateExpToLevelUp();       // Set initial experience requirement for level 2
        this.unallocatedStatPoints = 0; // No points at start, gained on level up

        this.inventory = new ArrayList<>(); // Initialize empty inventory
        this.equippedItems = new HashMap<>(); // Initialize empty equipped items map
    }

    /**
     * Calculates the player's maximum hit points based on their **Constitution** and **Level**.
     * This is a private helper method used internally whenever these stats might change.
     *
     * @return The calculated maximum HP.
     */
    private int calculateMaxHP() {
        return 50 + (int) (this.constitution * 5 + this.level * 10);
    }

    /**
     * Calculates the experience points required for the player to reach the next level.
     * This is a private helper method used internally for progression tracking.
     */
    private void calculateExpToLevelUp() {
        this.ExpToLvlUp = (int) (14.00 * Math.pow(this.level, 2) + 600.00 * this.level - 415.00);
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the name of the character.
     * @return The character's name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's current level.
     * @return The character's level.
     */
    @Override
    public int getLevel() {
        return level;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's current health points.
     * @return The current HP.
     */
    @Override
    public int getCurrentHP() {
        return currentHp;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's maximum health points.
     * @return The maximum HP.
     */
    @Override
    public int getMaxHP() {
        return maxHp;
    }

    /**
     * Applies damage to the player, reducing their current health.
     * If health drops to 0 or below, the player's current HP is capped at 0,
     * and a "death" message is printed to the console.
     *
     * @param damage The non-negative amount of damage to be taken.
     */
    @Override
    public void takeDamage(int damage) {
        this.currentHp -= damage;
        if (this.currentHp < 0) {
            this.currentHp = 0;
            System.out.println(this.name + " is dead."); // Death message
        }
        System.out.println(this.name + " took " + damage + " damage. Current HP: " + this.currentHp);
    }

    /**
     * Restores a specified amount of health to the player, up to their maximum HP.
     * Healing only applies if the player is currently alive.
     *
     * @param amount The non-negative amount of HP to heal.
     */
    public void healthRegen(int amount) {
        if (currentHp > 0) { // Can only heal if not dead
            this.currentHp += amount;
            if (this.currentHp > this.maxHp) {
                this.currentHp = this.maxHp; // Cap HP at max
            }
            System.out.println(this.name + " healed for " + amount + " HP. Current HP: " + this.currentHp);
        }
    }

    /**
     * Returns a formatted string indicating the player's current and maximum health (e.g., "50/100").
     * Useful for displaying health bars in the user interface.
     *
     * @return A {@code String} representation of the player's health.
     */
    public String showhealth() {
        return (this.getCurrentHP() + ("/") + this.getMaxHP());
    }

    /**
     * Checks if the player is currently alive (i.e., their current HP is greater than 0).
     *
     * @return {@code true} if the player is alive; {@code false} otherwise.
     */
    @Override
    public boolean isAlive() {
        return this.currentHp > 0;
    }

    /**
     * Awards experience points to the player. If enough experience is gained,
     * the player will automatically level up. This method handles multiple level-ups
     * if a large amount of experience is gained at once.
     *
     * @param amount The non-negative amount of experience points to gain.
     */
    public void gainExperience(int amount) {
        this.currentexperience += amount;
        System.out.println(this.name + " gained " + amount + " experience. Total: " + this.currentexperience);
        while (this.currentexperience >= this.ExpToLvlUp) {
            levelUp(); // Level up if enough experience
        }
    }

    /**
     * Advances the player to the next level. This involves:
     * <ul>
     * <li>Incrementing the player's level.</li>
     * <li>Deducting the experience required for the past level from current experience.</li>
     * <li>Calculating the new experience requirement for the next level.</li>
     * <li>Awarding 10 unallocated stat points for the player to distribute.</li>
     * <li>Recalculating maximum HP (due to level/constitution increase).</li>
     * <li>Restoring current HP to full.</li>
     * <li>Recalculating all derived combat statistics to reflect the new level.</li>
     * </ul>
     */
    public void levelUp() {
        this.level++;
        this.currentexperience -= this.ExpToLvlUp;
        calculateExpToLevelUp();

        this.unallocatedStatPoints += 10;

        this.maxHp = calculateMaxHP();           // Recalculate max HP due to level/constitution increase
        this.currentHp = this.maxHp;             // Restore HP to full

        // Recalculate derived stats after base stats (like level) change
        recalculateDerivedStats();
        System.out.println("\n" + this.name + " leveled up! Now level " + this.level);
        System.out.println("Player has " + this.unallocatedStatPoints + " stat points to allocate.");
    }

    /**
     * Adds a specified `amount` of points to a chosen base statistic.
     * After adding points, if Constitution is increased, `maxHp` is recalculated.
     * All derived combat stats are re-calculated to reflect the new base stats.
     *
     * @param statChoice An integer representing the stat to increase:
     * 1 = Strength, 2 = Dexterity, 3 = Intelligence,
     * 4 = Luck, 5 = Constitution, 6 = Charisma.
     * @param amount     The positive number of points to add to the stat.
     * If `amount` is not positive, no action is taken.
     */
    public void addStatPoint(int statChoice, int amount) {
        if (amount <= 0) return; // Do nothing if amount is not positive

        switch (statChoice) {
            case 1: strength += amount; break;
            case 2: dexterity += amount; break;
            case 3: intelligence += amount; break;
            case 4: luck += amount; break;
            case 5: constitution += amount; break;
            case 6: charisma += amount; break;
            default: return; // Invalid stat choice
        }
        System.out.println("Player: Added " + amount + " points to stat " + statChoice);

        this.maxHp = calculateMaxHP(); // Max HP may change with constitution increase
        if (this.currentHp > this.maxHp) {
            this.currentHp = this.maxHp; // Ensure current HP doesn't exceed new max
        }
        recalculateDerivedStats(); // Update all stats that depend on base stats
    }

    /**
     * Deducts a specified `amount` from the player's unallocated stat points.
     * The number of unallocated points will not drop below zero.
     *
     * @param amount The non-negative number of points to deduct.
     */
    public void deductUnallocatedStatPoints(int amount) {
        this.unallocatedStatPoints -= amount;
        if (this.unallocatedStatPoints < 0) {
            this.unallocatedStatPoints = 0; // Cap at zero
        }
    }

    /**
     * Recalculates all derived combat statistics of the player based on their
     * current base stats (Strength, Dexterity, Constitution, Luck) and Level.
     * This method should be called whenever a base stat or level changes to ensure
     * combat attributes are up-to-date.
     * **Requires a correctly implemented `CombatCalculator` class.**
     */
    public void recalculateDerivedStats() {
        // All derived stats are now calculated using CombatCalculator it will be our scaler
        this.dodge = CombatCalculator.calculateDodgeChance(this.dexterity);
        this.attackSpeed = CombatCalculator.calculateAttackSpeed(this.dexterity);
        this.parry = CombatCalculator.calculateParryChance(this.constitution); // Parry uses Constitution
        // For now, assuming base damage + strength. If you have an equipped weapon, use its damage.
        int baseWeaponDamage = 8; // Placeholder or actual base damage from character/weapon
        this.attackDmg = CombatCalculator.CalculateAttackDmg(baseWeaponDamage, this.strength);
        
        // Also add attack damage from equipped weapon
        EquipableItem mainHandWeapon = equippedItems.get(EquipmentSlot.WEAPON);
        if (mainHandWeapon != null && mainHandWeapon instanceof Weapon) { // Assuming Weapon is a subclass of EquipableItem
             this.attackDmg += ((Weapon) mainHandWeapon).getDamage(); // Adjust based on your Weapon class
        }

        this.critChance = CombatCalculator.calculateCritChance(this.luck);
        this.critDmg = CombatCalculator.calculateCritDamage(this.dexterity, this.luck);

        // Re-calculate armor based on base armor and equipped armor
        this.armor = 2; // Start with base armor
        for (EquipableItem item : equippedItems.values()) {
            if (item instanceof Armor) { // Assuming Armor is a subclass of EquipableItem
                this.armor += ((Armor) item).getDefense(); // Adjust based on your Armor class
            }
        }
    }

    /**
     * Returns the player's current experience points.
     * @return The current experience.
     */
    public int getExperience() {
        return currentexperience;
    }

    /**
     * Returns the experience points required for the player to reach the next level.
     * @return The experience to next level.
     */
    public int getExpToNextLevel() {
        return ExpToLvlUp;
    }

    /**
     * Returns the number of stat points the player has available to allocate.
     * @return The unallocated stat points.
     */
    public int getUnallocatedStatPoints() {
        return unallocatedStatPoints;
    }

    /**
     * Returns a formatted string containing the player's detailed statistics,
     * suitable for display in a user interface (e.g., in a JLabel using HTML).
     * Includes base stats and dynamically calculated derived combat stats.
     *
     * @return A {@code String} representation of the player's stats, formatted with HTML.
     */
    public String getStatsString() {
        return "<html>"
                + "Level: " + level + "<br>"
                + "exp: " + currentexperience + "/" + ExpToLvlUp + "<br>"
                + "HP: " + currentHp + "/" + maxHp + "<br>"
                + "Strength: " + strength + "<br>"
                + "Dexterity: " + dexterity + "<br>"
                + "Intelligence: " + intelligence + "<br>"
                + "Luck: " + luck + "<br>"
                + "Constitution: " + constitution + "<br>"
                + "Charisma: " + charisma + "<br>"
                + "Gold: " + gold + "<br>"
                + "Armor: " + armor + "<br>"
                + "Dodge: " + String.format("%.2f", dodge) + "<br>" // Format for better display
                + "Attack Speed: " + String.format("%.2f", attackSpeed) + "<br>"
                + "Parry: " + String.format("%.2f", parry) + "<br>"
                + "Crit Chance: " + String.format("%.2f", critChance) + "<br>" // Added crit display
                + "Crit Damage: " + String.format("%.2f", critDmg) + "<br>"    // Added crit display
                + "Attack Damage: " + attackDmg
                + "</html>";
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's Strength statistic.
     * Strength typically influences physical attack power.
     * @return The Strength value.
     */
    @Override
    public int getStrength() {
        return strength;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's Dexterity statistic.
     * Dexterity typically influences accuracy, dodge chance, and attack speed.
     * @return The Dexterity value.
     */
    @Override
    public int getDexterity() {
        return dexterity;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's Intelligence statistic.
     * Intelligence typically influences magical power or special abilities.
     * @return The Intelligence value.
     */
    @Override
    public int getIntelligence() {
        return intelligence;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's Luck statistic.
     * Luck typically influences critical hit chance and other random outcomes.
     * @return The Luck value.
     */
    @Override
    public int getLuck() {
        return luck;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's Constitution statistic.
     * Constitution typically influences maximum health and defensive capabilities like parry chance.
     * @return The Constitution value.
     */
    @Override
    public int getConstitution() {
        return constitution;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's Charisma statistic.
     * Charisma typically influences social interactions, though it might not be directly used in combat.
     * @return The Charisma value.
     */
    @Override
    public int getCharisma() {
        return charisma;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's current armor value.
     * Armor typically reduces incoming physical damage.
     * @return The armor value.
     */
    @Override
    public int getArmor() {
        return armor;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's calculated dodge chance.
     * This is typically a percentage (e.g., 0.05 for 5%).
     * @return The dodge chance as a {@code double}.
     */
    @Override
    public double getDodge() {
        return dodge;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's calculated attack speed.
     * This typically represents attacks per second.
     * @return The attack speed as a {@code double}.
     */
    @Override
    public double getAttackSpeed() {
        return attackSpeed;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's calculated parry chance.
     * This is typically a percentage (e.0.05 for 5%).
     * @return The parry chance as a {@code double}.
     */
    @Override
    public double getParry() {
        return parry;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's calculated base attack damage.
     * This is the raw damage before other combat modifiers (like crit or armor).
     * @return The attack damage value.
     */
    @Override
    public int getAttackDmg() {
        return attackDmg;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's calculated critical hit chance.
     * This is typically a percentage (e.g., 0.10 for 10%).
     * @return The critical hit chance as a {@code double}.
     */
    @Override
    public double getCritChance() {
        return critChance;
    }

    /**
     * Overrides the method from `CharctersGeneralMethods`.
     * Returns the character's calculated critical hit damage multiplier.
     * This is a multiplier (e.g., 2.0 for 200% damage).
     * @return The critical hit damage multiplier as a {@code double}.
     */
    @Override
    public double getCritDmg() {
        return critDmg;
    }

    /**
     * Returns the player's current class name.
     * @return The class name (e.g., "Adventurer").
     */
    public String getCurrentClass() {
        return currentClass;
    }

    /**
     * Adds a specified positive amount of gold to the player's possession.
     *
     * @param amount The amount of gold to add. Must be greater than 0.
     */
    public void addGold(int amount) {
        if (amount > 0) {
            this.gold += amount;
            System.out.println(name + " gained " + amount + " gold.");
        }
    }

    /**
     * Returns the player's current gold amount.
     * @return The amount of gold the player possesses.
     */
    public int getGold() {
        return gold;
    }
    /**
     * Deducts a specified positive amount of gold from the player's possession.
     * The player's gold will not drop below zero.
     *
     * @param amount The amount of gold to remove. Must be greater than 0.
     */
    public void removeGold(int amount) {
        if (amount > 0) {
            this.gold -= amount;
            if (this.gold < 0) {
                this.gold = 0; // Ensure gold doesn't go negative
            }
            System.out.println(name + " lost " + amount + " gold. Current gold: " + this.gold);
        }
    }

    /**
     * Returns the player's current inventory. The returned list is a direct reference
     * to the player's internal inventory, so modifications to this list will affect
     * the player's actual inventory.
     *
     * @return A {@code List} of {@link Item}s in the player's inventory.
     */
    public List<Item> getInventory() {
        return inventory;
    }

    /**
     * Adds a non-null {@link Item} to the player's inventory.
     *
     * @param item The {@link Item} to add. Must not be {@code null}.
     */
    public void addItem(Item item) {
        if (item != null) {
            this.inventory.add(item);
            System.out.println(this.name + " added " + item.getName() + " to inventory.");
        }
    }

    /**
     * Attempts to remove a specific {@link Item} from the player's inventory.
     * If the item is equipped, it will be unequipped first to ensure stats are correctly adjusted.
     *
     * @param item The {@link Item} to remove. Must not be {@code null}.
     * @return {@code true} if the item was successfully found and removed; {@code false} otherwise
     * (e.g., item not found in inventory or `item` parameter was `null`).
     */
    public boolean removeItem(Item item) {
        if (item != null) {
            // If the item to be removed is currently equipped, unequip it first.
            // This ensures its effects are removed and it's no longer in the equipped slot map.
            if (item instanceof EquipableItem) {
                EquipableItem equipableItem = (EquipableItem) item;
                if (isItemEquipped(equipableItem)) {
                    // Remove from equippedItems map and apply unequip effects
                    equippedItems.remove(equipableItem.getEquipSlot());
                    equipableItem.unequip(this); // Apply item's unequip logic (e.g., stat reduction)
                    System.out.println(this.name + " unequipped " + equipableItem.getName() + " before removal.");
                    recalculateDerivedStats(); // Recalculate stats after unequip effect
                }
            }

            boolean removed = this.inventory.remove(item);
            if (removed) {
                System.out.println(this.name + " removed " + item.getName() + " from inventory.");
            }
            return removed;
        }
        return false;
    }

    /**
     * Allows the player to use an item from their inventory.
     * The actual effect depends on the item's `use` implementation.
     *
     * @param item The item to use.
     */
    public void useItem(Item item) {
        if (item == null) {
            System.out.println("Cannot use a null item.");
            return;
        }
        if (!inventory.contains(item)) {
            System.out.println(name + " does not have " + item.getName() + " in inventory to use.");
            return;
        }
        System.out.println(name + " is attempting to use " + item.getName() + "...");
        item.use(this); // Item's use method will handle its effect, including potentially removing itself from inventory
        recalculateDerivedStats(); // In case using an item has temporary stat effects or changes maxHP
    }

    /**
     * Allows the player to equip an `EquipableItem`.
     * If an item is already equipped in the same slot, it will be unequipped first.
     * This method ensures the item remains in the player's main inventory list.
     *
     * @param item The equipable item to equip.
     */
    public void equipItem(EquipableItem item) {
        if (item == null) {
            System.out.println("Cannot equip a null item.");
            return;
        }
        // Ensure the player actually possesses the item in their main inventory list
        if (!inventory.contains(item)) {
            System.out.println(name + " does not have " + item.getName() + " in inventory to equip.");
            return;
        }
        if (level < item.getLevelRequirement()) {
            System.out.println(name + " is too low level (" + level + ") to equip " + item.getName() + " (requires " + item.getLevelRequirement() + ").");
            return;
        }

        EquipmentSlot slot = item.getEquipSlot();

        EquipableItem currentlyEquipped = equippedItems.get(slot);

        if (currentlyEquipped != null && !currentlyEquipped.equals(item)) { // Use .equals() for proper object comparison
            System.out.println("Unequipping " + currentlyEquipped.getName() + " from " + slot.name() + " before equipping " + item.getName() + ".");
            // Call unequipItem. This handles removing the old item from 'equippedItems' map
            // and applying its unequip effects. It does NOT add it back to 'inventory' because it never left.
            unequipItem(currentlyEquipped);
        } else if (currentlyEquipped != null && currentlyEquipped.equals(item)) {
            // The item is already equipped in this slot.
            System.out.println(item.getName() + " is already equipped in " + slot.name() + ".");
            return; // No action needed
        }


        //  here. The item remains in the 'inventory' list;
        // it's just also referenced in 'equippedItems'.
        equippedItems.put(slot, item); // Add to the equipped items map
        item.equip(this); // Apply item's stats/effects to the player
        System.out.println(name + " equipped " + item.getName() + " in " + slot.name() + ".");
        recalculateDerivedStats(); // Recalculate derived stats after equipping
    }

    /**
     * Allows the player to unequip an `EquipableItem`.
     * The item remains in the player's main inventory (as it was never removed from it).
     *
     * @param item The equipable item to unequip.
     */
    public void unequipItem(EquipableItem item) {
        if (item == null) {
            System.out.println("Cannot unequip a null item.");
            return;
        }
        // Check if the item is actually considered equipped
        if (!equippedItems.containsValue(item)) {
            System.out.println(name + " does not have " + item.getName() + " currently equipped.");
            return;
        }

        EquipmentSlot slot = item.getEquipSlot();

        // Verify that the item passed is indeed the one equipped in its designated slot
        if (equippedItems.get(slot) != null && equippedItems.get(slot).equals(item)) { // Use .equals()
            equippedItems.remove(slot); // Remove from the equipped items map
            item.unequip(this); // Remove item's stats/effects from the player
            System.out.println(name + " unequipped " + item.getName() + " from " + slot.name() + ".");
            recalculateDerivedStats(); // Recalculate derived stats after unequipping
        } else {
            System.out.println("Error: " + item.getName() + " is not equipped in its designated slot (" + slot.name() + "), or the equipped item is different.");
        }
    }

    /**
     * Checks if a specific equipable item is currently equipped by the player.
     * @param item The EquipableItem to check.
     * @return true if the item is equipped, false otherwise.
     */
    public boolean isItemEquipped(EquipableItem item) {
        if (item == null) {
            return false;
        }
        return equippedItems.containsValue(item);
    }

    /**
     * Returns the item equipped in a specific slot.
     * @param slot The equip slot (e.g., "Main Hand", "Head").
     * @return The EquipableItem in that slot, or null if nothing is equipped.
     */
    public EquipableItem getEquippedItem(EquipmentSlot slot) {
        return equippedItems.get(slot);
    }

    /**
     * Returns a map of all currently equipped items, keyed by their equip slot.
     * @return A Map<EquipmentSlot, EquipableItem> representing equipped gear.
     */
    public Map<EquipmentSlot, EquipableItem> getEquippedItems() {
        return equippedItems;
     }
    // Setters for existing fields (re-pasted for completeness, no changes here)
    /**
     * Sets the player's name.
     * @param name The new name for the player.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the player's maximum health points.
     * Use with caution, as maximum HP is typically derived from Constitution and Level.
     * Manually setting it might lead to inconsistencies if not managed carefully.
     * @param maxHP The new maximum HP.
     */
    public void setMaxHP(int maxHP) {
        this.maxHp = maxHP;
    }

    /**
     * Sets the player's current health points.
     * @param currentHP The new current HP.
     */
    public void setCurrentHP(int currentHP) {
        this.currentHp = currentHP;
    }

    /**
     * Sets the player's Strength statistic.
     * Automatically triggers a recalculation of derived combat stats (e.g., attack damage).
     * @param strength The new Strength value.
     */
    public void setStrength(int strength) {
        this.strength = strength;
        recalculateDerivedStats();
    }

    /**
     * Sets the player's Dexterity statistic.
     * Automatically triggers a recalculation of derived combat stats (e.g., dodge, attack speed, crit damage).
     * @param dexterity The new Dexterity value.
     */
    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
        recalculateDerivedStats();
    }

    /**
     * Sets the player's Intelligence statistic.
     * Automatically triggers a recalculation of derived combat stats (if Intelligence influences any).
     * @param intelligence The new Intelligence value.
     */
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
        recalculateDerivedStats();
    }

    /**
     * Sets the player's Luck statistic.
     * Automatically triggers a recalculation of derived combat stats (e.g., critical hit chance/damage).
     * @param luck The new Luck value.
     */
    public void setLuck(int luck) {
        this.luck = luck;
        recalculateDerivedStats();
    }

    /**
     * Sets the player's Constitution statistic.
     * Automatically triggers a recalculation of maximum HP and derived combat stats (e.g., parry chance).
     * @param constitution The new Constitution value.
     */
    public void setConstitution(int constitution) {
        this.constitution = constitution;
        this.maxHp = calculateMaxHP(); // Max HP also changes with Constitution
        recalculateDerivedStats();
    }

    /**
     * Sets the player's Charisma statistic.
     * Currently, Charisma doesn't directly influence derived combat stats, so `recalculateDerivedStats()`
     * is not explicitly called here, but it's good practice to consider if it ever would.
     * @param charisma The new Charisma value.
     */
    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    /**
     * Sets the player's current level.
     * Automatically triggers a recalculation of maximum HP and all derived combat stats.
     * @param level The new level.
     */
    public void setLevel(int level) {
        this.level = level;
        this.maxHp = calculateMaxHP(); // Max HP also changes with Level
        recalculateDerivedStats();
    }

    /**
     * Sets the player's current experience points.
     * @param experience The new current experience.
     */
    public void setExperience(int experience) {
        this.currentexperience = experience;
    }

    /**
     * Sets the player's armor value.
     * @param armor The new armor value.
     */
    public void setArmor(int armor) {
        this.armor = armor;
    }

    /**
     * Sets the player's current gold amount.
     * @param gold The new gold amount.
     */
    public void setGold(int gold) {
        this.gold = gold;
    }

    /**
     * Sets the number of unallocated stat points the player has available.
     * @param points The new number of unallocated stat points.
     */
    public void setUnallocatedStatPoints(int points) {
        this.unallocatedStatPoints = points;
    }

    // Setters for derived stats like setDodge, setAttackSpeed, etc./...
    public void setDodge(double dodge) { this.dodge = dodge; }
    public void setAttackSpeed(double attackSpeed) { this.attackSpeed = attackSpeed; }
    public void setParry(double parry) { this.parry = parry; }
    public void setAttackDmg(int attackDmg) { this.attackDmg = attackDmg; }
    public void setCritChance(double critChance) { this.critChance = critChance; }
    public void setCritDmg(double critDmg) { this.critDmg = critDmg; }
}