package de.noack.artificial.sl3.model;

import java.util.HashMap;
import java.util.Map;

public class Stock {
	private int maxSize;
	private HashMap <Item, Integer> inventory = new HashMap <>();

	public Stock(int maxSize) {
		this.maxSize = maxSize;
	}

	public HashMap <Item, Integer> getInventory() {
		return inventory;
	}

	public void putToInventory(Item item) {
		int neededSpace = item.getSize();
		if (isEnoughSpaceFor(neededSpace)) {
			if (inventory.containsKey(item)) {
				neededSpace += inventory.get(item);
			}
			inventory.put(item, neededSpace);
		}
	}

	private boolean isEnoughSpaceFor(int spaceNeeded) {
		return spaceNeeded < getStockLeft();
	}

	public Item retrieveSellableItem(String itemName) {
		for (Map.Entry <Item, Integer> inventoryEntry : inventory.entrySet()) {
			if (inventoryEntry.getKey().getName().equals(itemName) && inventoryEntry.getValue().intValue() * inventoryEntry.getKey().getSize() > 0) {
				inventory.put(inventoryEntry.getKey(), inventoryEntry.getValue() - inventoryEntry.getKey().getSize());
				return inventoryEntry.getKey();
			}
		}
		return null;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public int getStockLeft() {
		int sumUsedSpace = 0;
		for (Integer usedSpace : inventory.values()) {
			sumUsedSpace += usedSpace;
		}
		return (maxSize - sumUsedSpace);
	}

	public void recalculateThreshold() {

	}
}