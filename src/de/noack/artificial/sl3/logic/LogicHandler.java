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
    Integer stockSize = 0;

    private static LogicHandler ourInstance = new LogicHandler();

    public static LogicHandler getInstance() {
        return ourInstance;
    }

    public void initializeMarket(Integer stockSize) {
        market = new Market(stockSize);
    }

    public void addItem(String name, Integer stock, Integer deliveryTime) {
        market.getStock().buyForInventory(new Item(name, deliveryTime), stock);
    }

    public void addStockSizeToDisplay(GridPane gridPane) {
        gridPane.add(new Label("Stock Size: " + market.getStock().getMaxSize()), 0, 0);
    }

    public void displayItemData(GridPane gridPane, int xPos, int yPos) {

        market.createCashpoint();

        for (Map.Entry<Item, Integer> inventoryEntry : market.getStock().getInventory().entrySet()) {
            gridPane.add(new Label(inventoryEntry.getKey().getName()), xPos++, yPos);
            gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getDemand())), xPos++, yPos);
            gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getDeliveryTime())), xPos++, yPos);
            gridPane.add(new Label(String.valueOf(inventoryEntry.getValue())), xPos++, yPos);
            gridPane.add(new Label(inventoryEntry.getKey().getRecommendation()), xPos++, yPos);

            Button sellItem = new Button("Sell to Customer!");
            sellItem.setOnAction(e -> sellItemAndRefreshDisplay(inventoryEntry.getKey().getName()));
            gridPane.add(sellItem, xPos++, yPos);

            Button buyItem = new Button("Buy for Inventory!");
            buyItem.setOnAction(e -> market.getStock().buyForInventory(inventoryEntry.getKey(), 1));
            gridPane.add(buyItem, xPos, yPos++);

            xPos = 0;
        }
    }

    public void sellItemAndRefreshDisplay(String itemName) {
        market.getRandomCashpoint().sellItem(itemName);
        Main.initMainWindow();
    }
}