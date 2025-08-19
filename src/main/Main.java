package main;

import javax.swing.SwingUtilities;

/**
 * The `Main` class serves as the entry point for the entire 2D Dungeon Game application.
 * It contains the `main` method, which is the starting point for all Java applications.
 * This class is responsible for ensuring that the Swing graphical user interface (GUI)
 * is initialized and run safely on the Event Dispatch Thread (EDT).
 */
public class Main {

    /**
     * The `main` method is the primary entry point for the 2D Dungeon Game application.
     * When the Java Virtual Machine (JVM) starts the application, it executes this method.
     *
     * Its core purpose is to launch the game's main window (`GameWindow`). Crucially,
     * it does so using `javax.swing.SwingUtilities.invokeLater()`.
     *
     * @param args Command-line arguments passed to the application (not used in this game).
     */
    public static void main(String[] args) {
        /**
         * Uses `SwingUtilities.invokeLater()` to ensure that all Swing UI operations,
         * specifically the creation and display of the `GameWindow`, are executed
         * on the Event Dispatch Thread (EDT).
         *
         * The EDT is a single, dedicated thread in Swing applications responsible for
         * all GUI-related tasks, including drawing components, handling user input,
         * and updating the screen. Performing UI operations on any other thread can
         * lead to various concurrency issues such as:
         * <ul>
         * <li><b>Race Conditions:</b> Multiple threads trying to modify the same UI component simultaneously.</li>
         * <li><b>Deadlocks:</b> Threads waiting indefinitely for each other to release resources.</li>
         * <li><b>Inconsistent UI State:</b> Components not being drawn correctly or appearing corrupted.</li>
         * <li><b>Unresponsive UI:</b> The application freezing or becoming sluggish.</li>
         * </ul>
         * By using `invokeLater()`, the UI creation task is safely queued to be run
         * on the EDT, preventing these issues and ensuring a smooth, responsive,
         * and stable graphical user interface. This is a fundamental principle
         * for robust Swing application development.
         * 
         */
        SwingUtilities.invokeLater(new Runnable()
        {
            /**
             * The `run` method of this anonymous `Runnable` is executed on the Event Dispatch Thread (EDT).
             * It contains the code that safely creates and displays the game's main window (`GameWindow`),
             * ensuring all initial GUI setup adheres to Swing's threading rules.
             */
            public void run() {
                // 1. Create the central GamePanel instance.
                // This GamePanel will manage all your screens and will internally create the GameWindow.
                GamePanel gamePanel = new GamePanel(); // This uses the GamePanel() constructor

                // 2. Tell the GamePanel to start the game.
                // This method will create the GameWindow and make it visible.
                gamePanel.startGame();
            }
        });
    }
}




