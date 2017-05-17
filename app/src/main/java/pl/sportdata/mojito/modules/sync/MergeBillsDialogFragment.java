package pl.sportdata.mojito.modules.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;

import java.lang.reflect.Type;
import java.util.List;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.DataProvider;
import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.DataProviderSyncListener;
import pl.sportdata.mojito.entities.bills.Bill;

public class MergeBillsDialogFragment extends AppCompatDialogFragment {

    private static final String BILLS_KEY = "bills_key";
    private Listener listener;
    private List<Bill> bills;

    public static MergeBillsDialogFragment newInstance(List<Bill> bills) {
        MergeBillsDialogFragment f = new MergeBillsDialogFragment();
        Bundle args = new Bundle();
        Gson gson = new GsonBuilder().create();
        args.putString(BILLS_KEY, gson.toJson(bills));
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<List<Bill>>() {
            }.getType();
            bills = gson.fromJson(args.getString(BILLS_KEY), listType);
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
        dataProvider.mergeBills(bills, new DataProviderSyncListener() {
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
