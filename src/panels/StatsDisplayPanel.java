package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import charcters.Player;
import main.GamePanel;

/**
 * The `StatsDisplayPanel` class represents a JPanel responsible for displaying a player's
 * statistics and providing an interface for allocating unspent stat points. It allows
 * players to view their current attributes, apply points to various stats, and confirm
 * these changes, which are then permanently applied to the `Player` object.
 *
 * This panel integrates with the `GamePanel` to switch screens and updates the
 * `GameMenu`'s stat points reminder.
 */
public class StatsDisplayPanel extends JPanel implements ActionListener {

    private Player currentPlayer; // Holds the currently loaded player character whose stats are displayed and modified.
    private GamePanel screenManager; // Manages screen transitions and provides access to other panels in the game.

    private JLabel nameLabel; // Displays the player's name or a general title for the panel.
    private JTextArea statsArea; // Displays the detailed current statistics of the player in a scrollable text area.
    private JButton backButton; // Button to return to the main game menu.

    // --- UI elements for Stat Allocation ---
    private JLabel levelUpReminderLabel; // Informs the player if they have unspent stat points.
    private JLabel pointsRemainingLabel; // Shows the number of stat points currently available for allocation within this session.

    private JPanel statAllocationPanel; // A container panel that holds all the UI elements related to stat point allocation.
    private final String[] STAT_NAMES = {"Strength", "Dexterity", "Intelligence", "Luck", "Constitution", "Charisma"}; // Array of strings holding the names of the core player stats.

    // Temporary allocation data for the current session
    private Map<Integer, Integer> tempStatIncreases; // A map storing temporary stat point additions: maps stat index (1-6) to the number of points added in the current session.
    private int tempUnallocatedPoints; // The number of stat points currently available to spend within the active allocation session.
    private int initialUnallocatedPoints; // Stores the player's unallocated points when this panel was first opened, used to track changes.

    private JButton confirmStatsButton; // Button to permanently apply all temporary stat allocations to the player.

    // UI elements for each stat row, allowing individual stat manipulation
    private JLabel[] statValueLabels;    // Array of labels displaying the base numerical value of each stat.
    private JLabel[] tempIncreaseLabels; // Array of labels showing the temporary increase for each stat (e.g., "(+X)").
    private JButton[] plusButtons;        // Array of buttons, each allowing the addition of one point to a corresponding stat.
    private JButton[] minusButtons;       // Array of buttons, each allowing the subtraction of one point from a corresponding stat.
    private JTextField[] inputFields;    // Array of text fields for direct numerical input of multiple points for each stat.
    private JButton[] applyButtons;       // Array of buttons, each applying the points entered in the corresponding input field.

    /**
     * Constructs a new `StatsDisplayPanel`.
     * Initializes the UI components for displaying player statistics and for
     * facilitating stat point allocation. It sets up the layout, colors, fonts,
     * and attaches action listeners to interactive elements.
     *
     * @param manager The `GamePanel` instance, used for screen switching and
     * accessing other game panels.
     */
    public StatsDisplayPanel(GamePanel manager) {
        this.screenManager = manager;
        setLayout(new BorderLayout(10, 10)); // Uses BorderLayout with padding
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adds empty border for spacing
        setBackground(Color.DARK_GRAY); // Sets panel background color

        // North: Player Name / Title
        nameLabel = new JLabel("Player Stats");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(nameLabel, BorderLayout.NORTH);

        // Center: Main content area (stats display + allocation UI)
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false); // Makes panel transparent

        // Stats Display Area (JTextArea)
        statsArea = new JTextArea(10, 30);
        statsArea.setEditable(false); // Prevents user editing
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        statsArea.setBackground(Color.BLACK);
        statsArea.setForeground(Color.WHITE);
        mainContentPanel.add(new JScrollPane(statsArea)); // Adds scrollability to stats display
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer between sections

