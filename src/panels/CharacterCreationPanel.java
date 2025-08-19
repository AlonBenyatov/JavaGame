package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import main.GamePanel;
import main.GameWindow;
import charcters.Player;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * The `CharacterCreationPanel` class is a JPanel responsible for allowing the
 * player to create a new character by entering a name. Upon name entry,
 * it displays default player statistics and provides a "Continue" button
 * to proceed to the main game menu. It also handles keyboard input, specifically the Enter key.
 */
public class CharacterCreationPanel extends JPanel implements ActionListener, KeyListener {

    /**
     * A JLabel to prompt the user to enter their character's name.
     * @see JLabel
     */
    private JLabel nameLabel;
    /**
     * A JTextField where the player types in the name for their new character.
     * It also listens for keyboard input.
     * @see JTextField
     */
    private JTextField nameField;
    /**
     * A reference to the main GamePanel, used for managing screen transitions.
     * @see main.GamePanel
     */
    private GamePanel screenManager;
    /**
     * The Player object that gets instantiated with default stats once a name is entered.
     * @see charcters.Player
     */
    private Player player;
    /**
     * A JLabel to display the character's name after it has been created.
     * @see JLabel
     */
    private JLabel nameDisplayLabel;
    /**
     * A JLabel to display the newly created character's default statistics.
     * @see JLabel
     */
    private JLabel statsDisplayLabel;
    /**
     * A JButton to finalize character creation and proceed to the main game menu.
     * Initially hidden and disabled.
     * @see JButton
     */
    private JButton continueButton;
    /**
     * A JPanel used to group and organize the name input and stats display components.
     * @see JPanel
     */
    private JPanel leftPanel;
    /**
     * A JPanel primarily used to hold and center the continue button.
     * @see JPanel
     */
    private JPanel rightPanel; // Currently not used, but kept for potential layout adjustments
    /**
     * A JPanel currently acting as a container for the continueButton, positioned in the EAST of BorderLayout.
     * @see JPanel
     */
    private JPanel centerPanel;
    /**
     * A direct reference to the main GameWindow frame, used for accessing other panels and managing player data.
     * @see main.GameWindow
     */
    private GameWindow gameWindow;
    /**
     * An ImageIcon object representing the background image for this panel.
     * @see ImageIcon
     */
    private ImageIcon backgroundImage;

    /**
     * Constructs a new `CharacterCreationPanel`.
     * Initializes the UI components for name input, stat display, and the continue button.
     * Sets up the layout, loads the background image, and attaches event listeners.
     *
     * @param manager The `GamePanel` instance, which is central for screen management.
     */
    public CharacterCreationPanel(GamePanel manager) {
        this.screenManager = manager;
        this.gameWindow = manager.getGameWindow();

        // Load the background image directly from the file system path
        // NOTE: Using absolute file system paths like this is generally discouraged for
        // deployed applications as it makes the application non-portable.
        // For production, consider using ClassLoader.getResource() for resources within the JAR.
        try {
            File imageFile = new File("/C:/Users/Alon/eclipse-workspace/DungeonExplorer/CharacterCreation2.png");
            if (imageFile.exists()) {
                backgroundImage = new ImageIcon(ImageIO.read(imageFile));
            } else {
                System.err.println("Error loading background image: File not found at " + imageFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error reading background image data from file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during image loading from file system: " + e.getMessage());
        }

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        nameLabel = new JLabel("Enter your Hero's name:");
        nameLabel.setForeground(Color.RED);
        nameField = new JTextField(20);
        nameField.addKeyListener(this);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        namePanel.setOpaque(false);

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        nameDisplayLabel = new JLabel(" ");
        nameDisplayLabel.setForeground(Color.RED);
        statsDisplayLabel = new JLabel("");
        statsDisplayLabel.setFont(statsDisplayLabel.getFont().deriveFont(statsDisplayLabel.getFont().getSize() * 1.5f));
        nameDisplayLabel.setFont(nameDisplayLabel.getFont().deriveFont(nameDisplayLabel.getFont().getSize() * 1.5f));
        displayPanel.add(nameDisplayLabel);
        displayPanel.add(statsDisplayLabel);
        displayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        displayPanel.setOpaque(false);

        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);

        rightPanel = new JPanel(); // rightPanel is initialized but not used in the current layout
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        leftPanel.add(namePanel);
        leftPanel.add(displayPanel);

        continueButton = new JButton("Let's Continue");
        continueButton.addActionListener(this);
        continueButton.setVisible(false);
        continueButton.setEnabled(false);
        centerPanel.add(continueButton);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.EAST); // centerPanel is added to the EAST
        setOpaque(false);
    }

