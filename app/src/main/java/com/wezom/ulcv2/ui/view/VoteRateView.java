package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.interfaces.OnTimeIsOverListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 12.07.2016.
 */
public class VoteRateView extends RelativeLayout { //TODO delete

    private static final int SECOND_IN_MILLISECOND = 1000;

    @BindView(R.id.view_vote_rate_avatar)
    ImageView mAvatarImageView;
    @BindView(R.id.view_vote_rate_dislike_button)
    TextView mDislikeTextView;
    @BindView(R.id.view_vote_rate_like_button)
    TextView mLikeTextView;
    @BindView(R.id.activity_game_vote_view)
    VoteView mVoteView;

    @Setter
    @Accessors(prefix = "m")
    private OnTimeIsOverListener mTimeIsOverListener;
    private CountDownTimer mCountDownTimer;
    private double mMaxProgress;
    private double mCurrentProgress;

    public VoteRateView(Context context) {
        super(context);
        initViews(context);
    }

    public VoteRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public VoteRateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    public void setOnDislikeClickListener(OnClickListener listener) {
        mDislikeTextView.setOnClickListener(listener);
    }

    public void setOnLikeClickListener(OnClickListener listener) {
        mLikeTextView.setOnClickListener(listener);
    }

    public void setAvatarImage(String image) {
        Picasso.with(getContext())
                .load(image)
                .placeholder(R.drawable.ic_default_user_avatar)
                .into(mAvatarImageView);
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
                mVoteView.setProgress((long) (((mMaxProgress - millisUntilFinished) / mMaxProgress) * 100.0));
            }

            @Override
            public void onFinish() {
                mVoteView.setProgress(100);
                mTimeIsOverListener.onTimeIsOverListener();
            }
        };
        mCountDownTimer.start();
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_vote, this);
        ButterKnife.bind(this);
    }
}