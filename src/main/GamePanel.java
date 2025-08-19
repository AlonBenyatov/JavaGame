package main;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

import panels.Dungeon1Panel;
import panels.BattlePanel;
import panels.StatsDisplayPanel;
import panels.GameMenu;
// import panels.GamePlayPanel; // <--- REMOVED THIS IMPORT
import charcters.Player;
import charcters.Enemy;

/**
 * The `GamePanel` class acts as the central screen manager for the entire game's user interface.
 * It extends `JPanel` and uses a `CardLayout` to switch between different game screens (panels).
 *
 * This panel is responsible for:
 * <ul>
 * <li>Holding references to all distinct game screens (e.g., Main Menu, Gameplay, Battle, etc.).</li>
 * <li>Providing a method to switch between these screens seamlessly.</li>
 * <li>Keeping track of the currently displayed screen and the previously displayed screen.</li>
 * </ul>
 * The `GameWindow` typically adds this `GamePanel` to its content pane, making it the primary
 * visible component that orchestrates all other UI views.
 */
public class GamePanel extends JPanel {

    private CardLayout cardLayout;
    private HashMap<String, JPanel> screens;
    private GameWindow gameWindow; // Reference to the main GameWindow (set after creation)
    private String previousScreen;
    private JPanel currentScreen;

    /**
     * Constructs a new `GamePanel`.
     * Initializes the `CardLayout` as the layout manager for this panel,
     * and sets up the `HashMap` to store individual game screens.
     * The `GameWindow` reference will be set via `setGameWindow()` after it's created.
     */
    public GamePanel() { // No longer takes GameWindow in constructor
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        screens = new HashMap<>();
        this.previousScreen = "menu";
    }

    /**
     * This method is called by `Main.java` to start the game.
     * It creates the `GameWindow` and passes 'this' (the GamePanel instance) to it.
     * It then sets the `GameWindow` reference back into this `GamePanel`.
     */
    public void startGame() { // <--- NEW/RE-ADDED METHOD
        // Initialize GameWindow and pass 'this' (the GamePanel instance) to it.
        gameWindow = new GameWindow(this); // The GameWindow constructor accepts GamePanel
        gameWindow.setVisible(true); // Make the main JFrame visible
        // The GameWindow constructor will internally call gamePanel.setGameWindow(this);
        // So, explicitly calling setGameWindow(this) here is redundant if GameWindow does it.
        // If GameWindow DOESN'T call gamePanel.setGameWindow(this), then uncomment the line below.
        // this.setGameWindow(gameWindow);
    }

    /**
     * Sets the reference to the main `GameWindow` that contains this `GamePanel`.
     * This method is called by `GameWindow` after both `GameWindow` and `GamePanel` are created.
     *
     * @param window The main `GameWindow` instance.
     */
    public void setGameWindow(GameWindow window) {
        this.gameWindow = window;
    }

    /**
     * Adds a new game screen (JPanel) to the `GamePanel`, making it available
     * for display via the `CardLayout`.
     *
     * @param name The unique `String` identifier for this screen (e.g., "menu", "gameplay").
     * @param panel The `JPanel` instance representing the screen to add.
     */
    public void addScreen(String name, JPanel panel) {
        screens.put(name, panel);
        add(panel, name);
    }

    /**
     * Switches the currently displayed screen to the one identified by `screenName`.
     * This method updates the `CardLayout` to show the specified panel, records
     * the new current screen, and updates player data on relevant panels.
     *
     * @param screenName The `String` identifier of the screen to switch to.
     */
    public void switchToScreen(String screenName) {
        CardLayout cl = (CardLayout) (getLayout());
        cl.show(this, screenName);

        this.previousScreen = screenName;
        this.currentScreen = screens.get(screenName);

        if (gameWindow != null) {
            Player currentPlayer = gameWindow.getCurrentPlayer();

            // Removed `instanceof GamePlayPanel` check as it no longer exists.
            // Ensure other relevant panels are updated if they need player data.
            // The `setCurrentPlayer` in GameWindow already calls `setPlayer` on these,
            // so this block here is partially redundant, but keeping specific updates
            // if a screen needs *immediate* refresh on switch to.
            if (currentScreen instanceof StatsDisplayPanel) {
                ((StatsDisplayPanel) currentScreen).setPlayer(currentPlayer);
            } else if (currentScreen instanceof GameMenu) {
                ((GameMenu) currentScreen).setPlayer(currentPlayer);
            }
            // You might want to add other panels here if they also need to update
            // their display immediately upon being switched to.
            // E.g., if you switch to a Dungeon1Panel and it needs to display player HP.
            // if (currentScreen instanceof Dungeon1Panel) {
            //     ((Dungeon1Panel) currentScreen).setPlayer(currentPlayer);
            // }
            // If BattlePanel needs immediate player data update on switch:
            // if (currentScreen instanceof BattlePanel) {
            //     ((BattlePanel) currentScreen).setPlayer(currentPlayer);
            // }
        }
        System.out.println("Switched to screen: " + screenName);
    }

    /**
     * Returns the `String` identifier of the screen that was previously displayed.
     * This can be useful for "back" buttons or returning to the calling screen.
     *
     * @return The `String` name of the previous screen.
     */
    public String getPreviousScreen() {
        return this.previousScreen;
    }

    /**
     * Returns the reference to the main `GameWindow` that contains this `GamePanel`.
     * This allows sub-panels to access the `GameWindow` for global operations.
     *
     * @return The parent `GameWindow` instance.
     */
    public GameWindow getGameWindow() {
        return gameWindow;
    }

    /**
     * Returns a reference to the `JPanel` that is currently displayed by the `CardLayout`.
     *
     * @return The `JPanel` instance currently visible on screen.
     */
    public JPanel getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Delegates the action of switching to a battle screen to the main `GameWindow`.
     * This method acts as a bridge, allowing sub-panels (like `Dungeon1Panel`) to
     * initiate a battle without needing a direct reference to `GameWindow` themselves.
     * The `GameWindow` is responsible for setting up the `BattlePanel` with combatants
     * and performing the screen transition.
     *
     * @param player The `Player` object participating in the battle.
     * @param enemy The `Enemy` object participating in the battle.
     */
    public void switchToBattle(Player player, Enemy enemy) {
        if (gameWindow != null) {
            gameWindow.switchToBattle(player, enemy);
        } else {
            System.err.println("Error: Cannot switch to battle. GameWindow reference is null in GamePanel.");
        }
    }
}