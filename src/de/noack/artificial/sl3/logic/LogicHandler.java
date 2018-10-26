package de.noack.artificial.sl3.logic;

import de.noack.artificial.sl3.gui.EvolutionaryStockSimulation;
import de.noack.artificial.sl3.model.Item;
import de.noack.artificial.sl3.model.Market;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class LogicHandler {

	private Market market;
	int day = 0;
	double maximumFitness = 0;

	ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	// Diese Liste enthält Referenzen auf zukünftig durchzuführende Iterationen.
	List <ScheduledFuture> taskList = new ArrayList <>();

	// Liste aller "perfekten" Thresholds
	List <String> perfect = new ArrayList <>();

	private static LogicHandler ourInstance = new LogicHandler();

	public static LogicHandler getInstance() {
		return ourInstance;
	}

	public void initializeMarket(Integer stockSize) {
		market = new Market(stockSize);
	}

	public void addItem(String name, Integer size, Integer deliveryTime) {
		market.getStock().getInventory().put(new Item(name, deliveryTime, size, market.getStock()), 0);
	}

	public void addHeaderToDisplay(GridPane gridPane) {
		gridPane.add(new Label("Stock Size: " + market.getStock().getMaxSize() + " Left: " + market.getStock().getStockLeft()), 0, 0);
		gridPane.add(new Label("Day: " + String.valueOf(day)), 1, 0);
		gridPane.add(new Label("Fitness: " + maximumFitness), 2, 0);
	}

	public void displayItemData(GridPane gridPane, int xPos, int yPos) {

		market.createCashpoint();

		Button sellEachItem = new Button("Simulate selling an item each day!");
		sellEachItem.setOnAction(e -> simulate());
		gridPane.add(sellEachItem, 0, 1);

		Button stopSimulation = new Button("Stop simulation!");
		stopSimulation.setOnAction(e -> stopSimulation());
		gridPane.add(stopSimulation, 1, 1);

		for (Map.Entry <Item, Integer> inventoryEntry : market.getStock().getInventory().entrySet()) {
			gridPane.add(new Label(inventoryEntry.getKey().getName()), xPos++, yPos);
			gridPane.add(new Label("|"), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getSize())), xPos++, yPos);
			gridPane.add(new Label("|"), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getDeliveryTime())), xPos++, yPos);
			gridPane.add(new Label("|"), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getValue() / inventoryEntry.getKey().getSize())), xPos++, yPos);
			gridPane.add(new Label("|"), xPos++, yPos);
			gridPane.add(new Label(String.valueOf(inventoryEntry.getKey().getThreshold())), xPos++, yPos);
			gridPane.add(new Label("|"), xPos++, yPos);
			gridPane.add(new Label(inventoryEntry.getKey().getRecommendation()), xPos, yPos++);

			xPos = 0;
		}
		for(String perfectResult : perfect) {
			gridPane.add(new Label(perfectResult), 0, yPos++);
		}
	}

	private void simulate() {
		executorService = new ScheduledThreadPoolExecutor(8);
		taskList.add(executorService.scheduleAtFixedRate(() -> {
			buyIfRecommended();
			System.out.println("Sell each item once: ");
			for (Map.Entry <Item, Integer> stockEntry : market.getStock().getInventory().entrySet())
				market.getRandomCashpoint().sellItem(stockEntry.getKey());
			day++;
			market.getOrders().storeDelivery();
			if (day > 7) {
				calculateFitnessAndThreshold();
			}
			market.getOrders().nextDay();
			market.refreshRecommendations();
			Platform.runLater(EvolutionaryStockSimulation::initMainWindow);
		}, 1, 100, TimeUnit.MILLISECONDS));
	}

	private void stopSimulation() {
		for (ScheduledFuture futureTask : taskList) futureTask.cancel(true);
		executorService.shutdownNow();
	}

	private void buyIfRecommended() {
		for (Item item : market.getStock().getInventory().keySet())
			if ("Buy for Inventory!".equals(item.getRecommendation())) for (int i = 0; i <= item.getThreshold(); i++) market.getOrders().order(item);
	}

	private double calculateFitnessAndThreshold() {
		double cumulatedFitness = 0;

		// Gesamte Fitness errechnen und "schlechteste" Ware finden.
		Item worstItem = null;
		for (Item item : market.getStock().getInventory().keySet()) {
			double itemFitness = item.getOrderRule().calculateFitness();
			if (worstItem == null || worstItem.getOrderRule().calculateFitness() > item.getOrderRule().calculateFitness()) {
				worstItem = item;
			}
			cumulatedFitness += itemFitness;
		}
		cumulatedFitness = cumulatedFitness / market.getStock().getInventory().size();
		if (cumulatedFitness < maximumFitness) market.getStock().getInventory().keySet().forEach(item -> item.getOrderRule().resetThreshold());
		else {
			maximumFitness = cumulatedFitness;
			for (Item item : market.getStock().getInventory().keySet()) item.getOrderRule().saveThreshold();
		}
		for (Item item : market.getStock().getInventory().keySet()) {
			if (item.equals(worstItem)) {
				enhanceWorstItem(item);
				continue;
			}
			item.getOrderRule().mutateThreshold();
		}
		if (cumulatedFitness == 1.0) {
			StringBuilder builder = new StringBuilder();
			builder.append("Perfect Fitness achieved: ");
			for (Item item : market.getStock().getInventory().keySet()) {
				builder.append(" Item \"" + item.getName() + "\" Threshold: " + item.getThreshold());
			}
			perfect.add(builder.toString());
		}

		return cumulatedFitness;
	}

	private void enhanceWorstItem(Item item) {
		// Schlechteste Ware wird verbessert
		if (item.getOrderRule().getCustomerUnhappiness() > item.getOrderRule().getStockOverflow()) {
			item.setThreshold(item.getThreshold() + 1);
		}
		if (item.getOrderRule().getCustomerUnhappiness() < item.getOrderRule().getStockOverflow() && item.getThreshold() > 0) {
			item.setThreshold(item.getThreshold() - 1);
		}
	}
}