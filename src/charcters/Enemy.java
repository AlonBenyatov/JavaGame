package charcters;

/**
 * Abstract base class for all enemies in the game.
 * Implements `CharctersGeneralMethods` to ensure all enemies conform to
 * the standard character interface. Provides common attributes and
 * methods for enemy behavior, leaving specific implementations (like stat
 * scaling and action selection) to subclasses.
 */
public abstract class Enemy implements CharctersGeneralMethods {
    protected String name;
    protected int level;
    protected EnemyRarity rarity;
    protected int currentHP;
    protected int maxHP;
    protected int strength;
    protected int dexterity;
    protected int intelligence;
    protected int constitution;
    protected int luck;
    protected int charisma;

    // This is the *derived/final* combat stat for the instance.
    // For armor, it will now hold the directly set base value.
    protected int armor;

    // This is the *derived/final* combat stat for the instance, calculated based on base value and scaling.
    protected int attackDmg;

    // --- New Base Field for Direct Configuration by Subclasses ---
    // This value is set by a constant in each concrete enemy subclass.
    protected int baseAttackDmg; // Base attack damage, will still be used in attackDmg formula
    protected int goldReward;       // Base gold reward for this specific enemy instance
    protected int experienceReward; // Base experience reward for this specific enemy instance
    // ------------------------------------------------------------

    protected double dodge;
    protected double attackSpeed;
    protected double parry;
    protected double critChance;
    protected double critDmg;


    /**
     * Constructs a new `Enemy` instance.
     * Initializes the enemy's fundamental properties such as name, level, and rarity.
     * Subclasses are responsible for setting their specific core stats, base damage/armor,
     * rewards, max HP, and then calling `recalculateDerivedStats()`.
     *
     * @param name The name of this enemy (e.g., "Goblin", "Slime").
     * @param level The current level of this enemy.
     * @param rarity The {@link EnemyRarity} of this enemy, which influences its power and rewards.
     */
    public Enemy(String name, int level, EnemyRarity rarity) {
        this.name = name;
        this.level = level;
        this.rarity = rarity;

        // Core stats, base combat stats, and rewards are initialized by subclasses.
        // Placeholders or default 0 values are used here, as they'll be overwritten.
        this.strength = 0;
        this.dexterity = 0;
        this.intelligence = 0;
        this.constitution = 0;
        this.luck = 0;
        this.charisma = 0;
        this.armor = 0; // Final armor placeholder
        this.baseAttackDmg = 0; // Base attack damage placeholder
        this.goldReward = 0;
        this.experienceReward = 0;

        // Derived stats will be calculated by `recalculateDerivedStats()` after base stats are set.
        this.dodge = 0.0;
        this.attackSpeed = 0.0;
        this.parry = 0.0;
        this.attackDmg = 0;
        this.critChance = 0.0;
        this.critDmg = 0.0;

        this.maxHP = 0; // Placeholder, will be set in subclass
        this.currentHP = 0; // Placeholder, will be set in subclass
    }

    // --- Methods from CharctersGeneralMethods ---
    @Override
    public String getName() { return name; }
    @Override
    public int getLevel() { return level; }
    @Override
    public int getCurrentHP() { return currentHP; }
    @Override
    public int getMaxHP() { return maxHP; }
    @Override
    public void takeDamage(int damage) {
        this.currentHP -= damage;
        if (this.currentHP < 0) {
            this.currentHP = 0;
        }
        System.out.println(this.name + " took " + damage + " damage. Current HP: " + this.currentHP);
    }
    @Override
    public boolean isAlive() { return this.currentHP > 0; }
    @Override
    public int getStrength() { return strength; }
    @Override
    public int getDexterity() { return dexterity; }
    @Override
    public int getIntelligence() { return intelligence; }
    @Override
    public int getConstitution() { return constitution; }
    @Override
    public int getLuck() { return luck; }
    @Override
    public int getCharisma() { return charisma; }

    // --- Getters for Derived Combat Stats ---
    @Override
    public int getArmor() { return armor; } // Now directly returns the base value set by subclass
    @Override
    public int getAttackDmg() { return attackDmg; }
    @Override
    public double getDodge() { return dodge; }
    @Override
    public double getAttackSpeed() { return attackSpeed; }
    @Override
    public double getParry() { return parry; }
    @Override
    public double getCritChance() { return critChance; }
    @Override
    public double getCritDmg() { return critDmg; }

