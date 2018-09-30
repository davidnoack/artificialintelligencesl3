package de.noack.artificial.sl3.model;

import java.util.HashSet;

public class Cashpoint extends DomainElement {

    private Market parent;
    private HashSet<SoldItems> soldItems;

    public Cashpoint(Market parent) {
        super();
        this.parent = parent;
        this.soldItems = new HashSet<>();
    }

    public void sellItem(String itemName) {
        Item itemToSell = parent.getStock().retrieveSellableItem(itemName);
        for (SoldItems alreadySoldItems : soldItems) {
            if (alreadySoldItems.isItemEqualTo(itemToSell)) {
                getParent().recalculateDemandForAllItems();
                alreadySoldItems.increaseCount();
                return;
            }
        }
        getParent().recalculateDemandForAllItems();
        soldItems.add(new SoldItems(itemToSell));
    }

    public HashSet<SoldItems> getSoldItems() {
        return soldItems;
    }

    public Market getParent() {
        return parent;
    }
}