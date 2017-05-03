package site.hanschen.pretty.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * fix the bug when image zooming crashing in ViewPager (catch IllegalArgumentException exceptions)
 *
 * @author HansChen
 */
public class ViewPagerCatchException extends ViewPager {

    public ViewPagerCatchException(Context context) {
        super(context);
    }

    public ViewPagerCatchException(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
