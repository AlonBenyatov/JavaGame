package panels;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import charcters.Player;
import items.Item;
import items.equipments.BronzeShortsword;
import items.equipments.WoddenShield;
import items.EquipableItem; // Import EquipableItem for type checking
import main.GamePanel;
import util.GameSaver;

import java.util.ArrayList;
import java.util.List;

/**
 * The `ShopPanel` class represents the in-game shop where players can
 * buy and sell items, potentially upgrading their gear.
 * It extends `JPanel` to provide a graphical user interface for shop interactions.
 */
public class ShopPanel extends JPanel implements ActionListener {

    /**
     * A reference to the main `GamePanel`, used for managing screen transitions.
     * @see main.GamePanel
     */
    private GamePanel screenManager;

    /**
     * The `Player` object currently interacting with the shop.
     * This allows the shop to access the player's gold and inventory.
     * @see charcters.Player
     */
    private Player currentPlayer;

    /**
     * `JLabel` to display the player's current gold amount.
     * @see JLabel
     */
    private JLabel playerGoldLabel;

    /**
     * `JList` to display items available for purchase in the shop.
     * @see JList
     */
    private JList<Item> shopItemList;
    /**
     * `DefaultListModel` to manage the data displayed in `shopItemList`.
     * @see DefaultListModel
     */
    private DefaultListModel<Item> shopItemListModel;
    /**
     * `JTextArea` to show detailed information about the currently selected item in the shop.
     * @see JTextArea
     */
    private JTextArea shopItemDetailsArea;
    /**
     * `JButton` to initiate the purchase of the selected item from the shop.
     * @see JButton
     */
    private JButton buyButton;

    /**
     * `JList` to display items currently in the player's inventory that can be sold.
     * @see JList
     */
    private JList<Item> playerInventoryList;
    /**
     * `DefaultListModel` to manage the data displayed in `playerInventoryList`.
     * @see DefaultListModel
     */
    private DefaultListModel<Item> playerInventoryListModel;
    /**
     * `JTextArea` to show detailed information about the currently selected item in the player's inventory.
     * @see JTextArea
     */
    private JTextArea playerItemDetailsArea;
    /**
     * `JButton` to initiate the sale of the selected item from the player's inventory.
     * @see JButton
     */
    private JButton sellButton;

    /**
     * `JButton` to return to the main game menu.
     * @see JButton
     */
    private JButton backButton;

    // GameSaver instance
    private GameSaver gameSaver;

    // Custom renderer for player's inventory list
    private InventoryItemRenderer playerInventoryRenderer;

    // A static list of sample items the shop sells.
    private static final List<Item> SAMPLE_SHOP_ITEMS = new ArrayList<>();
    static {
        SAMPLE_SHOP_ITEMS.add(new BronzeShortsword());
        SAMPLE_SHOP_ITEMS.add(new WoddenShield());
        // You can add more items here later, like new Armor(), new CraftItem(), etc.
    }