        // --- Stat Allocation UI Setup ---
        statAllocationPanel = new JPanel();
        statAllocationPanel.setLayout(new BoxLayout(statAllocationPanel, BoxLayout.Y_AXIS));
        statAllocationPanel.setOpaque(false);
        statAllocationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE),"Allocate Points",
                                                                         javax.swing.border.TitledBorder.CENTER,
                                                                         javax.swing.border.TitledBorder.TOP,
                                                                         new Font("Arial", Font.BOLD, 18),
                                                                         Color.YELLOW));

        levelUpReminderLabel = new JLabel(" "); // Initially empty, visibility controlled by points
        levelUpReminderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        levelUpReminderLabel.setForeground(Color.CYAN);
        levelUpReminderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statAllocationPanel.add(levelUpReminderLabel);
        statAllocationPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        pointsRemainingLabel = new JLabel("Points Remaining: 0"); // Displays temporary remaining points
        pointsRemainingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pointsRemainingLabel.setForeground(Color.ORANGE);
        pointsRemainingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statAllocationPanel.add(pointsRemainingLabel);
        statAllocationPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Initialize arrays for stat UI elements
        statValueLabels = new JLabel[STAT_NAMES.length];
        tempIncreaseLabels = new JLabel[STAT_NAMES.length];
        plusButtons = new JButton[STAT_NAMES.length];
        minusButtons = new JButton[STAT_NAMES.length];
        inputFields = new JTextField[STAT_NAMES.length];
        applyButtons = new JButton[STAT_NAMES.length];

        // Loop to create UI for each individual stat (Strength, Dexterity, etc.)
        for (int i = 0; i < STAT_NAMES.length; i++) {
            final int statIndex = i + 1; // Stats are 1-indexed in Player methods (e.g., getStat(1) for Strength)
            JPanel statRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            statRowPanel.setOpaque(false);
            statRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel name = new JLabel(STAT_NAMES[i] + ": ");
            name.setForeground(Color.WHITE);
            name.setFont(new Font("Arial", Font.PLAIN, 14));
            statRowPanel.add(name);

            statValueLabels[i] = new JLabel("0"); // Placeholder for base stat value
            statValueLabels[i].setForeground(Color.WHITE);
            statValueLabels[i].setFont(new Font("Arial", Font.BOLD, 14));
            statRowPanel.add(statValueLabels[i]);

            tempIncreaseLabels[i] = new JLabel(" (+0)"); // Placeholder for temporary increase
            tempIncreaseLabels[i].setForeground(Color.GREEN);
            tempIncreaseLabels[i].setFont(new Font("Arial", Font.PLAIN, 12));
            statRowPanel.add(tempIncreaseLabels[i]);

            statRowPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Spacer

            // Minus Button for decrementing stat points
            minusButtons[i] = new JButton("-");
            minusButtons[i].setFont(new Font("Arial", Font.BOLD, 12));
            minusButtons[i].setPreferredSize(new Dimension(30, 25));
            minusButtons[i].setMargin(new Insets(0,0,0,0));
            minusButtons[i].addActionListener(e -> handleStatChange(statIndex, -1)); // Lambda for action listener
            statRowPanel.add(minusButtons[i]);

            // Plus Button for incrementing stat points
            plusButtons[i] = new JButton("+");
            plusButtons[i].setFont(new Font("Arial", Font.BOLD, 12));
            plusButtons[i].setPreferredSize(new Dimension(30, 25));
            plusButtons[i].setMargin(new Insets(0,0,0,0));
            plusButtons[i].addActionListener(e -> handleStatChange(statIndex, 1)); // Lambda for action listener
            statRowPanel.add(plusButtons[i]);

            statRowPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Spacer

            // Text Field for direct numerical input of points
            inputFields[i] = new JTextField(4);
            inputFields[i].setFont(new Font("Arial", Font.PLAIN, 12));
            inputFields[i].setHorizontalAlignment(JTextField.CENTER);
            statRowPanel.add(inputFields[i]);

            // Apply Button for points from input field
            applyButtons[i] = new JButton("Apply");
            applyButtons[i].setFont(new Font("Arial", Font.BOLD, 12));
            applyButtons[i].setPreferredSize(new Dimension(60, 25));
            applyButtons[i].setMargin(new Insets(0,0,0,0));
            // Lambda calls helper method with statIndex and text from corresponding input field
            applyButtons[i].addActionListener(e -> handleApplyInput(statIndex, inputFields[statIndex-1].getText()));
            statRowPanel.add(applyButtons[i]);

            statAllocationPanel.add(statRowPanel); // Adds the fully constructed stat row to the allocation panel
        }

        mainContentPanel.add(statAllocationPanel); // Adds the allocation panel to the main content area

        // Confirm Stats Button
        confirmStatsButton = new JButton("Confirm Stats");
        confirmStatsButton.setFont(new Font("Arial", Font.BOLD, 18));
        confirmStatsButton.setBackground(new Color(50, 150, 50)); // Greenish background for confirmation
        confirmStatsButton.setForeground(Color.WHITE);
        confirmStatsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmStatsButton.addActionListener(e -> confirmAllocations()); // Lambda for confirmation action
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        mainContentPanel.add(confirmStatsButton);
        // --- END Stat Allocation UI ---

        add(mainContentPanel, BorderLayout.CENTER); // Adds the main content panel to the center of the `StatsDisplayPanel`

        // South: Back Button
        backButton = new JButton("Back to Game Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.addActionListener(this); // Registers this panel as the ActionListener for the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initialize temporary allocation map
        tempStatIncreases = new HashMap<>();
        updateDisplay(); // Initial display update to reflect current state (usually no player yet)
    }

    /**
     * Handles actions triggered by the '+' and '-' buttons for stat allocation.
     * It increments or decrements the temporary points allocated to a specific stat
     * and updates the `tempUnallocatedPoints` accordingly.
     *
     * Displays a warning message if no points are left to allocate.
     *
     * @param statIndex The 1-indexed identifier of the stat to change (e.g., 1 for Strength).
     * @param change The amount to change the stat by (typically +1 or -1).
     */
    private void handleStatChange(int statIndex, int change) {
        if (currentPlayer == null) return; // Prevents actions if no player is loaded.

        int currentTempIncrease = tempStatIncreases.getOrDefault(statIndex, 0);

        if (change > 0) { // Logic for adding points
            if (tempUnallocatedPoints > 0) {
                tempUnallocatedPoints--;
                tempStatIncreases.put(statIndex, currentTempIncrease + 1);
            } else {
                JOptionPane.showMessageDialog(this, "No stat points left to allocate!", "No Points", JOptionPane.INFORMATION_MESSAGE);
            }
        } else { // Logic for subtracting points
            if (currentTempIncrease > 0) { // Can only subtract if points were temporarily added to this stat
                tempUnallocatedPoints++;
                tempStatIncreases.put(statIndex, currentTempIncrease - 1);
            }
            // If currentTempIncrease is 0, no action is taken as cannot subtract from base.
        }
        updateDisplay(); // Refreshes the UI to show changes
    }

    /**
     * Handles actions triggered by the "Apply" buttons next to the text input fields.
     * It attempts to parse the input text as an integer representing points to add
     * to a specific stat. It validates the input and ensures sufficient unallocated
     * points are available before applying the changes temporarily.
     *
     * Displays warning messages for invalid input or insufficient points.
     *
     * @param statIndex The 1-indexed identifier of the stat to apply points to.
     * @param inputText The string content from the input `JTextField`.
     */
    private void handleApplyInput(int statIndex, String inputText) {
        if (currentPlayer == null) return; // Prevents actions if no player is loaded.

        try {
            int pointsToAdd = Integer.parseInt(inputText.trim()); // Parses input to integer
            if (pointsToAdd <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (pointsToAdd <= tempUnallocatedPoints) { // Checks if enough temporary points are available
                tempUnallocatedPoints -= pointsToAdd;
                tempStatIncreases.put(statIndex, tempStatIncreases.getOrDefault(statIndex, 0) + pointsToAdd);
                inputFields[statIndex-1].setText(""); // Clears the input field after successful application
            } else {
                JOptionPane.showMessageDialog(this, "You only have " + tempUnallocatedPoints + " points left.", "Not Enough Points", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
        updateDisplay(); // Refreshes the UI to show changes
    }

    /**
     * Confirms and applies the temporary stat allocations to the actual `Player` object.
     * This method is invoked when the "Confirm Stats" button is pressed.
     *
     * It iterates through all temporarily increased stats and uses the `Player`'s
     * `addStatPoint` method to permanently apply them. It then deducts the total
     * allocated points from the player's unallocated pool.
     *
     * If the player had points but chose not to spend any, a confirmation dialog
     * is shown. After confirmation, the temporary state is reset, the UI is updated,
     * the game menu's reminder button is refreshed, and the screen switches back to the game menu.
     */
    private void confirmAllocations() {
        if (currentPlayer == null) {
            JOptionPane.showMessageDialog(this, "No player data to confirm stats for.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculates how many points were actually allocated in this session.
        int totalPointsAllocatedInSession = initialUnallocatedPoints - tempUnallocatedPoints;

        // Prompts user if they had points but didn't allocate any during this session.
        if (totalPointsAllocatedInSession == 0 && initialUnallocatedPoints > 0) {
            int response = JOptionPane.showConfirmDialog(this,
                "You have " + initialUnallocatedPoints + " unallocated points. Are you sure you want to confirm without spending them?",
                "Unspent Points", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
                return; // Stays on the panel if user chooses not to confirm.
            }
        }

        // Applies temporary increases to the actual Player object's base stats.
        for (Map.Entry<Integer, Integer> entry : tempStatIncreases.entrySet()) {
            int statIndex = entry.getKey();
            int amount = entry.getValue();
            if (amount > 0) {
                currentPlayer.addStatPoint(statIndex, amount); // Uses Player's internal method to update stat.
            }
        }
        // in case player gets some constitution points
        currentPlayer.setCurrentHP(currentPlayer.getMaxHP());
        
        // Deducts the total points spent from the Player's unallocated stat points.
        currentPlayer.deductUnallocatedStatPoints(totalPointsAllocatedInSession);

        // Resets the temporary allocation state for the next time the panel is opened.
        tempStatIncreases.clear();
        tempUnallocatedPoints = 0;
        initialUnallocatedPoints = 0;

        JOptionPane.showMessageDialog(this, "Stats confirmed and applied!", "Success", JOptionPane.INFORMATION_MESSAGE);
        updateDisplay(); // Refreshes the display to show the permanently updated stats and clear allocation UI.
        
        util.GameSaver.saveGame(currentPlayer); // Save the player's updated stats to file
        System.out.println("DEBUG: Player stats saved after allocation confirmation for " + currentPlayer.getName());
        // Updates the stat points reminder button on the GameMenu to reflect any changes.
        screenManager.getGameWindow().getGameMenuPanel().updateReminderButton();
        screenManager.switchToScreen("gameMenu"); // Navigates back to the Game Menu.
    }

    /**
     * Sets the current `Player` object for this panel. This method is crucial
     * for initializing the panel's display with the correct player data and
     * setting up the temporary stat allocation state.
     *
     * When a player is set, the panel's temporary unallocated points and
     * temporary stat increases are reset, reflecting the player's actual
     * unallocated points upon entry to this screen.
     *
     * @param player The `Player` object to display stats for and allow allocation.
     */
    public void setPlayer(Player player) {
        this.currentPlayer = player;
        System.out.println("StatsDisplayPanel: Player set to: " + (player != null ? player.getName() : "null")); // Debug message

        // Initializes temporary allocation state based on the current player's unallocated points.
        if (this.currentPlayer != null) {
            this.initialUnallocatedPoints = currentPlayer.getUnallocatedStatPoints();
            this.tempUnallocatedPoints = this.initialUnallocatedPoints;
            this.tempStatIncreases.clear(); // Clears any temporary allocations from a previous session.
        } else {
            // Resets state if no player is provided (e.g., after character creation or loading).
            this.initialUnallocatedPoints = 0;
            this.tempUnallocatedPoints = 0;
            this.tempStatIncreases.clear();
        }
        updateDisplay(); // Updates the UI to reflect the newly set player's stats and allocation state.
    }

    /**
     * Updates the display of player statistics. This method is kept for
     * potential external calls, but internally it delegates to `setPlayer`
     * to ensure proper initialization of allocation logic.
     *
     * @param player The `Player` object whose statistics should be updated on the display.
     */
    public void updateStats(Player player) {
        setPlayer(player); // Delegates to setPlayer to ensure proper initialization of allocation logic.
    }

    /**
     * Refreshes all display elements on the panel, including the main stats area,
     * the level-up reminder, points remaining, and the individual stat allocation rows.
     * It updates stat values, temporary increases, and enables/disables allocation buttons
     * based on the current `currentPlayer` and `tempUnallocatedPoints`.
     *
     * If no player is currently set, the panel displays a "No Player Data" message
     * and hides/disables the allocation UI.
     */
    private void updateDisplay() {
        if (currentPlayer != null) {
            nameLabel.setText("Stats for: " + currentPlayer.getName());
            statsArea.setText(getPlayerStatsString(currentPlayer));

            // Update allocation UI visibility and state based on player's actual points.
            int pointsOnPlayer = currentPlayer.getUnallocatedStatPoints();
            if (pointsOnPlayer > 0) {
                levelUpReminderLabel.setText("You have " + pointsOnPlayer + " points to spend!");
                levelUpReminderLabel.setVisible(true);
            } else {
                levelUpReminderLabel.setVisible(false);
            }

            // Displays points remaining for the current allocation session.
            pointsRemainingLabel.setText("Points Remaining: " + tempUnallocatedPoints);

            // Updates each individual stat row's display and button states.
            for (int i = 0; i < STAT_NAMES.length; i++) {
                int statIndex = i + 1;
                int baseStatValue = getPlayerStatValue(currentPlayer, statIndex); // Actual stat value.
                int tempIncrease = tempStatIncreases.getOrDefault(statIndex, 0); // Temporary points added.

                statValueLabels[i].setText(String.valueOf(baseStatValue + tempIncrease)); // Shows effective stat.
                tempIncreaseLabels[i].setText(" (+" + tempIncrease + ")"); // Shows temporary increase.
                tempIncreaseLabels[i].setVisible(tempIncrease > 0); // Hides if no temporary increase.

                // Enables/disables buttons based on point availability and temporary allocation.
                plusButtons[i].setEnabled(tempUnallocatedPoints > 0);
                minusButtons[i].setEnabled(tempIncrease > 0);
                inputFields[i].setEnabled(tempUnallocatedPoints > 0);
                applyButtons[i].setEnabled(tempUnallocatedPoints > 0);
            }

            // The "Confirm Stats" button is enabled only if there are pending temporary changes.
            confirmStatsButton.setEnabled(initialUnallocatedPoints != tempUnallocatedPoints);
            statAllocationPanel.setVisible(true); // Shows allocation panel if a player is set.
        } else {
            // Resets display and hides/disables allocation UI if no player is loaded.
            nameLabel.setText("No Player Data");
            statsArea.setText("Create a character to view stats.");
            levelUpReminderLabel.setVisible(false);
            pointsRemainingLabel.setText("Points Remaining: 0");
            for (int i = 0; i < STAT_NAMES.length; i++) {
                statValueLabels[i].setText("0");
                tempIncreaseLabels[i].setText(" (+0)");
                tempIncreaseLabels[i].setVisible(false);
                plusButtons[i].setEnabled(false);
                minusButtons[i].setEnabled(false);
                inputFields[i].setEnabled(false);
                applyButtons[i].setEnabled(false);
            }
            confirmStatsButton.setEnabled(false);
            statAllocationPanel.setVisible(false); // Hides allocation panel.
        }
        revalidate(); // Re-lays out the components
        repaint();    // Repaints the panel
    }

    /**
     * A helper method to retrieve the numerical value of a specific stat
     * from the `Player` object based on its 1-indexed identifier.
     *
     * @param player The `Player` object from which to get the stat value.
     * @param statIndex The 1-indexed identifier of the stat (1 for Strength, 2 for Dexterity, etc.).
     * @return The integer value of the requested stat. Returns 0 for an invalid stat index.
     */
    private int getPlayerStatValue(Player player, int statIndex) {
        switch (statIndex) {
            case 1: return player.getStrength();
            case 2: return player.getDexterity();
            case 3: return player.getIntelligence();
            case 4: return player.getLuck();
            case 5: return player.getConstitution();
            case 6: return player.getCharisma(); // Note: Method name 'getcharisma' might be a typo, usually 'getCharisma'.
            default: return 0;
        }
    }

    /**
     * Generates a formatted string containing the current player's detailed statistics.
     * This string includes core attributes (Name, Level, XP), derived stats (HP, Attack Damage),
     * and base stats (Strength, Dexterity, etc.).
     *
     * This method was likely reused from a previous `LevelUpPanel` for consistency
     * in displaying player information.
     *
     * @param player The `Player` object whose stats are to be formatted.
     * @return A `String` containing the player's formatted statistics, suitable for display in a `JTextArea`.
     */
    private String getPlayerStatsString(Player player) {
        // This string will show the *actual* player stats, reflecting any permanently applied changes,
        // but not temporary allocations from the current session that haven't been confirmed yet.
        return "Name: " + player.getName() + "\n"
                + "Level: " + player.getLevel() + "\n"
                + "XP: " + player.getExperience() + "/" + player.getExpToNextLevel() + "\n"
                + "--------------------\n"
                + "HP: " + player.getCurrentHP() + "/" + player.getMaxHP() + "\n"
                + "Strength: " + player.getStrength() + "\n"
                + "Dexterity: " + player.getDexterity() + "\n"
                + "Intelligence: " + player.getIntelligence() + "\n"
                + "Luck: " + player.getLuck() + "\n"
                + "Constitution: " + player.getConstitution() + "\n"
                + "Charisma: " + player.getCharisma() + "\n" // Consider renaming getcharisma() to getCharisma() in Player class.
                + "--------------------\n"
                + "Gold: " + player.getGold() + "\n"
                + "Armor: " + player.getArmor() + "\n"
                + "Dodge: " + String.format("%.2f", player.getDodge()) + "\n"
                + "Attack Speed: " + String.format("%.2f", player.getAttackSpeed()) + "\n"
                + "Parry: " + String.format("%.2f", player.getParry()) + "\n"
                + "Attack Damage: " + player.getAttackDmg();
    }

    /**
     * Implements the `ActionListener` interface to handle events, specifically
     * the "Back to Game Menu" button click.
     *
     * If the player has unconfirmed stat changes or unallocated points, it prompts
     * the user with a warning dialog before returning to the game menu.
     * Unconfirmed changes will be lost if the user proceeds.
     *
     * @param e The `ActionEvent` triggered by a UI component.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            System.out.println("Back to Game Menu button clicked from StatsDisplayPanel"); // Debug message
            // Checks if the allocation state has changed (i.e., points were allocated but not confirmed).
            if (currentPlayer != null && (initialUnallocatedPoints != tempUnallocatedPoints)) {
                int response = JOptionPane.showConfirmDialog(this,
                    "You have unconfirmed stat changes or unallocated points.\n" +
                    "Are you sure you want to return to the menu? Unconfirmed changes will be lost.",
                    "Unsaved Changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.NO_OPTION) {
                    return; // Stays on the panel if user chooses not to return.
                }
            }
            // Updates the reminder button on the GameMenu (in case points were spent and confirmed previously).
            screenManager.getGameWindow().getGameMenuPanel().updateReminderButton();
            screenManager.switchToScreen("gameMenu"); // Switches back to the Game Menu.
        }
    }
}