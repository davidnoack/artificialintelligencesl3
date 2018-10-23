package de.noack.artificial.sl3.logic;

import de.noack.artificial.sl3.gui.EvolutionaryStockSimulation;
import de.noack.artificial.sl3.model.Item;
import de.noack.artificial.sl3.model.Market;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Map;

public class LogicHandler {

	private Market market;
	int day = 0;

	private static LogicHandler ourInstance = new LogicHandler();

	public static LogicHandler getInstance() {
		return ourInstance;
	}

	public void initializeMarket(Integer stockSize) {
		market = new Market(stockSize);
	}

	public void addItem(String name, Integer size, Integer deliveryTime) {
		market.getStock().putToInventory(new Item(name, deliveryTime, size));
	}

	public void addHeaderToDisplay(GridPane gridPane) {
		gridPane.add(new Label("Stock Size: " + market.getStock().getMaxSize() + " Left: " + market.getStock().getStockLeft()), 0, 0);
		gridPane.add(new Label("Day: " + String.valueOf(day)), 1, 0);
		gridPane.add(new Label("Fitness: " + calculateFitness()), 2, 0);
	}

	public void displayItemData(GridPane gridPane, int xPos, int yPos) {

		market.createCashpoint();

		Button sellEachItem = new Button("Sell each Item + next day!");
		sellEachItem.setOnAction(e -> sellEachItemOnce());
		gridPane.add(sellEachItem, 0, 1);

		for (Map.Entry <Item, Integer> inventoryEntry : market.getStock().getInventory().entrySet()) {
			gridPane.add(new Label(inventoryEntry.getKey().getName()), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getSize())), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getDeliveryTime())), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getValue() / inventoryEntry.getKey().getSize())), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getThreshold())), xPos++, yPos);
			gridPane.add(new Label(inventoryEntry.getKey().getRecommendation()), xPos++, yPos);
			Button sellItem = new Button("Sell Item!");
			sellItem.setOnAction(e -> sellItemAndRefreshDisplay(inventoryEntry.getKey().getName()));
			gridPane.add(sellItem, xPos++, yPos);
			Button buyItem = new Button("Buy for Inventory!");
			buyItem.setOnAction(e -> market.getOrders().order(inventoryEntry.getKey()));
			gridPane.add(buyItem, xPos, yPos++);

			xPos = 0;
		}
	}

	public void sellEachItemOnce() {
		for (Map.Entry <Item, Integer> stockEntry : market.getStock().getInventory().entrySet()) {
			market.getRandomCashpoint().sellItem(stockEntry.getKey().getName());
		}
		day++;
		buyIfRecommended();
		market.getOrders().nextDay();
	}

	public void buyIfRecommended() {
		for (Item item : market.getStock().getInventory().keySet()) {
			if ("Buy for Inventory!".equals(item.getRecommendation())) {
				buyForInventoryAndRefreshDisplay(item);
			}
		}
	}

	/**
	 * Ruft die Methode zum Kauf einer Ware auf und aktualisiert die Anzeige
	 *
	 * @param itemToBuy
	 */
	private void buyForInventoryAndRefreshDisplay(Item itemToBuy) {
		market.getStock().putToInventory(itemToBuy);
		refreshDisplay();
	}

	/**
	 * Ruft die Methode zum Verkauf einer Ware auf und aktualisiert die Anzeige
	 *
	 * @param itemName
	 */
	private void sellItemAndRefreshDisplay(String itemName) {
		market.getRandomCashpoint().sellItem(itemName);
		refreshDisplay();
	}

	/**
	 * Hier wird die Nachfrage neu berechnet, die Empfehlungen angepasst und
	 * das Hauptfenster neu gerendert.
	 */
	private void refreshDisplay() {
		market.refresh();
		EvolutionaryStockSimulation.initMainWindow();
	}

	public double calculateFitness() {
		double result = 1;
		for (Item item : market.getStock().getInventory().keySet()) {
			result += item.getOrderRule().getCustomerUnhappiness();
			result += item.getOrderRule().getStockOverflow();
		}
		return 1 / result;
	}
}