package handlers;

import java.util.*;
import charcters.CharctersGeneralMethods;
import charcters.Enemy;
import charcters.EnemyRarity;
import charcters.Player;
import charcters.CombatCalculator;
import charcters.Slime;
import charcters.Wolf;
import charcters.EnemyType;
import panels.GameMenu;
import javax.swing.JOptionPane;
import util.GameSaver; // <--- NEW: Import GameSaver

/**
 * The `LoopHandler` class manages sequences of battles, applying special rules like
 * stat boosts for enemies and all-or-nothing rewards. It encapsulates the game's
 * logic for multi-battle loops.
 */
public class LoopHandler {

    private main.GamePanel screenManager;
    private Player player;
    private Handler mainHandler;

    // State variables for the current battle loop
    private int totalBattlesInLoop;
    private int battlesWonInCurrentLoop;
    private int accumulatedExp;
    private int accumulatedGold;

    // Parameters for the enemies generated within this specific loop
    private EnemyType currentLoopEnemyType;
    private int currentLoopTierStartingLevel;
    private double currentLoopStatMultiplier;

    /**
     * Constructs a new `LoopHandler`.
     *
     * @param screenManager The central `GamePanel` that manages screen/panel switching.
     * @param player The `Player` character whose actions this handler will manage.
     * @param mainHandler The main `Handler` instance, used for enemy creation.
     */
    public LoopHandler(main.GamePanel screenManager, Player player, Handler mainHandler) {
        this.screenManager = screenManager;
        this.player = player;
        this.mainHandler = mainHandler;
        resetLoopState();
    }

    /**
     * Overloaded constructor for cases where the player might be set later.
     *
     * @param screenManager The central `GamePanel` that manages screen/panel switching.
     * @param mainHandler The main `Handler` instance, used for enemy creation.
     */
    public LoopHandler(main.GamePanel screenManager, Handler mainHandler) {
        this.screenManager = screenManager;
        this.mainHandler = mainHandler;
        resetLoopState();
    }

    /**
     * Sets or updates the player character for this handler.
     * @param player The current `Player` instance.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Resets all internal state variables related to the battle loop.
     * This prepares the handler for a new loop or clears it after a loop ends.
     */
    private void resetLoopState() {
        this.totalBattlesInLoop = 0;
        this.battlesWonInCurrentLoop = 0;
        this.accumulatedExp = 0;
        this.accumulatedGold = 0;
        this.currentLoopEnemyType = null;
        this.currentLoopTierStartingLevel = 0;
        this.currentLoopStatMultiplier = 1.0;
        updateDungeonLoopStatus("No Battle Loop Active");
    }

    /**
     * Initiates a new battle loop with specified parameters.
     *
     * @param numBattles The total number of battles for this loop (1-100).
     * @param enemyType The type of enemy to fight consistently throughout the loop.
     * @param tierStartingLevel The base level for generating enemies in this loop.
     * @param statMultiplier The multiplier to apply to enemies' core stats in this loop.
     */
    public void startBattleLoop(int numBattles, EnemyType enemyType, int tierStartingLevel, double statMultiplier) {
        if (player == null || !player.isAlive()) {
            System.err.println("Cannot start battle loop: Player is null or not alive.");
            JOptionPane.showMessageDialog(screenManager.getGameWindow(),
                                          "Your player is defeated or not loaded! Please heal or load a game.",
                                          "Cannot Start Loop", JOptionPane.WARNING_MESSAGE);
            screenManager.getGameWindow().switchToGameMenu(player);
            return;
        }

        if (numBattles < 1 || numBattles > 100) {
            System.err.println("LoopHandler received an invalid number of battles: " + numBattles + ". Aborting loop start.");
            return;
        }

        resetLoopState();

        this.totalBattlesInLoop = numBattles;
        this.currentLoopEnemyType = enemyType;
        this.currentLoopTierStartingLevel = tierStartingLevel;
        this.currentLoopStatMultiplier = statMultiplier;

        System.out.println("Starting battle loop: " + totalBattlesInLoop + " battles against " + currentLoopEnemyType.name() + " with " + String.format("%.0f", (statMultiplier - 1.0) * 100) + "% stat boost.");
        updateDungeonLoopStatus("Battle Loop: 0/" + totalBattlesInLoop);

        startNextBattle();
    }

