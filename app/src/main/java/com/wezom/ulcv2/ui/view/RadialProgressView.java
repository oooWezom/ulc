package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.interfaces.OnTimeIsOverListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 29.06.2016.
 */
public class RadialProgressView extends RelativeLayout {
    private static final int SECOND_IN_MILLISECOND = 1000;

    @BindView(R.id.view_radial_progress_image_view)
    ImageView mImageView;
    @BindView(R.id.view_radial_progress_bar)
    CircleProgressBar mProgressBar;

    @Setter
    @Accessors(prefix = "m")
    private OnTimeIsOverListener mTimeIsOverListener;
    private CountDownTimer mCountDownTimer;
    private double mMaxProgress;
    private double mCurrentProgress;


    public RadialProgressView(Context context) {
        super(context);
        initViews(context);
    }

    public RadialProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public void setImageView(String avatar) {
        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + avatar)
                .placeholder(getResources().getDrawable(R.drawable.ic_default_user_avatar))
                .into(mImageView);
    }

    public void setInitData(OnTimeIsOverListener listener, double maxProgress) {
        mTimeIsOverListener = listener;
        mMaxProgress = maxProgress * SECOND_IN_MILLISECOND;
        mCurrentProgress = maxProgress;
    }

    public void start() {
        mCountDownTimer = new CountDownTimer((long) mMaxProgress, SECOND_IN_MILLISECOND) {

            @Override
            public void onTick(long millisUntilFinished) {
                mProgressBar.setProgress((long) (((mMaxProgress - millisUntilFinished) / mMaxProgress) * 100.0));
            }

            @Override
            public void onFinish() {
                mProgressBar.setProgress(100);
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    mTimeIsOverListener.onTimeIsOverListener();
                }, 500);
            }
        };
        mCountDownTimer.start();
    }

    public void finish() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_radial_progress, this);
        ButterKnife.bind(this);
    }
}
