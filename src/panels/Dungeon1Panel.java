package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import charcters.Player;
import charcters.Enemy; // Keep this import, though direct Enemy creation moves to LoopHandler
import charcters.Slime; // Keep for type checking if needed, but direct creation is less here
import charcters.Wolf; // Keep for type checking if needed, but direct creation is less here
import handlers.Handler; // Still needed for general game logic, but enemy creation shifts
import charcters.EnemyRarity; // Keep for enemy type definitions
import charcters.EnemyType;   // Keep for enemy type definitions
import main.GamePanel;
import java.util.Random; // Not directly used in this panel's logic anymore, but can keep
import handlers.LoopHandler; // --- NEW IMPORT ---

/**
 * The `Dungeon1Panel` class represents a game panel that serves as the entry point
 * to the first dungeon area. It allows the player to initiate fights against
 * different types of enemies (e.g., Slimes, Wolves) and navigate back to the game menu.
 * It now supports initiating single battles or multi-battle loops with custom difficulty.
 */
public class Dungeon1Panel extends JPanel implements ActionListener {

    private GamePanel screenManager; // Manages overall screen transitions
    private Player currentPlayer; // The player currently interacting with the dungeon

    // --- NEW: Loop Handler and UI Elements for Loop Control ---
    private LoopHandler loopHandler; // Reference to the LoopHandler to start battle loops
    private JLabel loopStatusLabel; // Label to display current battle loop status (e.g., "Battle Loop: 3/10")
    private JTextField numBattlesInput; // Text field for user to enter number of battles (1-100)
    // --- END NEW ---

    private JButton backButton; // Button to return to the game menu
    private JLabel dungeonLabel; // Label for the Slime dungeon area
    private JButton slimeFightButton; // Button to start a fight with a Slime
    private JLabel nextDungeonLabel; // Label for the Wolf dungeon area
    private JButton wolfFightButton; // Button to start a fight with a Wolf
    private Random random = new Random(); // Random object, possibly for future random enemy levels/attributes
    private Handler handler; // Handler for creating enemies (now used by LoopHandler)

