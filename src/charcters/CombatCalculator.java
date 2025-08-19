package charcters;

import java.util.Random;

/**
 * The `CombatCalculator` class provides static utility methods for calculating
 * various combat-related statistics and outcomes, such as hit chance, dodge,
 * parry, critical hits, attack speed, and final damage.
 * It encapsulates the core mathematical formulas that govern combat mechanics
 * in the game.
 */
public class CombatCalculator {

    // --- Combat Stat Base Values and Scaling Constants ---
    private static final double BASE_CRIT_CHANCE = 0.05; // 5% base critical hit chance
    private static final double CRIT_CHANCE_PER_LUCK = 0.0005; // 0.05% crit chance per point of Luck
    private static final double BASE_CRIT_DAMAGE = 2.0; // 200% base critical hit damage multiplier
    private static final double CRIT_DAMAGE_PER_DEXTERITY = 0.001; // 0.1% crit damage per point of Dexterity
    private static final double BASE_DODGE_CHANCE = 0.01; // 1% base dodge chance
    private static final double DODGE_CHANCE_PER_DEXTERITY = 0.0002; // 0.02% dodge chance per point of Dexterity
    private static final double BASE_PARRY_CHANCE = 0.01; // 1% base parry chance
    private static final double PARRY_CHANCE_PER_CONSTITUTION = 0.0004; // 0.04% parry chance per point of Constitution
    private static final double BASE_HIT_CHANCE = 0.75; // 75% base hit chance
    private static final double HIT_CHANCE_PER_DEXTERITY = 0.01; // 1% hit chance per point of Attacker Dexterity
    private static final double PARRY_DAMAGE_REDUCTION_MULTIPLIER = 0.4; // Damage is reduced to 40% (60% reduction) on parry
    private static final double DEFAULT_ATTACK_SPEED = 0.500; // Default attacks per second (e.g., 0.5 attacks/second or 1 attack every 2 seconds)
    private static final double ATTACK_SPEED_PER_DEXTERITY = 0.005; // Increases attack speed by 0.005 attacks/sec per point of Dexterity
    private static final double DMG_Per_Str = 0.4; // Damage increase per point of Strength

    // --- Armor Mitigation Constants (Asymptotic Formula) ---
    /**
     * The maximum percentage of damage armor can mitigate (as a decimal).
     * For example, 0.60 means armor can mitigate up to 60% of incoming damage.
     */
    private static final double MAX_ARMOR_MITIGATION_TARGET = 0.60;
    /**
     * A constant that determines how quickly the armor mitigation approaches the
     * `MAX_ARMOR_MITIGATION_TARGET`.
     * <ul>
     * <li>Lower value: Armor is more effective at lower amounts, quicker to approach max reduction.</li>
     * <li>Higher value: Armor is less effective at lower amounts, more armor needed for high reduction.</li>
     * </ul>
     */
    private static final double ARMOR_MITIGATION_DENOMINATOR_CONSTANT = 50.0;

    private static final Random random = new Random(); // Random number generator for combat rolls

    /**
     * Calculates the critical hit chance based on the attacker's Luck statistic.
     * The chance increases linearly with Luck.
     *
     * @param attackerLuck The Luck stat of the attacking character.
     * @return The critical hit chance as a `double` between 0.0 and 1.0 (representing 0% to 100%).
     */
    public static double calculateCritChance(int attackerLuck) {
        return BASE_CRIT_CHANCE + attackerLuck * CRIT_CHANCE_PER_LUCK;
    }

    /**
     * Calculates the critical hit damage multiplier based on the attacker's
     * Dexterity and Luck statistics. Both stats contribute to increased critical damage.
     *
     * @param attackerDexterity The Dexterity stat of the attacking character.
     * @param attackerLuck The Luck stat of the attacking character.
     * @return The critical hit damage multiplier as a `double` (e.g., 2.0 for 200% damage).
     */
    public static double calculateCritDamage(int attackerDexterity, int attackerLuck) {
        return BASE_CRIT_DAMAGE + attackerDexterity * CRIT_DAMAGE_PER_DEXTERITY + attackerLuck * CRIT_CHANCE_PER_LUCK;
    }

    /**
     * Calculates the dodge chance for a character based on their Dexterity statistic.
     * Higher Dexterity results in a higher chance to dodge incoming attacks.
     *
     * @param targetDexterity The Dexterity stat of the character attempting to dodge.
     * @return The dodge chance as a `double` between 0.0 and 1.0.
     */
    public static double calculateDodgeChance(int targetDexterity) {
        return BASE_DODGE_CHANCE + targetDexterity * DODGE_CHANCE_PER_DEXTERITY;
    }

    /**
     * Calculates the parry chance for a character based on their Constitution statistic.
     * Higher Constitution results in a higher chance to parry incoming attacks.
     *
     * @param targetConstitution The Constitution stat of the character attempting to parry.
     * @return The parry chance as a `double` between 0.0 and 1.0.
     */
    public static double calculateParryChance(int targetConstitution) {
        return BASE_PARRY_CHANCE + targetConstitution * PARRY_CHANCE_PER_CONSTITUTION;
    }

