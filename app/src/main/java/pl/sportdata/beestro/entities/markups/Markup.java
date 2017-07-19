package pl.sportdata.beestro.entities.markups;

import pl.sportdata.beestro.entities.base.BeestroObject;

public class Markup extends BeestroObject {

    public Markup(int id, String name, boolean hasExtras, String type, float price, int extras) {
        super(id, name, hasExtras, type, price, extras);
    }
}
