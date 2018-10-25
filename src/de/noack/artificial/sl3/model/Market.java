package de.noack.artificial.sl3.model;

import de.noack.artificial.sl3.gui.EvolutionaryStockSimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Market {

    private Stock stock;
    private Orders orders = new Orders();

    private List<Cashpoint> cashpoints = new ArrayList<>();

    public Market(int maxStockSize) {
        super();
        stock = new Stock(maxStockSize);
    }

    public void createCashpoint() {
        cashpoints.add(new Cashpoint(this));
    }

    public void refresh() {
        for (Map.Entry<Item, Integer> itemsInStock : stock.getInventory().entrySet()) {
            Item item = itemsInStock.getKey();
            double count = itemsInStock.getValue().doubleValue();
            String buy = "Buy for Inventory!";
            System.out.println(item.getName() + " " + item.getThreshold());
            System.out.println("Fitness: " + item.getOrderRule().calculateFitness());
            if (count <= item.getThreshold()) {
                item.setRecommendation(buy);
            } else {
                item.setRecommendation("");
            }
        }
        for (Item item : orders.getDelivery()) {
            stock.putToInventory(item);
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