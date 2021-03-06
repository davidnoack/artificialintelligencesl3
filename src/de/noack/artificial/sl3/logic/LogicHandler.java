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

/**
 * Der LogicHandler kapselt alle Zugriffe von außen auf das Modell. Er initialisiert den Markt
 * und damit den Kontext der Anwendung. Er gibt alle Daten des Modells auf der Oberfläche aus
 * und steuert die Zugriffe welche über die Oberfläche getätigt werden.
 */
public class LogicHandler {

	private Market market;

	// Tag bzw. Iterationsnummer
	private int day = 0;

	// Maximale erreichte Fitness
	double maximumFitness = 0;
	// Aktuell erreichte Fitness
	double currentFitness = 0;

	// Service zur Planung von Tasks
	ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	// Diese Liste enthält Referenzen auf zukünftig durchzuführende Iterationen.
	List <ScheduledFuture> taskList = new ArrayList <>();

	// Anzeige der aktuell besten Thresholds
	String perfect = "";

	private static LogicHandler ourInstance = new LogicHandler();

	public static LogicHandler getInstance() {
		return ourInstance;
	}

	/**
	 * Initialisiert den Markt mit der über die Oberfläche übermittelten Lagergröße.
	 *
	 * @param stockSize
	 */
	public void initializeMarket(Integer stockSize) {
		market = new Market(stockSize);
	}

	/**
	 * Initialisiert die Items, welche in der Aufgabenstellung vorgegeben worden sind.
	 *
	 * @param name
	 * @param size
	 * @param deliveryTime
	 */
	public void addItem(String name, Integer size, Integer deliveryTime) {
		market.getStock().getInventory().put(new Item(name, deliveryTime, size, market.getStock()), 0);
	}

	/**
	 * Fügt Lagergröße, aktuelle Lagerfüllung, Tag bzw. Iteration und die bisher maximal erreichte Fitness an.
	 *
	 * @param gridPane
	 */
	public void addHeaderToDisplay(GridPane gridPane) {
		gridPane.add(new Label("Stock Size: " + market.getStock().getMaxSize() + " Left: " +
				market.getStock().getStockLeft()), 0, 0);
		gridPane.add(new Label("Day: " + String.valueOf(day)), 1, 0);
		gridPane.add(new Label("Best Fitness: " + maximumFitness), 2, 0);
		gridPane.add(new Label("Current Fitness: " + currentFitness), 3, 0);
	}

	/**
	 * Erstellt jeweils einen Button zum Starten und einen zum Stoppen der Simulation. Des weiteren werden
	 * die Daten der Waren ausgegeben und überprüft, ob ein "perfektes" Ergebnis vorliegt. Dieses wird,
	 * sofern vorhanden, auch ausgegeben.
	 *
	 * @param gridPane
	 * @param xPos
	 * @param yPos
	 */
	public void displayItemData(GridPane gridPane, int xPos, int yPos) {
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
		gridPane.add(new Label(perfect), 0, yPos, 11, 1);
	}

	/**
	 * Hier wird die Simulation der Tage angestoßen. Je Tag wird jede Ware einmal verkauft, es wird, falls empfohlen,
	 * nachbestellt, die Lieferungen des Tages werden abgeholt und der nächste Tag wird begonnen. Zudem werden die
	 * Empfehlungen aktualisiert und der Hauptbildschirm neu gerendert.
	 */
	private void simulate() {
		executorService = new ScheduledThreadPoolExecutor(8);
		taskList.add(executorService.scheduleAtFixedRate(() -> {
			buyIfRecommended();
			for (Map.Entry <Item, Integer> stockEntry : market.getStock().getInventory().entrySet())
				market.getStock().retrieveSellableItem(stockEntry.getKey());
			day++;
			market.getOrders().storeDelivery();
			if(Double.valueOf(day) % 100 == 0) calculateFitnessAndThreshold();
			market.getOrders().nextDay();
			market.refreshRecommendations();
			// Erforderlich, da über JavaFX Thread zu handlen
			Platform.runLater(EvolutionaryStockSimulation::initMainWindow);
		}, 1, 100, TimeUnit.MILLISECONDS));
	}