    /**
     * Constructs a new `Dungeon1Panel`.
     * Initializes the panel's layout, sets its background, and creates UI components
     * for different enemy encounter areas, including labels and "Fight" buttons.
     * A "Back to Game Menu" button is also added.
     *
     * @param manager The `GamePanel` instance, used for screen switching.
     * @param handler The `Handler` instance, responsible for creating game entities like enemies.
     */
    public Dungeon1Panel(GamePanel manager, Handler handler) {
        this.screenManager = manager;
        this.handler = handler;
        // loopHandler will be set via setLoopHandler() from GameWindow

        // Main Dungeon1Panel background color
        setBackground(new Color(40, 40, 50)); // Dark background for the dungeon

        setLayout(new BorderLayout(10, 10)); // Uses BorderLayout for main panel organization, with gaps

        // --- NEW: Loop Status Label (at the very top) ---
        loopStatusLabel = new JLabel("No Battle Loop Active", SwingConstants.CENTER);
        loopStatusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        loopStatusLabel.setForeground(Color.CYAN);
        loopStatusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(loopStatusLabel, BorderLayout.NORTH); // Add to the top of the panel

        // --- Central Panel for Controls (holds enemy type buttons and loop input) ---
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS)); // Arranges components vertically
        controlPanel.setOpaque(false); // Make it transparent to show parent background
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Adds padding

        // --- NEW: Input for Number of Battles ---
        JPanel numBattlesInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        numBattlesInputPanel.setOpaque(false);
        numBattlesInputPanel.add(new JLabel("<html><font color='white'>Number of Battles (1-100):</font></html>"));
        numBattlesInput = new JTextField("1", 5); // Default to 1 battle, width 5
        numBattlesInput.setHorizontalAlignment(JTextField.CENTER);
        numBattlesInput.setFont(new Font("Arial", Font.PLAIN, 16));
        numBattlesInputPanel.add(numBattlesInput);
        controlPanel.add(numBattlesInputPanel);

        controlPanel.add(Box.createVerticalStrut(20)); // Spacing between input and buttons

        // --- Buttons for starting loops against specific enemies ---
        Font buttonFont = new Font("Arial", Font.BOLD, 18); // Define a common button font
        Color buttonBgColor = new Color(70, 130, 180); // SteelBlue
        Color buttonFgColor = Color.WHITE;

        // Slime Loop Button
        slimeFightButton = new JButton("Fight Slime (Loop)");
        slimeFightButton.setFont(buttonFont);
        slimeFightButton.setBackground(buttonBgColor);
        slimeFightButton.setForeground(buttonFgColor);
        slimeFightButton.setFocusPainted(false);
        slimeFightButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        slimeFightButton.addActionListener(this); // Register this panel as listener
        controlPanel.add(slimeFightButton);
        controlPanel.add(Box.createVerticalStrut(10)); // Spacing

        // Wolf Loop Button
        wolfFightButton = new JButton("Fight Wolf (Loop)");
        wolfFightButton.setFont(buttonFont);
        wolfFightButton.setBackground(buttonBgColor);
        wolfFightButton.setForeground(buttonFgColor);
        wolfFightButton.setFocusPainted(false);
        wolfFightButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        wolfFightButton.addActionListener(this); // Register this panel as listener
        controlPanel.add(wolfFightButton);
        controlPanel.add(Box.createVerticalGlue()); // Absorbs extra vertical space

        add(controlPanel, BorderLayout.CENTER); // Add the control panel to the center

        // --- Panel for the back button at the bottom ---
        backButton = new JButton("Back to Game Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(100, 100, 100)); // Dark gray
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(this); // Registers this panel as listener

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Centers the back button
        southPanel.setOpaque(false); // Make it transparent
        southPanel.add(backButton); // Add back button to its panel
        add(southPanel, BorderLayout.SOUTH); // Add button panel to the bottom (SOUTH) of the main Dungeon1Panel
    }

    /**
     * Sets the current `Player` object for this dungeon panel.
     * This player will be used when initiating fights.
     * A debug message is printed to the console when the player is set.
     *
     * @param player The `Player` instance that has entered this dungeon.
     */
    public void setPlayer(Player player) {
        this.currentPlayer = player;
        System.out.println("Player " + currentPlayer.getName() + " entered Dungeon 1.");
        // No repaint needed here as UI elements are not directly player-specific
    }

    /**
     * Sets the `LoopHandler` instance for this panel. This is crucial
     * for initiating battle loops.
     * @param loopHandler The `LoopHandler` instance.
     */
    public void setLoopHandler(LoopHandler loopHandler) {
        this.loopHandler = loopHandler;
    }

    /**
     * Updates the text of the battle loop status label.
     * @param text The status message to display.
     */
    public void updateLoopStatusLabel(String text) {
        loopStatusLabel.setText(text);
        // Ensure label is visible if it's not the "No Battle Loop Active" message
        loopStatusLabel.setVisible(!text.equals("No Battle Loop Active"));
    }


    /**
     * Handles action events generated by buttons in the dungeon panel.
     * It responds to clicks on the "Back to Game Menu" button, "Fight Slime (Loop)" button,
     * and "Fight Wolf (Loop)" button. For fight buttons, it parses the desired number of battles,
     * calculates the enemy stat multiplier, and then initiates a battle loop via the `LoopHandler`.
     *
     * @param e The `ActionEvent` triggered by a button click.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            System.out.println("Back to Game Menu button clicked from Dungeon 1"); // Debug message
            screenManager.getGameWindow().switchToGameMenu(currentPlayer); // Switches back to the game menu screen
        } else if (e.getSource() == slimeFightButton || e.getSource() == wolfFightButton) {
            if (currentPlayer == null) {
                System.out.println("Error: No player loaded to start the fight."); // Debug message
                JOptionPane.showMessageDialog(this, "No player loaded. Please load a game first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (loopHandler == null) {
                System.out.println("Error: LoopHandler not initialized.");
                JOptionPane.showMessageDialog(this, "Game system error: LoopHandler not ready.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            EnemyType enemyType;
            int tierStartingLevel;

            if (e.getSource() == slimeFightButton) {
                System.out.println("Fight Slime (Loop) button clicked");
                enemyType = EnemyType.SLIME;
                tierStartingLevel = 1; // Slimes always start at tier level 1
            } else { // wolfFightButton
                System.out.println("Fight Wolf (Loop) button clicked");
                enemyType = EnemyType.WOLF;
                tierStartingLevel = 6; // Wolves always start at tier level 6
            }

            try {
                int numBattles = Integer.parseInt(numBattlesInput.getText());
                if (numBattles < 1 || numBattles > 100) {
                    JOptionPane.showMessageDialog(this,
                                                  "Please enter a number between 1 and 100 for battles.",
                                                  "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Calculate stat multiplier based on numBattles
                double statMultiplier;
                if (numBattles == 1) {
                    statMultiplier = 1.0; // 0% boost for single battle
                } else if (numBattles >= 2 && numBattles <= 10) {
                    statMultiplier = 1.05; // 5% boost
                } else if (numBattles >= 11 && numBattles <= 20) {
                    statMultiplier = 1.10; // 10% boost
                } else if (numBattles >= 21 && numBattles <= 30) {
                    statMultiplier = 1.15; // 15% boost
                } else if (numBattles >= 31 && numBattles <= 40) {
                    statMultiplier = 1.20; // 20% boost
                } else if (numBattles >= 41 && numBattles <= 50) {
                    statMultiplier = 1.25; // 25% boost
                } else if (numBattles >= 51 && numBattles <= 60) {
                    statMultiplier = 1.30; // 30% boost
                } else if (numBattles >= 61 && numBattles <= 70) {
                    statMultiplier = 1.35; // 35% boost
                } else if (numBattles >= 71 && numBattles <= 80) {
                    statMultiplier = 1.40; // 40% boost
                } else if (numBattles >= 81 && numBattles <= 90) {
                    statMultiplier = 1.45; // 45% boost
                } else { // 91-100 battles
                    statMultiplier = 1.50; // 50% boost
                }

                // Start the battle loop via the LoopHandler
                loopHandler.startBattleLoop(numBattles, enemyType, tierStartingLevel, statMultiplier);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number (1-100) for battles.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}