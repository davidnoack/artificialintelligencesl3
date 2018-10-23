package de.noack.artificial.sl3.model;

public class Item {

	private String name;
	private String recommendation;
	private int deliveryTime;
	private int threshold;
	private int size;

	private OrderRule orderRule;

	public Item(String name, int deliveryTime, int size) {
		super();
		this.name = name;
		recommendation = "";
		this.deliveryTime = deliveryTime;
		threshold = 0;
		this.size = size;
		orderRule = new OrderRule(this);
	}

	public String getName() {
		return name;
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

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public int getSize() {
		return size;
	}

	public OrderRule getOrderRule() {
		return orderRule;
	}
}