package pl.sportdata.beestro.modules.bill;

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
import android.widget.EditText;

import java.util.Locale;

import pl.sportdata.beestro.BeestroApplication;
import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.DataProvider;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.entries.Entry;
import pl.sportdata.beestro.entities.items.Item;
import pl.sportdata.beestro.entities.users.PermissionUtils;
import pl.sportdata.beestro.entities.users.User;
import pl.sportdata.beestro.utils.DecimalDigitsInputFilter;
import pl.sportdata.beestro.widgets.NumberPickerView;

public class EntryEditDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnClickListener {

    private static final String ENTRY_KEY = "entry";
    private static final String GUESTS_KEY = "guests";
    public static final String TAG = "entry-edit-dialog-fragment";
    private Entry entry;
    private int originalGuest;
    private OnEntryEditDialogFragmentListener listener;
    private NumberPickerView amountFixedPicker;
    private NumberPickerView guestPicker;
    private CheckBox cancelledCheckbox;
    private DataProvider dataProvider;
    private EditText descriptionEditText;
    private EditText priceEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entry = (Entry) getArguments().getSerializable(ENTRY_KEY);
            originalGuest = entry.getGuest();
        }

        dataProvider = DataProviderFactory.getDataProvider(getActivity());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_entry_edit, null);

        descriptionEditText = (EditText) view.findViewById(R.id.description_text);
        descriptionEditText.setText(entry.getDescription());

        amountFixedPicker = (NumberPickerView) view.findViewById(R.id.amount_picker_view);
        amountFixedPicker.setValue(entry.getAmount());
        amountFixedPicker.setManualEditEnabled(true);

        guestPicker = (NumberPickerView) view.findViewById(R.id.guest_picker_view);
        guestPicker.setValue(entry.getGuest());
        guestPicker.setManualEditEnabled(false);

        priceEditText = (EditText) view.findViewById(R.id.price_text);
        priceEditText.setText(String.format(Locale.getDefault(), "%.2f", entry.getPrice()));
        priceEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        if ("N".equals(dataProvider.getItem(entry.getItemId()).type)) {
            priceEditText.setEnabled(false);
        }

        cancelledCheckbox = (CheckBox) view.findViewById(R.id.storno_checkbox);
        if (entry.isCancelled()) {
            cancelledCheckbox.setChecked(true);
        }

        if (entry.isNew()) {
            cancelledCheckbox.setEnabled(false);
        } else {
            User user = ((BeestroApplication) getActivity().getApplication()).getLoggedUser();
            cancelledCheckbox.setEnabled(PermissionUtils.canUserCancelEntry(user) && !entry.isCancelled());
            descriptionEditText.setVisibility(View.GONE);
            amountFixedPicker.setEnabled(false);
            priceEditText.setEnabled(false);
        }

        builder.setView(view);
        Item item = dataProvider.getItem(entry.getItemId());
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
        if (context instanceof OnEntryEditDialogFragmentListener) {
            listener = (OnEntryEditDialogFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnEntryEditDialogFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                    updateAmount();
                    updateGuest();
                    updateDescription();
                    updateCancelled();
                    entry.setModified(true);
                    listener.onEntryEditionFinished(entry, originalGuest != entry.getGuest());
                    break;
            }
        }
    }

    private void updateCancelled() {
        entry.setCancelled(cancelledCheckbox.isChecked());
    }

    private void updateAmount() {
        entry.setAmount(amountFixedPicker.getValue());
    }

    private void updateGuest() {
        entry.setGuest((int) guestPicker.getValue());
    }

    private void updateDescription() {
        entry.setDescription(descriptionEditText.getText().toString());
    }

    private void updatePrice() {
        try {
            entry.setPrice(Float.parseFloat(priceEditText.getText().toString()));
        } catch (NumberFormatException ignored) {

        }
    }

    public static EntryEditDialogFragment newInstance(@NonNull Entry entry, int guests) {
        EntryEditDialogFragment fragment = new EntryEditDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ENTRY_KEY, entry);
        args.putInt(GUESTS_KEY, guests);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnEntryEditDialogFragmentListener {

        void onEntryEditionFinished(Entry entry, boolean guestChanged);

        void onEntryEditionCancelled();
    }
}