package main;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import charcters.CombatCalculator;
import charcters.Enemy;
import charcters.Player;
import handlers.Handler;
import handlers.LoopHandler;
import panels.BattlePanel;
import panels.CharacterCreationPanel;
import panels.Dungeon1Panel;
import panels.GameMenu;
import panels.InventoryPanel;
import panels.Menu;
import panels.Options;
import panels.SavedGames;
import panels.ShopPanel; // <--- ADD THIS IMPORT
import panels.StatsDisplayPanel;
import util.GameSaver;

/**
 * The `GameWindow` class extends `JFrame` and serves as the main application window for the 2D Dungeon Game.
 * It acts as the primary container for all other game panels (like the main menu, character creation, battle, etc.)
 * and manages the transitions between these different screens. It also holds central references to the
 * `Player` object and core game logic handlers.
 */
public class GameWindow extends JFrame {

    /**
     * An `ImageIcon` object intended to hold an image used as a background.
     * While present, its direct use for the `GameWindow`'s background drawing is not explicit in the current code,
     * suggesting it might be intended for custom drawing within `GameWindow` or for other panels.
     * @see javax.swing.ImageIcon
     */
    private ImageIcon backgroundImageIcon;

    /**
     * An `Image` object derived from `backgroundImageIcon`. Similar to `backgroundImageIcon`,
     * its direct application for a static `GameWindow` background seems absent in the current code.
     * It might be a remnant or intended for future custom painting in the `GameWindow` or passed to other panels.
     * @see java.awt.Image
     */
    private Image backgroundImage;

    /**
     * An instance of `CharacterCreationPanel`, which provides the user interface
     * for players to create a new game character.
     * @see panels.CharacterCreationPanel
     */
    private CharacterCreationPanel characterCreationPanel;

    /**
     * A crucial reference to the main `GamePanel` instance. This `GamePanel` uses a `CardLayout`
     * to manage and swap between different game screens (e.g., `CharacterCreationPanel`, `Menu`, `BattlePanel`).
     * `GameWindow` uses this reference to control which screen is currently displayed.
     * @see main.GamePanel
     * @see java.awt.CardLayout
     */
    private GamePanel gamePanel;

    /**
     * An instance of `SavedGames`, the UI panel responsible for displaying a list of available
     * save files and facilitating the loading of previously saved games.
     * @see panels.SavedGames
     */
    private SavedGames savedGamesPanel;

    /**
     * An instance of `Options`, the UI panel where users can access and configure
     * various game settings and preferences.
     * @see panels.Options
     */
    private Options optionsPanel;

    /**
     * An instance of `GameMenu`, which serves as the central "game hub" after a character is created
     * or a game is loaded. From here, players typically access features like viewing stats,
     * managing inventory, or entering dungeons. This panel effectively replaces an earlier `gameplayPanel` concept.
     * @see panels.GameMenu
     */
    private GameMenu gameMenuPanel;

    /**
     * A `String` variable used to store the identifier of the screen that was active
     * immediately *before* switching to another screen (e.g., used by the `Options` panel
     * to know which screen to return to after settings are adjusted).
     * @see java.lang.String
     */
    private String previousScreen;

    /**
     * A `Player` object that temporarily holds character data when a game is
     * loaded from a save file, before that data is fully initialized and set as the `currentPlayer`.
     * @see charcters.Player
     */
    private Player loadedPlayer;

    /**
     * The `Player` object representing the character currently being played in the game.
     * This is the central player instance that gets passed to various panels and game logic handlers
     * to ensure consistent data across the application.
     * @see charcters.Player
     */
    private Player currentPlayer;

    /**
     * An instance of `StatsDisplayPanel`, which is a dedicated panel for displaying
     * and potentially allowing the player to manage their character's statistics and attributes.
     * @see panels.StatsDisplayPanel
     */
    private StatsDisplayPanel statsDisplayPanel;

    /**
     * An instance of `Dungeon1Panel`, representing the UI screen for the first dungeon or exploration area
     * where the player can encounter enemies and navigate environments.
     * @see panels.Dungeon1Panel
     */
    private Dungeon1Panel dungeon1Panel;

