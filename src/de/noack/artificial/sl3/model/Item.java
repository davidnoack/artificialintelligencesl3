package de.noack.artificial.sl3.model;

/**
 * Item stellt eine Ware dar. Diese besitzt einen Namen, eine Empfehlung,
 * eine Lieferzeit, einen Grenzwert, ab welchem sie gekauft werden sollte,
 * eine individuelle Größe und hat eine Referenz auf das Lager, welchem sie
 * gehört.
 */
public class Item {

	private String name;
	private String recommendation;
	private int deliveryTime;
	private int threshold;
	private int size;
	private Stock parentStock;

	// Informationen zur Grenzwertermittlung
	private OrderRule orderRule;

	/**
	 * Initialisierung mit Namen, Lieferzeit und Eltern-Lager. Grenzwert und
	 * Empfehlung sind initial 0 bzw. leer.
	 *
	 * @param name
	 * @param deliveryTime
	 * @param size
	 * @param parentStock
	 */
	public Item(String name, int deliveryTime, int size, Stock parentStock) {
		super();
		this.name = name;
		recommendation = "";
		this.deliveryTime = deliveryTime;
		threshold = 0;
		this.size = size;
		this.parentStock = parentStock;
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

	public Stock getParentStock() {
		return parentStock;
	}
}