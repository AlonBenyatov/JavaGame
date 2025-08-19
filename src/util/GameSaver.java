package util;

import charcters.Player;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The `GameSaver` class provides static utility methods for handling the persistence
 * of player data within the game. It manages the creation, saving, loading, and
 * deletion of game saves. This class primarily utilizes player names to identify
 * unique save files, moving away from a fixed-slot saving system.
 *
 * It ensures the necessary directory structure for saves exists and includes
 * robust error handling for file operations.
 */
public class GameSaver {

    private static final String SAVE_DIRECTORY = "saves/";
    // The AUTO_SAVE_SLOT has been removed as saving is now based on player names
    // rather than predefined slots.

    /**
     * Ensures that the dedicated save directory, specified by `SAVE_DIRECTORY`, exists.
     * If the directory does not exist, it attempts to create it.
     *
     * This method prints messages to the console indicating the success or failure
     * of the directory creation. It should be called once at the application's startup
     * (e.g., in the `GameWindow` constructor) to prepare the save environment.
     */
    public static void createSaveDirectory() {
        File dir = new File(SAVE_DIRECTORY);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("GameSaver: Save directory created: " + SAVE_DIRECTORY);
            } else {
                System.err.println("GameSaver: Failed to create save directory: " + SAVE_DIRECTORY);
            }
        }
    }

    /**
     * Serializes a `Player` object and saves its current state to a file.
     * The filename is dynamically generated based on the player's name, ensuring
     * each player has a unique save file (e.g., "saves/PlayerName.ser").
     * The player's name is sanitized to be file-system friendly by replacing
     * problematic characters with underscores.
     *
     * If the provided `Player` object is null, or if its name is null or empty,
     * an error message is printed to the console, and the save operation is aborted.
     *
     * @param player The `Player` object whose state needs to be saved.
     */
    public static void saveGame(Player player) {
        // The 'slot' parameter was removed from the method signature as saving
        // is now done by player name, eliminating the need for a specific slot number.
        if (player == null || player.getName() == null || player.getName().trim().isEmpty()) {
            System.err.println("GameSaver: Error: No valid player or player name provided for saving.");
            return;
        }

        // Uses the player's name to create a unique filename.
        // Sanitizes the name by replacing characters that are not alphanumeric, dot, or hyphen with underscores.
        String safePlayerName = player.getName().replaceAll("[^a-zA-Z0-9.\\-]", "_");
        String filename = SAVE_DIRECTORY + safePlayerName + ".ser"; // Uses .ser extension for serialized objects.

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            System.out.println("GameSaver: Saving player '" + player.getName() + "' to " + filename);
            oos.writeObject(player);
            System.out.println("GameSaver: Player data saved successfully.");
        } catch (IOException e) {
            System.err.println("GameSaver: Error saving character data for '" + player.getName() + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Performs an automatic save operation for the given player.
     * This method internally calls the `saveGame(Player player)` method,
     * meaning auto-saves are also identified and managed by the player's name.
     *
     * @param player The `Player` object to be auto-saved.
     */
    public static void autoSave(Player player) {
        System.out.println("GameSaver: Initiating auto-save for player: " + player.getName() + ".");
        saveGame(player); // Auto-save now directly uses the player's name for saving.
        System.out.println("GameSaver: Auto-save process complete.");
    }

    /**
     * Deserializes a `Player` object from a save file identified by the provided
     * player's name. It constructs the expected filename (e.g., "saves/PlayerName.ser")
     * and attempts to read the `Player` object from it.
     *
     * Upon successful loading, the method recalculates the loaded player's
     * derived statistics (like max HP, attack, defense) to ensure they are
     * up-to-date with current base stats and equipment.
     *
     * @param playerName The name of the player whose save file is to be loaded.
     * @return The `Player` object loaded from the file if successful.
     * Returns `null` if no save file is found for the given player name,
     * or if an `IOException` or `ClassNotFoundException` occurs during loading
     * (indicating a corrupted or unreadable file).
     */
    public static Player loadGame(String playerName) {
        // The parameter was changed from an integer slot to a String playerName
        // to align with the new player-name-based saving system.
        if (playerName == null || playerName.trim().isEmpty()) {
            System.err.println("GameSaver: Error: No player name provided for loading.");
            return null;
        }
        String safePlayerName = playerName.replaceAll("[^a-zA-Z0-9.\\-]", "_"); // Sanitizes the player name for filename.
        String filename = SAVE_DIRECTORY + safePlayerName + ".ser"; // Constructs the full path to the save file.
        Player loadedPlayer = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            loadedPlayer = (Player) ois.readObject();
            System.out.println("GameSaver: Loaded character '" + loadedPlayer.getName() + "' from " + filename);
            if (loadedPlayer != null) {
                loadedPlayer.recalculateDerivedStats(); // Recalculates stats after loading.
                System.out.println("GameSaver: Derived stats recalculated for loaded player.");
            }
            return loadedPlayer;
        } catch (FileNotFoundException e) {
            System.out.println("GameSaver: No save file found for player '" + playerName + "' at " + filename);
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("GameSaver: Error loading game for '" + playerName + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes the save file associated with the given player's name.
     * It constructs the expected filename based on the sanitized player name
     * and attempts to delete the corresponding `.ser` file from the `SAVE_DIRECTORY`.
     *
     * @param playerName The name of the player whose save file is to be deleted.
     * @return `true` if the file was successfully deleted or if it did not exist
     * (in which case it's considered successful as the save is no longer present).
     * Returns `false` if an error occurred during deletion (e.g., permissions issue).
     */
    public static boolean deleteSave(String playerName) {
        // The parameter was changed from an integer slot to a String playerName.
        if (playerName == null || playerName.trim().isEmpty()) {
            System.err.println("GameSaver: Error: No player name provided for deletion.");
            return false;
        }
        String safePlayerName = playerName.replaceAll("[^a-zA-Z0-9.\\-]", "_"); // Sanitizes the player name.
        File fileToDelete = new File(SAVE_DIRECTORY + safePlayerName + ".ser"); // Path to the file to delete.
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                System.out.println("GameSaver: Save file for player '" + playerName + "' deleted successfully.");
                return true;
            } else {
                System.err.println("GameSaver: Failed to delete save file for player '" + playerName + "'.");
                return false;
            }
        } else {
            System.out.println("GameSaver: Save file for player '" + playerName + "' is already empty (no file found).");
            return true; // If the file doesn't exist, it's already "deleted" from the user's perspective.
        }
    }

    /**
     * Attempts to read the player name from a save file identified by an integer slot number.
     * This method assumes a filename format like "save[slot].dat".
     *
     * IMPORTANT: This method is a remnant of a previous saving system using fixed "slots"
     * and a different file extension (`.dat`). Its functionality might not align with
     * the current player-name-based `.ser` saving design. It might be considered
     * deprecated or targeted for removal/re-evaluation in the current architecture.
     *
     * @param slot The integer representing the legacy save slot.
     * @return The name of the player found in the specified slot.
     * Returns `null` if no file exists for the given slot.
     * Returns "Corrupted Save" if an `IOException` or `ClassNotFoundException`
     * occurs during reading, indicating a potentially corrupted file.
     */
    public static String getPlayerNameFromSave(int slot) {
        String filename = SAVE_DIRECTORY + "save" + slot + ".dat"; // Assumes a .dat extension for old saves.
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Player savedPlayer = (Player) ois.readObject();
            return savedPlayer.getName();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("GameSaver: Error reading player name from slot " + slot + ": " + e.getMessage());
            return "Corrupted Save"; // Indicates a problem with the file's content.
        }
    }
    
    /**
     * Scans the `SAVE_DIRECTORY` for all current save files (those ending with `.ser`),
     * attempts to load each one, and extracts the player's name from the `Player` object
     * stored within.
     *
     * This method provides a list of all player names for whom valid save files exist.
     * It is particularly useful for populating "Load Game" or "Delete Game" menus,
     * allowing the user to select from available saves by character name.
     *
     * Errors encountered while reading individual files (e.g., corrupted files)
     * are logged to the console, but the method continues to process other files
     * and does not include corrupted entries in the returned list.
     *
     * @return A `List` of `String` objects, where each string is the name of a saved player.
     * The list will be empty if no valid save files are found or if an
     * error prevents reading all files.
     */
    public static List<String> getAllSaveFileNames() {
        List<String> playerNames = new ArrayList<>();
        File dir = new File(SAVE_DIRECTORY);
        if (dir.exists() && dir.isDirectory()) {
            // Filters files to only include those ending with ".ser" (case-insensitive).
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".ser"));
                                                                                            
            if (files != null) {
                for (File file : files) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        Player savedPlayer = (Player) ois.readObject();
                        // Adds the player's name to the list only if both the player object
                        // and its name are successfully read.
                        if (savedPlayer != null && savedPlayer.getName() != null) {
                            playerNames.add(savedPlayer.getName());
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        // Logs errors for corrupted or unreadable files, but does not stop the scan.
                        System.err.println("GameSaver: Error reading player name from save file " + file.getName() + ": " + e.getMessage());
                        // Optional: file.delete(); // Could uncomment to automatically remove corrupted files.
                    }
                }
            }
        }
        return playerNames;
    }
}