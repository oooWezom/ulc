package com.wezom.ulcv2.ui.behaviours;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kartavtsev.s on 20.01.2016.
 */
public class AvatarBehavior extends CoordinatorLayout.Behavior<ViewGroup> {

    private Context mContext;

    public AvatarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ViewGroup child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            float scale = (float) dependency.getBottom() / (dependency.getHeight());
            scale = (float) Math.pow(scale, 3);
            child.setScaleX(scale);
            child.setScaleY(scale);
            child.setPivotX(0);
            child.setPivotY(1f);
            if (scale < 0.8f) {
                float alphaScale = 1 - ((0.8f - scale) / (0.8f - 0.7f));
                child.setAlpha(alphaScale);
            } else {
                child.setAlpha(1f);
            }
            child.setVisibility(child.getAlpha() > 0 ? View.VISIBLE : View.GONE);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewGroup child, View dependency) {
        return (dependency instanceof AppBarLayout);
    }
}