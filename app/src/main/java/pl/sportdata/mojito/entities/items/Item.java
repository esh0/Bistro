package pl.sportdata.mojito.entities.items;

import pl.sportdata.mojito.entities.base.MojitoObject;

public class Item extends MojitoObject {

    public int groupId;

    public Item(int id, int groupId, String name, float price, String type, boolean hasExtras, int extras) {
        super(id, name, hasExtras, type, price, extras);
        this.groupId = groupId;
    }
}
