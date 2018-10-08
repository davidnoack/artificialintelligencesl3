package de.noack.artificial.sl3.model;

public class Cashpoint extends DomainElement {

	private Market parent;

	public Cashpoint(Market parent) {
		super();
		this.parent = parent;
	}

	public void sellItem(String itemName) {
		parent.getStock().retrieveSellableItem(itemName);
		getParent().refreshRecommendations();
	}

	public Market getParent() {
		return parent;
	}
}