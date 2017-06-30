package com.wezom.ulcv2.ui.view;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ui.listeners.UlcActionButtonClickListener;

/**
 * Created: Zorin A.
 * Date: 30.05.2016.
 */
public class UlcActionButtonView extends FrameLayout implements View.OnClickListener {
    private static final int ANIMATION_DURATION = 400;

    private ImageView mMainButton;
    private View mToTalkButton;
    private View mToPlayButton;

    private UlcActionButtonClickListener mClickListener;
    private boolean mIsViewExpanded;
    private boolean mIsAnimationInProgress;
    private float mExpandOffset;
    private float mExpandOffsetY;
    private float mExpandOffsetX;
    private TransitionDrawable mRootDrawable;
    private View mRoot;
    private boolean mIsTintEnabled;

    public UlcActionButtonView(Context context) {
        super(context);
        init(context);
    }

    public UlcActionButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ulc_button_layout, this);
        mMainButton = (ImageView) view.findViewById(R.id.ulc_action_button_main);
        mToPlayButton = view.findViewById(R.id.ulc_action_button_2_play);
        mToTalkButton = view.findViewById(R.id.ulc_action_button_2_talk);
        mRoot = view.findViewById(R.id.ulc_button_root);
        mRootDrawable = (TransitionDrawable) mRoot.getBackground();

        mMainButton.setOnClickListener(this);
        mToPlayButton.setOnClickListener(this);
        mToTalkButton.setOnClickListener(this);
        mRoot.setOnClickListener(this);
        mRoot.setClickable(false);

        mExpandOffset = context.getResources().getDimensionPixelSize(R.dimen.ulc_action_button_offset);
        mExpandOffsetX = context.getResources().getDimensionPixelSize(R.dimen.ulc_action_button_x_offset);
        mExpandOffsetY = context.getResources().getDimensionPixelSize(R.dimen.ulc_action_button_y_offset);
    }

    public void setActionListener(UlcActionButtonClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setTintEnabled(boolean isEnabled) {
        mIsTintEnabled = isEnabled;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ulc_action_button_main:
                performMainAction();
                break;
            case R.id.ulc_action_button_2_talk:
                onToTalkClicked(v);
                break;
            case R.id.ulc_action_button_2_play:
                onToPlayClicked(v);
                break;
            case R.id.ulc_button_root:
                performMainAction();
                break;

        }
    }

    private void onToTalkClicked(View view) {
        if (mClickListener != null) {
            mClickListener.on2TalkClick(view);
        }
    }

    private void onToPlayClicked(View view) {
        if (mClickListener != null) {
            mClickListener.on2PlayClick(view);
        }
    }

    private void performMainAction() {
        if (mIsAnimationInProgress) {
            return;
        }
        if (mIsViewExpanded) {
            collapseView();
        } else {
            expandView();
        }
        switchState();
    }

    private void expandView() {
        animateButtonOffset(mToPlayButton, (int) -mExpandOffsetX, (int) -mExpandOffset, true);
        animateButtonOffset(mToTalkButton, (int) -mExpandOffset, (int) -mExpandOffsetY, true);
        mMainButton.setImageResource(R.drawable.selector_ulc_action_button_expanded);

        if (mIsTintEnabled) {
            mRootDrawable.startTransition(ANIMATION_DURATION);
            mRoot.setClickable(true);
        }
    }

    private void collapseView() {
        animateButtonOffset(mToPlayButton, (int) mExpandOffsetX, (int) mExpandOffset, false);
        animateButtonOffset(mToTalkButton, (int) mExpandOffset, (int) mExpandOffsetY, false);
        mMainButton.setImageResource(R.drawable.selector_ulc_action_button_collapsed);

        if (mIsTintEnabled) {
            mRootDrawable.reverseTransition(ANIMATION_DURATION);
            mRoot.setClickable(false);
        }
    }

    private void animateButtonOffset(View view, int x, int y, boolean isFadeIn) {
        view.animate()
                .alpha(isFadeIn ? 1 : 0)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(ANIMATION_DURATION).translationXBy(x)
                .translationYBy(y).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimationInProgress = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimationInProgress = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        })
                .start();
    }

    private void switchState() {
        mIsViewExpanded = !mIsViewExpanded;
    }
}
