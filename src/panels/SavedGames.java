package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import main.GamePanel;
import charcters.Player;
import util.GameSaver;
import java.util.List;
import java.util.ArrayList;

/**
 * The `SavedGames` class represents a JPanel that provides a user interface for
 * managing game save files. It allows players to load existing saved games
 * (identified by player name) and delete save files.
 *
 * This panel dynamically generates load buttons based on the available save files
 * found in the game's save directory. It integrates with the `GamePanel` to
 * switch screens and uses `GameSaver` for all file-related operations.
 */
public class SavedGames extends JPanel implements ActionListener {

    private GamePanel screenManager; // Manages screen transitions and provides access to other game window functionalities.
    private JPanel saveGridPanel; // A panel using GridLayout to dynamically display load game buttons.
    private JButton backToMenuButton; // Button to navigate back to the main menu.
    private JButton deleteSaveButton; // Button to initiate the process of deleting a saved game.

    // A list to store references to the dynamically created load buttons.
    // This allows for easy management (e.g., clearing and re-populating).
    private List<JButton> loadButtons; // Stores references to the dynamically created "Load" buttons for individual save files.

    /**
     * Constructs a new `SavedGames` panel.
     * Initializes the UI components, including the grid for displaying save buttons,
     * and control buttons for navigating back to the main menu and deleting saves.
     * It sets up the layout, attaches action listeners, and ensures the save directory
     * exists before updating the display of save buttons.
     *
     * @param manager The `GamePanel` instance, used for screen switching and
     * accessing other game window functionalities.
     */
    public SavedGames(GamePanel manager) {
        System.out.println("DEBUG: SavedGames panel is being created!");
        this.screenManager = manager;
        this.loadButtons = new ArrayList<>(); // Initializes the list to hold load buttons.

        setLayout(new BorderLayout());

        // Configures the panel that will dynamically hold the save game buttons.
        // GridLayout(0, 2) means any number of rows, fixed at 2 columns, with 10px gaps.
        saveGridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        saveGridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(saveGridPanel, BorderLayout.CENTER);

        // Sets up the bottom panel for action buttons.
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Initializes and configures the "Delete Save" button.
        deleteSaveButton = new JButton("Delete Save");
        deleteSaveButton.addActionListener(this);
        deleteSaveButton.setForeground(Color.BLUE);
        bottomPanel.add(deleteSaveButton);

        // Initializes and configures the "Back to Menu" button.
        backToMenuButton = new JButton("Back to Menu");
        backToMenuButton.addActionListener(this);
        backToMenuButton.setForeground(Color.RED);
        bottomPanel.add(backToMenuButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Ensures the game's save directory exists.
        GameSaver.createSaveDirectory();
        // Populates the save buttons based on existing save files when the panel is created.
        updateSaveButtons();
    }

    /**
     * Dynamically creates and updates the load game buttons displayed on the panel.
     * This method clears any existing buttons, retrieves the current list of saved
     * player names from `GameSaver`, and creates a new button for each save file.
     *
     * If no saved games are found, a "No saved games found." message is displayed.
     * Each load button is configured with an action listener that triggers the
     * `loadGame` method for the corresponding player.
     */
    public void updateSaveButtons() {
        saveGridPanel.removeAll(); // Removes all previously added components from the grid panel.
        loadButtons.clear();        // Clears the internal list of button references.

        List<String> savedPlayerNames = GameSaver.getAllSaveFileNames(); // Fetches names of all existing save files.

        if (savedPlayerNames.isEmpty()) {
            // Displays a message if no save files are present.
            saveGridPanel.add(new JLabel("No saved games found.", SwingConstants.CENTER));
            saveGridPanel.add(new JLabel("")); // Adds an empty label for layout consistency.
        } else {
            // Iterates through each saved player name to create a corresponding load button.
            for (String playerName : savedPlayerNames) {
                JButton loadBtn = new JButton("Load: " + playerName);
                // Uses a lambda expression to set an action listener that calls loadGame with the specific player name.
                loadBtn.addActionListener(e -> loadGame(playerName));
                loadButtons.add(loadBtn); // Adds the button to the internal list.
                saveGridPanel.add(loadBtn); // Adds the button to the UI panel.
            }
        }
        
        // Re-validates and re-paints the panel to reflect the changes in button layout.
        saveGridPanel.revalidate();
        saveGridPanel.repaint();
    }

    /**
     * Attempts to load a game for the specified player name.
     * It calls `GameSaver.loadGame` to retrieve the `Player` object.
     * If successful, it initializes the game window with the loaded player data
     * and switches to the appropriate game screen.
     *
     * If loading fails (e.g., file not found or corrupted), it displays an error
     * message to the user and refreshes the save buttons.
     *
     * @param playerName The name of the player whose game is to be loaded.
     */
    private void loadGame(String playerName) {
        Player loadedPlayer = GameSaver.loadGame(playerName); // Attempts to load the player.
        if (loadedPlayer != null) {
            System.out.println("SavedGames: Game loaded for player " + loadedPlayer.getName());
            // Passes the loaded player to the GameWindow to initialize the game state.
            screenManager.getGameWindow().loadAndInitializeGame(loadedPlayer);
        } else {
            // Displays an error message if loading was unsuccessful.
            JOptionPane.showMessageDialog(this, "Error or no save found for player '" + playerName + "'. It might be corrupted or was deleted.", "Load Error", JOptionPane.ERROR_MESSAGE);
            updateSaveButtons(); // Refreshes buttons in case a file disappeared or was corrupted.
        }
    }

    /**
     * Attempts to delete the save file associated with the given player name.
     * It calls `GameSaver.deleteSave` to perform the deletion.
     *
     * Upon successful deletion, it displays a confirmation message to the user
     * and refreshes the save buttons on the panel. If deletion fails, an error
     * message is displayed.
     *
     * @param playerName The name of the player whose save file is to be deleted.
     */
    private void deleteSave(String playerName) {
        if (GameSaver.deleteSave(playerName)) { // Attempts to delete the save file.
            JOptionPane.showMessageDialog(this, "Save for '" + playerName + "' deleted successfully.", "Delete Confirmation", JOptionPane.INFORMATION_MESSAGE);
            updateSaveButtons(); // Refreshes buttons after successful deletion.
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete save for '" + playerName + "' or it was already empty.", "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Prompts the user to select a save file to delete from a list of available
     * saved games. It retrieves the list of player names from `GameSaver` and
     * presents them in an input dialog.
     *
     * If the user confirms the deletion, the `deleteSave` method is called
     * for the selected player name.
     */
    private void promptToDeleteSave() {
        List<String> savedPlayerNames = GameSaver.getAllSaveFileNames(); // Gets all available save names.

        if (savedPlayerNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No saved games to delete.", "Delete Save", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Converts the list of player names to an array for use in JOptionPane.showInputDialog.
        String[] options = savedPlayerNames.toArray(new String[0]);

        // Displays an input dialog to allow the user to select a save to delete.
        String selectedPlayerName = (String) JOptionPane.showInputDialog(
                this,
                "Choose a character save to delete:",
                "Delete Save",
                JOptionPane.QUESTION_MESSAGE,
                null, // No custom icon.
                options,
                options[0] // Sets the first option as default.
        );

        if (selectedPlayerName != null && !selectedPlayerName.isEmpty()) {
            // Prompts for confirmation before proceeding with deletion.
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to PERMANENTLY delete the save for '" + selectedPlayerName + "'?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                deleteSave(selectedPlayerName); // Calls the deleteSave method with the selected player name.
            } else {
                System.out.println("Deletion cancelled by user.");
            }
        }
    }

    /**
     * Implements the `ActionListener` interface to handle events triggered by
     * buttons on this panel. It specifically handles clicks on the "Back to Menu"
     * and "Delete Save" buttons.
     *
     * Load buttons are handled by their individual lambda action listeners
     * defined in `updateSaveButtons()`.
     *
     * @param e The `ActionEvent` triggered by a UI component.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backToMenuButton) {
            if (screenManager != null) {
                screenManager.switchToScreen("menu"); // Switches back to the main menu.
            }
        } else if (e.getSource() == deleteSaveButton) {
            promptToDeleteSave(); // Triggers the process to prompt for save deletion.
        }
        // Actions for load buttons are handled by their specific lambda listeners
        // created in updateSaveButtons(), so no need to check saveButtons[i] here.
    }
}