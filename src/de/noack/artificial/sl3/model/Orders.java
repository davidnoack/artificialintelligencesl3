package de.noack.artificial.sl3.model;

import java.util.*;

/**
 * Orders bietet eine Liste der anstehenden Lieferungen.
 */
public class Orders {

	//Map mit den verbleibenden Tagen als Key und den Items, welche dann kommen
	//als Value
	private Map <Integer, List <Item>> orders = new HashMap <>();

	/**
	 * Lässt alle Warenlisten einen Tag "näher rutschen"
	 */
	public void nextDay() {
		for (Map.Entry <Integer, List <Item>> order : orders.entrySet()) {
			if(order.getKey() > 0 ) {
				if (orders.containsKey(order.getKey() - 1)) {
					orders.get(order.getKey() - 1).addAll(order.getValue());
				} else {
					orders.put(order.getKey() - 1, order.getValue());
				}
				orders.get(order.getKey()).removeAll(order.getValue());
			}
		}
	}

	/**
	 * Gibt alle Items zurück welche 0 verbleibende Liefertage haben
	 *
	 * @return Liste von Items
	 */
	public List <Item> getDelivery() {
		if (orders.containsKey(0)) return orders.get(0);
		return Collections.emptyList();
	}

	/**
	 * Bestellt eine Ware, indem sie mit ihrer jeweiligen Lieferzeit in
	 * die Map gepackt wird
	 *
	 * @param itemToOrder
	 */
	public void order(Item itemToOrder) {
		if(orders.containsKey(itemToOrder.getDeliveryTime())) {
			orders.get(itemToOrder.getDeliveryTime()).add(itemToOrder);
		} else {
			ArrayList<Item> itemList = new ArrayList <>();
			itemList.add(itemToOrder);
			orders.put(itemToOrder.getDeliveryTime(), itemList);
		}
	}
}