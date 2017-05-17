package pl.sportdata.mojito.modules.sync;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.DataProviderSyncListener;

public class SyncDialogFragment extends AppCompatDialogFragment implements DataProviderSyncListener {

    private Listener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle(R.string.syncing);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DataProviderFactory.getDataProvider(getActivity()).sync(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            listener = (Listener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SyncDialogFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onSyncFinished(@Nullable String error) {
        if (listener != null) {
            listener.onSyncFinished(error);
        }

        dismiss();
    }

    public interface Listener {

        void onSyncFinished(@Nullable String error);
    }
}
