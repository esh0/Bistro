package pl.sportdata.mojito.entities.discounts;

import pl.sportdata.mojito.entities.base.MojitoObject;

public class Discount extends MojitoObject {

    public Discount(int id, String name, boolean hasExtras, String type, float price, int extras) {
        super(id, name, hasExtras, type, price, extras);
    }
}
