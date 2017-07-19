package pl.sportdata.beestro.entities.items;

import pl.sportdata.beestro.entities.base.BeestroObject;

public class Item extends BeestroObject {

    public int groupId;

    public Item(int id, int groupId, String name, float price, String type, boolean hasExtras, int extras) {
        super(id, name, hasExtras, type, price, extras);
        this.groupId = groupId;
    }
}
