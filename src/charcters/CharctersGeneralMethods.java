package charcters;

/**
 * Defines the essential methods and attributes that all combat-capable
 * characters (both players and enemies) in the game must possess.
 * This interface establishes a common contract for interacting with any
 * entity that participates in combat, enabling polymorphic behavior
 * in systems like the CombatManager.
 */
public interface CharctersGeneralMethods {

    /**
     * Returns the name of the character.
     * @return The character's name.
     */
    String getName();

    /**
     * Returns the character's current level.
     * @return The character's level.
     */
    int getLevel();

    /**
     * Returns the character's current health points.
     * @return The current HP.
     */
    int getCurrentHP();

    /**
     * Returns the character's maximum health points.
     * @return The maximum HP.
     */
    int getMaxHP();

    /**
     * Applies damage to the character, reducing their current health.
     * Implementations should handle health dropping below zero (i.e., character death).
     * @param damage The amount of damage to be taken.
     */
    void takeDamage(int damage);

    /**
     * Checks if the character is currently alive.
     * @return {@code true} if the character's current HP is greater than 0; {@code false} otherwise.
     */
    boolean isAlive();

    /**
     * Returns the character's Strength statistic.
     * Strength typically influences physical attack power.
     * @return The Strength value.
     */
    int getStrength();

    /**
     * Returns the character's Dexterity statistic.
     * Dexterity typically influences accuracy, dodge chance, and attack speed.
     * @return The Dexterity value.
     */
    int getDexterity();

    /**
     * Returns the character's Intelligence statistic.
     * Intelligence typically influences magical power or special abilities.
     * @return The Intelligence value.
     */
    int getIntelligence();

    /**
     * Returns the character's Luck statistic.
     * Luck typically influences critical hit chance and other random outcomes.
     * @return The Luck value.
     */
    int getLuck();

    /**
     * Returns the character's Constitution statistic.
     * Constitution typically influences maximum health and defensive capabilities like parry chance.
     * @return The Constitution value.
     */
    int getConstitution();

    /**
     * Returns the character's Charisma statistic.
     * Charisma typically influences social interactions, though it might not be directly used in combat.
     * @return The Charisma value.
     */
    int getCharisma();

    /**
     * Returns the character's current armor value.
     * Armor typically reduces incoming physical damage.
     * @return The armor value.
     */
    int getArmor();

    /**
     * Returns the character's calculated dodge chance.
     * This is typically a percentage (e.g., 0.05 for 5%).
     * @return The dodge chance as a {@code double}.
     */
    double getDodge();

    /**
     * Returns the character's calculated attack speed.
     * This typically represents attacks per second.
     * @return The attack speed as a {@code double}.
     */
    double getAttackSpeed();

    /**
     * Returns the character's calculated parry chance.
     * This is typically a percentage (e.g., 0.05 for 5%).
     * @return The parry chance as a {@code double}.
     */
    double getParry();

    /**
     * Returns the character's calculated base attack damage.
     * This is the raw damage before other combat modifiers (like crit or armor).
     * @return The attack damage value.
     */
    int getAttackDmg();

    /**
     * Returns the character's calculated critical hit chance.
     * This is typically a percentage (e.g., 0.10 for 10%).
     * @return The critical hit chance as a {@code double}.
     */
    double getCritChance();

    /**
     * Returns the character's calculated critical hit damage multiplier.
     * This is a multiplier (e.g., 2.0 for 200% damage).
     * @return The critical hit damage multiplier as a {@code double}.
     */
    double getCritDmg();
}