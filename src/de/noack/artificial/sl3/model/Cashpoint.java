package de.noack.artificial.sl3.model;

public class Cashpoint {

	private Market parent;

	public Cashpoint(Market parent) {
		super();
		this.parent = parent;
	}

	public void sellItem(String itemName) {
		parent.getStock().retrieveSellableItem(itemName);
		getParent().refresh();
	}

	public Market getParent() {
		return parent;
	}
}