package de.noack.artificial.sl3.model;

/**
 * Stellt eine Kasse dar, welche Waren verkäuft
 */
public class Cashpoint {

	private Market parent;

	public Cashpoint(Market parent) {
		super();
		this.parent = parent;
	}

	/**
	 * Die übergebene Ware wird, falls vorhanden,
	 * aus dem Lager geholt und dadurch "verkauft"
	 *
	 * @param item
	 */
	public void sellItem(Item item) {
		parent.getStock().retrieveSellableItem(item);
	}
}