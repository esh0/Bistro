package pl.sportdata.mojito.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pl.sportdata.mojito.R;

public class NumberPickerView extends RelativeLayout implements View.OnClickListener {

    private TextView valueTextView;
    private Listener listener;
    private int value;
    private int minValue = 1;
    private int maxValue = Integer.MAX_VALUE;

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
        valueTextView = (TextView) findViewById(R.id.value);
        findViewById(R.id.add_value).setOnClickListener(this);
        findViewById(R.id.sub_value).setOnClickListener(this);

        updateValueText();
    }

    private void updateValueText() {
        valueTextView.setText(String.valueOf(value));
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        updateValueText();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_value:
                if (value < maxValue) {
                    value++;
                    onValueChanged();
                }
                break;
            case R.id.sub_value:
                if (value > minValue) {
                    value--;
                    onValueChanged();
                }
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

    public interface Listener {

        void onValueChanged(int value);
    }
}