	/**
	 * Stoppt die Simulation. Ggf. werden dennoch laufende Tasks weiter verarbeitet.
	 */
	private void stopSimulation() {
		for (ScheduledFuture futureTask : taskList) futureTask.cancel(true);
		executorService.shutdownNow();
	}

	/**
	 * Wenn der Kauf einer Ware empfohlen wird, wird die Ware nachbestellt.
	 */
	private void buyIfRecommended() {
		for (Item item : market.getStock().getInventory().keySet())
			if ("Buy for Inventory!".equals(item.getRecommendation())) for (int i = 0; i <= item.getThreshold(); i++)
				market.getOrders().order(item);
	}

	/**
	 * Ermittelt anhand der individuellen Fitnesswerte der Items den aktuellen Gesamtfitnesswert. Vorsicht vorm
	 * Overfitting! Es wird die "schlechteste Ware" ermittelt und verbessert. Zudem wird geprüft, ob die aktuelle
	 * Fitness die Gesamtfitness übersteigt. Ist die Fitness schlechter, wird für jedes Item überprüft, ob die
	 * Kundenzufriedenheit schlechter ist, oder die Lagerüber- füllung. Falls das Ergebnis besser ist als das vorher
	 * maximale, werden die Thresholds gespeichert, damit diese später ausgegeben werden können.
	 *
	 * @return Fitnesswert
	 */
	private double calculateFitnessAndThreshold() {
		currentFitness = 0;

		// Gesamte Fitness errechnen und "schlechteste" Ware finden.
		Item worstItem = null;
		for (Item item : market.getStock().getInventory().keySet()) {
			double itemFitness = item.getOrderRule().calculateFitness();
			if (worstItem == null || worstItem.getOrderRule().calculateFitness() >
					item.getOrderRule().calculateFitness()) {
				worstItem = item;
			}
			currentFitness += itemFitness;
		}
		currentFitness = currentFitness / market.getStock().getInventory().size();
		if (currentFitness < maximumFitness) market.getStock().getInventory().keySet()
				.forEach(item -> item.getOrderRule().resetThreshold());
		else {
			maximumFitness = currentFitness;
			StringBuilder builder = new StringBuilder();
			builder.append("Current best Fitness achieved with: ");
			for (Item item : market.getStock().getInventory().keySet()) {
				builder.append(" Item \"" + item.getName() + "\" Threshold: " + item.getThreshold());
			}
			perfect = builder.toString();
			for (Item item : market.getStock().getInventory().keySet()) item.getOrderRule().saveThreshold();
		}
		for (Item item : market.getStock().getInventory().keySet()) {
			if (item.equals(worstItem)) {
				enhanceWorstItem(item);
				continue;
			}
			item.getOrderRule().mutateThreshold();
			// Wert kann sinnvollerweise nicht größer sein, als das Lager an dieser Ware aufnehmen kann
			// Double, um Probleme mit Integer-Divisionen zu vermeinden
			if (item.getThreshold() > Double.valueOf(market.getStock().getMaxSize()) / Double.valueOf(item.getSize()))
				item.setThreshold(item.getThreshold() - 1);
		}

		return currentFitness;
	}

	/**
	 * Das schlechteste Item wird anhand der Unzufriedenheitswerte überprüft und korrigiert.
	 *
	 * @param item
	 */
	private void enhanceWorstItem(Item item) {
		// Schlechteste Ware wird verbessert
		if (item.getOrderRule().getCustomerUnhappiness() > item.getOrderRule().getStockOverflow()) {
			item.setThreshold(item.getThreshold() + 1);
		}
		if (item.getOrderRule().getCustomerUnhappiness() < item.getOrderRule().getStockOverflow()
				&& item.getThreshold() > 0) {
			item.setThreshold(item.getThreshold() - 1);
		}
	}
}