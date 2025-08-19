package handlers;

import charcters.Player;
import charcters.Enemy;
import java.util.HashMap;
import java.util.Map;

// The TimeHandler class is responsible for managing time-based mechanics,
// specifically tracking cooldowns for combat actions like attacks for various entities.
public class TimeHandler {

    // A map to store the System.nanoTime() of the last recorded attack for each combatant.
    // Using Object as the key type allows us to store cooldowns for both Player and Enemy instances,
    // as they are distinct objects that need individual cooldown tracking.
    private Map<Object, Long> lastAttackTimes;

    // This is the base duration (in seconds) that an attack would take if a combatant
    // had an Attack Speed of 1.0. Higher Attack Speed values will reduce this duration.
    // Example: If BASE_ATTACK_COOLDOWN_SECONDS is 1.0, an entity with AS=1.0 attacks every 1 second.
    // An entity with AS=2.0 attacks every 0.5 seconds (1.0 / 2.0).
    private final double BASE_ATTACK_COOLDOWN_SECONDS = 1.0;

    /**
     * Constructor for TimeHandler. Initializes the map that stores last attack times.
     */
    public TimeHandler() {
        lastAttackTimes = new HashMap<>();
    }

    /**
     * Safely retrieves the last recorded attack time (in nanoseconds) for a given combatant.
     * This method is public to allow other classes (like BattlePanel) to check a combatant's
     * cooldown status for display or debugging without directly accessing the private map.
     *
     * @param combatant The Player or Enemy object whose last attack time is needed.
     * @return The System.nanoTime() when the combatant last attacked, or 0L if they have not
     * attacked yet (ensuring they can attack immediately).
     */
    public long getLastAttackTime(Object combatant) {
        // Using getOrDefault ensures that if a combatant hasn't attacked yet (no entry in map),
        // it defaults to 0, allowing 'canCombatantAttack' to correctly determine they are ready.
        return lastAttackTimes.getOrDefault(combatant, 0L);
    }

    /**
     * Calculates the required cooldown duration (in nanoseconds) for a combatant's attack.
     * This duration is inversely proportional to the combatant's attack speed.
     *
     * @param combatant The Player or Enemy whose attack cooldown is to be calculated.
     * @return The cooldown duration in nanoseconds. Returns Long.MAX_VALUE if the combatant's
     * attack speed is zero or negative, effectively preventing attacks.
     */
    public long getAttackCooldownNanos(Object combatant) {
        double attackSpeed = 0.0;

        // Determine the attack speed based on the combatant's type (Player or Enemy).
        if (combatant instanceof Player) {
            attackSpeed = ((Player) combatant).getAttackSpeed();
        } else if (combatant instanceof Enemy) {
            attackSpeed = ((Enemy) combatant).getAttackSpeed();
        } else {
            // Log a warning if an unexpected object type is passed, and prevent attack.
            System.err.println("Warning: getAttackCooldownNanos called with unknown combatant type: " + combatant.getClass().getName());
            return Long.MAX_VALUE;
        }

        // Ensure attack speed is positive to avoid division by zero or nonsensical cooldowns.
        if (attackSpeed <= 0) {
            return Long.MAX_VALUE; // Effectively means infinite cooldown, disallowing attack.
        }

        // Calculate cooldown: (Base Cooldown in seconds / Attack Speed) * Nanoseconds per second
        // 1_000_000_000L is 1 billion nanoseconds in a second.
        return (long) ((BASE_ATTACK_COOLDOWN_SECONDS / attackSpeed) * 1_000_000_000L);
    }

    /**
     * Checks if a given combatant (Player or Enemy) is currently able to perform an attack.
     * This is determined by comparing the time since their last attack against their required cooldown.
     *
     * @param combatant The Player or Enemy to check.
     * @return true if the combatant's attack cooldown has elapsed, false otherwise.
     */
    public boolean canCombatantAttack(Object combatant) {
        if (combatant == null) return false; // A null combatant cannot attack.

        long currentTime = System.nanoTime();
        long lastAttackTime = getLastAttackTime(combatant); // Use the public getter for last attack time.

        long timeSinceLastAttack = currentTime - lastAttackTime;

        // An attack can occur if the time elapsed since the last attack is greater than or equal to the cooldown.
        return timeSinceLastAttack >= getAttackCooldownNanos(combatant);
    }

    /**
     * Records the current time as the moment a combatant has just performed an attack.
     * This resets their cooldown timer, preventing them from attacking again until the
     * cooldown duration has passed.
     *
     * @param combatant The Player or Enemy who has just attacked.
     */
    public void recordCombatantAttack(Object combatant) {
        if (combatant == null) return;
        lastAttackTimes.put(combatant, System.nanoTime());
    }

    /**
     * Resets the attack cooldown for a specific combatant. This removes their entry from the map,
     * effectively allowing them to attack immediately as if they never attacked before.
     * Useful at the beginning of a new battle or upon certain game events.
     *
     * @param combatant The Player or Enemy whose cooldown is to be reset.
     */
    public void resetCombatantCooldown(Object combatant) {
        if (combatant == null) return;
        lastAttackTimes.remove(combatant);
    }

    /**
     * Clears all recorded cooldowns for all combatants managed by this TimeHandler.
     * Useful for resetting the entire game state, e.g., starting a new game session.
     */
    public void resetAllCooldowns() {
        lastAttackTimes.clear();
    }
}