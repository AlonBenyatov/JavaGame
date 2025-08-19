package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import main.GamePanel;
import main.GameWindow;
import charcters.Player;
import util.GameSaver;

/**
 * The `GameMenu` class represents the in-game menu panel, accessible during gameplay.
 * It provides options for the player to navigate to different game sections
 * like the dungeon, inventory, stats, shop, options, save the game, or exit.
 * It also includes a dynamic reminder button if the player has unallocated stat points.
 */
public class GameMenu extends JPanel implements ActionListener {

    /**
     * A central manager for handling screen transitions throughout the game.
     * This reference allows `GameMenu` to switch to other panels like Dungeon, Inventory, etc.
     */
    private GamePanel screenManager;
    /**
     * A `JLabel` displaying the title of this panel, typically "Game Menu".
     */
    private JLabel titleLabel;
    /**
     * A `JButton` that, when clicked, transitions the game to the dungeon exploration screen.
     */
    private JButton dungeonButton;
    /**
     * A `JButton` that, when clicked, transitions the game to the player's inventory management screen.
     */
    private JButton inventoryButton;
    /**
     * A `JButton` that, when clicked, transitions the game to the player's character statistics display and allocation screen.
     */
    private JButton statsButton;
    /**
     * A `JButton` that, when clicked, transitions the game to the shop interface where players can buy and sell items.
     */
    private JButton shopButton;
    /**
     * A `JButton` that, when clicked, transitions the game to the options/settings panel.
     */
    private JButton optionsButton;
    /**
     * A `JButton` that, when clicked, initiates the game saving process for the current player.
     */
    private JButton saveGameButton;
    /**
     * A `JButton` that serves as a dynamic reminder. It becomes visible and clickable
     * when the player has unallocated stat points, prompting them to visit the stats screen.
     */
    private JButton spendPointsReminderButton;
    /**
     * Stores the name of the screen from which this menu was last accessed.
     * While declared, its primary use case (for a "back" button) isn't directly
     * implemented in this specific `GameMenu` (which usually doesn't have a 'back'
     * in the same way Options or Inventory might, as it's a central hub).
     */
    private String previousScreen;
    /**
     * A direct reference to the main `GameWindow`. This allows for accessing
     * specific panels within the `GameWindow` (e.g., `getDungeon1Panel()`)
     * to pass player data or trigger specific panel-level updates before switching.
     */
    private GameWindow gameWindow;
    /**
     * The `Player` instance currently active in the game. This object holds
     * all the player's data (stats, inventory, equipped items, etc.) and is
     * essential for performing game actions and updating UI elements based on player state.
     */
    private Player currentPlayer;
    /**
     * A `JButton` that, when clicked, prompts the user to confirm exiting the application.
     * It typically triggers an auto-save before terminating the program.
     */
    private JButton exitButton;

    /**
     * Constructs a new `GameMenu` panel.
     * Initializes the layout, sets background color, creates and styles all menu buttons.
     * It also sets up the reminder button for unallocated stat points, initially hidden.
     * Action listeners are added to all buttons.
     *
     * @param manager The `GamePanel` instance, which is central for screen management.
     */
    public GameMenu(GamePanel manager) {
        this.screenManager = manager;
        this.gameWindow = manager.getGameWindow(); // Gets the GameWindow instance from GamePanel
        setLayout(new BorderLayout()); // Uses BorderLayout for overall structure
        setBackground(Color.DARK_GRAY); // Sets the background color of the panel

        // --- Title Label ---
        titleLabel = new JLabel("Game Menu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH); // Adds title to the top

        // --- Center Panel for Buttons ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); // Arranges buttons vertically
        centerPanel.setOpaque(false); // Makes it transparent to show parent background
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100)); // Adds padding

        Dimension buttonSize = new Dimension(200, 40); // Standard size for all buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 16); // Standard font for all buttons

        // Create and style each primary menu button
        dungeonButton = createStyledButton("Go Inside Dungeon", buttonSize, buttonFont);
        inventoryButton = createStyledButton("Go to Inventory", buttonSize, buttonFont);
        statsButton = createStyledButton("Display Stats", buttonSize, buttonFont);
        shopButton = createStyledButton("Shop", buttonSize, buttonFont);
        optionsButton = createStyledButton("Options", buttonSize, buttonFont);
        saveGameButton = createStyledButton("Save Game", buttonSize, buttonFont);
        exitButton = createStyledButton("Exit Game", buttonSize, buttonFont);
        exitButton.setBackground(new Color(178, 34, 34)); // Special red background for exit button

