package pl.sportdata.mojito.entities.entries;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import pl.sportdata.mojito.entities.base.MojitoLocalObject;

public class Entry extends MojitoLocalObject {

    private String description;
    private float price;
    @SerializedName("is_cancelled")
    private boolean cancelled;
    @SerializedName("is_moved")
    private boolean moved;
    private float amount;
    @SerializedName("item_id")
    private int itemId;
    private int id;
    @SerializedName("owner_id")
    private int ownerId;
    private int guest = 1;

    private int billEntriesGroup;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getGuest() {
        return guest;
    }

    public void setGuest(int guest) {
        this.guest = guest;
    }

    public int getBillEntriesGroup() {
        return billEntriesGroup;
    }

    public void setBillEntriesGroup(int billEntriesGroup) {
        this.billEntriesGroup = billEntriesGroup;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Entry entry = (Entry) o;

        if (itemId != entry.itemId) {
            return false;
        }
        return id == entry.id;

    }

    @Override
    public int hashCode() {
        int result = itemId;
        result = 31 * result + id;
        return result;
    }

    @Override
    public Entry copy() {
        MojitoLocalObject clonedSuper = super.copy();
        Entry cloned = new Entry();
        cloned.setModified(clonedSuper.isModified());
        cloned.setNew(clonedSuper.isNew());
        cloned.setLegacyId(clonedSuper.getLegacyId());
        cloned.setGuid(UUID.randomUUID().toString());
        cloned.description = description;
        cloned.price = price;
        cloned.cancelled = cancelled;
        cloned.moved = moved;
        cloned.amount = amount;
        cloned.itemId = itemId;
        cloned.id = id;
        cloned.ownerId = ownerId;
        cloned.guest = guest;
        cloned.billEntriesGroup = billEntriesGroup;

        return cloned;
    }
}
