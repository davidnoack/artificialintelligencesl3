package de.noack.artificial.sl3.model;

import de.noack.artificial.sl3.gui.Main;

import java.util.HashMap;
import java.util.Map;

public class Stock {

    private int maxSize;
    private HashMap<Item, Integer> inventory = new HashMap<>();

    public Stock(int maxSize) {
        this.maxSize = maxSize;
    }

    public HashMap<Item, Integer> getInventory() {
        return inventory;
    }

    public void buyForInventory(Item item, int amount) {
        if (isEnoughSpaceFor(amount)) {
            Integer newAmount = amount;
            if (inventory.containsKey(item)) {
                newAmount += inventory.get(item);
            }
            inventory.put(item, newAmount);
            Main.initMainWindow();
        }
    }

    private boolean isEnoughSpaceFor(int amount) {
        int sumUsedAmount = 0;
        for (Integer usedAmount : inventory.values()) {
            sumUsedAmount += usedAmount;
            if (amount > (maxSize - sumUsedAmount)) {
                return false;
            }
        }
        return true;
    }

    public Item retrieveSellableItem(String itemName) {
        for (Map.Entry<Item, Integer> inventoryEntry : inventory.entrySet()) {
            if (inventoryEntry.getKey().getName().equals(itemName) && inventoryEntry.getValue().intValue() > 0) {
                inventory.put(inventoryEntry.getKey(), inventoryEntry.getValue() - 1);
                return inventoryEntry.getKey();
            }
        }
        return null;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
