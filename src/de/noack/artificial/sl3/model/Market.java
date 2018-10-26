package de.noack.artificial.sl3.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Ein Markt, welcher über ein Lager (Stock), mehrerer Kassen, sowie einem
 * "Orders"-Objekt zur Verwaltung der Bestellungen verfügt.
 */
public class Market {

	private Stock stock;
	private Orders orders = new Orders();
	private List <Cashpoint> cashpoints = new ArrayList <>();

	/**
	 * Mit der Initialisierung des Marktes geht eine Initialisierung des dazugehörigen
	 * Lagers einher. Daher wird die maximale Lagergröße benötigt.
	 *
	 * @param maxStockSize
	 */
	public Market(int maxStockSize) {
		stock = new Stock(maxStockSize);
	}

	/**
	 * Erstellt eine Kasse und fügt diesen Markt als Elternteil hinzu.
	 */
	public void createCashpoint() {
		cashpoints.add(new Cashpoint(this));
	}

	/**
	 * Die Methode "refreshRecommendations" iteriert über alle Waren des Lagers und prüft,
	 * ob für eine Warensorte weniger Stücke auf Lager sind, als der Grenzwert "vorschreibt".
	 * Ist der Grenzwert unterschritten, wird eine Kaufempfehlung gegeben.
	 */
	public void refreshRecommendations() {
		for (Map.Entry <Item, Integer> itemsInStock : stock.getInventory().entrySet()) {
			Item item = itemsInStock.getKey();
			if (itemsInStock.getValue() <= item.getThreshold()) item.setRecommendation("Buy for Inventory!");
			else item.setRecommendation("");
		}
	}

	/**
	 * Gibt eine zufällige der verfügbaren Kassen zurück
	 *
	 * @return Kasse
	 */
	public Cashpoint getRandomCashpoint() {
		return cashpoints.get((new Random()).nextInt(cashpoints.size()));
	}

	public Stock getStock() {
		return stock;
	}

	public Orders getOrders() {
		return orders;
	}
}