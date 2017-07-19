package pl.sportdata.beestro.widgets;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by kszalach on 2016-12-24.
 */

public class PagerSwipeItemFrameLayout extends CardView {

    private boolean mCanSwipeLeft;
    private boolean mCanSwipeRight;

    public PagerSwipeItemFrameLayout(Context context) {
        super(context);
    }

    public PagerSwipeItemFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PagerSwipeItemFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return true;
        /*if (mCanSwipeLeft && direction > 0) {
            // return true to avoid view pager consume swipe left (= scroll right) touch events
            return true;
        }
        if (mCanSwipeRight && direction < 0) {
            // return true to avoid view pager consume swipe right (= scroll left) touch events
            return true;
        }

        return false;*/
    }

    public void setCanSwipeLeft(boolean canSwipeLeft) {
        mCanSwipeLeft = canSwipeLeft;
    }

    public void setCanSwipeRight(boolean canSwipeRight) {
        mCanSwipeRight = canSwipeRight;
    }
}

