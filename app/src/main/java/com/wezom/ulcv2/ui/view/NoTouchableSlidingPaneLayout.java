package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 17.07.2016
 * Time: 15:53
 */

public class NoTouchableSlidingPaneLayout extends SlidingPaneLayout {
    Context mContext;

    public NoTouchableSlidingPaneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
//        int activeSide = displayMetrics.widthPixels / 2;
//        return ev.getX() > activeSide && super.onTouchEvent(ev);
        return false;
    }
}
