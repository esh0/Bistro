package pl.sportdata.beestro.entities.base;

import com.google.gson.annotations.SerializedName;

public class BeestroObject extends BeestroLocalObject {

    @SerializedName("has_extras")
    public final boolean hasExtras;
    public final String name;
    public final String type;
    public final int extras;
    public final int id;
    public final float price;

    public BeestroObject(int id, String name, boolean hasExtras, String type, float price, int extras) {
        this.id = id;
        this.name = name;
        this.hasExtras = hasExtras;
        this.type = type;
        this.price = price;
        this.extras = extras;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BeestroObject that = (BeestroObject) o;

        if (id != that.id) {
            return false;
        }
        if (Float.compare(that.price, price) != 0) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return type != null ? type.equals(that.type) : that.type == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        return result;
    }
}
