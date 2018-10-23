package de.noack.artificial.sl3.model;

public class OrderRule {

	private Item parentItem;

	// Codierung:
	private int customerUnhappiness;
	private int stockOverflow;

	// Fitness:
	private double oldFitness;

	private int oldThreshold;

	public OrderRule(Item parentItem) {
		this.parentItem = parentItem;
		customerUnhappiness = 0;
		stockOverflow = 0;
		oldFitness = 0;
	}

	public int getCustomerUnhappiness() {
		return customerUnhappiness;
	}

	public void increaseCustomerUnhappiness() {
		customerUnhappiness++;
		recalculateThreshold();
	}

	public void decreaseCustomerUnhappiness() {
		if (customerUnhappiness > 0) customerUnhappiness--;
		recalculateThreshold();
	}

	public int getStockOverflow() {
		return stockOverflow;
	}

	public void increaseStockOverflow() {
		stockOverflow++;
		recalculateThreshold();
	}

	public void decreaseStockOverflow() {
		if (stockOverflow > 0) stockOverflow--;
		recalculateThreshold();
	}

	private void recalculateThreshold() {
		int threshold = parentItem.getThreshold();
		double customerUnhappinessFactor = customerUnhappiness;
		double stockOverflowFactor = stockOverflow;

		if(calculateFitness() > oldFitness) {
			parentItem.setThreshold(oldThreshold);
			oldFitness = calculateFitness();
			return;
		}

		// To avoid division with 0
		if (customerUnhappinessFactor == 0) {
			customerUnhappinessFactor = Double.MIN_VALUE;
		}
		if (stockOverflowFactor == 0) {
			stockOverflowFactor = Double.MIN_VALUE;
		}
		double ratio = customerUnhappinessFactor / stockOverflowFactor;

		if (ratio > 1) {
			threshold++;
		} else if (ratio < 1 && threshold > 0) {
			threshold--;
		}
		oldThreshold = threshold;
		parentItem.setThreshold(threshold);
	}

	private double calculateFitness() {
		return (customerUnhappiness + stockOverflow) == 0 ? 0 : 1 / (customerUnhappiness + stockOverflow);
	}
}