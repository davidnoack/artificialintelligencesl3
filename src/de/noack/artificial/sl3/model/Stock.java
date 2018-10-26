package de.noack.artificial.sl3.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stock stellt einen Bestand in Form eines Lagers dar. Dieser besitzt eine Maximalgröße, sowie ein Inventar. Der Bestand kann selbst Waren
 * nachbestellen.
 */
public class Stock {

	// Größe des Lagers
	private int maxSize;

	// Inventar der Waren mit Anzahl der lagernden
	private Map <Item, Integer> inventory = new ConcurrentHashMap <>();

	public Stock(int maxSize) {
		this.maxSize = maxSize;
	}

	public Map <Item, Integer> getInventory() {
		return inventory;
	}

	/**
	 * Wenn genug Platz im Inventar ist, wird der Bestand um eine Ware erweitert Zusätzlich wird der OrderRule mitgeteilt, ob das Lager voll ist
	 *
	 * @param item
	 */
	public void putToInventory(Item item) {
		System.out.println("Try to put item: " + item.getName() + " to inventory.");
		if (isEnoughSpaceFor(item.getSize())) {
			inventory.put(item, inventory.get(item) + item.getSize());
			item.getOrderRule().decreaseStockOverflow();
		} else {
			item.getOrderRule().increaseStockOverflow();
		}
	}

	/**
	 * Prüft, ob die Summe der einzel lagernden Waren inkl. der zu kaufenden Menge die Größe des Lagers nicht übersteigen würden
	 *
	 * @param spaceNeeded
	 * @return genug Platz vorhanden?
	 */
	public boolean isEnoughSpaceFor(double spaceNeeded) {
		return spaceNeeded <= getStockLeft();
	}

	public Item retrieveSellableItem(Item item) {
		System.out.println("Try to retrieve sellable Item: " + item.getName());
		for (Map.Entry <Item, Integer> inventoryEntry : inventory.entrySet())
			if (inventoryEntry.getKey().equals(item)) {
				System.out.println("Item found!");
				if (inventoryEntry.getValue() > 0) {
					System.out.println("Item available!");
					inventory.put(inventoryEntry.getKey(), inventoryEntry.getValue() - inventoryEntry.getKey().getSize());
					System.out.println("Put key: " + inventoryEntry.getKey().getName() + " Value: " + inventory.get(inventoryEntry.getKey()));
					inventoryEntry.getKey().getOrderRule().decreaseCustomerUnhappiness();
					return inventoryEntry.getKey();
				} else {
					inventoryEntry.getKey().getOrderRule().increaseCustomerUnhappiness();
				}
			}
		return null;
	}

	/**
	 * Prüft, wieviel Platz im Lager frei ist.
	 *
	 * @return Freier Platz
	 */
	public double getStockLeft() {
		double sumUsedSpace = inventory.values().stream().mapToDouble(usedSpace -> usedSpace).sum();
		return (maxSize - sumUsedSpace);
	}

	public int getMaxSize() {
		return maxSize;
	}
}