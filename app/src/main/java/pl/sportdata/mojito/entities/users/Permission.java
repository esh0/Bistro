package pl.sportdata.mojito.entities.users;

import com.google.gson.annotations.SerializedName;

import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class Permission implements Serializable {

    public static final int PERMISSION_MODE_UNAUTHORIZED = 0; //niedostępne
    public static final int PERMISSION_MODE_SUPERVISED = 1; //autoryzowane
    public static final int PERMISSION_MODE_AUTHORIZED = 2; //dostępne
    @SerializedName("bills_close")
    @PermissionMode
    public final int billsClose; // zamykanie rachunku
    @SerializedName("price_change")
    @PermissionMode
    public final int priceChange; // nie wiemy co to, nie używamy
    @SerializedName("bills_overtake")
    @PermissionMode
    public final int billsOvertake; // przejmowanie rachunku z obecnego właściela na nowego, uprawnienia musi mieć osoba przekazująca
    @SerializedName("immediate_cancellation")
    @PermissionMode
    public final int immediateCancellation; // storno pozycji rachunku bez wcześniejszej synchronizacji
    @PermissionMode
    public final int cancellation; // storno pozycji rachunku po synchronizacji
    @SerializedName("bills_prefill")
    @PermissionMode
    public final int billsPrefill; // nie wiemy co to, nie używamy

    public Permission(@PermissionMode int billsClose, @PermissionMode int priceChange, @PermissionMode int billsOvertake,
            @PermissionMode int immediateCancellation, @PermissionMode int cancellation, @PermissionMode int billsPrefill) {
        this.billsClose = billsClose;
        this.priceChange = priceChange;
        this.billsOvertake = billsOvertake;
        this.immediateCancellation = immediateCancellation;
        this.cancellation = cancellation;
        this.billsPrefill = billsPrefill;
    }

    public static Permission createAllPermission() {
        return new Permission(PERMISSION_MODE_AUTHORIZED, PERMISSION_MODE_AUTHORIZED, PERMISSION_MODE_AUTHORIZED, PERMISSION_MODE_AUTHORIZED,
                PERMISSION_MODE_AUTHORIZED, PERMISSION_MODE_AUTHORIZED);
    }

    public static Permission createSupervisedPermission() {
        return new Permission(PERMISSION_MODE_SUPERVISED, PERMISSION_MODE_SUPERVISED, PERMISSION_MODE_SUPERVISED, PERMISSION_MODE_SUPERVISED,
                PERMISSION_MODE_SUPERVISED, PERMISSION_MODE_SUPERVISED);
    }

    @Retention(SOURCE)
    @IntDef({PERMISSION_MODE_UNAUTHORIZED, PERMISSION_MODE_SUPERVISED, PERMISSION_MODE_AUTHORIZED})
    public @interface PermissionMode {

    }
}
