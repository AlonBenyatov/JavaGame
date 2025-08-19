package handlers;

import java.util.*; // For Random and other utility classes
import charcters.CharctersGeneralMethods; // Import the common interface
import charcters.Enemy;
import charcters.EnemyRarity;
import charcters.Player;
import charcters.CombatCalculator;
import charcters.Slime; // Specific enemy type
import charcters.Wolf;  // Specific enemy type
import charcters.EnemyType; // Enum for different enemy types
// import charcters.Snake; // Uncomment if you have a Snake class

/**
 * The `Handler` class serves as a central hub for various game mechanics and
 * logic operations, particularly those involving randomness and combat.
 * It encapsulates the `Random` instance used throughout the game to ensure
 * consistent random number generation and provides methods for creating enemies
 * with specific properties and handling combat actions.
 *
 * It also manages an instance of `AutoAttackHandler` to process automatic
 * combat turns between entities.
 */
public class Handler {

    private final Random random; // The central Random instance for the game
    private final CombatCalculator combatCalculator; // Reference to the combat calculation logic
    /**
     * An instance of `AutoAttackHandler`, which manages the automatic
     * attack sequence between a player and an an enemy (or vice-versa).
     * This is made public so other parts of the game (e.g., `BattlePanel`)
     * can easily access and utilize its combat logic.
     */
    public final AutoAttackHandler autoAttackHandler;

    /**
     * Constructs a new `Handler` instance.
     * Initializes the central `Random` object and takes a `CombatCalculator`
     * instance to perform combat-related calculations. It also sets up the
     * `AutoAttackHandler`, passing it the necessary dependencies.
     *
     * @param combatCalculator The `CombatCalculator` instance used for combat calculations.
     */
    public Handler(CombatCalculator combatCalculator) {
        this.random = new Random(); // Initialize the random number generator
        this.combatCalculator = combatCalculator; // Store the combat calculator
        // Initialize the AutoAttackHandler, providing it with the combat calculator
        // and this Handler's random instance for consistent randomness.
        this.autoAttackHandler = new AutoAttackHandler(combatCalculator, this.random);
    }

    /**
     * An enumeration defining various actions that can be performed in the game.
     * Currently includes combat actions and HP regeneration.
     */
    public enum Action {
        /** Represents a standard attack action. */
        ATTACK,
        /** Represents an action to regenerate health points. */
        RegenHp // Consider renaming to regenHP or RegenerateHP for clarity
    }

    /**
     * The `AutoAttackHandler` is a static nested class within `Handler`
     * that specifically manages the detailed logic for automatic attacks
     * between two combatants (a `Player` and an `Enemy`, or vice-versa).
     * It handles hit chance, dodge, parry, critical hits, and calculates final damage.
     */
    public static class AutoAttackHandler {
        private final CombatCalculator combatCalculator; // For calculating damage, hit chance, crit chance etc.
        private final Random random; // The Random instance inherited from the parent Handler

        // The AttackOutcome class was NOT present in the original state.

        /**
         * Constructs a new `AutoAttackHandler`.
         *
         * @param combatCalculator The `CombatCalculator` instance to use for all combat-related calculations.
         * @param random The `Random` instance to use for all random number generation within this handler.
         */
        public AutoAttackHandler(CombatCalculator combatCalculator, Random random) {
            this.combatCalculator = combatCalculator;
            this.random = random;
        }

