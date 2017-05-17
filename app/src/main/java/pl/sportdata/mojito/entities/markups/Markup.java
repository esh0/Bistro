package pl.sportdata.mojito.entities.markups;

import pl.sportdata.mojito.entities.base.MojitoObject;

public class Markup extends MojitoObject {

    public Markup(int id, String name, boolean hasExtras, String type, float price, int extras) {
        super(id, name, hasExtras, type, price, extras);
    }
}
