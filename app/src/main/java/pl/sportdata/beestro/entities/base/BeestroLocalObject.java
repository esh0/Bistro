package pl.sportdata.beestro.entities.base;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BeestroLocalObject implements Serializable {

    private boolean modified;
    @SerializedName("is_new")
    private boolean isNew;
    @SerializedName("legacy_id")
    private String legacyId;
    private String guid;

    public BeestroLocalObject() {

    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public BeestroLocalObject copy() {
        BeestroLocalObject cloned = new BeestroLocalObject();
        cloned.modified = modified;
        cloned.isNew = isNew;
        cloned.legacyId = legacyId;
        cloned.guid = guid;

        return cloned;
    }
}
