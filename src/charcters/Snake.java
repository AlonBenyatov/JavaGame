package charcters;

public class Snake extends Enemy 
{
	
    // Modify these numbers directly to adjust the default properties for all snakes.
    private static final int INITIAL_BASE_ATTACK_DMG = 20;
    private static final int INITIAL_BASE_ARMOR = 8; // This will be the final armor value
    private static final int INITIAL_BASE_EXP_REWARD = 150;
    private static final int INITIAL_BASE_GOLD_REWARD = 8;
    
    
    public Snake(String name, int level, EnemyRarity rarity)
    {
		super(name, level, rarity);
		// TODO Auto-generated constructor stub
	}




@Override
public int calculateMaxHP() {
	// TODO Auto-generated method stub
	return 0;
}

@Override
protected void recalculateDerivedStats() {
	// TODO Auto-generated method stub
	
}

}
     /** 
      * maybe i will continue  later idkk 
      */

