package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.ProfileClickedEvent;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.net.models.Profile;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zorin.a on 17.06.2016.
 */
public class SearchItemView extends RelativeLayout implements ViewModel<Profile> {

    EventBus mBus;
    Profile mData;

    @BindView(R.id.view_search_avatar_image)
    CircularImageView mAvatarImage;
    @BindView(R.id.view_search_name_text)
    TextView mNameText;
    @BindView(R.id.view_search_status_image_view)
    ImageView mStatusImageView;
    @BindView(R.id.view_search_level)
    TextView mLevel;

    public SearchItemView(Context context, EventBus bus) {
        super(context);
        init(context);
        mBus = bus;
    }

    public SearchItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void setData(Profile data) {
        if (data == null) {
            return;
        }
        mData = data;
        mNameText.setText(data.getName());
        mLevel.setText(getContext().getString(R.string.lvl_format, data.getLevel()));
        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + data.getAvatar())
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .into(mAvatarImage);

        if (data.getStatus() != null) {
            mStatusImageView.setVisibility(VISIBLE);
            switch (data.getStatus().getId()) {
                case Constants.OFFLINE:
                    mStatusImageView.setVisibility(GONE);
                    break;
                case Constants.ONLINE:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_online));
                    break;
                case Constants.PLAYING:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_2play));
                    break;
                case Constants.TALKING:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_2talk));
                    break;
            }
        }
    }

    @OnClick(R.id.view_search_ripple_layout)
    public void onClick() {
        mBus.post(new ProfileClickedEvent(mData.getId()));
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_search_item, this);
        ButterKnife.bind(this);
    }
}
