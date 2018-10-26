package de.noack.artificial.sl3.model;

import java.util.Random;

/**
 * Die Klasse "OrderRule" dient zur Ermittlung von Bestellregeln. Diese werden für die dazugehörige
 * Ware (parentItem) ermittelt. Benötigt wird die Kundenunzufriedenheit (customerUnhappiness), sowie
 * die Lagerüberfüllung (stockOverflow). Beide Werte sollen möglichst niedrig gehalten werden. Sie
 * besitzen "increase" und "decrease" Methdoden, da diese Werte immer lediglich um 1 in- bzw. de-
 * krementiert werden sollen. Zusätzlich wird der alte Grenzwert der Ware vorgemerkt werden, falls
 * die Fitness sinkt. Der Grenzwert wird mit 0 initialisiert, die Negativwerte mit 100, damit der Algo-
 * rithmus sich aus dem "Kredit" arbeiten muss.
 */
public class OrderRule {

	private Item parentItem;

	// Codierung:
	private double customerUnhappiness;
	private double stockOverflow;

	private int oldThreshold;

	public OrderRule(Item parentItem) {
		this.parentItem = parentItem;
		customerUnhappiness = 100;
		stockOverflow = 100;
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

	/**
	 * Speichert den alten Grenzwert, sofern dieser im Kontext der Lagerung überhaupt realistisch ist.
	 */
	public void saveThreshold() {
		if (parentItem.getThreshold() <= Double.valueOf(parentItem.getParentStock().getMaxSize()) /
				Double.valueOf(parentItem.getSize()))
			oldThreshold = parentItem.getThreshold();
	}

	/**
	 * Setzt den Grenzwert der Ware mangels Fitness zurück
	 */
	public void resetThreshold() {
		parentItem.setThreshold(oldThreshold);
	}

	/**
	 * Mutiert den Grenzwert, indem dieser mit einer bestimmten Wahrscheinlichkeit um bis zu
	 * 4 Punkten erhöht oder gemindert wird.
	 * Mit einer Wahrscheinlichkeit von 1% wird der Wert sogar auf 0 zurückgesetzt.
	 */
	public void mutateThreshold() {
		if (new Random().nextDouble() < 0.1) {
			parentItem.setThreshold(oldThreshold + 1);
		} else if (new Random().nextDouble() < 0.1 && oldThreshold >= 1) {
			parentItem.setThreshold(oldThreshold - 1);
		} else if (new Random().nextDouble() < 0.05) {
			parentItem.setThreshold(oldThreshold + 2);
		} else if (new Random().nextDouble() < 0.05 && oldThreshold >= 2) {
			parentItem.setThreshold(oldThreshold - 2);
		} else if (new Random().nextDouble() < 0.025) {
			parentItem.setThreshold(oldThreshold + 3);
		} else if (new Random().nextDouble() < 0.025 && oldThreshold >= 3) {
			parentItem.setThreshold(oldThreshold - 3);
		} else if (new Random().nextDouble() < 0.0125) {
			parentItem.setThreshold(oldThreshold + 4);
		} else if (new Random().nextDouble() < 0.0125 && oldThreshold >= 4) {
			parentItem.setThreshold(oldThreshold - 4);
		} else if (new Random().nextDouble() < 0.01) {
			oldThreshold = 0;
			parentItem.setThreshold(oldThreshold);
		}
	}

	/**
	 * Die Fitness soll möglichst hoch sein, wenn Kundenunzufriedenheit und Lagerüberfüllung 0
	 * sind Ansonsten wird der Fitnesswert durch Erhöhung einer oder beider Werte gemindert.
	 *
	 * @return Individueller Fitnesswert einer Ware
	 */
	public double calculateFitness() {
		if (customerUnhappiness == 0 && stockOverflow == 0) return 1;
		return 1 / (customerUnhappiness + stockOverflow);
	}

	public double getCustomerUnhappiness() {
		return customerUnhappiness;
	}

	public double getStockOverflow() {
		return stockOverflow;
	}
}