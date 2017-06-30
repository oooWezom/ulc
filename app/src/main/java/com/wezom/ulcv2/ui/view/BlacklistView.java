package com.wezom.ulcv2.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.BlacklistRemoveRequestEvent;
import com.wezom.ulcv2.events.ProfileClickedEvent;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.net.models.BlacklistRecord;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kartavtsev.s on 11.02.2016.
 */
public class BlacklistView extends RelativeLayout implements ViewModel<BlacklistRecord> {

    @BindView(R.id.view_blacklist_avatar)
    CircularImageView mAvatar;
    @BindView(R.id.view_blacklist_name_text)
    TextView mNameText;
    @BindView(R.id.view_blacklist_layout)
    RelativeLayout mBlacklistLayout;
    @BindView(R.id.view_blacklist_remove_button)
    ImageView mRemoveButton;

    private int mId;
    private EventBus mBus;

    public BlacklistView(Context context, EventBus bus) {
        super(context);
        mBus = bus;
        init();
    }

    public BlacklistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BlacklistView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public BlacklistView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void setData(BlacklistRecord data) {
        mId = data.getId();
        mNameText.setText(data.getName());
        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + data.getAvatar())
                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                .into(mAvatar);
    }

    @OnClick(R.id.view_blacklist_remove_button)
    public void onRemoveClick() {
        mBus.post(new BlacklistRemoveRequestEvent(mId));
    }

    private void init() {
        inflate(getContext(), R.layout.view_blacklist, this);
        ButterKnife.bind(this);
    }
}
