package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.QuantityMapper;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.net.models.Talk;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created: Zorin A.
 * Date: 14.07.2016.
 */
public class ActiveTalkItemView extends RelativeLayout implements ViewModel<Talk> {
    private static final int ONE_USER_MODE = 1;
    private static final int TWO_USER_MODE = 2;

    //region views


    @BindView(R.id.view_active_talk_multi_user_layout)
    View mMultiUserLayout;

    @BindView(R.id.view_active_talk_single_user_layout)
    View mSingleUserLayout;

    //region common views

    @BindView(R.id.view_active_talk_icon)
    ImageView mTalkCategoryImageView;
    @BindView(R.id.view_active_talk_talk_name_text)
    TextView mTalkTitleTextView;
    @BindView(R.id.view_active_talk_likes)
    TextView mLikesTextView;
    @BindView(R.id.view_active_talk_watchers)
    TextView mWatchersTextView;
    //endregion

    //region single person mode
    @BindView(R.id.view_active_talk_single_webcam_image)
    ImageView mSingleUserPhotoImageView;
    @BindView(R.id.view_active_talk_single_avatar_image)
    ImageView mSingleUserAvatarImageView;
    @BindView(R.id.view_active_talk_single_level_text)
    TextView mSingleUserLevelTextView;
    @BindView(R.id.view_active_talk_single_name_text)
    TextView mSingleUserNameTextView;

    //endregion

    //region multiple person mode

    @BindView(R.id.view_active_talk_first_name_text)
    TextView mFirstUserNameTextView;
    @BindView(R.id.view_active_talk_second_name_text)
    TextView mSecondUserNameTextView;
    @BindView(R.id.view_active_talk_first_level_text)
    TextView mFirstLevelTextView;
    @BindView(R.id.view_active_talk_second_level_text)
    TextView mSecondLevelTextView;

    @BindView(R.id.view_active_talk_first_webcam_image)
    ImageView mFirstUserPhotoImageView;
    @BindView(R.id.view_active_talk_second_webcam_image)
    ImageView mSecondUserPhotoImageView;

    @BindView(R.id.view_active_talk_first_avatar_image)
    ImageView mFirstUserAvatarImageView;
    @BindView(R.id.view_active_talk_second_avatar_image)
    ImageView mSecondUserAvatarImageView;


    //endregion

    //endregion

    @BindString(R.string.lvl)
    String lvl;
    private int mDisplayMode;

    public ActiveTalkItemView(Context context) {
        super(context);
        init(context);
    }

    public ActiveTalkItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_active_talk, this);
        ButterKnife.bind(this);
    }

    @Override
    public void setData(Talk data) {
//        Constants.ACTIVE_SESSIONS_PREVIEW_URL + "t_" + String.valueOf(data.getId())+".jpg"; // пока не работающая ссылка на превьюшку пользователя в толке
        Category category = Category.getCategoryById(data.getCategory());
        if (category == null) {
            return;
        }
        mTalkTitleTextView.setText(category.getName());
        Picasso.with(getContext())
                .load(Utils.getCorrectCategoryIconSizeURL(getContext(), true, false) + category.getIcon())
                .resizeDimen(R.dimen.view_active_talk_icon_size, R.dimen.view_active_talk_icon_size)
                .onlyScaleDown()
                .into(mTalkCategoryImageView);


        mLikesTextView.setText(new QuantityMapper().convertFrom(data.getLikes()));
        mWatchersTextView.setText(new QuantityMapper().convertFrom(data.getSpectators()));

        switchVisibilityState(data);
        if (mDisplayMode == ActiveTalkItemView.ONE_USER_MODE) {

            mSingleUserLevelTextView.setText(String.format("%d %s", data.getStreamer().getLevel(), lvl));
            mSingleUserNameTextView.setText(data.getStreamer().getName());

            Picasso.with(getContext()).load(Constants.ACTIVE_SESSIONS_PREVIEW_URL + "t_" + String.valueOf(data.getId()) + ".jpg").into(mSingleUserPhotoImageView);
            Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + data.getStreamer().getAvatar()).fit().into(mSingleUserAvatarImageView);

        } else {
            mFirstUserNameTextView.setText(data.getStreamer().getName());
            mSecondUserNameTextView.setText(data.getLinked().get(0).getStreamer().getName());

            mFirstLevelTextView.setText(String.format("%d %s", data.getStreamer().getLevel(), lvl));
            mSecondLevelTextView.setText(String.format("%d %s", data.getLinked().get(0).getStreamer().getLevel(), lvl));

            Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + data.getStreamer().getAvatar()).fit().into(mFirstUserAvatarImageView);
            Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + data.getLinked().get(0).getStreamer().getAvatar()).fit().into(mSecondUserAvatarImageView);

            Picasso.with(getContext()).load(Constants.ACTIVE_SESSIONS_PREVIEW_URL + "t_" + String.valueOf(data.getId()) + ".jpg").into(mFirstUserPhotoImageView);
            Picasso.with(getContext()).load(Constants.ACTIVE_SESSIONS_PREVIEW_URL + "t_" + String.valueOf(data.getLinked().get(0).getId()) + ".jpg").into(mSecondUserPhotoImageView);
        }
    }

    private void switchVisibilityState(Talk data) {
        if (data.getLinked() == null || data.getLinked().size() == 0) {
            mDisplayMode = ActiveTalkItemView.ONE_USER_MODE;
            mMultiUserLayout.setVisibility(GONE);
            mSingleUserLayout.setVisibility(VISIBLE);
        } else {
            mDisplayMode = ActiveTalkItemView.TWO_USER_MODE;
            mMultiUserLayout.setVisibility(VISIBLE);
            mSingleUserLayout.setVisibility(GONE);
        }
    }
}