    /**
     * An instance of `BattlePanel`, the UI screen where active combat encounters
     * between the player and enemies take place.
     * @see panels.BattlePanel
     */
    private BattlePanel battlePanel;

    /**
     * An instance of `InventoryPanel`, the UI screen where the player can view,
     * manage, and interact with their collected items and equipped gear.
     * @see panels.InventoryPanel
     */
    private InventoryPanel inventoryPanel;

    /**
     * An instance of `ShopPanel`, the UI screen where the player can buy and sell items.
     * @see panels.ShopPanel
     */
    private ShopPanel shopPanel; // <--- ADDED THIS PRIVATE VARIABLE

    /**
     * An instance of `CombatCalculator`, a utility class responsible for
     * performing the core calculations for damage, hit chances, critical strikes,
     * and other combat-related mechanics.
     * @see charcters.CombatCalculator
     */
    private CombatCalculator combatCalculator;

    /**
     * An instance of the `Handler` class, which likely centralizes various
     * game logic operations and manages key game systems, such as the `AutoAttackHandler`.
     * It acts as a bridge for common game mechanics shared across different panels.
     * @see handlers.Handler
     */
    private Handler gameHandler;

    /**
     * An instance of `LoopHandler`, designed to manage sequential game events or
     * "loops," such as a series of battles within a dungeon run or other repeatable content.
     * It provides a structured flow for managing recurring game segments.
     * @see handlers.LoopHandler
     */
    private LoopHandler loopHandler;

