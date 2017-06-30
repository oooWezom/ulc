package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.interfaces.OnNotificationClickListener;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.net.models.User;
import com.wezom.ulcv2.net.models.responses.websocket.NotificationResponse;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sivolotskiy.v on 19.07.2016.
 */
public class NotificationView extends RelativeLayout implements ViewModel<NotificationResponse> {

    public static final int TALK_TYPE = 1;
    public static final int GAME_TYPE = 2;
    @BindView(R.id.view_notification_avatar_image_view)
    ImageView mAvatarImageView;
    @BindView(R.id.view_notification_status_image_view)
    ImageView mStatusImage;
    @BindView(R.id.view_notification_title_text_view)
    TextView mTitleTextView;
    @BindView(R.id.activity_home_notification_context_text_view)
    TextView mContextTextView;
    OnNotificationClickListener mListener;
    private int mSessionType;
    private NotificationResponse mData;
    private EventBus mBus;

    public NotificationView(Context context, EventBus bus) {
        super(context);
        mBus = bus;
        init();
    }

    @Override
    public void setData(NotificationResponse data) {
        if (data == null) {
            return;
        }
        mData = data;

        if (data.getGame() != null) {
            setGameType();
        } else {
            setTalkType();
        }
    }

    public void setListener(OnNotificationClickListener listener) {
        mListener = listener;
    }

    @OnClick(R.id.view_notification_close_image_view)
    void onCloseClick() {
        if (mListener != null) {
            int position = ((RecyclerView) getParent()).getChildAdapterPosition(this);
            mListener.onCloseNotification(position);
        }
    }

    @OnClick(R.id.activity_home_notification_layout)
    void onNotificationClick() {
        if (mListener != null) {
            int position = ((RecyclerView) getParent()).getChildAdapterPosition(this);
            mListener.onNotificationClick(position, mSessionType);
        }
    }

    private void setGameType() {
        mSessionType = GAME_TYPE;
        int starterId = mData.getUserId();
        User firstUser = mData.getGame().getUsers().get(0);
        String starterName;
        String starterAvatar;
        if (starterId == firstUser.getId()) {
            starterName = firstUser.getName();
            starterAvatar = firstUser.getAvatar();
        } else {
            starterName = mData.getGame().getUsers().get(1).getName();
            starterAvatar = mData.getGame().getUsers().get(1).getAvatar();
        }

        mTitleTextView.setText(starterName);
        mContextTextView.setText(getResources().getString(R.string.is_in_2play, starterName));

        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + starterAvatar)
                .placeholder(R.drawable.ic_default_user_avatar)
                .into(mAvatarImageView);
        mStatusImage.setImageResource(R.drawable.ic_2play_white);
    }

    private void setTalkType() {
        mSessionType = TALK_TYPE;
        mTitleTextView.setText(mData.getTalk().getStreamer().getName());
        mContextTextView.setText(getResources().getString(R.string.is_in_2talk, mData.getTalk().getStreamer().getName()));

        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + mData.getTalk().getStreamer().getAvatar())
                .placeholder(R.drawable.ic_default_user_avatar)
                .into(mAvatarImageView);

        Picasso.with(getContext())
                .load(Utils.getCorrectCategoryIconSizeURL(getContext(), false, false)
                        + Category.getCategoryById(mData.getTalk().getCategory()).getIcon())
                .into(mStatusImage);
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_notification, this);
        ButterKnife.bind(view);
    }
}
