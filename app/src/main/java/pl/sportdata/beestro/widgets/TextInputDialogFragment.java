package pl.sportdata.beestro.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pl.sportdata.beestro.R;

public class TextInputDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnClickListener, View.OnClickListener {

    private static final String KEY_TITLE = "title";
    private static final String KEY_DEFAULT_VALUE = "default-value";
    private static final String KEY_POSITIVE_BUTTON_LABEL = "positive-button-label";
    private static final String KEY_NEGATIVE_BUTTON_LABEL = "negative-button-label";
    private static final String KEY_NEUTRAL_BUTTON_LABEL = "neutral-button-label";
    private static final String KEY_SHOULD_VALIDATE = "should-validate";

    private String title;
    private String defaultValue;
    private String positiveButtonLabel;
    private String negativeButtonLabel;
    private String neutralButtonLabel;
    private OnTextInputDialogFragmentListener listener;
    private boolean shouldValidate;

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
            shouldValidate = args.getBoolean(KEY_SHOULD_VALIDATE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        int offset = getActivity().getResources().getDimensionPixelSize(R.dimen.text_margin);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_textinput, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(title).setView(view, offset, offset, offset, offset);

        if (!TextUtils.isEmpty(defaultValue)) {
            ((TextView) view.findViewById(R.id.text_input_edit_text)).setText(defaultValue);
            ((EditText) view.findViewById(R.id.text_input_edit_text)).selectAll();
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
                    button.setOnClickListener(TextInputDialogFragment.this);
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
        if (context instanceof OnTextInputDialogFragmentListener) {
            listener = (OnTextInputDialogFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnTextInputDialogFragmentListener");
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
            EditText TextInput = (EditText) getDialog().findViewById(R.id.text_input_edit_text);
            if (TextInput != null) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        listener.onNegativeButtonClicked(getTag());
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        listener.onNeutralButtonClicked(getTag());
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        listener.onPositiveButtonClicked(TextInput.getText().toString(), getTag());
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).getId() == view.getId()) {
            EditText TextInput = (EditText) getDialog().findViewById(R.id.text_input_edit_text);
            if (!shouldValidate || !TextUtils.isEmpty(TextInput.getText())) {
                onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
                getDialog().dismiss();
            } else {
                TextInput.setError(getActivity().getString(R.string.error_field_required));
            }
        }
    }

    public static class Builder {

        private final Bundle args;
        private final TextInputDialogFragment fragment;

        public Builder() {
            fragment = new TextInputDialogFragment();
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

        public Builder setShouldValidate(boolean shouldValidate) {
            args.putBoolean(KEY_SHOULD_VALIDATE, shouldValidate);
            return this;
        }

        public TextInputDialogFragment build() {
            fragment.setArguments(args);
            return fragment;
        }
    }

    public interface OnTextInputDialogFragmentListener {

        void onPositiveButtonClicked(String value, String textInputFragmentTag);

        void onNegativeButtonClicked(String textInputFragmentTag);

        void onNeutralButtonClicked(String textInputFragmentTag);
    }
}