        /**
         * Handles an automatic attack sequence between an attacker and a defender.
         * This method calculates hit chance, applies dodge/parry, determines critical hits,
         * calculates final damage, and applies it to the defender. It returns the
         * total damage dealt.
         *
         * @param attacker The `CharctersGeneralMethods` representing the attacker (can be Player or Enemy).
         * @param defender The `CharctersGeneralMethods` representing the defender (can be Player or Enemy).
         * @return The total damage dealt. Returns 0 damage if the attack misses, is dodged, parried, or on error.
         */
        public int handleAutoAttack(CharctersGeneralMethods attacker, CharctersGeneralMethods defender) {
            // Determine attacker's base damage and names.
            int baseDamage = attacker.getAttackDmg(); // Assuming getAttackDmg() is in CharctersGeneralMethods
            String attackerName = attacker.getName();
            String defenderName = defender.getName();

            // Calculate and check for a miss.
            double hitChance = CombatCalculator.calculateHitChance(defender.getDexterity(), attacker.getDexterity());

            if (this.random.nextDouble() > hitChance) {
                System.out.println(attackerName + " misses " + defenderName + ".");
                return 0; // Return 0 damage on miss
            }

            // Calculate and check for dodge or parry.
            double dodgeChance = CombatCalculator.calculateDodgeChance(defender.getDexterity());
            double parryChance = CombatCalculator.calculateParryChance(defender.getConstitution());

            if (this.random.nextDouble() < dodgeChance) {
                System.out.println(defenderName + " dodges the attack from " + attackerName + ".");
                return 0; // Return 0 damage on dodge
            }

            if (this.random.nextDouble() < parryChance) {
                System.out.println(defenderName + " parries the attack from " + attackerName + ".");
                return 0; // Return 0 damage on parry
            }

            // Determine if the attack is a critical hit.
            boolean isCritical = false; // This 'isCritical' was internal to handleAutoAttack.
            double critMultiplier = 1.0;

            // This original logic might still cause "always crit" if getCritChance() is > 1.0
            if (this.random.nextDouble() < attacker.getCritChance()) {
                isCritical = true;
                critMultiplier = attacker.getCritDmg();
                System.out.println(attackerName + " lands a critical hit!");
            }

            // Calculate final damage.
            int damageToDeal;
            // The calculateFinalDamage methods require specific Player/Enemy types, so casting is necessary here.
            if (attacker instanceof Enemy && defender instanceof Player) {
                damageToDeal = CombatCalculator.calculateFinalDamage((Enemy) attacker, (Player) defender, baseDamage, (float)critMultiplier);
            } else if (attacker instanceof Player && defender instanceof Enemy) {
                damageToDeal = CombatCalculator.calculateFinalDamage((Player) attacker, (Enemy) defender, baseDamage, (float)critMultiplier);
            } else {
                System.err.println("Error: Mismatched attacker/defender types for final damage calculation in AutoAttackHandler. " +
                                   "Attacker: " + attacker.getClass().getSimpleName() + ", Defender: " + defender.getClass().getSimpleName());
                return 0; // Return 0 on error
            }

            // This call to takeDamage() is what caused the double damage later,
            // as it was also called in BattlePanel. It's present here again as original.
            defender.takeDamage(damageToDeal);

            // In the original state, only the damage was returned.
            return damageToDeal;
        }
    }

    /**
     * Generates a specific type of enemy with rarity determined by global probabilities,
     * and its level determined by a probabilistic distribution within a 5-level cycle.
     * The starting level of the cycle is determined by the EnemyType.
     *
     * @param type The specific type of enemy to create (e.g., {@link EnemyType#SLIME}).
     * @param tierStartingLevel The absolute starting level of the 5-level tier for this enemy type (e.g., 1 for Slime, 6 for Wolf).
     * @param statMultiplier The multiplier to apply to the enemy's core stats and primary combat stats.
     * @return A new {@link Enemy} object of the specified type with determined rarity, level, and boosted stats.
     * @throws UnsupportedOperationException If the requested enemy type is not yet implemented.
     */
    public Enemy createEnemy(EnemyType type, int tierStartingLevel, double statMultiplier) { // MODIFIED: Added statMultiplier
        EnemyRarity rarity = determineRarityBasedOnProbability(); // Determine rarity first

        // Determine the relative level within the 0-4 range (Level 1-5, 6-10, etc. relative to tier start)
        int relativeLevelModifier = determineRelativeLevelRoll();

        // Calculate the final absolute enemy level
        int finalEnemyLevel = tierStartingLevel + relativeLevelModifier;

        // Ensure enemy level is at least 1
        if (finalEnemyLevel < 1) {
            finalEnemyLevel = 1;
        }

        System.out.println("Generating " + type + ": Tier Start Lvl=" + tierStartingLevel +
                            ", Relative Modifier=+" + relativeLevelModifier + ", Final Enemy Lvl=" + finalEnemyLevel +
                            ", Rarity=" + rarity + ", Stat Multiplier=" + String.format("%.2f", statMultiplier)); // UPDATED: Debug output

        Enemy newEnemy; // Declare Enemy variable to hold the created instance

        switch (type) {
            case SLIME:
                newEnemy = new Slime(finalEnemyLevel, rarity);
                break;
            case WOLF:
                newEnemy = new Wolf(finalEnemyLevel, rarity);
                break;
            case SNAKE: // Uncomment if you have a Snake class and added it to EnemyType enum
                // newEnemy = new Snake(finalEnemyLevel, rarity);
                throw new UnsupportedOperationException("Snake enemy type not yet implemented."); // Placeholder
            default:
                System.err.println("Warning: Attempted to create unknown enemy type: " + type + ". Defaulting to Slime.");
                newEnemy = new Slime(finalEnemyLevel, rarity);
                break;
        }

        // Apply the stat multiplier AFTER the enemy is created and its rarity/level stats are set.
        // The boostCoreStats method in Enemy handles applying this to the specified stats.
        if (statMultiplier != 1.0) { // Only apply boost if it's not the default 1.0 (0% increase)
            newEnemy.boostCoreStats(statMultiplier);
        }

        return newEnemy;
    }