    // --- Getters for Base Configurable Values (New Public API) ---
    public int getBaseAttackDmg() { return baseAttackDmg; }
    // REMOVED: getBaseArmor() is no longer needed as 'armor' directly holds the base value.

    // --- Getters for Rewards (Public API) ---
    public int getGoldReward() { return goldReward; }
    public int getExperienceReward() { return experienceReward; }

    // --- Setters for Rewards (Public API for dynamic changes) ---
    public void setGoldReward(int goldReward) {
    	 this.goldReward = goldReward;
    }
    public void setExperienceReward(int experienceReward) {
        this.experienceReward = experienceReward;
    }

    // --- Abstract Methods (Must be implemented by subclasses) ---
    /**
     * Abstract method to calculate the maximum HP for a specific enemy type.
     * Concrete subclasses must provide their own implementation for this.
     * @return The calculated maximum HP.
     */
    public abstract int calculateMaxHP();

    /**
     * Recalculates all derived combat statistics (dodge, attack speed, parry, crit, final attack damage)
     * based on the enemy's current base stats (Strength, Dexterity, Constitution, Luck)
     * and the configured `baseAttackDmg`.
     * This method must be called by concrete subclasses after their core stats and `baseAttackDmg` are set.
     */
    protected abstract void recalculateDerivedStats();

    // --- Common Enemy Methods ---
    public EnemyRarity getRarity() { return rarity; }

    /**
     * Calculates the total gold reward for defeating this enemy,
     * factoring in its base gold reward (from the 'goldReward' field) and rarity.
     * The `rewardMultiplier` values within this method are unchanged as per your instruction.
     * @return The total gold reward.
     */
    public int calculateGoldReward() {
        double rewardMultiplier = 1.0;
        switch (rarity) {
            case UNCOMMON:
                rewardMultiplier = 5.0;
                break;
            case RARE:
                rewardMultiplier = 30.0;
                break;
            case LEGENDARY:
                rewardMultiplier = 1000.0;
                break;
            default: // COMMON
                rewardMultiplier = 1.0;
                break;
        }
        // Now uses the 'goldReward' field, which is set in subclasses.
        return (int) (this.goldReward * rewardMultiplier);
    }

    /**
     * Calculates the total experience reward for defeating this enemy,
     * factoring in its base experience reward (from the 'experienceReward' field) and rarity.
     * The `expMultiplier` values within this method are unchanged as per your instruction.
     * @return The total experience reward.
     */
    protected int calculateRewardExp() {
        double expMultiplier = 1.0;
        switch (rarity) {
            case UNCOMMON:
                expMultiplier = 5.0;
                break;
            case RARE:
                expMultiplier = 30.0;
                break;
            case LEGENDARY:
                expMultiplier = 1000.0;
                break;
            default: // COMMON
                expMultiplier = 1.0;
                break;
        }
        // Now uses the 'experienceReward' field, which is set in subclasses.
        return (int) (this.experienceReward * expMultiplier);
    }

    /**
     * Applies a stat multiplier to the enemy's core attributes and primary combat stats.
     * This method is used to dynamically boost an enemy's power (e.g., during battle loops).
     *
     * @param multiplier The factor by which to increase the stats (e.g., 1.05 for a 5% boost).
     */
    public void boostCoreStats(double multiplier) {
        // Boost core stats directly
        this.strength = (int) (this.strength * multiplier);
        this.dexterity = (int) (this.dexterity * multiplier);
        this.intelligence = (int) (this.intelligence * multiplier);
        this.luck = (int) (this.luck * multiplier);
        this.charisma = (int) (this.charisma * multiplier);
        this.constitution = (int) (this.constitution * multiplier);

        // Also boost maxHP and armor directly.
        // It's crucial that maxHP and armor also scale, otherwise the enemy won't be tougher defensively/hp-wise.
        this.maxHP = (int) (this.maxHP * multiplier);
        this.currentHP = this.maxHP; // Heal to new maxHP after boost

        this.armor = (int) (this.armor * multiplier); // Boost armor directly

        // After boosting core stats, recalculate derived stats like attackDmg, dodge, etc.
        // This ensures these combat stats reflect the new, boosted core stats.
        recalculateDerivedStats();
    }
}