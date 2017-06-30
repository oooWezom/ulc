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
import com.wezom.ulcv2.events.ProfileClickedEvent;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.net.models.Follower;
import com.wezom.ulcv2.net.models.Status;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wezom.ulcv2.net.models.Status.OFFLINE;

/**
 * Created by kartavtsev.s on 21.01.2016.
 */
public class FollowItemView extends RelativeLayout implements ViewModel<Follower> {

    private final EventBus mBus;
    @BindView(R.id.view_follow_avatar_image)
    CircularImageView mAvatarImage;
    @BindView(R.id.view_follow_name_text)
    TextView mNameText;
    @BindView(R.id.view_follow_status_image_view)
    ImageView mStatusImageView;
    @BindView(R.id.view_follow_level)
    TextView mLevel;

    private Follower mData;

    public FollowItemView(Context context, EventBus bus) {
        super(context);
        mBus = bus;
        init();
    }

    @Override
    public void setData(Follower data) {
        if (data == null) {
            return;
        }

        mData = data;
        mNameText.setText(data.getName());
        mLevel.setText(getContext().getString(R.string.lvl_format, data.getLevel()));
        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + data.getAvatar())
                .placeholder(R.drawable.ic_default_user_avatar)
                .into(mAvatarImage);

        mStatusImageView.setVisibility(VISIBLE);
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

    @OnClick(R.id.view_follow_ripple_layout)
    public void onClick() {
        mBus.post(new ProfileClickedEvent(mData.getId()));
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_follow, this);
        ButterKnife.bind(view);
    }
}
