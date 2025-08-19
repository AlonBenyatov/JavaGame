package panels;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import charcters.Player;
import items.Item;
import items.EquipableItem;
import main.GamePanel;

/**
 * The `InventoryPanel` class represents a JPanel that displays a player's inventory
 * and provides options to interact with items such as equipping, using, or discarding them.
 * It features a list of items and a detailed view for the selected item.
 */
public class InventoryPanel extends JPanel implements ActionListener {

    /**
     * A reference to the main game panel, used for managing screen transitions
     * (e.g., switching back to the game menu).
     */
    private GamePanel screenManager;
    /**
     * The `Player` object whose inventory is currently being displayed and managed.
     * All inventory actions (equip, use, discard) are performed on this player.
     */
    private Player player;

    /**
     * The `JList` component that visually displays the list of items in the player's inventory.
     * Users select items from this list to view their details or perform actions.
     */
    private JList<Item> inventoryList;
    /**
     * The `DefaultListModel` backing the `inventoryList`. This model holds the actual
     * `Item` objects and allows for dynamic addition or removal of items, which updates
     * the `JList` display.
     */
    private DefaultListModel<Item> inventoryListModel;
    /**
     * A `JTextArea` used to display detailed information about the currently
     * selected item from the `inventoryList`. It's read-only.
     */
    private JTextArea itemDetailsArea;

    /**
     * The button used to equip or unequip an `EquipableItem`. Its text changes dynamically
     * between "Equip" and "Unequip" based on the selected item's equipped status.
     */
    private JButton equipButton;
    /**
     * The button used to discard (permanently remove) a selected item from the inventory.
     */
    private JButton discardButton;
    /**
     * The button that allows the user to navigate back to the main game menu screen.
     */
    private JButton backButton;

    // Custom renderer for player's inventory list within this panel
    private InventoryItemRenderer inventoryItemRenderer;

