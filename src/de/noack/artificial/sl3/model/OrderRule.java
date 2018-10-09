package de.noack.artificial.sl3.model;

public class OrderRule {

	private Item parentItem;

	// Codierung:
	private int customerUnhappiness;
	private int stockOverflow;

	// Fitness:
	private double oldFitness;
	private double newFitness;

	public OrderRule(Item parentItem) {
		this.parentItem = parentItem;
		customerUnhappiness = 0;
		stockOverflow = 0;
		oldFitness = 0;
		newFitness = 0;
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
		// Lower Threshold, if stock is full
		int threshold = parentItem.getThreshold();
		if (stockOverflow > 0 && threshold > 0) {
			parentItem.setThreshold(threshold--);
		}
		// Increase Threshold, if customers are unhappy and there is less overflow
		if (customerUnhappiness > 0 && customerUnhappiness > stockOverflow) {
			// For each unhappy customer threshold is raised by delivery day count
			parentItem.setThreshold(threshold + customerUnhappiness * parentItem.getDeliveryTime());
		}
	}
}
