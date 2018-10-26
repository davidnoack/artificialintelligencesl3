package de.noack.artificial.sl3.model;

public class Cashpoint {

	private Market parent;

	public Cashpoint(Market parent) {
		super();
		this.parent = parent;
	}

	public void sellItem(Item item) {
		parent.getStock().retrieveSellableItem(item);
	}
}