package charcters;

public class Slime extends Enemy {

    // --- EASILY CHANGEABLE BASE VALUES FOR SLIMES ---
    // Modify these numbers directly to adjust the default properties for all Slimes.
    private static final int INITIAL_BASE_ATTACK_DMG = 6;
    private static final int INITIAL_BASE_ARMOR = 1; // This will be the final armor value
    private static final int INITIAL_BASE_EXP_REWARD = 10;
    private static final int INITIAL_BASE_GOLD_REWARD = 2;
    // -----------------------------------------------

    public Slime(int level, EnemyRarity rarity) {
        super(getSlimeFullDisplayName(rarity, level), level, rarity);
        // Call setSlimeStats to set base stats and rewards
        setSlimeStats(rarity);
        // Crucial: Recalculate derived stats AFTER base stats are set
        recalculateDerivedStats();
    }

    private static String getSlimeFullDisplayName(EnemyRarity rarity, int level) {
        String color = "";
        switch (rarity) {
            case COMMON:
                color = "Blue";
                break;
            case UNCOMMON:
                color = "Green";
                break;
            case RARE:
                color = "Red";
                break;
            case LEGENDARY:
                color = "Yellow";
                break;
            default:
                color = "Generic";
                break;
        }
        return color + " Slime, Level " + level + ", " + rarity;
    }

    private void setSlimeStats(EnemyRarity rarity) {
        double statMultiplier = 1.0;
        switch (rarity) {
            case UNCOMMON:
                statMultiplier = 1.5 + (Math.random() * 0.2);
                break;
            case RARE:
                statMultiplier = 2.5 + (Math.random() * 0.5);
                break;
            case LEGENDARY:
                statMultiplier = 7.5;
                break;
            case COMMON:
            default:
                break;
        }

        // Set core base stats that will be scaled by level/rarity
        this.strength = (int) (6 + (level / 2) * statMultiplier);
        this.dexterity = (int) (4 + (level / 2) * statMultiplier);
        this.intelligence = (int) (3 + (level / 2) * statMultiplier);
        this.constitution = (int) (5 + (level / 2) * statMultiplier);
        this.luck = (int) (3 + (level / 2) * statMultiplier);
        this.charisma = (int) (1 + (level / 2) * statMultiplier);

        // Set the non-scaling base values directly from constants
        this.baseAttackDmg = INITIAL_BASE_ATTACK_DMG;
        this.experienceReward = INITIAL_BASE_EXP_REWARD*level;
        this.goldReward = INITIAL_BASE_GOLD_REWARD;

        // Set the final armor value directly from its constant, no formula
        this.armor = INITIAL_BASE_ARMOR;

        // Max HP calculation
        this.maxHP = (int) (this.constitution * 6 + 35 + (level * 8) * statMultiplier);
        this.currentHP = this.maxHP; // Ensure currentHP is set after maxHP
    }

    @Override
    public int calculateMaxHP() {
        return this.maxHP; // Max HP is already calculated and set in setSlimeStats
    }

    /**
     * Recalculates all derived combat statistics for a Slime.
     * This implementation uses the 'baseAttackDmg' set in setSlimeStats.
     */
    @Override
    protected void recalculateDerivedStats() {
        this.dodge = CombatCalculator.calculateDodgeChance(this.dexterity);
        this.attackSpeed = CombatCalculator.calculateAttackSpeed(this.dexterity);
        this.parry = CombatCalculator.calculateParryChance(this.constitution);
        // Use the baseAttackDmg set in setSlimeStats
        this.attackDmg = CombatCalculator.CalculateAttackDmg(this.baseAttackDmg, this.strength);
        this.critChance = CombatCalculator.calculateCritChance(this.luck);
        this.critDmg = CombatCalculator.calculateCritDamage(this.dexterity, this.luck);
    }
}