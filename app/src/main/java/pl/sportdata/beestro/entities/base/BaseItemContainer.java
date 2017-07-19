package pl.sportdata.beestro.entities.base;

import java.io.Serializable;
import java.util.List;

public class BaseItemContainer<T> implements Serializable {

    public final List<T> items;
    public final int id;
    public final String name;

    public BaseItemContainer(List<T> items, int id, String name) {
        this.items = items;
        this.id = id;
        this.name = name;
    }
}
