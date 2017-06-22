package pl.sportdata.mojito.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import pl.sportdata.mojito.R;

public class NumberPickerView extends RelativeLayout implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {

    private EditText valueTextView;
    private Listener listener;
    private float value;
    private float minValue = 1;
    private float maxValue = Float.MAX_VALUE;

    public NumberPickerView(Context context) {
        super(context);
        init(context);
    }

    public NumberPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NumberPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_number_picker, this);
        valueTextView = (EditText) findViewById(R.id.value);
        valueTextView.addTextChangedListener(this);
        valueTextView.setOnEditorActionListener(this);
        findViewById(R.id.add_value).setOnClickListener(this);
        findViewById(R.id.sub_value).setOnClickListener(this);

        updateValueText();
    }

    private void updateValueText() {
        valueTextView.removeTextChangedListener(this);
        valueTextView.setText(String.format(Locale.getDefault(), value - (int) value > 0 ? "%.2f" : "%.0f", value));
        valueTextView.addTextChangedListener(this);
    }

    public void setManualEditEnabled(boolean enabled) {
        valueTextView.setEnabled(enabled);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
        updateValueText();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_value:
                if (value < maxValue) {
                    value = (int) value + 1;
                }
                onValueChanged();
                break;
            case R.id.sub_value:
                if (value > minValue) {
                    value = (int) value - 1;
                }
                onValueChanged();
                break;
        }
    }

    private void onValueChanged() {
        updateValueText();
        if (listener != null) {
            listener.onValueChanged(value);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        listener = null;
        super.onDetachedFromWindow();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            value = Float.parseFloat(s.toString());
            if (!valueTextView.hasFocus()) {
                updateValueText();
            }
        } catch (NumberFormatException | NullPointerException ignored) {

        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            updateValueText();
        }
        return false;
    }

    public interface Listener {

        void onValueChanged(float value);
    }
}
