package de.noack.artificial.sl3.model;

import de.noack.artificial.sl3.gui.Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Market extends DomainElement {

	private Stock stock;

	private HashSet <Cashpoint> cashpoints = new HashSet <>();

	public Market(int maxStockSize) {
		super();
		createStock(maxStockSize);
	}

	public void createCashpoint() {
		cashpoints.add(new Cashpoint(this));
	}

	public void createStock(int maxSize) {
		stock = new Stock(maxSize);
	}

	public Stock getStock() {
		return stock;
	}

	public void refreshRecommendations() {
		for (Map.Entry <Item, Integer> itemsInStock : stock.getInventory().entrySet()) {
			Item item = itemsInStock.getKey();
			double count = itemsInStock.getValue().doubleValue();
			String buy = "Buy for Inventory!";
			if (count <= item.getThreshold()) {
				item.setRecommendation(buy);
			} else {
				item.setRecommendation("");
			}
		}
		Main.initMainWindow();
	}

	public Cashpoint getRandomCashpoint() {
		for (Cashpoint cashpoint : cashpoints) {
			return cashpoint;
		}
		return null;
	}
}