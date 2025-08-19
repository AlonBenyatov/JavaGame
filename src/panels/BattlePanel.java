package panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingConstants;
// ....................................................................................
import main.GamePanel;
import charcters.Player;
import charcters.Enemy;
import charcters.Slime;
import charcters.Wolf;
import charcters.EnemyRarity;
import handlers.Handler.AutoAttackHandler;
import handlers.TimeHandler;
import util.GameSaver;
import handlers.LoopHandler; 

/**
 * The `BattlePanel` class serves as the main screen for combat encounters in the game.
 * It manages the display of the player and enemy, their health, attack cooldowns,
 * and visualizes damage through pop-up animations. It integrates with `GamePanel`
 * for screen transitions and coordinates battle logic with `AutoAttackHandler` and `TimeHandler`.
 * This panel also supports battle loops managed by `LoopHandler`.
 */
public class BattlePanel extends JPanel {

    /**
     * A reference to the main `GamePanel`, used for managing screen transitions.
     * @see main.GamePanel
     */
    private GamePanel screenManager;
    /**
     * The `Player` character currently engaged in battle.
     * @see charcters.Player
     */
    private Player player;
    /**
     * The `Enemy` character currently engaged in battle with the player.
     * @see charcters.Enemy
     */
    private Enemy enemy;

    /**
     * A boolean flag indicating whether a battle is currently active.
     */
    private boolean inBattle = false;
    /**
     * An instance of `AutoAttackHandler` responsible for calculating and applying damage
     * during auto-attacks between combatants.
     * @see handlers.Handler.AutoAttackHandler
     */
    private AutoAttackHandler attackHandler;

    /**
     * A `JLabel` that displays a notification when the player levels up,
     * reminding them to spend stat points. It is typically hidden when not needed.
     * @see JLabel
     */
    private JLabel levelUpNotificationLabel;

    /**
     * A `JLabel` displaying the player's current status, including name, level, HP, and attack cooldown.
     * @see JLabel
     */
    private JLabel playerStatusLabel;
    /**
     * A `JLabel` displaying the enemy's current status, including name, HP, and attack cooldown.
     * @see JLabel
     */
    private JLabel enemyStatusLabel;

    /**
     * A custom inner JPanel responsible for rendering the visual stage of the battle,
     * including character images and animated damage pop-ups.
     */
    private CustomBattleStagePanel battleStagePanel;

    /**
     * An instance of `TimeHandler` used to manage attack cooldowns for both the player and enemy.
     * @see handlers.TimeHandler
     */
    private TimeHandler timeHandler;
    /**
     * A Swing `Timer` that drives the main game loop during battle,
     * triggering periodic updates for combat, UI, and animations.
     * @see Timer
     */
    private Timer gameLoopTimer;

    /**
     * A reference to a `LoopHandler` instance. This field is used when battles
     * are part of a continuous loop (e.g., dungeon runs), allowing the battle
     * panel to notify the loop handler upon battle completion.
     * @see handlers.LoopHandler
     */
    private LoopHandler loopHandler;

    /**
     * The base file system path for loading images, defined as a static final string
     * to ensure consistent and easily modifiable resource location.
     * Note: Using absolute file system paths like this is generally discouraged for
     * deployed applications as it makes the application non-portable.
     * For production, consider using ClassLoader.getResource() for resources within the JAR.
     */
    private static final String IMAGE_BASE_PATH = "C:" + File.separator + "Users" + File.separator + "Alon" +
                                                  File.separator + "eclipse-workspace" + File.separator + "DungeonExplorer" + File.separator;
 
    /**
     * The `DamagePopUp` inner class represents a transient visual element that displays
     * damage numbers floating upwards and fading out after an attack.
     * Each instance manages its own position, transparency, and lifetime.
     */
    private static class DamagePopUp {
        /** The numerical amount of damage to display. */
        int damageAmount;
        /** The initial position (x, y) where the pop-up appears on the screen. */
        Point startPosition;
        /** The system time (in nanoseconds) when the pop-up was created, used for animation timing. */
        long startTime;
        /** The color of the damage text, including its alpha (transparency) component which changes over time. */
        Color color;
        /** The current Y-coordinate of the pop-up, which animates upwards over its lifetime. */
        int currentY;
        /** The total duration (in nanoseconds) for which the damage pop-up remains visible before fading out. */
        private static final long DURATION_NANOS = 1_500_000_000L; // 1.5 seconds

