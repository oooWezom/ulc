package com.wezom.ulcv2.ui.behaviours;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kartavtsev.s on 20.01.2016.
 */
public class PlaceholderBehavior extends CoordinatorLayout.Behavior<ViewGroup> {

    public PlaceholderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ViewGroup child, View dependency) {
        float translationY = dependency.getHeight() - ViewCompat.getTranslationY(dependency);
        child.setPadding(0, 0, 0, (int) translationY);
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewGroup child, View dependency) {
        return (dependency instanceof Snackbar.SnackbarLayout);
    }
}