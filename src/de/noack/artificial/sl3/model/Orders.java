package de.noack.artificial.sl3.model;

import java.util.*;

public class Orders {

	private Map <Integer, List <Item>> orders = new HashMap <>();

	public void nextDay() {
		for (Map.Entry <Integer, List <Item>> order : orders.entrySet()) {
			if (order.getKey() > 0 && orders.containsKey(order.getKey() - 1)) {
				orders.get(order.getKey() - 1).addAll(order.getValue());
				orders.get(order.getKey()).removeAll(order.getValue());
			}
		}
	}

	public List <Item> getDelivery() {
		if (orders.containsKey(0)) {
			return orders.get(0);
		}
		return Collections.emptyList();
	}

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
