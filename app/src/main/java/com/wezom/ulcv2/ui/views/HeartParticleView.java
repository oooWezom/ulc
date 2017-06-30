package com.wezom.ulcv2.ui.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;
import com.wezom.ulcv2.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sivolotskiy.v on 25.07.2016.
 */
public class HeartParticleView extends RelativeLayout implements Runnable {

    private static final float ALPHA_HIDE = 0.0f;
    private static final float ALPHA_SHOW = 1.0f;
    private static final float SPEED_MIN_RANGE = 0.05f;
    private static final float SPEED_MAX_RANGE = 0.1f;
    private static final int HEART_COUNT = 3;
    private static final int ANIMATION_TIME_TO_LIVE = 500;
    private static final int ANIMATION_HEARD = 300;

    @BindView(R.id.view_heart_particle_image_view)
    ImageView mImageView;
    @BindView(R.id.view_heart_particle_text_view)
    TextView mCountTextView;
    @BindView(R.id.view_heart_particle_parent_view)
    LinearLayout mParentLayout;

    private int mCount;
    private Handler mHandler;

    public HeartParticleView(Context context) {
        super(context);
        init(context);
    }

    public HeartParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void run() {
        mParentLayout.animate().alpha(ALPHA_HIDE);
    }

    public void shootHearts(Activity activity) {
        if (mHandler != null) {
            mHandler.removeCallbacks(this);
        }

        mCount++;
        mCountTextView.setText(String.valueOf(mCount));

        mParentLayout.animate().alpha(ALPHA_SHOW);
        new ParticleSystem(activity, HEART_COUNT, R.drawable.ic_heart_small, ANIMATION_TIME_TO_LIVE)
                .setSpeedRange(SPEED_MIN_RANGE, SPEED_MAX_RANGE)
                .setFadeOut(ANIMATION_HEARD)
                .setScaleRange(10, 50)
                .oneShot(mImageView, HEART_COUNT);

        mHandler = new Handler();
        mHandler.postDelayed(this, 1000);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_heart_particle, this);
        ButterKnife.bind(this);
        mParentLayout.setAlpha(ALPHA_HIDE);
    }
}
