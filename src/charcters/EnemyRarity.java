package charcters;

/**
 * Defines the different rarity tiers for enemies in the game.
 * Each rarity level influences an enemy's stats, encounter rate (typically),
 * and the rewards (experience and gold) gained upon its defeat.
 */
public enum EnemyRarity {
    /** Represents a common enemy, typically with standard stats and rewards. */
    COMMON,
    /** Represents an uncommon enemy, generally stronger than common enemies with better rewards. */
    UNCOMMON,
    /** Represents a rare enemy, significantly more powerful and offering substantial rewards. */
    RARE,
    /** Represents a legendary enemy, the most powerful and rarest type, providing exceptional rewards. */
    LEGENDARY
}