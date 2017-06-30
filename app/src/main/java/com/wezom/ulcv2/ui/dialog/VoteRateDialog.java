package com.wezom.ulcv2.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.interfaces.OnTimeIsOverListener;
import com.wezom.ulcv2.ui.view.VoteView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Setter;
import lombok.experimental.Accessors;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.Window.FEATURE_NO_TITLE;
import static com.wezom.ulcv2.common.Constants.CONTENT_DEV_URL;

/**
 * Created by Sivolotskiy.v on 12.07.2016.
 */
@Accessors(prefix = "m")
@FragmentWithArgs
public class VoteRateDialog extends BaseDialog {

    private static final long SECOND_IN_MILLISECOND = 1000L;

    @Inject
    Picasso mPicasso;

    @BindView(R.id.view_vote_rate_avatar)
    ImageView mAvatarImageView;
    @BindView(R.id.activity_game_vote_view)
    VoteView mVoteView;
    @BindView(R.id.view_vote_username_text_view)
    TextView mUserNameTextView;

    @Arg(required = false)
    String avatarUrl;
    @Arg(required = false)
    String userName;
    @Arg(required = false)
    long maxProgress;

    @Setter private OnDialogResult mOnDialogResult;
    private CountDownTimer mCountDownTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = initViews(inflater);

        setUpDialog();
        setUpFields();
        startTimer();

        return view;
    }

    @Override
    public void injectDependencies() {
        getActivityComponent().inject(this);
    }

    private View initViews(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.view_vote, null);
        bindView(view);
        return view;
    }

    @OnClick(R.id.view_vote_rate_like_button)
    public void OnLike() {
        if (mOnDialogResult != null) {
            mOnDialogResult.onLike();
            dismiss();
        }
    }

    @OnClick(R.id.view_vote_rate_dislike_button)
    public void OnDislike() {
        if (mOnDialogResult != null) {
            mOnDialogResult.onDislike();
            dismiss();
        }
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(maxProgress, SECOND_IN_MILLISECOND) {

            @Override
            public void onTick(long millisUntilFinished) {
                mVoteView.setProgress((((maxProgress - millisUntilFinished) / (float) maxProgress) * 100.0f));
            }

            @Override
            public void onFinish() {
                mVoteView.setProgress(100);
                new Handler().postDelayed(() -> dismiss(), 1000L);
            }
        };

        mCountDownTimer.start();
    }

    private void setUpFields() {
        final String fullUrl = !TextUtils.isEmpty(avatarUrl) ? CONTENT_DEV_URL + avatarUrl : null;
        mPicasso.with(getContext())
                .load(fullUrl)
                .placeholder(R.drawable.bg_avatar_big_placeholder)
                .error(R.drawable.bg_avatar_big_placeholder)
                .into(mAvatarImageView);

        mUserNameTextView.setText(userName);
    }

    @Override
    protected void setUpDialog() {
        Dialog mDialog = getDialog();
        mDialog.setCancelable(false);
        mDialog.getWindow().setLayout(WRAP_CONTENT, WRAP_CONTENT);
        mDialog.requestWindowFeature(FEATURE_NO_TITLE);
        View view = mDialog.getWindow().getDecorView();
        view.setBackgroundResource(android.R.color.transparent);
        mDialog.setCanceledOnTouchOutside(true);
    }

    public interface OnDialogResult {
        void onLike();
        void onDislike();
    }
}