        /**
         * Constructs a new `DamagePopUp` instance.
         * @param damageAmount The amount of damage to display.
         * @param position The starting `Point` for the pop-up.
         * @param color The initial `Color` of the text.
         */
        public DamagePopUp(int damageAmount, Point position, Color color) {
            this.damageAmount = damageAmount;
            this.startPosition = position;
            this.startTime = System.nanoTime();
            this.color = color;
            this.currentY = position.y;
        }

        /**
         * Checks if the damage pop-up has completed its animation and is fully faded.
         * @return `true` if the pop-up's display duration has passed; `false` otherwise.
         */
        public boolean isFaded() {
            return System.nanoTime() - startTime > DURATION_NANOS;
        }

        /**
         * Updates the pop-up's position and transparency based on the elapsed time.
         * This method is called repeatedly by the game loop to animate the pop-up.
         */
        public void update() {
            long elapsedTime = System.nanoTime() - startTime;
            double progress = (double) elapsedTime / DURATION_NANOS;

            currentY = (int) (startPosition.y - (50 * progress)); // Moves upwards

            int alpha = (int) (255 * (1.0 - progress)); // Fades out
            if (alpha < 0) alpha = 0; // Ensures alpha doesn't go negative
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        }

        /**
         * Draws the damage pop-up text on the provided `Graphics2D` context.
         * @param g The `Graphics2D` object used for drawing.
         * @see Graphics2D
         */
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String text = String.valueOf(damageAmount);
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, startPosition.x - (textWidth / 2), currentY); // Centers text horizontally
        }
    }

    /**
     * The `CustomBattleStagePanel` inner class is a specialized `JPanel` that visually
     * represents the battle arena. It is responsible for displaying the player and enemy
     * images, their HP overlay labels, and rendering animated damage pop-ups.
     */
    private class CustomBattleStagePanel extends JPanel {
        /** A JLabel to display the player's character image. */
        private JLabel playerImageLabel;
        /** A JLabel to display the enemy's character image. */
        private JLabel enemyImageLabel;
        /** A JLabel overlaid on the player's image to show current/max HP. */
        private JLabel playerHPOverlayLabel;
        /** A JLabel overlaid on the enemy's image to show current/max HP. */
        private JLabel enemyHPOverlayLabel;

        /** A list to hold `DamagePopUp` instances for damage dealt to the player. */
        private List<DamagePopUp> playerDamagePopUps;
        /** A list to hold `DamagePopUp` instances for damage dealt to the enemy. */
        private List<DamagePopUp> enemyDamagePopUps;

        /**
         * Constructs a new `CustomBattleStagePanel`.
         * Initializes image and HP overlay labels, sets their properties, and adds them to the panel.
         * Sets up the lists for damage pop-ups.
         */
        public CustomBattleStagePanel() {
            setLayout(null); // Uses absolute positioning for elements
            setBackground(new Color(23, 23, 25)); // Dark background for the battle stage

            playerImageLabel = new JLabel();
            enemyImageLabel = new JLabel();
            playerHPOverlayLabel = new JLabel();
            enemyHPOverlayLabel = new JLabel();

            playerHPOverlayLabel.setForeground(Color.WHITE);
            playerHPOverlayLabel.setFont(new Font("Arial", Font.BOLD, 16));
            playerHPOverlayLabel.setHorizontalAlignment(SwingConstants.CENTER);

            enemyHPOverlayLabel.setForeground(Color.WHITE);
            enemyHPOverlayLabel.setFont(new Font("Arial", Font.BOLD, 16));
            enemyHPOverlayLabel.setHorizontalAlignment(SwingConstants.CENTER);

            add(playerImageLabel);
            add(enemyImageLabel);
            add(playerHPOverlayLabel);
            add(enemyHPOverlayLabel);

            playerDamagePopUps = new ArrayList<>();
            enemyDamagePopUps = new ArrayList<>();
        }

        /** Returns the JLabel used for the player's image. */
        public JLabel getPlayerImageLabel() { return playerImageLabel; }
        /** Returns the JLabel used for the enemy's image. */
        public JLabel getEnemyImageLabel() { return enemyImageLabel; }
        /** Returns the JLabel used for the player's HP overlay. */
        public JLabel getPlayerHPOverlayLabel() { return playerHPOverlayLabel; }
        /** Returns the JLabel used for the enemy's HP overlay. */
        public JLabel getEnemyHPOverlayLabel() { return enemyHPOverlayLabel; }

        /**
         * Adds a new damage pop-up for damage dealt to the player.
         * Note: This implementation clears previous pop-ups for the player, showing only the latest.
         * @param popUp The `DamagePopUp` to add.
         */
        public void addPlayerDamagePopUp(DamagePopUp popUp) {
            playerDamagePopUps.clear(); // Keep if you want only one pop-up per entity at a time
            playerDamagePopUps.add(popUp);
        }

        /**
         * Adds a new damage pop-up for damage dealt to the enemy.
         * Note: This implementation clears previous pop-ups for the enemy, showing only the latest.
         * @param popUp The `DamagePopUp` to add.
         */
        public void addEnemyDamagePopUp(DamagePopUp popUp) {
            enemyDamagePopUps.clear(); // Keep if you want only one pop-up per entity at a time
            enemyDamagePopUps.add(popUp);
        }

        /**
         * Clears all active damage pop-ups for both player and enemy, then repaints the panel.
         */
        public void clearDamagePopUps() {
            playerDamagePopUps.clear();
            enemyDamagePopUps.clear();
            repaint();
        }

        /**
         * Iterates through a list of damage pop-ups, updates their state (position, alpha),
         * and removes those that have fully faded.
         * @param popUpsList The list of `DamagePopUp` objects to update and clean.
         */
        private void updateAndCleanPopUps(List<DamagePopUp> popUpsList) {
            Iterator<DamagePopUp> iterator = popUpsList.iterator();
            while (iterator.hasNext()) {
                DamagePopUp popUp = iterator.next();
                popUp.update();
                if (popUp.isFaded()) {
                    iterator.remove();
                }
            }
        }

        /**
         * Overrides the default paintComponent method to draw the damage pop-ups.
         * This method is called repeatedly by Swing's repaint mechanism.
         * @param g The `Graphics` object used for painting.
         * @see Graphics
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw all active player damage pop-ups
            for (DamagePopUp popUp : playerDamagePopUps) {
                popUp.draw(g2d);
            }

            // Draw all active enemy damage pop-ups
            for (DamagePopUp popUp : enemyDamagePopUps) {
                popUp.draw(g2d);
            }
            g2d.dispose(); // Release Graphics2D resources
        }
    }

    /**
     * Constructs a new `BattlePanel`.
     * Initializes the panel's layout, background, and core UI components.
     * Sets up the game loop timer that drives the battle mechanics and animations.
     *
     * @param manager The `GamePanel` instance for screen management.
     * @param attackHandler The `AutoAttackHandler` to use for combat calculations.
     * @see GamePanel
     * @see AutoAttackHandler
     */
    public BattlePanel(GamePanel manager, AutoAttackHandler attackHandler) {
        this.screenManager = manager;
        this.attackHandler = attackHandler;
        this.timeHandler = new TimeHandler();

        setLayout(new BorderLayout(10, 10)); // Uses BorderLayout with padding
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adds empty border for spacing
        setBackground(Color.BLACK); // Sets panel background color

        JPanel statusPanel = new JPanel(new BorderLayout()); // Panel for top status labels
        statusPanel.setOpaque(false);

        playerStatusLabel = new JLabel("Player: ");
        playerStatusLabel.setForeground(Color.WHITE);
        playerStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusPanel.add(playerStatusLabel, BorderLayout.WEST);

        enemyStatusLabel = new JLabel("Enemy: ");
        enemyStatusLabel.setForeground(Color.WHITE);
        enemyStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        enemyStatusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statusPanel.add(enemyStatusLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.NORTH);

        battleStagePanel = new CustomBattleStagePanel(); // Initialize the custom battle stage panel
        add(battleStagePanel, BorderLayout.CENTER);

        levelUpNotificationLabel = new JLabel("You have leveled up! Check Game Menu to spend points.");
        levelUpNotificationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        levelUpNotificationLabel.setForeground(Color.YELLOW);
        levelUpNotificationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        levelUpNotificationLabel.setVisible(false); // Initially hidden

        JPanel southPanel = new JPanel(new BorderLayout()); // Panel for bottom elements (like notifications)
        southPanel.setOpaque(false);

        JPanel notificationContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        notificationContainer.setOpaque(false);
        notificationContainer.add(levelUpNotificationLabel);
        southPanel.add(notificationContainer, BorderLayout.NORTH);

        add(southPanel, BorderLayout.SOUTH);

        System.out.println("BattlePanel initialized.");

        // Setup the main game loop timer
        gameLoopTimer = new Timer(100, new ActionListener() { // Fires every 100 milliseconds
            @Override
            public void actionPerformed(ActionEvent e) {
                // Only run battle logic if a battle is active and combatants exist
                if (inBattle && player != null && enemy != null) {
                    // Update and clean damage pop-ups for visual effects
                    battleStagePanel.updateAndCleanPopUps(battleStagePanel.playerDamagePopUps);
                    battleStagePanel.updateAndCleanPopUps(battleStagePanel.enemyDamagePopUps);

                    // Player attack turn
                    if (player.getCurrentHP() > 0 && enemy.getCurrentHP() > 0 && timeHandler.canCombatantAttack(player)) {
                        playerAttack();
                    }

                    // Enemy attack turn
                    if (player.getCurrentHP() > 0 && enemy.getCurrentHP() > 0 && timeHandler.canCombatantAttack(enemy)) {
                        enemyTurn();
                    }

                    updateBattleUI(); // Refresh HP and cooldown displays

                    // Check for battle conclusion
                    if (player.getCurrentHP() <= 0) {
                        handlePlayerDefeat();
                    } else if (enemy.getCurrentHP() <= 0) {
                        handleEnemyDefeat(enemy);
                    }
                }
                battleStagePanel.repaint(); // Request repaint of the battle stage to draw pop-ups
            }
        });
        gameLoopTimer.setRepeats(true); // Ensures the timer fires repeatedly
    }

    /**
     * Loads an image from the specified filename, scales it to the given dimensions,
     * and returns it as an `ImageIcon`. It uses the `IMAGE_BASE_PATH` for resolution.
     * Includes error handling for file not found or loading issues.
     *
     * @param filename The name of the image file (e.g., "Blueslime.png").
     * @param width The desired width for the scaled image.
     * @param height The desired height for the scaled image.
     * @return An `ImageIcon` of the scaled image, or `null` if loading fails.
     * @see ImageIcon
     * @see BufferedImage
     * @see ImageIO
     */
    private ImageIcon loadImage(String filename, int width, int height) {
        try {
            File imageFile = new File(IMAGE_BASE_PATH + filename);
            if (!imageFile.exists()) {
                System.err.println("Image file not found: " + imageFile.getAbsolutePath());
                return null;
            }
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage == null) {
                System.err.println("Could not read image: " + imageFile.getAbsolutePath());
                return null;
            }
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            System.err.println("Error loading image: " + filename + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the player and enemy combatants for a new battle.
     * Initializes the battle state, resets cooldowns, loads and positions
     * character images, and starts the game loop.
     *
     * @param player The `Player` object for the battle.
     * @param enemy The `Enemy` object to fight.
     * @param previousScreen The name of the screen from which the battle was initiated (e.g., "dungeon").
     * Not directly used for "back" action within BattlePanel, but can be useful context.
     */
    public void setCombatants(Player player, Enemy enemy, String previousScreen) {
        this.player = player;
        this.enemy = enemy;
        appendLog("A wild " + enemy.getName() + " (Level " + enemy.getLevel() + ") appears!");
        appendLog("Prepare for battle!");
        inBattle = true; // Set battle state to active

        levelUpNotificationLabel.setVisible(false); // Hide level up notification at battle start

        timeHandler.resetCombatantCooldown(player); // Reset player's attack cooldown
        timeHandler.resetCombatantCooldown(enemy); // Reset enemy's attack cooldown

        // --- Set and Position Player Image ---
        int playerImageWidth = 250;
        int playerImageHeight = 250;
        // Position player image relative to panel dimensions
        int playerImageX = 150;
        int playerImageY = battleStagePanel.getHeight() - playerImageHeight - 30;

        // Keeping "FaceEnemy1.png" as your player's image as per original code.
        battleStagePanel.getPlayerImageLabel().setIcon(loadImage("FaceEnemy1.png", playerImageWidth, playerImageHeight));
        battleStagePanel.getPlayerImageLabel().setBounds(playerImageX, playerImageY, playerImageWidth, playerImageHeight);

        // --- Set and Position Enemy Image ---
        String enemyImageFilename = "";
        int enemyImageWidth = 200; // Default enemy image width
        int enemyImageHeight = 200; // Default enemy image height
        // Position enemy image relative to panel dimensions
        int enemyImageX = getWidth() - enemyImageWidth - 150;
        int enemyImageY = 50;

        // Logic to select enemy image based on its type and rarity
        if (enemy instanceof Slime) {
            if (enemy.getRarity() == EnemyRarity.COMMON) {
                enemyImageFilename = "Blueslime.png";
            } else if (enemy.getRarity() == EnemyRarity.UNCOMMON) {
                enemyImageFilename = "Greenslime.png";
            } else if (enemy.getRarity() == EnemyRarity.RARE) {
                enemyImageFilename = "Redslime.png";
            } else if (enemy.getRarity() == EnemyRarity.LEGENDARY) {
                enemyImageFilename = "Yellowslime.png";
            } else {
                enemyImageFilename = "Blueslime.png"; // Default for unknown rarity
            }
            enemyImageWidth = 200; // Slimes are generally smaller
            enemyImageHeight = 200;
        } else if (enemy instanceof Wolf) {
            if (enemy.getRarity() == EnemyRarity.COMMON) {
                enemyImageFilename = "bluewolf.png";
            } else if (enemy.getRarity() == EnemyRarity.UNCOMMON) {
                enemyImageFilename = "greenwolf.png";
            } else if (enemy.getRarity() == EnemyRarity.RARE) {
                enemyImageFilename = "redwolf.png";
            } else if (enemy.getRarity() == EnemyRarity.LEGENDARY) {
                enemyImageFilename = "yellowwolf.png";
            }
            enemyImageWidth = 250; // Wolves are generally larger
            enemyImageHeight = 250;
        } else {
            enemyImageFilename = "placeholder_enemy.png"; // Fallback image for unknown enemy types
        }

        battleStagePanel.getEnemyImageLabel().setIcon(loadImage(enemyImageFilename, enemyImageWidth, enemyImageHeight));
        battleStagePanel.getEnemyImageLabel().setBounds(enemyImageX, enemyImageY, enemyImageWidth, enemyImageHeight);

        // --- Position HP Overlay Labels (relative to image labels) ---
        int playerHPOverlayWidth = playerImageWidth + 50; // HP bar wider than image
        int playerHPOverlayHeight = 30;
        int playerHPOverlayX = playerImageX - 25; // Centered relative to image
        int playerHPOverlayY = playerImageY - playerHPOverlayHeight - 5; // Above image
        battleStagePanel.getPlayerHPOverlayLabel().setBounds(playerHPOverlayX, playerHPOverlayY, playerHPOverlayWidth, playerHPOverlayHeight);

        int enemyHPOverlayWidth = enemyImageWidth + 50;
        int enemyHPOverlayHeight = 30;
        int enemyHPOverlayX = enemyImageX - 25;
        int enemyHPOverlayY = enemyImageY - enemyHPOverlayHeight - 5;
        battleStagePanel.getEnemyHPOverlayLabel().setBounds(enemyHPOverlayX, enemyHPOverlayY, enemyHPOverlayWidth, enemyHPOverlayHeight);


        battleStagePanel.revalidate(); // Re-layout components
        battleStagePanel.repaint(); // Repaint to show new images/positions

        battleStagePanel.clearDamagePopUps(); // Clear any lingering pop-ups from previous battles

        updateBattleUI(); // Initial UI update
        gameLoopTimer.start(); // Start the battle loop
    }

    /**
     * Overloaded method to set combatants without specifying a previous screen.
     * Defaults the previous screen name to "unknown".
     * @param player The `Player` object for the battle.
     * @param enemy The `Enemy` object to fight.
     */
    public void setCombatants(Player player, Enemy enemy) {
        setCombatants(player, enemy, "unknown");
    }

    /**
     * Sets the player object for this battle panel. This is used when the player
     * character is updated or changed externally. It also updates the level-up
     * notification visibility.
     * @param player The `Player` object to set.
     * @see charcters.Player
     */
    public void setPlayer(Player player) {
        this.player = player;
        System.out.println("DEBUG (BattlePanel): setPlayer called. Player: " + (player != null ? player.getName() : "null") + ", HP: " + (player != null ? player.getCurrentHP() : "N/A") + ", XP: " + (player != null ? player.getExperience() : "N/A") + ", Gold: " + (player != null ? player.isAlive() : "N/A"));
        if (player != null && player.getUnallocatedStatPoints() > 0) {
            levelUpNotificationLabel.setVisible(true);
        } else {
            levelUpNotificationLabel.setVisible(false);
        }
        updateBattleUI();
    }

    /**
     * Sets the `LoopHandler` for this battle.
     * This reference is used to notify an ongoing game loop (e.g., dungeon exploration)
     * about the outcome of the battle, allowing the loop to manage rewards, healing,
     * and transition to the next state (next encounter or return to menu).
     * @param loopHandler The `LoopHandler` instance, or `null` if the battle is standalone.
     * @see handlers.LoopHandler
     */
    public void setLoopHandler(LoopHandler loopHandler) {
        this.loopHandler = loopHandler;
        System.out.println("BattlePanel: LoopHandler set to " + (loopHandler != null ? "active" : "null"));
    }

    /**
     * Updates the status labels (player and enemy HP, attack cooldowns)
     * displayed at the top of the battle panel. This method is called repeatedly
     * by the game loop to provide real-time battle information.
     */
    private void updateBattleUI() {
        long currentTime = System.nanoTime();

        if (player != null) {
            playerStatusLabel.setText("Player: " + player.getName() + " (Lvl " + player.getLevel() + ") HP: " + player.getCurrentHP() + "/" + player.getMaxHP());
            if (player.getCurrentHP() > 0) { // Only show cooldown if player is alive
                long lastAttack = timeHandler.getLastAttackTime(player);
                long cooldownRemainingNanos = timeHandler.getAttackCooldownNanos(player) - (currentTime - lastAttack);

                if (cooldownRemainingNanos > 0) {
                    double remainingSeconds = (double) cooldownRemainingNanos / 1_000_000_000L;
                    playerStatusLabel.setText(playerStatusLabel.getText() + String.format(" (CD: %.1fs)", remainingSeconds));
                }
            }
        } else {
            playerStatusLabel.setText("Player: N/A");
        }

        if (enemy != null) {
            enemyStatusLabel.setText("Enemy: " + enemy.getName() + " HP: " + enemy.getCurrentHP() + "/" + enemy.getMaxHP());
            if (enemy.getCurrentHP() > 0) { // Only show cooldown if enemy is alive
                long lastAttack = timeHandler.getLastAttackTime(enemy);
                long cooldownRemainingNanos = timeHandler.getAttackCooldownNanos(enemy) - (currentTime - lastAttack);

                if (cooldownRemainingNanos > 0) {
                    double remainingSeconds = (double) cooldownRemainingNanos / 1_000_000_000L;
                    enemyStatusLabel.setText(enemyStatusLabel.getText() + String.format(" (CD: %.1fs)", remainingSeconds));
                }
            }
        } else {
            enemyStatusLabel.setText("Enemy: N/A");
        }

        // Update HP overlay labels on the battle stage (under the images)
        if (player != null) {
            battleStagePanel.getPlayerHPOverlayLabel().setText(String.format("HP: %d/%d", player.getCurrentHP(), player.getMaxHP()));
        } else {
            battleStagePanel.getPlayerHPOverlayLabel().setText("");
        }
        if (enemy != null) {
            battleStagePanel.getEnemyHPOverlayLabel().setText(String.format("HP: %d/%d", enemy.getCurrentHP(), enemy.getMaxHP()));
        } else {
            battleStagePanel.getEnemyHPOverlayLabel().setText("");
        }
    }

    /**
     * Appends a message to the system console, serving as a simple battle log.
     * @param message The `String` message to log.
     */
    private void appendLog(String message) {
        System.out.println(message);
    }

    /**
     * Simulates the player's attack action.
     * If conditions allow (player and enemy exist, enemy is alive, and player can attack),
     * it calculates damage, records the attack time, and creates a damage pop-up over the enemy.
     */
    private void playerAttack() {
        if (player != null && enemy != null && enemy.getCurrentHP() > 0 && inBattle && player.getCurrentHP() > 0) {
            appendLog(player.getName() + " attacks " + enemy.getName() + "!");
            int damageDealt = attackHandler.handleAutoAttack(player, enemy);
            timeHandler.recordCombatantAttack(player); // Record player's last attack time

            // Calculate position for damage pop-up over the enemy
            Point enemyPos = battleStagePanel.getEnemyImageLabel().getLocation();
            int centerX = enemyPos.x + battleStagePanel.getEnemyImageLabel().getWidth() / 2;
            int startY = enemyPos.y;
            // Enemy takes damage: pop-up is red
            battleStagePanel.addEnemyDamagePopUp(new DamagePopUp(damageDealt, new Point(centerX, startY), Color.RED));
        }
    }

    /**
     * Simulates the enemy's attack action.
     * If conditions allow (player and enemy exist, player is alive, and enemy can attack),
     * it calculates damage, records the attack time, and creates a damage pop-up over the player.
     */
    private void enemyTurn() {
        if (player != null && enemy != null && player.getCurrentHP() > 0 && enemy.getCurrentHP() > 0 && inBattle) {
            appendLog(enemy.getName() + " attacks " + player.getName() + "!");
            int damageDealt = attackHandler.handleAutoAttack(enemy, player);
            timeHandler.recordCombatantAttack(enemy); // Record enemy's last attack time

            // Calculate position for damage pop-up over the player
            Point playerPos = battleStagePanel.getPlayerImageLabel().getLocation();
            int centerX = playerPos.x + battleStagePanel.getPlayerImageLabel().getWidth() / 2;
            int startY = playerPos.y;
            // Player takes damage: pop-up is red
            battleStagePanel.addPlayerDamagePopUp(new DamagePopUp(damageDealt, new Point(centerX, startY), Color.RED));
        }
    }

    /**
     * Handles the logic when the enemy is defeated.
     * Stops the game loop, logs the victory, and either defers to a `LoopHandler`
     * (if in a loop) or applies rewards, heals the player, saves the game,
     * and transitions back to the game menu.
     * @param defeatedEnemy The `Enemy` object that was defeated.
     * @see GameSaver
     */
    private void handleEnemyDefeat(Enemy defeatedEnemy) {
        gameLoopTimer.stop(); // Stop the battle updates
        appendLog("\n--- Battle Ended ---");
        appendLog("You have defeated the " + defeatedEnemy.getName() + "!");
        inBattle = false; // Mark battle as inactive

        battleStagePanel.clearDamagePopUps(); // Clear any remaining damage pop-ups

        // Check if a battle loop is active (e.g., in a dungeon run)
        if (loopHandler != null) {
            System.out.println("BattlePanel: Notifying LoopHandler of enemy defeat (player won).");
            loopHandler.onBattleEnd(true, defeatedEnemy); // Notify loop handler that player won
            return; // Exit this method, as LoopHandler will manage subsequent actions (rewards, healing, screen switch)
        }

        // --- Original standalone battle logic (only runs if no loopHandler is active) ---
        int expGained = defeatedEnemy.getExperienceReward();
        int goldGained = defeatedEnemy.getGoldReward();

        player.gainExperience(expGained); // Award experience
        player.addGold(goldGained); // Award gold
        player.setCurrentHP(player.getMaxHP()); // Heal player to full HP

        System.out.println("DEBUG (BattlePanel): AFTER REWARD/HEAL - Player " + player.getName() + " XP: " + player.getExperience() + ", Gold: " + player.getGold() + ", HP: " + player.getCurrentHP());

        appendLog("You gained " + expGained + " experience and " + goldGained + " gold.");
        appendLog("Your HP has been restored.");
        updateBattleUI(); // Update UI to reflect new HP/XP/Gold

        System.out.println("Battle won! Attempting to auto-save player progress...");
        if (player != null) {
            try {
                GameSaver.autoSave(player); // Auto-save player progress
                System.out.println("Auto-save successful after battle.");
                System.out.println("DEBUG (BattlePanel): AFTER AUTOSAVE CALL - Player " + player.getName() + " XP: " + player.getExperience() + ", Gold: " + player.getGold() + ", HP: " + player.getCurrentHP());
            } catch (Exception ex) {
                System.err.println("ERROR: Auto-save failed after battle: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            System.out.println("No player object to auto-save after battle.");
        }

        // Display level up notification if applicable
        if (player.getUnallocatedStatPoints() > 0) {
            levelUpNotificationLabel.setVisible(true);
            appendLog("You have leveled up! Allocate points in Stats Display from the Game Menu.");
            JOptionPane.showMessageDialog(this, "Battle finished! You leveled up!", "Combat End", JOptionPane.INFORMATION_MESSAGE);
        } else {
            levelUpNotificationLabel.setVisible(false);
            JOptionPane.showMessageDialog(this, "Battle finished!", "Combat End", JOptionPane.INFORMATION_MESSAGE);
        }

        // Update the GameMenu's reminder button and switch back to Game Menu
        screenManager.getGameWindow().getGameMenuPanel().updateReminderButton();
        screenManager.getGameWindow().switchToGameMenu(player);
        // --- End original standalone battle logic ---
    }

    /**
     * Handles the logic when the player is defeated in battle.
     * Stops the game loop, logs the defeat, and either defers to a `LoopHandler`
     * (if in a loop) or restores player HP, saves the game, and transitions
     * back to the game menu.
     * @see GameSaver
     */
    private void handlePlayerDefeat() {
        gameLoopTimer.stop(); // Stop the battle updates
        appendLog("\n--- Battle Ended ---");
        appendLog("You have been defeated by the " + enemy.getName() + "!");
        inBattle = false; // Mark battle as inactive

        battleStagePanel.clearDamagePopUps(); // Clear any remaining damage pop-ups

        // Check if a battle loop is active (e.g., in a dungeon run)
        if (loopHandler != null) {
            System.out.println("BattlePanel: Notifying LoopHandler of player defeat (player lost).");
            loopHandler.onBattleEnd(false, enemy); // Notify loop handler that player lost
            return; // Exit this method, as LoopHandler will manage subsequent actions (no rewards, screen switch)
        }

        // --- Original standalone battle logic (only runs if no loopHandler is active) ---
        player.setCurrentHP(player.getMaxHP()); // Heal player to full HP after defeat
        appendLog("Your HP has been restored for your next attempt.");
        updateBattleUI(); // Update UI to reflect new HP

        System.out.println("DEBUG (BattlePanel): AFTER DEFEAT/HEAL - Player " + player.getName() + " XP: " + player.getExperience() + ", Gold: " + player.getGold() + ", HP: " + player.getCurrentHP());

        System.out.println("Player defeated. Attempting to auto-save player's healed state...");
        if (player != null) {
            try {
                GameSaver.autoSave(player); // Auto-save player's state (healed)
                System.out.println("Auto-save successful after defeat.");
                System.out.println("DEBUG (BattlePanel): AFTER AUTOSAVE CALL - Player " + player.getName() + " XP: " + player.getExperience() + ", Gold: " + player.getGold() + ", HP: " + player.getCurrentHP());
            } catch (Exception ex) {
                System.err.println("ERROR: Auto-save failed after defeat: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            System.out.println("No player object to auto-save after defeat.");
        }

        JOptionPane.showMessageDialog(this, "You have been defeated!", "Game Over", JOptionPane.ERROR_MESSAGE);
        // Update the GameMenu's reminder button and switch back to Game Menu
        screenManager.getGameWindow().getGameMenuPanel().updateReminderButton();
        screenManager.getGameWindow().switchToGameMenu(player);
        // --- End original standalone battle logic ---
    }
}