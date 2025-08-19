package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.GamePanel; // Import GamePanel to access switchToScreen

/**
 * The `Options` class represents a JPanel that serves as the game's settings menu.
 * It provides UI elements for players to adjust various game options such as
 * music volume, sound effects volume, and graphics settings.
 *
 * This panel allows navigation back to the previous screen it was accessed from,
 * integrating with the `GamePanel` for screen management.
 */
public class Options extends JPanel implements ActionListener {

    private JLabel musicVolumeLabel; // A label to indicate the music volume slider.
    private JSlider musicVolumeSlider; // A slider control for adjusting the background music volume.
    private JLabel graphicsLabel; // A label to indicate the graphics settings dropdown.
    private JComboBox<String> graphicsSettings; // A dropdown menu for selecting different graphics quality settings.
    private JLabel soundEffectsVolumeLabel; // A label to indicate the sound effects volume slider.
    private JSlider soundEffectsVolumeSlider; // A slider control for adjusting the sound effects volume.
    private JButton backButton; // Button to return to the previous screen.
    private GamePanel screenManager; // A reference to the main GamePanel to handle screen transitions.
    private String previousScreen; // Stores the name of the screen that navigated to this Options panel, allowing for a proper return.

    /**
     * Constructs a new `Options` panel.
     * Initializes all the UI components for game settings, including labels,
     * sliders for volume control, a combo box for graphics settings, and a
     * back button. It sets up the panel's layout and attaches action listeners.
     *
     * @param manager The `GamePanel` instance, which provides the functionality
     * to switch between different game screens.
     */
    public Options(GamePanel manager) {
        this.screenManager = manager; // Stores the reference to the GamePanel.

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Arranges components vertically.
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Adds padding around the panel.

        // --- Music Volume Controls ---
        musicVolumeLabel = new JLabel("Music Volume:");
        musicVolumeLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligns label to the left.
        musicVolumeSlider = new JSlider(0, 100, 50); // Slider from 0 to 100, default 50.
        musicVolumeSlider.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligns slider to the left.

        // --- Graphics Settings Controls ---
        graphicsLabel = new JLabel("Graphics:");
        graphicsLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligns label to the left.
        String[] graphicsOptions = {"Low", "Medium", "High"}; // Available graphics settings.
        graphicsSettings = new JComboBox<>(graphicsOptions); // Dropdown for graphics settings.
        graphicsSettings.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligns combo box to the left.

        // --- Sound Effects Volume Controls ---
        soundEffectsVolumeLabel = new JLabel("Sound Effects Volume:");
        soundEffectsVolumeLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligns label to the left.
        soundEffectsVolumeSlider = new JSlider(0, 100, 75); // Slider from 0 to 100, default 75.
        soundEffectsVolumeSlider.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligns slider to the left.

        // --- Back Button ---
        backButton = new JButton("Back");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligns button to the left.
        backButton.addActionListener(this); // Registers this panel as the action listener for the button.

        // Adds components to the panel with vertical spacing.
        add(musicVolumeLabel);
        add(musicVolumeSlider);
        add(Box.createVerticalStrut(15)); // Adds a rigid vertical space.

        add(graphicsLabel);
        add(graphicsSettings);
        add(Box.createVerticalStrut(15));

        add(soundEffectsVolumeLabel);
        add(soundEffectsVolumeSlider);
        add(Box.createVerticalStrut(15));
        add(backButton); // Adds the back button to the panel.
    }

    /**
     * Retrieves the current value of the music volume slider.
     *
     * @return An integer representing the music volume, ranging from 0 to 100.
     */
    public int getMusicVolume() {
        return musicVolumeSlider.getValue();
    }

    /**
     * Retrieves the currently selected graphics setting from the combo box.
     *
     * @return A `String` representing the selected graphics setting (e.g., "Low", "Medium", "High").
     */
    public String getGraphicsSetting() {
        return (String) graphicsSettings.getSelectedItem();
    }

    /**
     * Retrieves the current value of the sound effects volume slider.
     *
     * @return An integer representing the sound effects volume, ranging from 0 to 100.
     */
    public int getSoundEffectsVolume() {
        return soundEffectsVolumeSlider.getValue();
    }

    /**
     * Sets the name of the screen from which the `Options` panel was accessed.
     * This allows the back button to return to the correct previous screen.
     *
     * @param screen A `String` representing the name of the previous screen.
     */
    public void setPreviousScreen(String screen) {
        this.previousScreen = screen;
    }

    /**
     * Implements the `ActionListener` interface to handle events, specifically
     * the "Back" button click.
     *
     * When the back button is pressed, it attempts to switch the `GamePanel`
     * back to the `previousScreen`. If `previousScreen` is null, it defaults
     * to switching to the "menu" screen and logs an error.
     *
     * @param e The `ActionEvent` triggered by a UI component (e.g., button click).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            if (screenManager != null) {
                if (previousScreen != null) {
                    screenManager.switchToScreen(previousScreen); // Switches to the stored previous screen.
                } else {
                    System.out.println("ERROR: previousScreen is NULL in Options!"); // Debug message for null previous screen.
                    screenManager.switchToScreen("menu"); // Fallback to the main menu if previous screen is unknown.
                }
            } else {
                System.out.println("Error: screenManager is null. Cannot switch back to menu."); // Debug message if screenManager is null.
            }
        }
    }
}