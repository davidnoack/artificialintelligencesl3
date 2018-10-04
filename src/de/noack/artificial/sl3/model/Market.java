package de.noack.artificial.sl3.model;

import de.noack.artificial.sl3.gui.Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Market extends DomainElement {

    private Stock stock;

    private HashSet<Cashpoint> cashpoints = new HashSet<>();

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

    public void recalculateDemandForAllItems() {
        int amountOfAllSoldItems = 0;
        HashMap<Item, Integer> amountsPerItem = new HashMap<>();
        for (Cashpoint singleCashpoint : cashpoints) {
            HashSet<SoldItems> allSoldItemsOfThisCashpoint = singleCashpoint.getSoldItems();
            for (SoldItems soldItems : allSoldItemsOfThisCashpoint) {
                amountOfAllSoldItems += soldItems.getCount();
                int oldAmount = amountsPerItem.get(soldItems.getItem()) == null ? 0 : amountsPerItem.get(soldItems.getItem());
                amountsPerItem.put(soldItems.getItem(), oldAmount + soldItems.getCount());
            }
        }
        for (Map.Entry<Item, Integer> itemWithSellingCount : amountsPerItem.entrySet())
            itemWithSellingCount.getKey().setDemand(itemWithSellingCount.getValue() / amountOfAllSoldItems);
        refreshRecommendations();
    }

    public void refreshRecommendations() {
        for (Map.Entry<Item, Integer> itemsInStock : stock.getInventory().entrySet()) {
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