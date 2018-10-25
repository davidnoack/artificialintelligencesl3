package de.noack.artificial.sl3.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Stock stellt einen Bestand in Form eines Lagers dar. Dieser besitzt eine Maximalgröße,
 * sowie ein Inventar. Der Bestand kann selbst Waren nachbestellen.
 */
public class Stock {

	// Größe des Lagers
	private int maxSize;

	// Inventar der Waren mit Anzahl der lagernden
	private HashMap <Item, Integer> inventory = new HashMap <>();

	public Stock(int maxSize) {
		this.maxSize = maxSize;
	}

	public HashMap <Item, Integer> getInventory() {
		return inventory;
	}

	/**
	 * Wenn genug Platz im Inventar ist, wird der Bestand um eine Ware erweitert
	 * Zusätzlich wird der OrderRule mitgeteilt, ob das Lager voll ist
	 *
	 * @param item
	 */
	public void putToInventory(Item item) {
		int neededSpace = item.getSize();
		if (isEnoughSpaceFor(neededSpace)) {
			if (inventory.containsKey(item)) {
				neededSpace += inventory.get(item);
			}
			inventory.put(item, neededSpace);
			item.getOrderRule().decreaseStockOverflow();
		} else {
			item.getOrderRule().increaseStockOverflow();
		}
	}

	/**
	 * Prüft, ob die Summe der einzel lagernden Waren inkl. der zu kaufenden Menge
	 * die Größe des Lagers nicht übersteigen würden
	 *
	 * @param spaceNeeded
	 * @return genug Platz vorhanden?
	 */
	public boolean isEnoughSpaceFor(int spaceNeeded) {
		return spaceNeeded < getStockLeft();
	}

	public Item retrieveSellableItem(String itemName) {
		for (Map.Entry <Item, Integer> inventoryEntry : inventory.entrySet()) {
			if (inventoryEntry.getKey().getName().equals(itemName)) {
				if(inventoryEntry.getValue().intValue() * inventoryEntry.getKey().getSize() > 0) {
					inventory.put(inventoryEntry.getKey(), inventoryEntry.getValue() - inventoryEntry.getKey().getSize());
					inventoryEntry.getKey().getOrderRule().decreaseCustomerUnhappiness();
					return inventoryEntry.getKey();
				} else {
					inventoryEntry.getKey().getOrderRule().increaseCustomerUnhappiness();
				}
			}
		}
		return null;
	}

	/**
	 * Prüft, wieviel Platz im Lager frei ist.
	 *
	 * @return Freier Platz
	 */
	public int getStockLeft() {
		int sumUsedSpace = 0;
		for (Integer usedSpace : inventory.values()) sumUsedSpace += usedSpace;
		return (maxSize - sumUsedSpace);
	}

	public int getMaxSize() {
		return maxSize;
	}
}