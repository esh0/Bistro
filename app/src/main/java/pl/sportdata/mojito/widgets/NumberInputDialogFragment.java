package pl.sportdata.mojito.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.utils.AllowedCharsInputFilter;
import pl.sportdata.mojito.utils.DecimalDigitsInputFilter;

public class NumberInputDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnClickListener, View.OnClickListener {

    private static final String KEY_TITLE = "title";
    private static final String KEY_DEFAULT_VALUE = "default-value";
    private static final String KEY_POSITIVE_BUTTON_LABEL = "positive-button-label";
    private static final String KEY_NEGATIVE_BUTTON_LABEL = "negative-button-label";
    private static final String KEY_NEUTRAL_BUTTON_LABEL = "neutral-button-label";
    private static final String KEY_ALLOW_DECIMALS = "allow-decimals";
    private static final String KEY_ALLOW_TEXT = "allow-text";
    private static final String KEY_ALLOWED_CHARS = "allowed-chars";

    private String title;
    private String defaultValue;
    private String positiveButtonLabel;
    private String negativeButtonLabel;
    private String neutralButtonLabel;
    private boolean allowDecimals;
    private boolean allowText;
    private OnNumberInputDialogFragmentListener listener;
    private char[] allowedChars;

    public static NumberInputDialogFragment newInstance(String title, @Nullable String defaultValue, String positiveButtonLabel) {
        NumberInputDialogFragment fragment = new NumberInputDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_DEFAULT_VALUE, defaultValue);
        args.putString(KEY_POSITIVE_BUTTON_LABEL, positiveButtonLabel);
        fragment.setArguments(args);
        return fragment;
    }

    public static NumberInputDialogFragment newInstance(String title, @Nullable String defaultValue, String positiveButtonLabel, String negativeButtonLabel) {
        NumberInputDialogFragment fragment = new NumberInputDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_DEFAULT_VALUE, defaultValue);
        args.putString(KEY_POSITIVE_BUTTON_LABEL, positiveButtonLabel);
        args.putString(KEY_NEGATIVE_BUTTON_LABEL, negativeButtonLabel);
        fragment.setArguments(args);
        return fragment;
    }

    public static NumberInputDialogFragment newInstance(String title, @Nullable String defaultValue, String positiveButtonLabel, String negativeButtonLabel,
            String neutralButtonLabel) {
        NumberInputDialogFragment fragment = new NumberInputDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_DEFAULT_VALUE, defaultValue);
        args.putString(KEY_POSITIVE_BUTTON_LABEL, positiveButtonLabel);
        args.putString(KEY_NEGATIVE_BUTTON_LABEL, negativeButtonLabel);
        args.putString(KEY_NEUTRAL_BUTTON_LABEL, neutralButtonLabel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString(KEY_TITLE);
            defaultValue = args.getString(KEY_DEFAULT_VALUE);
            positiveButtonLabel = args.getString(KEY_POSITIVE_BUTTON_LABEL);
            negativeButtonLabel = args.getString(KEY_NEGATIVE_BUTTON_LABEL);
            neutralButtonLabel = args.getString(KEY_NEUTRAL_BUTTON_LABEL);
            allowDecimals = args.getBoolean(KEY_ALLOW_DECIMALS);
            allowText = args.getBoolean(KEY_ALLOW_TEXT);
            allowedChars = args.getCharArray(KEY_ALLOWED_CHARS);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        int offset = getActivity().getResources().getDimensionPixelSize(R.dimen.text_margin);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_numberinput, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(title).setView(view, offset, offset, offset, offset);

        if (!TextUtils.isEmpty(defaultValue)) {
            ((TextView) view.findViewById(R.id.number_input_edit_text)).setText(defaultValue);
            ((EditText) view.findViewById(R.id.number_input_edit_text)).selectAll();
        }

        if (allowDecimals) {
            ((EditText) view.findViewById(R.id.number_input_edit_text)).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ((EditText) view.findViewById(R.id.number_input_edit_text)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        } else if (allowText) {
            ((EditText) view.findViewById(R.id.number_input_edit_text)).setInputType(InputType.TYPE_CLASS_TEXT);
        }

        if (allowedChars != null && allowedChars.length > 0) {
            InputFilter[] currentFilters = ((EditText) view.findViewById(R.id.number_input_edit_text)).getFilters();
            InputFilter[] newFilters;
            if (currentFilters == null || currentFilters.length == 0) {
                newFilters = new InputFilter[1];
            } else {
                newFilters = new InputFilter[currentFilters.length + 1];
                for (int i = 0, size = currentFilters.length; i < size; i++) {
                    newFilters[i] = currentFilters[i];
                }
            }

            newFilters[newFilters.length - 1] = new AllowedCharsInputFilter(allowedChars);
            ((EditText) view.findViewById(R.id.number_input_edit_text)).setFilters(newFilters);
        }

        if (!TextUtils.isEmpty(positiveButtonLabel)) {
            builder.setPositiveButton(positiveButtonLabel, this);
        }

        if (!TextUtils.isEmpty(negativeButtonLabel)) {
            builder.setNeutralButton(negativeButtonLabel, this);
        }

        if (!TextUtils.isEmpty(neutralButtonLabel)) {
            builder.setNeutralButton(neutralButtonLabel, this);
        }

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
                if (button != null) {
                    button.setOnClickListener(NumberInputDialogFragment.this);
                }
            }
        });
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNumberInputDialogFragmentListener) {
            listener = (OnNumberInputDialogFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnNumberInputDialogFragmentListener");
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
            EditText numberInput = (EditText) getDialog().findViewById(R.id.number_input_edit_text);
            if (numberInput != null) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        listener.onNegativeButtonClicked(getTag());
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        listener.onNeutralButtonClicked(getTag());
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        listener.onPositiveButtonClicked(numberInput.getText().toString(), getTag());
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).getId() == view.getId()) {
            EditText numberInput = (EditText) getDialog().findViewById(R.id.number_input_edit_text);
            if (!TextUtils.isEmpty(numberInput.getText())) {
                onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
                getDialog().dismiss();
            } else {
                numberInput.setError(getActivity().getString(R.string.error_field_required));
            }
        }
    }

    public static class Builder {

        private final Bundle args;
        private final NumberInputDialogFragment fragment;

        public Builder() {
            fragment = new NumberInputDialogFragment();
            args = new Bundle();
        }

        public Builder setTitle(String title) {
            args.putString(KEY_TITLE, title);
            return this;
        }

        public Builder setDefaultValue(String defaultValue) {
            args.putString(KEY_DEFAULT_VALUE, defaultValue);
            return this;
        }

        public Builder setPositiveButtonLabel(String positiveButtonLabel) {
            args.putString(KEY_POSITIVE_BUTTON_LABEL, positiveButtonLabel);
            return this;
        }

        public Builder setNegativeButtonLabel(String negativeButtonLabel) {
            args.putString(KEY_NEGATIVE_BUTTON_LABEL, negativeButtonLabel);
            return this;
        }

        public Builder setNeutralButtonLabel(String neutralButtonLabel) {
            args.putString(KEY_NEUTRAL_BUTTON_LABEL, neutralButtonLabel);
            return this;
        }

        public Builder setAllowDecimals(boolean allowDecimals) {
            args.putBoolean(KEY_ALLOW_DECIMALS, allowDecimals);
            return this;
        }

        public Builder setAllowText(boolean allowText) {
            args.putBoolean(KEY_ALLOW_TEXT, allowText);
            return this;
        }

        public Builder setAllowedCharacters(char[] characters) {
            args.putCharArray(KEY_ALLOWED_CHARS, characters);
            return this;
        }

        public NumberInputDialogFragment build() {
            fragment.setArguments(args);
            return fragment;
        }
    }

    public interface OnNumberInputDialogFragmentListener {

        void onPositiveButtonClicked(String value, String numberInputFragmentTag);

        void onNegativeButtonClicked(String numberInputFragmentTag);

        void onNeutralButtonClicked(String numberInputFragmentTag);
    }
}
