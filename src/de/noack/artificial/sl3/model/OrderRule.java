package de.noack.artificial.sl3.model;

import java.util.Random;

public class OrderRule {

    private Item parentItem;

    // Codierung:
    private double customerUnhappiness;
    private double stockOverflow;

    private int oldThreshold;

    public OrderRule(Item parentItem) {
        this.parentItem = parentItem;
        customerUnhappiness = 0;
        stockOverflow = 0;
        oldThreshold = 0;
    }

    public void increaseCustomerUnhappiness() {
        customerUnhappiness++;
    }

    public void decreaseCustomerUnhappiness() {
        if (customerUnhappiness > 0) customerUnhappiness--;
    }

    public void increaseStockOverflow() {
        stockOverflow++;
    }

    public void decreaseStockOverflow() {
        if (stockOverflow > 0) stockOverflow--;
    }

    public void saveThreshold() {
        oldThreshold = parentItem.getThreshold();
    }

    public void resetThreshold() {
        parentItem.setThreshold(oldThreshold);
    }

    public void mutateThreshold() {
        if(new Random().nextDouble() < 0.01) {
            oldThreshold++;
            parentItem.setThreshold(oldThreshold);
        } else if(new Random().nextDouble() < 0.01 && oldThreshold > 0) {
            oldThreshold--;
            parentItem.setThreshold(oldThreshold);
        }
    }

    public double calculateFitness() {
        if(customerUnhappiness == 0 && stockOverflow == 0) return 1;
        return 1 / (customerUnhappiness + stockOverflow);
    }

    public double getCustomerUnhappiness() {
        return customerUnhappiness;
    }

    public double getStockOverflow() {
        return stockOverflow;
    }
}