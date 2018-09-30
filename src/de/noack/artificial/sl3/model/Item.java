package de.noack.artificial.sl3.model;

public class Item extends DomainElement {

    private String name;
    private double demand;
    private String recommendation;
    private int deliveryTime;
    private int threshold;

    public Item(String name, int deliveryTime) {
        super();
        this.name = name;
        recommendation = "";
        demand = 0;
        this.deliveryTime = deliveryTime;
    }

    public String getName() {
        return name;
    }

    public double getDemand() {
        return demand;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}