    /**
     * Constructs a new `GameWindow`.
     * This constructor is called by `GamePanel.startGame()` and receives the `GamePanel` instance.
     * It then initializes all UI components and game logic, establishing proper connections.
     *
     * @param gamePanel The main `GamePanel` instance which contains and manages all sub-screens
     * using a `CardLayout`. This `GamePanel` is added to the `GameWindow`.
     */
    public GameWindow(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.gamePanel.setGameWindow(this); // Essential two-way connection for mutual control

        setTitle("My Dungeon Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensures the application exits when the window is closed
        setSize(800, 600); // Sets the initial size of the game window
        setLocationRelativeTo(null); // Centers the window on the screen

        ImageIcon gameIcon = new ImageIcon("gate12.jpg"); // Loads the game icon for the window

        // These image variables are kept for now, but if GameWindow itself doesn't
        // directly draw a static background, they might be redundant here.
        // If your main GamePanel (the manager) is responsible for a global background,
        // that's where these resources should primarily be managed.
        backgroundImageIcon = new ImageIcon("CharacterCreation2.png");
        backgroundImage = backgroundImageIcon.getImage();

        combatCalculator = new CombatCalculator(); // Initializes the combat calculation logic
        gameHandler = new Handler(combatCalculator); // Initializes the main game logic handler
        GameSaver.createSaveDirectory(); // Ensures the save game directory exists before any save/load operations

        add(this.gamePanel, BorderLayout.CENTER); // Adds the main GamePanel instance to the center of the JFrame

        // Initialize LoopHandler, passing the now non-null 'gamePanel' and 'gameHandler'
        loopHandler = new LoopHandler(this.gamePanel, gameHandler);

        // Initialize all individual game screen panels, passing the GamePanel reference
        // so they can request screen transitions.
        characterCreationPanel = new CharacterCreationPanel(this.gamePanel);
        Menu mainMenuPanel = new Menu(this.gamePanel);
        optionsPanel = new Options(this.gamePanel);
        savedGamesPanel = new SavedGames(this.gamePanel);
        gameMenuPanel = new GameMenu(this.gamePanel);
        statsDisplayPanel = new StatsDisplayPanel(this.gamePanel);
        dungeon1Panel = new Dungeon1Panel(this.gamePanel, gameHandler);
        dungeon1Panel.setLoopHandler(loopHandler); // Provide the dungeon panel with the loop handler
        battlePanel = new BattlePanel(this.gamePanel, gameHandler.autoAttackHandler); // Battle panel needs auto-attack handler
        inventoryPanel = new InventoryPanel(this.gamePanel);
        shopPanel = new ShopPanel(this.gamePanel); // <--- ADDED THIS INSTANTIATION

        // Add all initialized panels to the GamePanel's CardLayout, associating each with a unique string key.
        this.gamePanel.addScreen("menu", mainMenuPanel);
        this.gamePanel.addScreen("options", optionsPanel);
        this.gamePanel.addScreen("characterCreation", characterCreationPanel);
        this.gamePanel.addScreen("savedGames", savedGamesPanel);
        this.gamePanel.addScreen("gameMenu", gameMenuPanel);
        this.gamePanel.addScreen("statsDisplay", statsDisplayPanel);
        this.gamePanel.addScreen("dungeon1", dungeon1Panel);
        this.gamePanel.addScreen("battlePanel", battlePanel);
        this.gamePanel.addScreen("inventory", inventoryPanel);
        this.gamePanel.addScreen("shop", shopPanel); // <--- ADDED THIS PANEL TO CARDLAYOUT

        setVisible(true); // Makes the GameWindow visible to the user

        if (gameIcon != null) {
            setIconImage(gameIcon.getImage()); // Sets the window icon if the image was loaded successfully
        }

        System.out.println("DEBUG: GameWindow initialized. Switching to menu.");
        this.gamePanel.switchToScreen("menu"); // Sets the initial screen to the main menu
        previousScreen = "menu"; // Records the initial screen for potential 'back' functionality
    }

    /**
     * Retrieves the `Player` object currently active in the game.
     * This is the character the user is currently controlling or interacting with.
     *
     * @return The `Player` object representing the current player.
     * @see charcters.Player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the active `Player` object for the entire game.
     * This method is crucial for updating the game state when a new character is created,
     * a game is loaded, or the player character needs to be refreshed across different panels.
     * It also propagates the `Player` instance to all relevant panels that need to
     * display or interact with the player's data.
     *
     * @param player The `Player` object to set as the current active player.
     * @see charcters.Player
     */
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
        System.out.println("DEBUG: Current Player set to: " + (player != null ? player.getName() : "null") +
                               ", HP: " + (player != null ? player.getCurrentHP() : "N/A") +
                               ", XP: " + (player != null ? player.getExperience() : "N/A") +
                               ", Gold: " + (player != null ? player.getGold() : "N/A"));

        // Propagate the player object to all panels that need it
        if (dungeon1Panel != null) {
            dungeon1Panel.setPlayer(player);
        }
        if (battlePanel != null) {
            battlePanel.setPlayer(player);
        }
        if (gameMenuPanel != null) {
            gameMenuPanel.setPlayer(player);
        }
        if (statsDisplayPanel != null) {
            statsDisplayPanel.setPlayer(player);
        }
        if (inventoryPanel != null) {
            inventoryPanel.setPlayer(player);
        }
        if (shopPanel != null) { // <--- ADDED PLAYER PROPAGATION TO SHOP PANEL
            shopPanel.setPlayer(player);
        }
        if (loopHandler != null) {
            loopHandler.setPlayer(player);
        }
    }

    /**
     * Retrieves the instance of the `StatsDisplayPanel`.
     * This allows other components to access the panel responsible for displaying
     * and interacting with the player's statistics.
     *
     * @return The `StatsDisplayPanel` instance.
     * @see panels.StatsDisplayPanel
     */
    public StatsDisplayPanel getStatsDisplayPanel() {
        return statsDisplayPanel;
    }

    /**
     * Retrieves the instance of the `Dungeon1Panel`.
     * This allows other components to access the panel that manages the first dungeon's
     * exploration and events.
     *
     * @return The `Dungeon1Panel` instance.
     * @see panels.Dungeon1Panel
     */
    public Dungeon1Panel getDungeon1Panel() {
        return dungeon1Panel;
    }

