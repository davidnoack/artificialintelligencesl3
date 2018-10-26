package de.noack.artificial.sl3.model;

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

    public void refreshRecommendations() {
        for (Map.Entry<Item, Integer> itemsInStock : stock.getInventory().entrySet()) {
            Item item = itemsInStock.getKey();
            String buy = "Buy for Inventory!";
            System.out.println(item.getName() + " " + item.getThreshold());
            System.out.println("Fitness: " + item.getOrderRule().calculateFitness());
            if (itemsInStock.getValue() <= item.getThreshold()) {
                item.setRecommendation(buy);
            } else {
                item.setRecommendation("");
            }
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