    /**
     * Constructs a new `ShopPanel`.
     * This constructor initializes the shop's UI components, sets up their layout,
     * applies basic styling, and attaches event listeners for user interactions.
     *
     * @param manager The main `GamePanel` instance, used for navigating between screens.
     */
    public ShopPanel(GamePanel manager) {
        this.screenManager = manager;
        this.gameSaver = new GameSaver(); // Initialize GameSaver

        setLayout(new BorderLayout(10, 10)); // Use BorderLayout for overall structure
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the panel
        setBackground(Color.DARK_GRAY); // Set a dark background for the shop

        // --- TOP: Title and Player Gold Display ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false); // Make transparent to show parent background

        JLabel titleLabel = new JLabel("The Grand Bazaar", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.YELLOW); // Shop title in yellow
        topPanel.add(titleLabel, BorderLayout.NORTH);

        playerGoldLabel = new JLabel("Gold: 0", SwingConstants.RIGHT); // Display player's gold
        playerGoldLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playerGoldLabel.setForeground(Color.ORANGE);
        playerGoldLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 10)); // Padding for gold label
        topPanel.add(playerGoldLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH); // Add combined title and gold to the top

        // --- CENTER: Shop Items (Buy) and Player Inventory (Sell) Sections ---
        JPanel mainContentPanel = new JPanel(new GridLayout(1, 2, 15, 0)); // Two columns for shop and inventory
        mainContentPanel.setOpaque(false);

        // --- Left Side: Shop Inventory (Items to Buy) ---
        JPanel shopSection = new JPanel(new BorderLayout(5, 5));
        shopSection.setOpaque(false);
        shopSection.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Items for Sale", 0, 0, new Font("Arial", Font.BOLD, 16), Color.WHITE));

        shopItemListModel = new DefaultListModel<>();
        shopItemList = new JList<>(shopItemListModel);
        shopItemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shopItemList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        shopItemList.setBackground(new Color(60, 60, 60)); // Dark list background
        shopItemList.setForeground(Color.LIGHT_GRAY); // Light gray text
        shopItemList.setSelectionBackground(new Color(100, 100, 100)); // Selection highlight
        shopItemList.setSelectionForeground(Color.CYAN); // Selected text color
        shopItemList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    displaySelectedShopItemDetails();
                    updateButtonStates();
                }
            }
        });
        shopSection.add(new JScrollPane(shopItemList), BorderLayout.CENTER);

        shopItemDetailsArea = new JTextArea("Select an item to see its details and price.");
        shopItemDetailsArea.setEditable(false);
        shopItemDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        shopItemDetailsArea.setBackground(new Color(60, 60, 60));
        shopItemDetailsArea.setForeground(Color.WHITE);
        shopItemDetailsArea.setLineWrap(true);
        shopItemDetailsArea.setWrapStyleWord(true);
        shopSection.add(new JScrollPane(shopItemDetailsArea), BorderLayout.SOUTH);

        buyButton = createStyledButton("Buy Item");
        buyButton.setBackground(new Color(34, 139, 34)); // Forest Green for buy
        shopSection.add(buyButton, BorderLayout.PAGE_END); // Use PAGE_END for button at bottom of section

        mainContentPanel.add(shopSection);

        // --- Right Side: Player Inventory (Items to Sell) ---
        JPanel playerInventorySection = new JPanel(new BorderLayout(5, 5));
        playerInventorySection.setOpaque(false);
        playerInventorySection.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Your Inventory (Sell)", 0, 0, new Font("Arial", Font.BOLD, 16), Color.WHITE));

        playerInventoryListModel = new DefaultListModel<>();
        playerInventoryList = new JList<>(playerInventoryListModel);

        // --- IMPORTANT CHANGE: Set the custom cell renderer here for player's inventory ---
        playerInventoryRenderer = new InventoryItemRenderer();
        playerInventoryList.setCellRenderer(playerInventoryRenderer);
        // --- End of IMPORTANT CHANGE ---

        playerInventoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerInventoryList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        playerInventoryList.setBackground(new Color(60, 60, 60));
        playerInventoryList.setForeground(Color.LIGHT_GRAY);
        playerInventoryList.setSelectionBackground(new Color(100, 100, 100));
        playerInventoryList.setSelectionForeground(Color.CYAN);
        playerInventoryList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    displaySelectedPlayerItemDetails();
                    updateButtonStates();
                }
            }
        });
        playerInventorySection.add(new JScrollPane(playerInventoryList), BorderLayout.CENTER);

        playerItemDetailsArea = new JTextArea("Select an item from your inventory to sell.");
        playerItemDetailsArea.setEditable(false);
        playerItemDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        playerItemDetailsArea.setBackground(new Color(60, 60, 60));
        playerItemDetailsArea.setForeground(Color.WHITE);
        playerItemDetailsArea.setLineWrap(true);
        playerItemDetailsArea.setWrapStyleWord(true);
        playerInventorySection.add(new JScrollPane(playerItemDetailsArea), BorderLayout.SOUTH);

        sellButton = createStyledButton("Sell Item");
        sellButton.setBackground(new Color(178, 34, 34)); // Firebrick Red for sell
        playerInventorySection.add(sellButton, BorderLayout.PAGE_END);

        mainContentPanel.add(playerInventorySection);

        add(mainContentPanel, BorderLayout.CENTER); // Add the two-column content panel

        // --- BOTTOM: Back Button ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)); // Center the back button
        bottomPanel.setOpaque(false);

        backButton = createStyledButton("Back to Menu");
        backButton.setBackground(new Color(70, 130, 180)); // Steel Blue for back
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH); // Add the back button panel to the bottom

        // Register action listeners
        buyButton.addActionListener(this);
        sellButton.addActionListener(this);
        backButton.addActionListener(this);

        updateButtonStates(); // Initial button state update
        refreshShopDisplay(); // Populate the shop with items
    }

    /**
     * Sets the current `Player` object for the `ShopPanel`.
     * IMPORTANT CHANGE: Pass the player to the custom renderer here.
     * @param player The `Player` object currently interacting with the shop.
     */
    public void setPlayer(Player player) {
        this.currentPlayer = player;
        System.out.println("DEBUG: ShopPanel received player: " + (player != null ? player.getName() : "null"));
        // Pass the player to the renderer
        if (playerInventoryRenderer != null) {
            playerInventoryRenderer.setPlayer(player);
        }
        refreshShopDisplay(); // Refresh shop display (e.g., if prices change for player - though not implemented here)
        refreshPlayerInventoryDisplay(); // Refresh player's inventory for selling
        updatePlayerGoldDisplay(); // Update gold display
        updateButtonStates(); // Update button states based on player's new status
    }

    /**
     * Helper method to create consistently styled `JButton` instances.
     * @param text The text to display on the button.
     * @return A new, styled `JButton` instance.
     * @see JButton
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder()); // Add a raised border for depth
        button.setPreferredSize(new Dimension(150, 30)); // Standard button size
        return button;
    }

    /**
     * Populates the shop's item list (`shopItemListModel`) with `SAMPLE_SHOP_ITEMS`.
     * In a real game, this would likely fetch items from a game world shop inventory.
     */
    private void refreshShopDisplay() {
        shopItemListModel.clear();
        for (Item item : SAMPLE_SHOP_ITEMS) {
            shopItemListModel.addElement(item);
        }
        if (!shopItemListModel.isEmpty()) {
            shopItemList.setSelectedIndex(0); // Select the first item by default
        } else {
            shopItemDetailsArea.setText("No items currently available for sale.");
        }
        updateButtonStates();
    }

    /**
     * Populates the player's inventory list (`playerInventoryListModel`)
     * from the `currentPlayer`'s actual inventory.
     * IMPORTANT: After updating the model, ensure the list repaints to trigger the renderer.
     */
    private void refreshPlayerInventoryDisplay() {
        playerInventoryListModel.clear();
        if (currentPlayer != null && currentPlayer.getInventory() != null) {
            // Filter out QuestItems if you don't want them to be sellable in the shop
            for (Item item : currentPlayer.getInventory()) {
                // Example: Only add non-QuestItems to the sellable list
                if (!(item instanceof items.QuestItem)) {
                    playerInventoryListModel.addElement(item);
                }
            }
        }
        if (!playerInventoryListModel.isEmpty()) {
            playerInventoryList.setSelectedIndex(0); // Select the first item by default
        } else {
            playerItemDetailsArea.setText("Your inventory is empty. Find some items to sell!");
        }
        // Force repaint to ensure renderer updates are shown, especially if equipped status changed
        playerInventoryList.repaint();
        updateButtonStates();
    }

    /**
     * Updates the text of the `playerGoldLabel` to reflect the current player's gold.
     */
    private void updatePlayerGoldDisplay() {
        if (currentPlayer != null) {
            playerGoldLabel.setText("Gold: " + currentPlayer.getGold());
        } else {
            playerGoldLabel.setText("Gold: N/A");
        }
    }

    /**
     * Displays detailed information about the currently selected item in the shop
     * within the `shopItemDetailsArea`.
     */
    private void displaySelectedShopItemDetails() {
        Item selectedItem = shopItemList.getSelectedValue();
        if (selectedItem != null) {
            shopItemDetailsArea.setText(selectedItem.getDetailedStats() + "\nPrice: " + selectedItem.getValue() + " Gold");
        } else {
            shopItemDetailsArea.setText("Select an item to see its details and price.");
        }
    }

    /**
     * Displays detailed information about the currently selected item in the player's inventory
     * within the `playerItemDetailsArea`. Also indicates its potential selling price.
     */
    private void displaySelectedPlayerItemDetails() {
        Item selectedItem = playerInventoryList.getSelectedValue();
        if (selectedItem != null) {
            // Sell for 50% of its base value. Adjust this percentage as needed.
            int sellPrice = (int) (selectedItem.getValue() * 0.5);
            playerItemDetailsArea.setText(selectedItem.getDetailedStats() + "\nSell Price: " + sellPrice + " Gold");
        } else {
            playerItemDetailsArea.setText("Select an item from your inventory to sell.");
        }
    }

    /**
     * Updates the enabled/disabled state of the buy and sell buttons
     * based on item selection and player's gold/inventory status.
     */
    private void updateButtonStates() {
        boolean playerExists = (currentPlayer != null);

        // Buy button logic
        Item selectedShopItem = shopItemList.getSelectedValue();
        boolean shopItemSelected = (selectedShopItem != null);
        boolean canAfford = playerExists && shopItemSelected && (currentPlayer.getGold() >= selectedShopItem.getValue());
        buyButton.setEnabled(canAfford);

        // Sell button logic
        Item selectedPlayerItem = playerInventoryList.getSelectedValue();
        boolean playerItemSelected = (selectedPlayerItem != null);
        sellButton.setEnabled(playerExists && playerItemSelected);
    }

    /**
     * Handles action events triggered by buttons on the `ShopPanel`.
     * Processes buy, sell, and back actions, updating game state and UI accordingly.
     * @param e The `ActionEvent` triggered by a UI component (e.g., button click).
     * @see ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            screenManager.switchToScreen("gameMenu");
            System.out.println("Navigating back to Game Menu from Shop.");
        } else if (e.getSource() == buyButton) {
            Item selectedItem = shopItemList.getSelectedValue();
            if (selectedItem != null && currentPlayer != null) {
                int cost = selectedItem.getValue();
                if (currentPlayer.getGold() >= cost) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Buy " + selectedItem.getName() + " for " + cost + " gold?",
                            "Confirm Purchase", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        currentPlayer.removeGold(cost);
                        currentPlayer.addItem(selectedItem); // Add item to player's inventory
                        JOptionPane.showMessageDialog(this, "Purchased " + selectedItem.getName() + "!", "Purchase Successful", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(currentPlayer.getName() + " bought " + selectedItem.getName());
                        // Refresh UI
                        refreshPlayerInventoryDisplay();
                        updatePlayerGoldDisplay();
                        updateButtonStates();
                        gameSaver.saveGame(currentPlayer); // Auto-save after purchase
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough gold!", "Purchase Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else if (e.getSource() == sellButton) {
            Item selectedItem = playerInventoryList.getSelectedValue();
            if (selectedItem != null && currentPlayer != null) {
                boolean proceedToSell = true; // Flag to control sale flow

                // --- NEW: Confirmation for selling equipped items ---
                if (selectedItem instanceof EquipableItem) {
                    EquipableItem equipableItem = (EquipableItem) selectedItem;
                    if (currentPlayer.isItemEquipped(equipableItem)) {
                        int equippedConfirm = JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to sell " + selectedItem.getName() + "? It is currently EQUIPPED.",
                                "Confirm Sale of Equipped Item", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (equippedConfirm != JOptionPane.YES_OPTION) {
                            proceedToSell = false; // User cancelled the equipped item warning
                        } else {
                            // If user confirms to sell equipped item, unequip it first
                            // This ensures proper stat recalculation and removal from equipped slot
                            currentPlayer.unequipItem(equipableItem);
                            // The item remains in inventory until sold in the next step.
                        }
                    }
                }

                if (proceedToSell) { // Only proceed if not an equipped item or if user confirmed the warning
                    // --- Existing Sell Confirmation (after potential equipped item warning) ---
                    int sellPrice = (int) (selectedItem.getValue() * 0.5); // Sell for 50% of its base value
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Sell " + selectedItem.getName() + " for " + sellPrice + " gold?",
                            "Confirm Sale", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        currentPlayer.addGold(sellPrice);
                        currentPlayer.removeItem(selectedItem); // Remove item from player's inventory
                        JOptionPane.showMessageDialog(this, "Sold " + selectedItem.getName() + " for " + sellPrice + " gold!", "Sale Successful", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(currentPlayer.getName() + " sold " + selectedItem.getName());
                        // Refresh UI
                        refreshPlayerInventoryDisplay();
                        updatePlayerGoldDisplay();
                        updateButtonStates();
                        gameSaver.saveGame(currentPlayer); // Auto-save after sale
                    }
                }
            }
        }
    }

    /**
     * Custom ListCellRenderer for player inventory items.
     * Displays `Name (Gold Value, Rarity)` and appends `(Equipped)` if the item is an EquipableItem
     * and is currently equipped by the player.
     */
    private class InventoryItemRenderer extends DefaultListCellRenderer {
        private Player player; // Reference to the current player to check equipped status

        /**
         * Sets the current player for the renderer.
         * This is crucial for checking the equipped status of items.
         * @param player The current player object.
         */
        public void setPlayer(Player player) {
            this.player = player;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            // Call the super method to handle default styling (background color, selection, etc.)
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // Ensure the value is an Item
            if (value instanceof Item) {
                Item item = (Item) value;
                // Base display format: Name (Gold Value, Rarity)
                String displayText = item.getName() + " (" + item.getValue() + " Gold, " + item.getRarity().name() + ")";

                // --- START OF CHANGES FOR "(Equipped)" DISPLAY ---
                // If it's an EquipableItem and we have a player, check if it's equipped
                if (item instanceof EquipableItem && player != null) {
                    EquipableItem equipableItem = (EquipableItem) item;
                    if (player.isItemEquipped(equipableItem)) {
                        displayText += " (Equipped)";
                    }
                }
                // --- END OF CHANGES FOR "(Equipped)" DISPLAY ---

                setText(displayText); // Set the final text for this list item
            }
            return this;
        }
    }
}