    /**
     * Calculates the chance for an attacker to hit a defender, taking into account
     * the attacker's Dexterity and the defender's dodge chance.
     *
     * @param targetDexterity The Dexterity stat of the defending character.
     * @param attackerDexterity The Dexterity stat of the attacking character.
     * @return The hit chance as a `double` between 0.0 and 1.0.
     */
    public static double calculateHitChance(int targetDexterity, int attackerDexterity) {
        // Base hit chance + attacker's dexterity bonus - defender's dodge chance
        return (BASE_HIT_CHANCE + attackerDexterity * HIT_CHANCE_PER_DEXTERITY - calculateDodgeChance(targetDexterity));
    }

    /**
     * Calculates the attack speed of a character in attacks per second.
     * Attack speed increases with the character's Dexterity.
     *
     * @param attackerDexterity The Dexterity stat of the attacking character.
     * @return The calculated attack speed as a `double`.
     */
    public static double calculateAttackSpeed(int attackerDexterity) {
        double calculatedAttackSpeed = DEFAULT_ATTACK_SPEED + attackerDexterity * ATTACK_SPEED_PER_DEXTERITY;
        return calculatedAttackSpeed;
    }

    /**
     * Calculates the total attack damage of a character, combining a base damage
     * value with a bonus derived from the character's Strength statistic.
     *
     * @param baseattackdmg The base attack damage value (e.g., from a weapon).
     * @param targetStr The Strength stat of the character whose damage is being calculated.
     * @return The calculated total attack damage as an `int`.
     */
    public static int CalculateAttackDmg(int baseattackdmg, int targetStr) {
        int attackDmg = (int) (baseattackdmg + targetStr * DMG_Per_Str);
        return attackDmg;
    }

    /**
     * Calculates the final damage dealt from an `Enemy` to a `Player`.
     * This method simulates the entire damage calculation process, including:
     * 1. **Hit Chance:** Determines if the attack connects, factoring in dodge.
     * 2. **Parry:** Checks if the target parries, reducing damage significantly.
     * 3. **Critical Hit:** Rolls for a critical hit, applying a damage multiplier if successful.
     * 4. **Armor Mitigation:** Reduces damage based on the target's armor using an asymptotic formula.
     *
     * Debugging output is printed to the console throughout the calculation.
     *
     * @param attacker The attacking `Enemy` character.
     * @param target The defending `Player` character.
     * @param baseDamage The initial raw damage value before any combat mechanics.
     * @param critMultiplier The critical damage multiplier to apply if a critical hit occurs.
     * @return The final integer damage value that the target will take (minimum 0).
     */
    public static int calculateFinalDamage(Enemy attacker, Player target, int baseDamage, float critMultiplier) {
        System.out.println("--- Enemy Attack Calculation ---"); // Debugging
        System.out.println("Attacker: " + attacker.getName() + ", Base Damage: " + baseDamage); // Debugging
        System.out.println("Defender: " + target.getName() + ", Defender Armor: " + target.getArmor()); // Debugging

        double finalDamage = baseDamage;

        // 1. Calculate hit chance and check for miss (dodge)
        double hitChance = calculateHitChance(target.getDexterity(), attacker.getDexterity());
        System.out.println("Calculated Hit Chance: " + String.format("%.2f", hitChance * 100) + "%"); // Debugging
        if (random.nextDouble() > hitChance) {
            System.out.println(target.getName() + " dodges the attack! Damage: 0"); // Debugging
            return 0; // Attack missed
        }
        System.out.println(target.getName() + " is hit."); // Debugging

        // 2. Check for parry
        double parryChance = calculateParryChance(target.getConstitution());
        System.out.println("Calculated Parry Chance: " + String.format("%.2f", parryChance * 100) + "%"); // Debugging
        if (random.nextDouble() < parryChance) {
            System.out.println(target.getName() + " parries the attack!"); // Debugging
            finalDamage *= PARRY_DAMAGE_REDUCTION_MULTIPLIER; // Apply parry damage reduction
            System.out.println("Damage after parry reduction: " + (int) finalDamage); // Debugging
            return (int) finalDamage; // Parry successful, damage is reduced and calculation ends
        }

        // 3. Check for critical hit
        double critChance = calculateCritChance(attacker.getLuck());
        System.out.println("Calculated Crit Chance: " + String.format("%.2f", critChance * 100) + "%"); // Debugging
        if (random.nextDouble() < critChance) {
            System.out.println(attacker.getName() + " lands a critical hit!"); // Debugging
            finalDamage *= critMultiplier; // Apply critical damage multiplier
            System.out.println("Damage after critical hit: " + (int) finalDamage); // Debugging
        } else {
            System.out.println("No critical hit."); // Debugging
        }

        // 4. Calculate mitigated damage (Armor) using an asymptotic formula
        // This formula ensures that armor provides diminishing returns and never reaches 100% mitigation.
        double damageReductionPercent = MAX_ARMOR_MITIGATION_TARGET *
                                        ((double)target.getArmor() / (target.getArmor() + ARMOR_MITIGATION_DENOMINATOR_CONSTANT));
        finalDamage = finalDamage * (1.0 - damageReductionPercent);
        System.out.println("Damage reduction from armor (" + String.format("%.2f", damageReductionPercent * 100) + "%): " + (int)(baseDamage - finalDamage)); // Debugging

        System.out.println("Final Damage: " + (int) finalDamage); // Debugging
        System.out.println("---------------------------------"); // Debugging
        return Math.max(0, (int) finalDamage); // Ensure damage is never negative
    }

