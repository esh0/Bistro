package pl.sportdata.mojito.modules.credentials;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.users.Permission;
import pl.sportdata.mojito.modules.base.BasePresenter;

public class PermissionsActivityPresenter extends BasePresenter<PermissionsActivityImpl> {

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissionsLabels();
    }

    private void updatePermissionsLabels() {
        Permission permissions = getApplication().getLoggedUser().permissions;
        getActivity().getBillsClosePermissionView().setPermission(getActivity().getString(R.string.perm_close_bills), permissions.billsClose);
        getActivity().getBillsOvertakePermissionView().setPermission(getActivity().getString(R.string.perm_bills_overtake), permissions.billsOvertake);
        getActivity().getImmediateCancellationPermissionView()
                .setPermission(getActivity().getString(R.string.perm_immediate_cancellation), permissions.immediateCancellation);
        getActivity().getCancellationPermissionView().setPermission(getActivity().getString(R.string.perm_cancellation), permissions.cancellation);
        getActivity().getBillsPrefillPermissionView().setPermission(getActivity().getString(R.string.perm_bills_prefill), permissions.billsPrefill);
    }
}
