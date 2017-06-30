package com.wezom.ulcv2.ui.views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Zorin A.
 * Date: 04.10.2016.
 */

public class SelfRemovableImageView extends AppCompatImageView {
    public SelfRemovableImageView(Context context) {
        super(context);
    }

    public SelfRemovableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void selfDestructIn(int duration) {
        rx.Observable
                .timer(duration, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    RelativeLayout parent = (RelativeLayout) getParent();
                    parent.removeView(SelfRemovableImageView.this);
                });

    }

    public <T extends ViewGroup> void selfDestructIn(int duration, T parent) {
        rx.Observable
                .timer(duration, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    parent.removeView(SelfRemovableImageView.this);
                });

    }
}
