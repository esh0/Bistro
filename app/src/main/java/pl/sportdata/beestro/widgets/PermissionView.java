package pl.sportdata.beestro.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.users.Permission;
import pl.sportdata.beestro.entities.users.PermissionUtils;

public class PermissionView extends LinearLayout {

    private TextView nameTextView;
    private TextView statusTextView;

    public PermissionView(Context context) {
        super(context);
        init(context);
    }

    public PermissionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PermissionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PermissionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_permission, this);
        nameTextView = (TextView) findViewById(R.id.name);
        statusTextView = (TextView) findViewById(R.id.status);
    }

    public void setPermission(String name, @Permission.PermissionMode int mode) {
        nameTextView.setText(name);
        statusTextView.setText(PermissionUtils.getPermissionModeLabel(mode));
    }
}
