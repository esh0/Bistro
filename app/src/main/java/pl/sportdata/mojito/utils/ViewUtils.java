package pl.sportdata.mojito.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

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
        alert.setTitle("Błąd serwera");
        WebView wv = new WebView(context);
        wv.loadData(error, "text/html", "UTF-8");
        alert.setView(wv);
        alert.show();
    }

}