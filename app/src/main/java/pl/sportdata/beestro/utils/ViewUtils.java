package pl.sportdata.beestro.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.LinearLayout;

import pl.sportdata.beestro.R;

public class ViewUtils {

    public static boolean hitTest(View v, int x, int y) {
        final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
        final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return x >= left && x <= right && y >= top && y <= bottom;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showWebViewDialog(@NonNull Context context, @NonNull String error) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.server_error);
        alert.setPositiveButton(R.string.ok, null);
        WebView wv = new WebView(context);
        wv.loadData(error, "text/html", "UTF-8");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = context.getResources().getDimensionPixelSize(R.dimen.small_margin);
        params.setMargins(margin, margin, margin, margin);
        alert.setView(wv);
        alert.show();
    }

    public static void addTouchDelegate(final View view) {
        final int diff = 15;
        final View parent = (View) view.getParent();
        parent.post(new Runnable() {
            @Override
            public void run() {
                final Rect rect = new Rect();
                view.getHitRect(rect);
                rect.top -= diff;
                rect.bottom += diff;
                rect.left -= diff;
                rect.right += diff;
                parent.setTouchDelegate(new TouchDelegate(rect, view));
            }
        });
    }
}