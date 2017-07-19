package pl.sportdata.beestro.entities.discounts;

import pl.sportdata.beestro.entities.base.BeestroObject;

public class Discount extends BeestroObject {

    public Discount(int id, String name, boolean hasExtras, String type, float price, int extras) {
        super(id, name, hasExtras, type, price, extras);
    }
}
