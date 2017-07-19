package pl.sportdata.beestro.modules.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;

import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.DataProvider;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.DataProviderSyncListener;
import pl.sportdata.beestro.entities.bills.Bill;

public class MergeBillsDialogFragment extends AppCompatDialogFragment {

    private static final String BILL_KEY = "bill_key";
    private Listener listener;
    private Bill bill;

    public static MergeBillsDialogFragment newInstance(Bill bill) {
        MergeBillsDialogFragment f = new MergeBillsDialogFragment();
        Bundle args = new Bundle();
        Gson gson = new GsonBuilder().create();
        args.putString(BILL_KEY, gson.toJson(bill));
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            Gson gson = new GsonBuilder().create();
            bill = gson.fromJson(args.getString(BILL_KEY), Bill.class);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.syncing));
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final DataProvider dataProvider = DataProviderFactory.getDataProvider(getActivity());
        dataProvider.mergeBills(bill, new DataProviderSyncListener() {
            @Override
            public void onSyncFinished(@Nullable String error) {
                if (listener != null) {
                    listener.onMergeBillsFinished(error);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            listener = (Listener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MergeBillsDialogFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface Listener {

        void onMergeBillsFinished(@Nullable String error);
    }
}