    /**
     * Overrides the default paintComponent method to draw the background image.
     * This ensures the image is displayed behind other components on the panel.
     * @param g The `Graphics` object used for painting.
     * @see Graphics
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Handles action events, primarily from the `continueButton`.
     * Upon clicking "Let's Continue", it passes the created `Player` object to the `GameWindow`
     * and switches to the game menu screen.
     * @param e The `ActionEvent` triggered by a button click.
     * @see ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == continueButton) {
            if (screenManager != null && player != null && gameWindow != null) {
                System.out.println("CharacterCreationPanel: Passing Player to GameWindow: " + player);
                gameWindow.switchToGameMenu(player);
            } else {
                System.out.println("CharacterCreationPanel: Error - screenManager, player, or gameWindow is null!");
            }
        }
    }

    /**
     * Creates a new `Player` object based on the name entered in `nameField`.
     * It validates the name, initializes the player, updates the display labels
     * with the new player's stats, and enables/shows the `continueButton`.
     */
    private void createCharacter() {
        String playerName = nameField.getText();
        if (playerName != null && !playerName.trim().isEmpty()) {
            player = new Player(playerName);
            System.out.println("Character created: " + player.getName());

            nameDisplayLabel.setText("<html><font color='red'>Name: " + player.getName() + "</font></html>");
            statsDisplayLabel.setText("<html><font color='red'>" + getPlayerStatsString(player) + "</font></html>");

            continueButton.setVisible(true);
            continueButton.setEnabled(true);

            nameField.setEditable(false); // Disable name input after character creation
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid name!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Generates an HTML-formatted string containing the player's detailed statistics.
     * This string is suitable for display in a `JLabel`.
     * @param player The `Player` object whose stats are to be displayed.
     * @return An HTML string representing the player's stats.
     * @see charcters.Player
     */
    private String getPlayerStatsString(Player player) {
        return "<html>"
                + "Level: " + player.getLevel() + "<br>"
                + "HP: " + player.getCurrentHP() + "/" + player.getMaxHP() + "<br>"
                + "Strength: " + player.getStrength() + "<br>"
                + "Dexterity: " + player.getDexterity() + "<br>"
                + "Intelligence: " + player.getIntelligence() + "<br>"
                + "Luck: " + player.getLuck() + "<br>"
                + "Constitution: " + player.getConstitution() + "<br>"
                + "Charisma: " + player.getCharisma() + "<br>"
                + "Gold: " + player.getGold() + "<br>"
                + "Armor: " + player.getArmor() + "<br>"
                + "Dodge: " + player.getDodge() + "<br>"
                + "Attack Speed: " + player.getAttackSpeed() + "<br>"
                + "Parry: " + player.getParry() + "<br>"
                + "Attack Damage: " + player.getAttackDmg()
                + "</html>";
    }

    /**
     * Retrieves the `Player` object currently associated with this panel.
     * @return The `Player` object.
     * @see charcters.Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Invoked when a key is typed (press and release generating a character).
     * Not actively used in this implementation.
     * @param e The `KeyEvent` generated by the key typed.
     * @see KeyEvent
     */
    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Invoked when a key is pressed down.
     * Specifically listens for the Enter key (`KeyEvent.VK_ENTER`).
     * If Enter is pressed and no character is created, it calls `createCharacter()`.
     * If a character is created and the continue button is active, it simulates a click on it.
     * @param e The `KeyEvent` generated by the key press.
     * @see KeyEvent
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (player == null) { // If player not yet created
                createCharacter();
            } else if (continueButton.isEnabled() && continueButton.isVisible()) { // If player created and can continue
                continueButton.doClick(); // Simulate button click
            }
        }
    }

    /**
     * Invoked when a key is released.
     * Not actively used in this implementation.
     * @param e The `KeyEvent` generated by the key release.
     * @see KeyEvent
     */
    @Override
    public void keyReleased(KeyEvent e) {}
}