    /**
     * Constructs a new `InventoryPanel`.
     * Initializes the UI components for displaying the inventory list, item details,
     * and action buttons. It sets up the layout, colors, fonts, and attaches
     * action listeners to interactive elements.
     *
     * @param manager The `GamePanel` instance, used for screen switching.
     */
    public InventoryPanel(GamePanel manager) {
        this.screenManager = manager;

        setLayout(new BorderLayout(10, 10)); // Uses BorderLayout with padding
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adds empty border for spacing
        setBackground(Color.DARK_GRAY); // Sets panel background color

        // --- TOP: Title ---
        JLabel titleLabel = new JLabel("Inventory");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // --- CENTER: Inventory List and Item Details ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // Two columns: list and details
        centerPanel.setOpaque(false); // Makes panel transparent

        // Left Side: Scrollable List of Inventory Items
        inventoryListModel = new DefaultListModel<>(); // Model to manage items in the JList
        inventoryList = new JList<>(inventoryListModel);
        inventoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allows only one item to be selected at a time
        inventoryList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inventoryList.setBackground(new Color(60, 60, 60)); // Dark gray background for list
        inventoryList.setForeground(Color.WHITE); // White text color
        inventoryList.setSelectionBackground(new Color(100, 100, 100)); // Lighter gray for selection background
        inventoryList.setSelectionForeground(Color.CYAN); // Cyan text for selected item

        // --- IMPORTANT CHANGE: Set the custom cell renderer here for inventory ---
        inventoryItemRenderer = new InventoryItemRenderer();
        inventoryList.setCellRenderer(inventoryItemRenderer);
        // --- End of IMPORTANT CHANGE ---

        // Attaches a ListSelectionListener to update item details when selection changes.
        inventoryList.addListSelectionListener(new ListSelectionListener() {
            /**
             * Called whenever the value of the selection changes.
             * This method is typically called twice for a single selection (once when adjusting, once when final).
             * The `!e.getValueIsAdjusting()` check ensures that the action is performed only once
             * when the user's selection is finalized.
             * @param e The event that characterizes the change.
             */
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    displaySelectedItemDetails(); // Displays details of the newly selected item
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(inventoryList); // Adds scrollability to the item list
        centerPanel.add(listScrollPane);

        // Right Side: Scrollable Area for Item Details
        itemDetailsArea = new JTextArea("Select an item from the list to view its details.");
        itemDetailsArea.setEditable(false); // Prevents user editing
        itemDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        itemDetailsArea.setBackground(new Color(60, 60, 60));
        itemDetailsArea.setForeground(Color.LIGHT_GRAY);
        itemDetailsArea.setLineWrap(true); // Enables word wrapping
        itemDetailsArea.setWrapStyleWord(true); // Wraps at word boundaries
        JScrollPane detailsScrollPane = new JScrollPane(itemDetailsArea); // Adds scrollability to details area
        centerPanel.add(detailsScrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM: Action Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Centers buttons with spacing
        buttonPanel.setOpaque(false); // Makes panel transparent

        // Create and style action buttons
        equipButton = createStyledButton("Equip / Unequip");
        discardButton = createStyledButton("Discard");
        backButton = createStyledButton("Back to Menu");

        // Add buttons to the button panel
        buttonPanel.add(equipButton);
        buttonPanel.add(discardButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Register this panel as the action listener for all buttons
        equipButton.addActionListener(this);
        discardButton.addActionListener(this);
        backButton.addActionListener(this);

        // Initially update button states (they will be disabled if no item is selected)
        updateActionButtonsState();
    }

    /**
     * A helper method to create and style common `JButton` instances.
     * Sets font, background, foreground, and removes focus painting for a consistent look.
     * @param text The text to display on the button.
     * @return A styled `JButton` instance.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180)); // Blueish background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false); // No border painted when focused
        return button;
    }

    /**
     * Sets the current player for the inventory panel.
     * This method should be called when the panel is made visible or when the
     * player character changes. It triggers a refresh of the inventory display.
     *
     * @param player The `Player` object whose inventory is to be displayed and managed.
     */
    public void setPlayer(Player player) {
        this.player = player;
        // Pass the player to the renderer
        if (inventoryItemRenderer != null) {
            inventoryItemRenderer.setPlayer(player);
        }
        refreshInventoryDisplay(); // Updates the UI to show the new player's inventory.
    }

    /**
     * Refreshes the display of the player's inventory list and item details area.
     * It clears the existing list model and repopulates it with the current items
     * from the `player`'s inventory. It also resets the item details area and
     * updates the state of action buttons.
     */
    public void refreshInventoryDisplay() {
        inventoryListModel.clear(); // Clears all items from the display list.
        if (player != null && player.getInventory() != null) {
            for (Item item : player.getInventory()) {
                inventoryListModel.addElement(item); // Adds each item from player's inventory to the list model.
            }
        }
        // If there's an item, select the first one by default after refresh.
        // This ensures details are shown if inventory isn't empty.
        if (!inventoryListModel.isEmpty() && inventoryList.getSelectedIndex() == -1) {
            inventoryList.setSelectedIndex(0);
        }
        // Force repaint to ensure renderer updates are shown, especially if equipped status changed
        inventoryList.repaint();
        displaySelectedItemDetails(); // Updates details and button states for currently selected or first item.
    }

    /**
     * Displays the detailed information of the currently selected item in the `itemDetailsArea`.
     * If no item is selected, it resets the details area to a default message.
     * This method also calls `updateActionButtonsState` to adjust button availability.
     */
    private void displaySelectedItemDetails() {
        Item selectedItem = inventoryList.getSelectedValue(); // Gets the currently selected item.
        if (selectedItem != null) {
            // Using getDetailedStats() from the Item class, which subclasses should override.
            itemDetailsArea.setText(selectedItem.getDetailedStats());

            // Update Equip/Unequip button text based on current equipped status
            if (selectedItem instanceof EquipableItem) {
                if (player != null && player.isItemEquipped((EquipableItem) selectedItem)) {
                    equipButton.setText("Unequip");
                } else {
                    equipButton.setText("Equip");
                }
            } else {
                equipButton.setText("Equip / Unequip"); // Reset to default if not equipable
            }

        } else {
            itemDetailsArea.setText("Select an item from the list to view its details."); // Default message if nothing selected.
            equipButton.setText("Equip / Unequip"); // Reset button text
        }
        updateActionButtonsState(); // Updates button states after displaying details.
    }

    /**
     * Updates the enabled/disabled state of the action buttons (Equip/Unequip, Discard).
     * Buttons are enabled only if an item is selected in the list and a player object is set.
     * It now correctly enables "Equip/Unequip" only for `EquipableItem`s.
     */
    private void updateActionButtonsState() {
        Item selectedItem = inventoryList.getSelectedValue();
        boolean itemSelected = (selectedItem != null);
        boolean playerExists = (player != null);

        // Equip button is enabled only if an item is selected, player exists, AND the item is equipable.
        equipButton.setEnabled(itemSelected && playerExists && (selectedItem instanceof EquipableItem));

        // Discard button is enabled if an item is selected and player exists.
        discardButton.setEnabled(itemSelected && playerExists);
    }

    /**
     * Implements the `ActionListener` interface to handle button click events.
     * It identifies which action button was clicked (Equip, Discard, Back)
     * and performs the corresponding logic, typically by interacting with the
     * `player` object.
     *
     * For "Discard", it includes a confirmation dialog. After any action,
     * the inventory display and button states are refreshed.
     *
     * @param e The `ActionEvent` triggered by a button click.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Item selectedItem = inventoryList.getSelectedValue(); // Gets the currently selected item.

        if (e.getSource() == equipButton) {
            if (selectedItem != null && player != null && selectedItem instanceof EquipableItem) {
                EquipableItem equipableItem = (EquipableItem) selectedItem;
                if (player.isItemEquipped(equipableItem)) {
                    player.unequipItem(equipableItem);
                } else {
                    player.equipItem(equipableItem);
                }
            } else if (selectedItem != null) { // Safeguard if button somehow enabled for non-equipable
                 JOptionPane.showMessageDialog(this, "This item cannot be equipped or unequipped.", "Inventory Action", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else if (e.getSource() == discardButton) {
            if (selectedItem != null && player != null) {
                // Confirmation dialog before discarding an item.
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to discard " + selectedItem.getName() + "? This cannot be undone.",
                    "Confirm Discard", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    player.removeItem(selectedItem); // Calls the player's method to remove the item.
                    JOptionPane.showMessageDialog(this, "Discarded: " + selectedItem.getName(), "Inventory Action", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("Discarding: " + selectedItem.getName());
                }
            }
        } else if (e.getSource() == backButton) {
            screenManager.switchToScreen("gameMenu"); // Switches back to the game menu screen.
            System.out.println("Navigating back to Game Menu from Inventory.");
        }

        // After any action, refresh the display and update button states to reflect changes.
        refreshInventoryDisplay();
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