    /**
     * Retrieves the instance of the `InventoryPanel`.
     * This allows other components to access the panel where the player's
     * items and equipment are managed.
     *
     * @return The `InventoryPanel` instance.
     * @see panels.InventoryPanel
     */
    public InventoryPanel getInventoryPanel() {
        return inventoryPanel;
    }

    /**
     * Retrieves the instance of the `GameMenu` panel.
     * This provides access to the central hub panel from which players
     * navigate to various game features.
     *
     * @return The `GameMenu` instance.
     * @see panels.GameMenu
     */
    public GameMenu getGameMenuPanel() {
        return gameMenuPanel;
    }

    /**
     * Retrieves the instance of the `BattlePanel`.
     * This provides access to the panel where combat encounters are displayed and handled.
     *
     * @return The `BattlePanel` instance.
     * @see panels.BattlePanel
     */
    public BattlePanel getBattlePanel() {
        return battlePanel;
    }

    /**
     * Retrieves the instance of the `ShopPanel`.
     * This allows other components to access the panel where players can buy and sell items.
     *
     * @return The `ShopPanel` instance.
     * @see panels.ShopPanel
     */
    public ShopPanel getShopPanel() { // <--- ADDED THIS METHOD
        return shopPanel;
    }

    /**
     * Switches the current game screen to the `GameMenu` (the central game hub)
     * and sets the provided `Player` object as the current active player.
     * This method is typically called after a new character is created or a game is loaded,
     * transitioning the user into the main game interaction area.
     *
     * @param player The `Player` object to set as the current player before switching to the Game Menu.
     * @see #setCurrentPlayer(Player)
     * @see main.GamePanel#switchToScreen(String)
     */
    public void switchToGameHub(Player player) {
        this.setCurrentPlayer(player);
        gamePanel.switchToScreen("gameMenu");
        System.out.println("DEBUG: Switched to Game Menu (as new 'gameplay' hub) with player: " + player.getName());
    }

    /**
     * Switches the current game screen to the `GameMenu` (the central game hub)
     * using the already established `currentPlayer` or a `loadedPlayer` if available.
     * This overloaded method is useful when the player object is already set or
     * when resuming after a load operation where the player is transferred to `currentPlayer`.
     *
     * @see #setCurrentPlayer(Player)
     * @see main.GamePanel#switchToScreen(String)
     */
    public void switchToGameHub() {
        if (currentPlayer != null) {
            gamePanel.switchToScreen("gameMenu");
            System.out.println("DEBUG: Switched to Game Menu (as new 'gameplay' hub) with current player: " + currentPlayer.getName());
        } else if (loadedPlayer != null) {
            this.setCurrentPlayer(loadedPlayer); // Transfer loaded player to current player
            gamePanel.switchToScreen("gameMenu");
            System.out.println("DEBUG: Switched to Game Menu (as new 'gameplay' hub) with loaded player: " + loadedPlayer.getName());
            loadedPlayer = null; // Clear loaded player once used
        } else {
            System.out.println("ERROR: No player data available to switch to Game Hub.");
        }
    }

    /**
     * Transitions the game screen to the `SavedGames` panel.
     * Before switching, it ensures the save buttons on the `SavedGames` panel are
     * updated to reflect the latest save file status.
     *
     * @see panels.SavedGames#updateSaveButtons()
     * @see main.GamePanel#switchToScreen(String)
     */
    public void switchToSavedGames() {
        savedGamesPanel.updateSaveButtons();
        gamePanel.switchToScreen("savedGames");
        System.out.println("DEBUG: Switched to Saved Games screen.");
    }

    /**
     * Switches the current game screen to the `Options` panel.
     * It records the screen from which the user navigated to options, allowing for
     * a seamless return after settings are adjusted.
     *
     * @param fromScreen A `String` identifier for the screen from which this method was called (e.g., "menu", "gameMenu").
     * @see panels.Options#setPreviousScreen(String)
     * @see main.GamePanel#switchToScreen(String)
     */
    public void switchToOptions(String fromScreen) {
        previousScreen = fromScreen;
        optionsPanel.setPreviousScreen(fromScreen);
        gamePanel.switchToScreen("options");
        System.out.println("DEBUG: Switched to Options screen from: " + fromScreen);
    }

