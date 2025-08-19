package panels;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import main.GamePanel;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import main.GameWindow; // Import GameWindow

/**
 * The `Menu` class represents the main menu panel of the game.
 * It displays options such as starting a new game, loading saved games,
 * accessing game options, and exiting the application.
 * This panel also features a custom background image.
 */
public class Menu extends JPanel implements ActionListener {

    // Buttons for various menu actions
    JButton newgame = new JButton("New Game");
    JButton savedgames = new JButton("Saved Games");
    JButton options = new JButton("Options");
    JButton exit = new JButton("Exit Game");
    
    private GamePanel screenManager; // Reference to GamePanel to manage screen transitions
    private Image imageMenu; // Stores the background image for the menu
    private GameWindow gameWindow; // Reference to the main GameWindow, used for specific screen switches

    /**
     * Constructs a new `Menu` panel.
     * Initializes the menu buttons, sets their appearance and behavior,
     * loads the background image, and configures the panel's layout.
     * It obtains references to the `GamePanel` and `GameWindow` for screen management.
     *
     * @param manager The `GamePanel` instance, which orchestrates screen changes.
     */
    public Menu(GamePanel manager) {
        this.screenManager = manager;
        this.gameWindow = manager.getGameWindow(); // Retrieves the GameWindow instance from the GamePanel

        // Load the background image.
        // Make sure "backround2.jpeg" is in the correct path relative to your compiled classes
        ImageIcon imageIcon = new ImageIcon("backround2.jpeg");
        imageMenu = imageIcon.getImage();

        // Customize the panel's properties
        setOpaque(false); // Makes the panel transparent so the background image can be seen
        // Uses FlowLayout to arrange buttons, centered, with horizontal and vertical gaps
        setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50)); 

        // Customize and add action listeners to each button
        newgame.setFocusable(false); // Prevents the button from getting focus on tab/click
        newgame.setBackground(Color.red); // Sets button background color
        newgame.addActionListener(this); // Registers this panel as the action listener for the button

        savedgames.setFocusable(false);
        savedgames.setBackground(Color.red);
        savedgames.addActionListener(this);

        options.setFocusable(false);
        options.setBackground(Color.red);
        options.addActionListener(this);

        exit.setFocusable(false); 
        exit.setBackground(Color.red);
        exit.addActionListener(this);

        // Add all configured buttons to the panel
        add(newgame);
        add(savedgames);
        add(options);
        add(exit);
    }

    /**
     * Overrides the `paintComponent` method to draw the background image on the panel.
     * This ensures the image scales to fit the panel's dimensions.
     *
     * @param g The `Graphics` context used for painting.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Calls the superclass method to ensure proper painting chain
        // Draws the background image, scaling it to fill the entire panel.
        if (imageMenu != null) {
            g.drawImage(imageMenu, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Implements the `ActionListener` interface to handle button click events.
     * It determines which button was clicked and performs the corresponding action,
     * such as switching screens or exiting the game.
     *
     * @param e The `ActionEvent` triggered by a UI component (e.g., button click).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newgame) {
            System.out.println("New Game"); // Debug message
            if (screenManager != null) {
                // Switches to the character creation screen
                screenManager.switchToScreen("characterCreation");
            }
        } else if (e.getSource() == savedgames) {
            System.out.println("load saved games"); // Debug message
            // Uses the GameWindow to switch to the saved games panel,
            // which handles updating the save list dynamically.
            if (gameWindow != null) {
                gameWindow.switchToSavedGames();
            }
        } else if (e.getSource() == options) {
            System.out.println("options open new panel(music,graphics,...)"); // Debug message
            if (gameWindow != null) {
                // Switches to the options panel, passing "menu" as the previous screen
                // so the back button in options can return here.
                gameWindow.switchToOptions("menu");
            }
        } else if (e.getSource() == exit) {
            System.out.println("exit the game"); // Debug message
            System.exit(0); // Terminates the application.
        }
    }
}