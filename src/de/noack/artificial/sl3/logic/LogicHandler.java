package de.noack.artificial.sl3.logic;

import de.noack.artificial.sl3.gui.Main;
import de.noack.artificial.sl3.model.Item;
import de.noack.artificial.sl3.model.Market;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Map;

public class LogicHandler {

	Market market;

	private static LogicHandler ourInstance = new LogicHandler();

	public static LogicHandler getInstance() {
		return ourInstance;
	}

	public void initializeMarket(Integer stockSize) {
		market = new Market(stockSize);
	}

	public void addItem(String name, Integer size, Integer deliveryTime) {
		market.getStock().buyForInventory(new Item(name, deliveryTime, size), 0);
	}

	public void addStockSizeToDisplay(GridPane gridPane) {
		gridPane.add(new Label("Stock Size: " + market.getStock().getMaxSize() + "Left: " + market.getStock().getStockLeft()), 0, 0);
	}

	public void displayItemData(GridPane gridPane, int xPos, int yPos) {

		market.createCashpoint();

		Button sellItem = new Button("Sell each Item once!");
		sellItem.setOnAction(e -> sellEachItemOnce());
		gridPane.add(sellItem, 0, 1);

		for (Map.Entry <Item, Integer> inventoryEntry : market.getStock().getInventory().entrySet()) {
			gridPane.add(new Label(inventoryEntry.getKey().getName()), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getSize())), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getDeliveryTime())), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getValue())), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getThreshold())), xPos++, yPos);
			gridPane.add(new Label(inventoryEntry.getKey().getRecommendation()), xPos++, yPos);
			Button buyItem = new Button("Buy for Inventory!");
			buyItem.setOnAction(e -> buyForInventory(inventoryEntry.getKey(), 1));
			gridPane.add(buyItem, xPos, yPos++);

			xPos = 0;
		}
	}

	public void sellEachItemOnce() {
		for (Map.Entry <Item, Integer> stockEntry : market.getStock().getInventory().entrySet()) {
			market.getRandomCashpoint().sellItem(stockEntry.getKey().getName());
		}
		Main.initMainWindow();
	}

	public void buyForInventory(Item item, int count) {
		market.getStock().buyForInventory(item, count);
		Main.initMainWindow();
	}
}