    /**
     * Switches the current game screen to the `GameMenu` and sets the provided `Player` object.
     * This is a specific utility method for transitioning to the game menu with a player.
     *
     * @param player The `Player` object to set as the current player.
     * @see #setCurrentPlayer(Player)
     * @see main.GamePanel#switchToScreen(String)
     */
    public void switchToGameMenu(Player player) {
        this.setCurrentPlayer(player);
        gamePanel.switchToScreen("gameMenu");
        System.out.println("DEBUG: Switched to Game Menu screen with player: " + player.getName());
    }

    /**
     * Retrieves the `Player` object that was previously loaded from a save file.
     * This method is typically called after `GameSaver.loadGame()` has populated `loadedPlayer`.
     *
     * @return The `Player` object that was loaded, or `null` if no player has been loaded.
     * @see util.GameSaver#loadGame(String)
     * @see charcters.Player
     */
    public Player getLoadedPlayer() {
        if (loadedPlayer == null) {
            System.out.println("DEBUG: getLoadedPlayer called, but loadedPlayer is null!");
        } else {
            System.out.println("DEBUG: getLoadedPlayer called, returning player " + loadedPlayer.getName() + " (HP=" + loadedPlayer.getCurrentHP() + ").");
        }
        return loadedPlayer;
    }

    /**
     * Saves the current game state for the given `Player` and then transitions
     * the game screen to the `GameMenu` (the central game hub).
     * This method combines saving and a screen switch for convenience.
     *
     * @param player The `Player` object whose data is to be saved.
     * @see #setCurrentPlayer(Player)
     * @see util.GameSaver#saveGame(Player)
     * @see #switchToGameHub(Player)
     */
    public void saveAndSwitchToGameHub(Player player) {
        this.setCurrentPlayer(player);
        GameSaver.saveGame(player);
        System.out.println("DEBUG: Game saved for player: " + player.getName());
        switchToGameHub(player);
    }

    /**
     * Saves a newly created character's initial state to a file.
     * This sets the new character as the current player and then persists their data.
     *
     * @param player The `Player` object representing the newly created character.
     * @see #setCurrentPlayer(Player)
     * @see util.GameSaver#saveGame(Player)
     */
    public void saveNewCharacter(Player player) {
        this.setCurrentPlayer(player);
        GameSaver.saveGame(player);
        System.out.println("DEBUG: New character '" + player.getName() + "' saved.");
    }

    /**
     * Switches the current game screen to the `BattlePanel` and sets up the combatants.
     * This method initiates a battle sequence, making the player and a specific enemy
     * ready for combat in the battle UI.
     *
     * @param player The `Player` object participating in the battle.
     * @param enemy The `Enemy` object the player will fight against.
     * @see panels.BattlePanel#setCombatants(Player, Enemy)
     * @see main.GamePanel#switchToScreen(String)
     */
    public void switchToBattle(Player player, Enemy enemy) {
        battlePanel.setCombatants(player, enemy);
        gamePanel.switchToScreen("battlePanel");
        System.out.println("DEBUG: Switched to Battle Panel with Player: " + player.getName() + " and Enemy: " + enemy.getName());
    }

    /**
     * Loads and initializes the game with a previously saved `Player` object.
     * This method is called after a game is loaded from the `SavedGames` panel.
     * It sets the loaded player as the current player and then transitions the UI
     * to the `GameMenu`, making the game playable.
     *
     * @param player The `Player` object loaded from a save file.
     * @see #setCurrentPlayer(Player)
     * @see main.GamePanel#switchToScreen(String)
     */
    public void loadAndInitializeGame(Player player) {
        System.out.println("DEBUG: loadAndInitializeGame started.");
        this.setCurrentPlayer(player);
        gamePanel.switchToScreen("gameMenu");
        System.out.println("DEBUG: Game initialized with loaded player: " + player.getName() + ". Switched to Game Menu.");
    }
}