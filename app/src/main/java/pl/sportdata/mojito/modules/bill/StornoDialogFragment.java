package pl.sportdata.mojito.modules.bill;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import pl.sportdata.mojito.MojitoApplication;
import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.DataProvider;
import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.base.MojitoObject;
import pl.sportdata.mojito.entities.entries.Entry;
import pl.sportdata.mojito.entities.users.PermissionUtils;
import pl.sportdata.mojito.entities.users.User;
import pl.sportdata.mojito.utils.DecimalDigitsInputFilter;

public class StornoDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String ENTRY_KEY = "entry";
    public static final String TAG = "storno-dialog-fragment";
    private Entry entry;
    private OnStornoDialogFragmentListener listener;
    private CheckBox stornoCheckbox;
    private DataProvider dataProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entry = (Entry) getArguments().getSerializable(ENTRY_KEY);
        }

        dataProvider = DataProviderFactory.getDataProvider(getActivity());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_storno, null);

        EditText priceEditText = (EditText) view.findViewById(R.id.price_text);
        priceEditText.setText(String.format("%.2f", entry.getPrice()));
        priceEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});

        stornoCheckbox = (CheckBox) view.findViewById(R.id.storno_checkbox);

        if (entry.isCancelled()) {
            stornoCheckbox.setChecked(true);
        }
        stornoCheckbox.setOnCheckedChangeListener(this);

        if (entry.isNew()) {
            stornoCheckbox.setEnabled(false);
        } else {
            User user = ((MojitoApplication) getActivity().getApplication()).getLoggedUser();
            stornoCheckbox.setEnabled(PermissionUtils.canUserCancelEntry(user) && !entry.isCancelled());
            priceEditText.setEnabled(false);
        }

        builder.setView(view);
        MojitoObject item = dataProvider.getItem(entry.getItemId());
        if (item == null) {
            item = dataProvider.getDiscount(entry.getItemId());
        }
        if (item == null) {
            item = dataProvider.getMarkup(entry.getItemId());
        }
        if (item == null) {
            item = dataProvider.getPaymentType(entry.getItemId());
        }

        builder.setTitle(item.name);
        builder.setPositiveButton(R.string.accept, this);
        builder.setNegativeButton(R.string.cancel, this);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStornoDialogFragmentListener) {
            listener = (OnStornoDialogFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnStornoDialogFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dataProvider = null;
        listener = null;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (getDialog() != null) {
            switch (which) {
                case DialogInterface.BUTTON_NEGATIVE:
                    listener.onEntryEditionCancelled();
                    break;
                case DialogInterface.BUTTON_POSITIVE:
                    updatePrice();
                    entry.setModified(true);
                    listener.onEntryEditionFinished(entry);
                    break;
            }
        }
    }

    private void updatePrice() {
        String price = ((EditText) getDialog().findViewById(R.id.price_text)).getText().toString();
        try {
            entry.setPrice(Float.parseFloat(price));
        } catch (NumberFormatException e) {

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        entry.setCancelled(b);
    }

    public static StornoDialogFragment newInstance(@NonNull Entry entry) {
        StornoDialogFragment fragment = new StornoDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ENTRY_KEY, entry);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnStornoDialogFragmentListener {

        void onEntryEditionFinished(Entry entry);

        void onEntryEditionCancelled();
    }
}