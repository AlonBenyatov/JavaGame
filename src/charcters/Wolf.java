package charcters;

public class Wolf extends Enemy {

    // Modify these numbers directly to adjust the default properties for all Wolves.
    private static final int INITIAL_BASE_ATTACK_DMG = 10;
    private static final int INITIAL_BASE_ARMOR = 4; // This will be the final armor value
    private static final int INITIAL_BASE_EXP_REWARD = 25;
    private static final int INITIAL_BASE_GOLD_REWARD = 3;
    // -----------------------------------------------

    public Wolf(int level, EnemyRarity rarity) {
        super(getWolfName(rarity, level), level, rarity);
        // Call setWolfStats to set base stats and rewards
        setWolfStats(rarity);
        // Crucial: Recalculate derived stats AFTER base stats are set
        recalculateDerivedStats();
    }

    private static String getWolfName(EnemyRarity rarity, int level) {
        String colorPrefix = "";
        switch (rarity) {
            case COMMON:
                colorPrefix = "Blue";
                break;
            case UNCOMMON:
                colorPrefix = "Green";
                break;
            case RARE:
                colorPrefix = "Red";
                break;
            case LEGENDARY:
                colorPrefix = "Yellow";
                break;
            default:
                colorPrefix = "Generic";
                break;
        }
        return colorPrefix + " Wolf, Level " + level + ", " + rarity;
    }

    private void setWolfStats(EnemyRarity rarity) {
        double statMultiplier = 1.0;
        switch (rarity) {
            case UNCOMMON:
                statMultiplier = 1.5 + (Math.random() * 0.3);
                break;
            case RARE:
                statMultiplier = 3 + (Math.random() * 0.5);
                break;
            case LEGENDARY:
                statMultiplier = 7.5;
                break;
            case COMMON:
            default:
                break;
        }

        // Set core base stats that will be scaled by level/rarity
        this.strength = (int) (6 + (level * 0.7) * statMultiplier);
        this.dexterity = (int) (6 + (level * 0.9) * statMultiplier);
        this.intelligence = (int) (2 + (level * 0.1) * statMultiplier);
        this.constitution = (int) (6 + (level * 0.5) * statMultiplier);
        this.luck = (int) (3 + (level * 0.3) * statMultiplier);
        this.charisma = (int) (1 + (level * 0.1) * statMultiplier);

        // Set the non-scaling base values directly from constants
        this.baseAttackDmg = INITIAL_BASE_ATTACK_DMG;
        this.experienceReward = INITIAL_BASE_EXP_REWARD*level;
        this.goldReward = INITIAL_BASE_GOLD_REWARD*level;

        // Set the final armor value directly from its constant, no formula
        this.armor = INITIAL_BASE_ARMOR;

        // Max HP calculation
        this.maxHP = (int) (this.constitution * 10 + 45 + (level * 10) * statMultiplier);
        this.currentHP = this.maxHP;
    }

    @Override
    public int calculateMaxHP() {
        return this.maxHP; // Max HP is already calculated and set in setWolfStats
    }

    /**
     * Recalculates all derived combat statistics for a Wolf.
     * This implementation uses the 'baseAttackDmg' set in setWolfStats.
     */
    @Override
    protected void recalculateDerivedStats() {
        this.dodge = CombatCalculator.calculateDodgeChance(this.dexterity);
        this.attackSpeed = CombatCalculator.calculateAttackSpeed(this.dexterity);
        this.parry = CombatCalculator.calculateParryChance(this.constitution);
        // Use the baseAttackDmg set in setWolfStats
        this.attackDmg = CombatCalculator.CalculateAttackDmg(this.baseAttackDmg, this.strength);
        this.critChance = CombatCalculator.calculateCritChance(this.luck);
        this.critDmg = CombatCalculator.calculateCritDamage(this.dexterity, this.luck);
    }
}