    /**
     * Calculates the final damage dealt from a `Player` to an `Enemy`.
     * This method simulates the entire damage calculation process, including:
     * 1. **Hit Chance:** Determines if the attack connects, factoring in dodge.
     * 2. **Parry:** Checks if the target parries, reducing damage significantly.
     * 3. **Critical Hit:** Rolls for a critical hit, applying a damage multiplier if successful.
     * 4. **Armor Mitigation:** Reduces damage based on the target's armor using an asymptotic formula.
     *
     * Debugging output is printed to the console throughout the calculation.
     *
     * @param attacker The attacking `Player` character.
     * @param defender The defending `Enemy` character.
     * @param baseDamage The initial raw damage value before any combat mechanics.
     * @param critMultiplier The critical damage multiplier to apply if a critical hit occurs.
     * @return The final integer damage value that the target will take (minimum 0).
     */
    public static int calculateFinalDamage(Player attacker, Enemy defender, int baseDamage, float critMultiplier) {
        System.out.println("--- Player Attack Calculation ---"); // Debugging
        System.out.println("Attacker: " + attacker.getName() + ", Base Damage: " + baseDamage); // Debugging
        System.out.println("Defender: " + defender.getName() + ", Defender Armor: " + defender.getArmor()); // Debugging

        double finalDamage = baseDamage;

        // 1. Calculate hit chance and check for miss (dodge)
        double hitChance = calculateHitChance(defender.getDexterity(), attacker.getDexterity());
        System.out.println("Calculated Hit Chance: " + String.format("%.2f", hitChance * 100) + "%"); // Debugging
        if (random.nextDouble() > hitChance) {
            System.out.println(defender.getName() + " dodges the attack! Damage: 0"); // Debugging
            return 0; // Attack missed
        }
        System.out.println(defender.getName() + " is hit."); // Debugging

        // 2. Check for parry
        double parryChance = calculateParryChance(defender.getConstitution());
        System.out.println("Calculated Parry Chance: " + String.format("%.2f", parryChance * 100) + "%"); // Debugging
        if (random.nextDouble() < parryChance) {
            System.out.println(defender.getName() + " parries the attack!"); // Debugging
            finalDamage *= PARRY_DAMAGE_REDUCTION_MULTIPLIER; // Apply parry damage reduction
            System.out.println("Damage after parry reduction: " + (int) finalDamage); // Debugging
            return (int) finalDamage; // Parry successful, damage is reduced and calculation ends
        }

        // 3. Check for critical hit
        double critChance = calculateCritChance(attacker.getLuck());
        System.out.println("Calculated Crit Chance: " + String.format("%.2f", critChance * 100) + "%"); // Debugging
        if (random.nextDouble() < critChance) {
            System.out.println(attacker.getName() + " lands a critical hit!"); // Debugging
            finalDamage *= critMultiplier; // Apply critical damage multiplier
            System.out.println("Damage after critical hit: " + (int) finalDamage); // Debugging
        } else {
            System.out.println("No critical hit."); // Debugging
        }

        // 4. Calculate mitigated damage (Armor) using an asymptotic formula
        double damageReductionPercent = MAX_ARMOR_MITIGATION_TARGET *
                                        ((double)defender.getArmor() / (defender.getArmor() + ARMOR_MITIGATION_DENOMINATOR_CONSTANT));
        finalDamage = finalDamage * (1.0 - damageReductionPercent);
        System.out.println("Damage reduction from armor (" + String.format("%.2f", damageReductionPercent * 100) + "%): " + (int)(baseDamage - finalDamage)); // Debugging

        System.out.println("Final Damage: " + (int) finalDamage); // Debugging
        System.out.println("---------------------------------"); // Debugging
        return Math.max(0, (int) finalDamage); // Ensure damage is never negative
    }

    /**
     * Calculates the rate of attacks per unit of time (e.g., per second)
     * based on a given attack speed. This is essentially the inverse of attack speed.
     *
     * @param calculatedAttackSpeed The calculated attack speed (e.g., attacks per second).
     * @return The rate of attacks (time per attack) as a `double`.
     */
    public static double RateOfAttacks(double calculatedAttackSpeed) {
        // Avoid division by zero for extremely low attack speeds, though unlikely with current constants
        if (calculatedAttackSpeed <= 0) {
            return Double.POSITIVE_INFINITY; // Or throw an exception, depending on desired behavior
        }
        double rateofattacks = Math.pow(calculatedAttackSpeed, -1); // 1 / attackSpeed
        return rateofattacks;
    }
}