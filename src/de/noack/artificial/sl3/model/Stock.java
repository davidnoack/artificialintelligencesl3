package de.noack.artificial.sl3.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stock stellt einen Bestand in Form eines Lagers dar. Dieser besitzt eine Maximalgröße,
 * sowie ein Inventar. Der Bestand kann selbst Waren nachbestellen.
 */
public class Stock {

	// Größe des Lagers
	private int maxSize;

	// Inventar der Waren mit Anzahl der lagernden
	// Concurrent, da parallele Lese- und Schreibzugriffe
	private Map <Item, Integer> inventory = new ConcurrentHashMap <>();

	public Stock(int maxSize) {
		this.maxSize = maxSize;
	}

	public Map <Item, Integer> getInventory() {
		return inventory;
	}

	/**
	 * Wenn genug Platz im Inventar ist, wird der Bestand um eine Ware erweitert.
	 * Zusätzlich wird eine etwaige Lagerüberfüllung mittels Erhöhung des Wertes
	 * in der OrderRule angemahnt.
	 *
	 * @param item
	 */
	public void putToInventory(Item item) {
		if (isEnoughSpaceFor(item.getSize())) {
			inventory.put(item, inventory.get(item) + item.getSize());
			item.getOrderRule().decreaseStockOverflow();
		} else {
			item.getOrderRule().increaseStockOverflow();
		}
	}

	/**
	 * Prüft, ob die Summe der einzel lagernden Waren inkl. der zu kaufenden Menge die
	 * Größe des Lagers nicht übersteigen würden
	 *
	 * @param spaceNeeded
	 * @return genug Platz vorhanden?
	 */
	public boolean isEnoughSpaceFor(double spaceNeeded) {
		return spaceNeeded <= getStockLeft();
	}

	/**
	 * Iteriert über das Inventar und versucht die zu verkaufende Ware zu finden. Ist diese gefunden,
	 * wird geprüft, ob noch etwas auf Lager ist und dann zurückgegeben. Ist die Ware nicht auf Lager,
	 * wird die Kundenunzufriedenheit in der OrderRule der Ware erhöht.
	 *
	 * @param item
	 * @return verkaufte Ware
	 */
	public Item retrieveSellableItem(Item item) {
		for (Map.Entry <Item, Integer> inventoryEntry : inventory.entrySet())
			if (inventoryEntry.getKey().equals(item)) {
				if (inventoryEntry.getValue() > 0) {
					inventory.put(inventoryEntry.getKey(), inventoryEntry.getValue() - inventoryEntry.getKey().getSize());
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