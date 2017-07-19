package pl.sportdata.beestro.modules.sync;

import android.os.Bundle;

import pl.sportdata.beestro.entities.DataProviderFactory;

public class FullSyncDialogFragment extends SyncDialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DataProviderFactory.getDataProvider(getActivity()).sync(this);
    }
}