    /**
     * Determines the rarity of an enemy based on predefined, cumulative probabilities.
     * This is the **only** method for probabilistic rarity determination in the game,
     * ensuring consistent rarity generation.
     *
     * <ul>
     * <li>**LEGENDARY:** 0.005% chance (`< 0.00005`)</li>
     * <li>**RARE:** 0.495% chance (`< 0.005` but `>= 0.00005`)</li>
     * <li>**UNCOMMON:** 7% chance (`< 0.075` but `>= 0.005`)</li>
     * <li>**COMMON:** 92.5% chance (`>= 0.075`)</li>
     * </ul>
     *
     * @return An {@link EnemyRarity} enum value (LEGENDARY, RARE, UNCOMMON, or COMMON).
     */
    private EnemyRarity determineRarityBasedOnProbability() {
        double chance = random.nextDouble(); // Use the central Random instance

        if (chance < 0.00005) {
            System.out.println("Generated Legendary Enemy Rarity! (Chance: " + String.format("%.5f", chance) + ")");
            return EnemyRarity.LEGENDARY;
        } else if (chance < 0.005) { // Cumulative probability: 0.00005 (LEG) + 0.00495 (RARE) = 0.005
            System.out.println("Generated Rare Enemy Rarity! (Chance: " + String.format("%.5f", chance) + ")");
            return EnemyRarity.RARE;
        } else if (chance < 0.075) { // Cumulative probability: 0.005 (RARE) + 0.07 (UNCOMMON) = 0.075
            System.out.println("Generated Uncommon Enemy Rarity! (Chance: " + String.format("%.5f", chance) + ")");
            return EnemyRarity.UNCOMMON;
        } else { // Remaining probability
            System.out.println("Generated Common Enemy Rarity! (Chance: " + String.format("%.5f", chance) + ")");
            return EnemyRarity.COMMON;
        }
    }

    /**
     * Determines a relative level modifier (0 to 4) for an enemy based on a weighted
     * probability distribution, influencing the enemy's level within its 5-level tier.
     * This modifier represents the position within a 5-level cycle (e.g., +0 for the first
     * level in the tier, +4 for the fifth).
     *
     * <ul>
     * <li>**Level +0:** 40% chance (e.g., actual level 1, 6, 11, etc.)</li>
     * <li>**Level +1:** 30% chance (e.g., actual level 2, 7, 12, etc.)</li>
     * <li>**Level +2:** 20% chance (e.g., actual level 3, 8, 13, etc.)</li>
     * <li>**Level +3:** 8% chance (e.g., actual level 4, 9, 14, etc.)</li>
     * <li>**Level +4:** 2% chance (e.g., actual level 5, 10, 15, etc.)</li>
     * </ul>
     *
     * @return An integer representing the level modifier (0 to 4).
     */
    private int determineRelativeLevelRoll() {
        double randRoll = random.nextDouble(); // Use the central Random instance

        if (randRoll < 0.40) {
            return 0; // 40% chance for the first level in the 5-level tier
        } else if (randRoll < 0.70) { // 0.40 + 0.30
            return 1; // 30% chance for the second level
        } else if (randRoll < 0.90) { // 0.70 + 0.20
            return 2; // 20% chance for the third level
        } else if (randRoll < 0.98) { // 0.90 + 0.08
            return 3; // 8% chance for the fourth level
        } else { // 0.98 + 0.02
            return 4; // 2% chance for the fifth (highest) level
        }
    }
}