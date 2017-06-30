package com.wezom.ulcv2.ui.views;


import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.wezom.ulcv2.R;


/**
 * simple custom view of a beating heart which is achieved by a scaling animation
 */
public class HeartBeatView extends AppCompatImageView {
    private static final float DEFAULT_SCALE_FACTOR = 0.1f;
    private static final int DEFAULT_DURATION = 40;
    private HeartBeatCallback heartBeatCallback = null;
    private Drawable heartDrawable;

    private boolean heartBeating = false;

    float scaleFactor = DEFAULT_SCALE_FACTOR;
    float reductionScaleFactor = -scaleFactor;
    int duration = DEFAULT_DURATION;

    private int numberOfHeartBeatsNeeded = 2;
    private int numberOfHeartPulsingDone = 0;

    public void setNumberOfHeartBeats(int numberOfHeartBeatsNeeded) {
        this.numberOfHeartBeatsNeeded = numberOfHeartBeatsNeeded;
    }

    public HeartBeatView(Context context) {
        super(context);
        init();
    }

    public HeartBeatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        populateFromAttributes(context, attrs);
        init();
    }

    public HeartBeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        populateFromAttributes(context, attrs);
        init();
    }

    private void init() {
        heartDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_heart_2);
        setImageDrawable(heartDrawable);
    }

    private void populateFromAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HeartBeatView, 0, 0);
        try {
            scaleFactor = a.getFloat(R.styleable.HeartBeatView_scaleFactor, DEFAULT_SCALE_FACTOR);
            reductionScaleFactor = -scaleFactor;
            duration = a.getInteger(R.styleable.HeartBeatView_duration, DEFAULT_DURATION);
        } finally {
            a.recycle();
        }
    }

    public void start() {
        if (!heartBeating) {
            numberOfHeartPulsingDone = 0;
            animate().scaleXBy(scaleFactor).scaleYBy(scaleFactor).setDuration(duration).setListener(scaleUpListener);
        }
    }

    public void stop() {
        heartBeating = false;
        clearAnimation();
    }

    private static final int milliInMinute = 60000;

    public void setDurationBasedOnBPM(int bpm) {
        if (bpm > 0) {
            duration = Math.round((milliInMinute / bpm) / 3f);
        }
    }

    public HeartBeatView setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public void setHeartBeatCallback(HeartBeatCallback callback) {
        this.heartBeatCallback = callback;
    }

    private final Animator.AnimatorListener scaleUpListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
            heartBeating = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            //we ignore heartBeating as we want to ensure the heart is reduced back to original size
            animate().scaleXBy(reductionScaleFactor).scaleYBy(reductionScaleFactor).setDuration(duration).setListener(scaleDownListener);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }
    };


    private final Animator.AnimatorListener scaleDownListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (numberOfHeartPulsingDone <= numberOfHeartBeatsNeeded) {
                animate().scaleXBy(scaleFactor).scaleYBy(scaleFactor).setDuration(duration).setListener(scaleUpListener);
                numberOfHeartPulsingDone++;
                if (heartBeatCallback != null) {
                    heartBeatCallback.execute();
                }
            }
            if (numberOfHeartPulsingDone > numberOfHeartBeatsNeeded) {
                stop();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }
    };
}