        // Add buttons to the center panel with vertical spacing
        centerPanel.add(Box.createVerticalGlue()); // Pushes buttons to center vertically
        centerPanel.add(dungeonButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        centerPanel.add(inventoryButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(statsButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(shopButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(optionsButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(saveGameButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Larger spacer before exit
        centerPanel.add(exitButton);
        centerPanel.add(Box.createVerticalGlue()); // Pushes buttons to center vertically

        // --- Spend Points Reminder Button (initially hidden) ---
        spendPointsReminderButton = createStyledButton("Spend Points (0 remaining)!", buttonSize, buttonFont);
        spendPointsReminderButton.setBackground(new Color(200, 100, 0)); // Orange color
        spendPointsReminderButton.setVisible(false); // Hidden by default
        spendPointsReminderButton.addActionListener(this); // Attaches action listener

        JPanel topOfCenterPanel = new JPanel(); // Panel to hold the reminder button
        topOfCenterPanel.setOpaque(false);
        topOfCenterPanel.setLayout(new BoxLayout(topOfCenterPanel, BoxLayout.Y_AXIS));
        topOfCenterPanel.add(spendPointsReminderButton);
        topOfCenterPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer below reminder

        // Combine the reminder button panel and the main button panel
        JPanel combinedCenter = new JPanel(new BorderLayout());
        combinedCenter.setOpaque(false);
        combinedCenter.add(topOfCenterPanel, BorderLayout.NORTH); // Reminder button at top of combined center
        combinedCenter.add(centerPanel, BorderLayout.CENTER); // Main buttons below it

        add(combinedCenter, BorderLayout.CENTER); // Add the combined panel to the main GameMenu

        // Add ActionListeners to all main buttons
        dungeonButton.addActionListener(this);
        inventoryButton.addActionListener(this);
        statsButton.addActionListener(this);
        shopButton.addActionListener(this);
        optionsButton.addActionListener(this);
        saveGameButton.addActionListener(this);
        exitButton.addActionListener(this);

        previousScreen = "menu"; // Default previous screen, though not strictly used for "back" action here
    }

    /**
     * Helper method to create consistently styled JButtons.
     * Sets alignment, preferred size, font, background, foreground, and disables focus painting.
     *
     * @param text The text to display on the button.
     * @param size The preferred `Dimension` for the button.
     * @param font The `Font` for the button's text.
     * @return A new, styled `JButton` instance.
     */
    private JButton createStyledButton(String text, Dimension size, Font font) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align within BoxLayout
        button.setMaximumSize(size); // Sets max size to prevent stretching
        button.setMinimumSize(size); // Sets min size
        button.setPreferredSize(size); // Sets preferred size
        button.setFont(font);
        button.setBackground(new Color(70, 70, 70)); // Dark gray background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false); // No border painted when focused
        return button;
    }

    /**
     * Sets the name of the previous screen. This can be used for navigation
     * purposes if the "back" logic were implemented differently.
     *
     * @param screen A `String` representing the name of the previous screen.
     */
    public void setPreviousScreen(String screen) {
        this.previousScreen = screen;
    }

    /**
     * Sets the current player for this game menu. This is important for
     * displaying player-specific information and enabling actions.
     * It also triggers an update of the "Spend Points" reminder button.
     *
     * @param player The `Player` object currently active in the game.
     */
    public void setPlayer(Player player) {
        this.currentPlayer = player;
        updateReminderButton(); // Updates the reminder button based on the new player's stats
    }

    /**
     * Updates the visibility, enabled state, and text of the
     * "Spend Points" reminder button based on the `currentPlayer`'s
     * unallocated stat points. The button is visible and enabled only
     * if the player has points to spend.
     */
    public void updateReminderButton() {
        if (currentPlayer != null && currentPlayer.getUnallocatedStatPoints() > 0) {
            spendPointsReminderButton.setText("Spend Points (" + currentPlayer.getUnallocatedStatPoints() + " remaining)!");
            spendPointsReminderButton.setVisible(true);
            spendPointsReminderButton.setEnabled(true);
        } else {
            spendPointsReminderButton.setVisible(false);
            spendPointsReminderButton.setEnabled(false);
        }
    }

    /**
     * Handles action events generated by the buttons in the game menu.
     * This method determines which button was clicked and performs the corresponding
     * action, such as switching to another game screen, saving the game, or exiting.
     * It includes checks for a valid `currentPlayer` before performing actions that require one.
     *
     * @param e The `ActionEvent` triggered by a button click.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == dungeonButton) {
            System.out.println("Go Inside Dungeon"); // Debug message
            if (this.currentPlayer != null) {
                gameWindow.getDungeon1Panel().setPlayer(this.currentPlayer); // Passes player to Dungeon panel
                screenManager.switchToScreen("dungeon1"); // Switches to dungeon screen
            } else {
                JOptionPane.showMessageDialog(this, "Please load or create a character first!", "No Character", JOptionPane.WARNING_MESSAGE);
                System.out.println("Error: No player to enter the dungeon with."); // Debug message
            }
        } else if (e.getSource() == inventoryButton) {
            System.out.println("Go to Inventory"); // Debug message
            if (this.currentPlayer != null) {
                // The InventoryPanel needs to be updated with the current player before switching.
                // Assuming GameWindow has a method like getInventoryPanel() that returns the panel instance.
                gameWindow.getInventoryPanel().setPlayer(this.currentPlayer);
                screenManager.switchToScreen("inventory"); // Switches to inventory screen
            } else {
                JOptionPane.showMessageDialog(this, "No character data to view inventory.", "No Character", JOptionPane.WARNING_MESSAGE);
                System.out.println("Error: No player for inventory."); // Debug message
            }
        } else if (e.getSource() == statsButton || e.getSource() == spendPointsReminderButton) {
            System.out.println("Display Stats / Spend Points"); // Debug message
            if (this.currentPlayer != null) {
                gameWindow.getStatsDisplayPanel().setPlayer(this.currentPlayer); // Passes player to StatsDisplay panel
                screenManager.switchToScreen("statsDisplay"); // Switches to stats display screen
            } else {
                JOptionPane.showMessageDialog(this, "No character data to display stats.", "No Character", JOptionPane.WARNING_MESSAGE);
                System.out.println("Error: No player data to display stats."); // Debug message
            }
        } else if (e.getSource() == shopButton) {
            System.out.println("Shop"); // Debug message
            if (this.currentPlayer != null) {
                // The ShopPanel needs to be updated with the current player before switching.
                // Assuming GameWindow has a method like getShopPanel() that returns the panel instance.
                gameWindow.getShopPanel().setPlayer(this.currentPlayer);
                screenManager.switchToScreen("shop"); // Switches to shop screen
            } else {
                JOptionPane.showMessageDialog(this, "No character data for shop.", "No Character", JOptionPane.WARNING_MESSAGE);
                System.out.println("Error: No player for shop.");
            }
        } else if (e.getSource() == optionsButton) {
            System.out.println("Options"); // Debug message
            if (gameWindow != null) {
                // Switches to the options panel, setting "gameMenu" as the screen to return to
                gameWindow.switchToOptions("gameMenu");
            }
        } else if (e.getSource() == saveGameButton) {
            System.out.println("Save Game button clicked"); // Debug message
            saveGame(); // Calls the private method to handle saving
        } else if (e.getSource() == exitButton) {
            System.out.println("Exit Game button clicked. Exiting application."); // Debug message
            // Optionally auto-save before exiting
            if (currentPlayer != null) {
                GameSaver.autoSave(currentPlayer); // Performs an automatic save on exit
            }
            System.exit(0); // Terminates the application
        }
        updateReminderButton(); // Always update reminder button state after an action, as player stats might change
    }

    /**
     * Initiates the game saving process.
     * It checks if a `currentPlayer` exists and, if so, calls `GameSaver.saveGame`
     * to persist the player's data. A confirmation dialog is shown to the user.
     * If no player is active, an error message is displayed.
     */
    private void saveGame() {
        if (currentPlayer != null) {
            GameSaver.saveGame(currentPlayer); // Saves the current player's data
            JOptionPane.showMessageDialog(this, "Game saved for " + currentPlayer.getName() + "!", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No character data to save.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}