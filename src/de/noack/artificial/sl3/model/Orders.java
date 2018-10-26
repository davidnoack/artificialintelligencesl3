package de.noack.artificial.sl3.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Orders bietet eine Liste der anstehenden Lieferungen.
 */
public class Orders {

	// Map mit den verbleibenden Tagen als Key und den Items, welche dann kommen
	// als Value
	private Map <Integer, List <Item>> orders = new ConcurrentHashMap <>();

	/**
	 * Lässt alle Warenlisten einen Tag "näher rutschen"
	 */
	public void nextDay() {
		if (orders.size() > 0) for (int index = 1; index <= 7; index++) {
			if (orders.containsKey(index)) {
				if (orders.containsKey(index - 1)) orders.get(index - 1).addAll(orders.get(index));
				else orders.put(index - 1, orders.get(index));
				orders.get(index).clear();
			}
		}
	}

	/**
	 * Gibt alle Items zurück welche 0 verbleibende Liefertage haben
	 *
	 * @return Liste von Items
	 */
	public void storeDelivery() {
		if (orders.containsKey(0)) for (Item item : orders.get(0))
			if (item.getParentStock().isEnoughSpaceFor(item.getSize())) {
				item.getParentStock().putToInventory(item);
				orders.get(0).remove(item);
			}
	}

	/**
	 * Bestellt eine Ware, indem sie mit ihrer jeweiligen Lieferzeit in die Map gepackt wird
	 *
	 * @param itemToOrder
	 */
	public void order(Item itemToOrder) {
		if (orders.containsKey(itemToOrder.getDeliveryTime())) {
			orders.get(itemToOrder.getDeliveryTime()).add(itemToOrder);
		} else {
			List <Item> itemList = new CopyOnWriteArrayList <>();
			itemList.add(itemToOrder);
			orders.put(itemToOrder.getDeliveryTime(), itemList);
		}
	}
}