    /**
     * Starts the next battle in the current loop.
     */
    private void startNextBattle() {
        if (battlesWonInCurrentLoop < totalBattlesInLoop) {
            int battleNumber = battlesWonInCurrentLoop + 1;
            System.out.println("Beginning Battle " + battleNumber + " of " + totalBattlesInLoop);
            updateDungeonLoopStatus("Battle Loop: " + battlesWonInCurrentLoop + "/" + totalBattlesInLoop + " (Battle " + battleNumber + " in progress)");

            Enemy newEnemy = mainHandler.createEnemy(currentLoopEnemyType, currentLoopTierStartingLevel, currentLoopStatMultiplier);

            // Make sure the BattlePanel's LoopHandler is set/cleared correctly around each battle
            screenManager.getGameWindow().getBattlePanel().setLoopHandler(this);
            screenManager.getGameWindow().switchToBattle(player, newEnemy);
        } else {
            handleLoopCompletion();
        }
    }

    /**
     * Callback method invoked by `BattlePanel` when a battle concludes.
     *
     * @param playerWon `true` if the player won the battle, `false` otherwise.
     * @param defeatedEnemy The `Enemy` that was defeated (or the one the player lost to).
     */
    public void onBattleEnd(boolean playerWon, Enemy defeatedEnemy) {
        screenManager.getGameWindow().getBattlePanel().setLoopHandler(null); // Clear reference from BattlePanel

        if (playerWon) {
            battlesWonInCurrentLoop++;
            accumulatedExp += defeatedEnemy.getExperienceReward();
            accumulatedGold += defeatedEnemy.getGoldReward();

            System.out.println("Battle " + battlesWonInCurrentLoop + " won! Accumulated Rewards: Exp=" + accumulatedExp + ", Gold=" + accumulatedGold);
            updateDungeonLoopStatus("Battle Loop: " + battlesWonInCurrentLoop + "/" + totalBattlesInLoop);

            if (battlesWonInCurrentLoop < totalBattlesInLoop) {
                System.out.println("Proceeding to next battle in loop...");
                player.setCurrentHP(player.getMaxHP()); // Heal player before next battle in loop
                startNextBattle();
            } else {
                handleLoopCompletion(); // All battles won
            }
        } else {
            handleLoopLoss(); // Player lost a battle
        }
    }

    /**
     * Handles the successful completion of an entire battle loop.
     */
    private void handleLoopCompletion() {
        System.out.println("Battle loop completed successfully! Total battles won: " + battlesWonInCurrentLoop);
        player.gainExperience(accumulatedExp); // Apply accumulated EXP
        player.addGold(accumulatedGold);       // Apply accumulated Gold
        player.setCurrentHP(player.getMaxHP()); // Fully heal player after loop completion

        // --- THE CRUCIAL ADDITION HERE ---
        if (player != null) {
            GameSaver.saveGame(player); // **SAVE THE PLAYER'S STATE AFTER GAINS**
            System.out.println("DEBUG: Player stats saved after loop completion for " + player.getName());
        } else {
            System.err.println("ERROR: Player is null when trying to save after loop completion!");
        }
        // --- END OF CRUCIAL ADDITION ---

        JOptionPane.showMessageDialog(screenManager.getGameWindow(),
                "Battle Loop Completed!\nYou gained " + accumulatedExp + " XP and " + accumulatedGold + " Gold!",
                "Loop Success", JOptionPane.INFORMATION_MESSAGE);

        GameMenu gameMenu = screenManager.getGameWindow().getGameMenuPanel();
        if (gameMenu != null) {
            gameMenu.updateReminderButton();
        }
        screenManager.getGameWindow().switchToGameMenu(player);
        resetLoopState(); // Reset handler for next loop
    }

    /**
     * Handles the scenario where the player loses a battle within the loop.
     */
    private void handleLoopLoss() {
        System.out.println("Battle loop failed on battle " + (battlesWonInCurrentLoop + 1) + " of " + totalBattlesInLoop + ".");
        System.out.println("No rewards gained. Returning to Game Menu.");
        player.setCurrentHP(player.getMaxHP()); // Heal to max HP, or handle defeat state if more granular

        JOptionPane.showMessageDialog(screenManager.getGameWindow(),
                "You were defeated during the battle loop.\nNo rewards earned this time.",
                "Loop Failed", JOptionPane.ERROR_MESSAGE);

        GameMenu gameMenu = screenManager.getGameWindow().getGameMenuPanel();
        if (gameMenu != null) {
            gameMenu.updateReminderButton();
        }
        screenManager.getGameWindow().switchToGameMenu(player);
        resetLoopState(); // Reset handler for next loop
    }

    /**
     * Updates the status label in the `Dungeon1Panel` to reflect the current loop state.
     * @param statusText The text to display on the loop status label.
     */
    private void updateDungeonLoopStatus(String statusText) {
        if (screenManager.getGameWindow().getDungeon1Panel() != null) {
            screenManager.getGameWindow().getDungeon1Panel().updateLoopStatusLabel(statusText);
        } else {
            System.out.println("Loop Status: " + statusText);
        }
    }
}