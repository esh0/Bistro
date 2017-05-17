package pl.sportdata.mojito.entities.bills;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pl.sportdata.mojito.entities.base.MojitoLocalObject;
import pl.sportdata.mojito.entities.entries.Entry;

public class Bill extends MojitoLocalObject {

    @SerializedName("extra_description")
    private String extraDescription;
    @SerializedName("table_number")
    private int tableNumber;
    @SerializedName("guest_number")
    private int guestNumber;
    private List<Entry> entries;
    @SerializedName("move_to")
    private int moveTo;
    private float value;
    private String guests;
    private int time;
    @SerializedName("owner_id")
    private int ownerId;
    private String type;
    private int id;
    private String card;
    private String blocked;
    private int closing;

    public String getExtraDescription() {
        return extraDescription;
    }

    public void setExtraDescription(String extraDescription) {
        this.extraDescription = extraDescription;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public int getMoveTo() {
        return moveTo;
    }

    public void setMoveTo(int moveTo) {
        this.moveTo = moveTo;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getGuests() {
        return guests;
    }

    public void setGuests(String guests) {
        this.guests = guests;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getClosing() {
        return closing;
    }

    public void setClosing(int closing) {
        this.closing = closing;
    }

    public int getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(int guestNumber) {
        this.guestNumber = guestNumber;
    }

    public Bill copy() {
        MojitoLocalObject clonedSuper = super.copy();
        Bill cloned = new Bill();
        cloned.setModified(clonedSuper.isModified());
        cloned.setNew(clonedSuper.isNew());
        cloned.setLegacyId(clonedSuper.getLegacyId());
        cloned.setGuid(UUID.randomUUID().toString());
        cloned.extraDescription = extraDescription;
        cloned.tableNumber = tableNumber;
        cloned.moveTo = moveTo;
        cloned.value = value;
        cloned.guests = guests;
        cloned.guestNumber = guestNumber;
        cloned.time = time;
        cloned.ownerId = ownerId;
        cloned.type = type;
        cloned.id = id;
        cloned.card = card;
        cloned.blocked = blocked;
        cloned.closing = closing;
        if (entries != null) {
            cloned.entries = new ArrayList<>(entries.size());
            for (int i = 0, size = entries.size(); i < size; i++) {
                cloned.entries.add(entries.get(i).copy());
            }
        }

        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Bill bill = (Bill) o;

        return id == bill.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
