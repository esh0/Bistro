package pl.sportdata.beestro.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

public class DrawableUtils {

    private static final int[] EMPTY_STATE = {};

    public static void clearState(Drawable drawable) {
        if (drawable != null) {
            drawable.setState(EMPTY_STATE);
        }
    }

    public static void setTint(ImageView view, Context context, @DrawableRes int resId, @ColorRes int tint) {
        Drawable bg;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bg = VectorDrawableCompat.create(context.getResources(), resId, null);
            view.setColorFilter(tint, PorterDuff.Mode.MULTIPLY);
        } else {
            bg = ContextCompat.getDrawable(context, resId);
            DrawableCompat.setTint(bg, context.getResources().getColor(tint));
        }
        view.setImageDrawable(bg);
    }
}
