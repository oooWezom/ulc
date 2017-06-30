package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.DialogSelectedEvent;
import com.wezom.ulcv2.events.ProfileClickedEvent;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.net.models.User;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sivolotskiy.v on 14.07.2016.
 */
public class UserItemView extends RelativeLayout implements ViewModel<User> {


    @BindView(R.id.view_user_avatar_image)
    CircularImageView mAvatarImage;
    @BindView(R.id.view_user_name_text_view)
    TextView mNameText;
    @BindView(R.id.view_user_lvl_text_view)
    TextView mLevel;
    @BindView(R.id.view_user_item_status_image)
    ImageView mStatusImageView;
    @BindView(R.id.view_user_chat_text_view)
    View mMessageButton;
    private User mData;
    private EventBus mBus;

    public UserItemView(Context context, EventBus bus) {
        super(context);
        mBus = bus;
        init();
    }

    @Override
    public void setData(User data) {
        if (data == null) {
            return;
        }

        int ownerId = PreferenceManager.getUserId(getContext());
        if (ownerId == data.getId()) {
            mMessageButton.setVisibility(GONE);
        }

        mData = data;
        mNameText.setText(data.getName());
        mLevel.setText(getContext().getString(R.string.lvl_format, data.getLevel()));
        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + data.getAvatar())
                .placeholder(R.drawable.ic_default_user_avatar)
                .into(mAvatarImage);

        if (data.getStatus() != null) {
            switch (data.getStatus().getId()) {
                case 1:
                    mStatusImageView.setVisibility(GONE);
                    break;
                case 2:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_online));
                    break;
                case 5:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_2play));
                    break;
                case 6:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_2talk));
                    break;
                default:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_online));
                    break;
            }
        }
    }

    @OnClick(R.id.view_user_layout)
    void onClickLayout() {
        mBus.post(new ProfileClickedEvent(mData.getId()));
    }

    @OnClick(R.id.view_user_chat_text_view)
    void onClickChat() {
        mBus.post(new DialogSelectedEvent(mData.getId(), mData.getName(), mData.getAvatar()));
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_user_item, this);
        ButterKnife.bind(view);
    }
}
