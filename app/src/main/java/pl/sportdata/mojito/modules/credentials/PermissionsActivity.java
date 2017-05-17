package pl.sportdata.mojito.modules.credentials;

import pl.sportdata.mojito.widgets.PermissionView;

public interface PermissionsActivity {

    PermissionView getBillsClosePermissionView();

    PermissionView getBillsOvertakePermissionView();

    PermissionView getImmediateCancellationPermissionView();

    PermissionView getCancellationPermissionView();

    PermissionView getBillsPrefillPermissionView();
}
