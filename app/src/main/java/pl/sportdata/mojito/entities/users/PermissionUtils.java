package pl.sportdata.mojito.entities.users;

import android.support.annotation.NonNull;

public class PermissionUtils {

    @NonNull
    public static String getPermissionModeLabel(@Permission.PermissionMode int mode) {
        String label = "";
        switch (mode) {
            case Permission.PERMISSION_MODE_AUTHORIZED:
                label = "dostępne";
                break;
            case Permission.PERMISSION_MODE_UNAUTHORIZED:
                label = "niedostępne";
                break;
            case Permission.PERMISSION_MODE_SUPERVISED:
                label = "autoryzowane";
                break;
        }
        return label;
    }

    public static boolean canUserCloseBills(@NonNull User user) {
        return user.permissions.billsClose == Permission.PERMISSION_MODE_AUTHORIZED;
    }

    public static boolean canUserPrefillBills(@NonNull User user) {
        return user.permissions.billsPrefill == Permission.PERMISSION_MODE_AUTHORIZED;
    }

    public static boolean canUserCancelEntryImmediate(@NonNull User user) {
        return user.permissions.immediateCancellation == Permission.PERMISSION_MODE_AUTHORIZED;
    }

    public static boolean canUserCancelEntry(@NonNull User user) {
        return user.permissions.cancellation == Permission.PERMISSION_MODE_AUTHORIZED;
    }

    public static boolean canUserOvertakeBill(@NonNull User user) {
        return user.permissions.billsOvertake == Permission.PERMISSION_MODE_AUTHORIZED;
    }
}
