package de.noack.artificial.sl3.logic;

import de.noack.artificial.sl3.gui.Main;
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
			sellItem.setOnAction(e -> sellItem(inventoryEntry.getKey()));
			gridPane.add(sellItem, xPos++, yPos);
			Button buyItem = new Button("Buy for Inventory!");
			buyItem.setOnAction(e -> buyForInventory(inventoryEntry.getKey()));
			gridPane.add(buyItem, xPos, yPos++);

			xPos = 0;
		}
	}

	public void sellItem(Item item) {
		market.getRandomCashpoint().sellItem(item.getName());
		Main.initMainWindow();
	}

	public void sellEachItemOnce() {
		for (Map.Entry <Item, Integer> stockEntry : market.getStock().getInventory().entrySet()) {
			market.getRandomCashpoint().sellItem(stockEntry.getKey().getName());
		}
		day++;
		buyIfRecommended();
		market.getOrders().nextDay();
		market.refresh();
	}

	public void buyForInventory(Item item) {
		market.getOrders().order(item);
		market.refresh();
	}

	public void buyIfRecommended() {
		for (Item item : market.getStock().getInventory().keySet()) {
			if ("Buy for Inventory!".equals(item.getRecommendation())) {
				buyForInventory(item);
			}
		}
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