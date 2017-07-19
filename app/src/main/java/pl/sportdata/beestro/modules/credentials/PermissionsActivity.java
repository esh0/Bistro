package pl.sportdata.beestro.modules.credentials;

import pl.sportdata.beestro.widgets.PermissionView;

public interface PermissionsActivity {

    PermissionView getBillsClosePermissionView();

    PermissionView getBillsOvertakePermissionView();

    PermissionView getImmediateCancellationPermissionView();

    PermissionView getCancellationPermissionView();

    PermissionView getBillsPrefillPermissionView();
}
