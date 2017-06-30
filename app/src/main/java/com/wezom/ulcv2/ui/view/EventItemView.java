package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.QuantityMapper;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.events.SessionInfoEvent;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.view.ViewModel;
import com.wezom.ulcv2.net.models.Event;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kartavtsev.s on 20.01.2016.
 */
public class EventItemView extends RelativeLayout implements ViewModel<Event> {

    public final static int START_FOLLOW = 1;
    public final static int GAME_SESSION_RESULT = 2;
    public final static int TALK_SESSION_RESULT = 4;
    private  EventBus mBus;
    @BindView(R.id.view_event_center_image_view)
    ImageView mCenterImage;
    @BindView(R.id.view_event_left_name_text)
    TextView mLeftNameText;
    @BindView(R.id.view_event_lvl_up_text_view)
    TextView mOwnStatusText;
    @BindView(R.id.view_newsfeed_followers_view)
    FollowersView mFollowersView;
    @BindView(R.id.view_event_lvl_count_text_view)
    TextView mLvlCountTextView;
    @BindView(R.id.view_event_status_text_view)
    TextView mOpponentStatusText;
    @BindView(R.id.view_event_opponent_lvl_text_view)
    TextView mLvlOpponentCountTextView;
    @BindView(R.id.view_event_date_text)
    TextView mDateText;
    @BindView(R.id.view_newsfeed_follows)
    TextView mFollowsTextView;
    @BindView(R.id.view_newsfeed_image_view)
    ImageView mEventImageView;
    @BindView(R.id.view_event_right_name_text)
    TextView mRightNameText;
    private Event mData;
    private ArrayList<Category> mCategories;

    public EventItemView(Context context, EventBus bus, ArrayList<Category> categories) {
        super(context);
        mBus = bus;
        mCategories = categories;
        init();
    }

    public EventItemView(Context context) {
        super(context);
        init();
    }

    public EventItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EventItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setData(Event data) {
        if (data == null) {
            return;
        }

        mData = data;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM");
        Date date = new Date(data.getCreatedTimestamp() * 1000);
        String createDate = simpleDateFormat.format(date);
        mDateText.setText(createDate);
        mFollowsTextView.setVisibility(VISIBLE);
        mRightNameText.setVisibility(View.VISIBLE);

        switch (data.getTypeId()) {
            case START_FOLLOW:
                setStartFollowType(data);
                break;
            case GAME_SESSION_RESULT:
                setGameSessionType(data);
                break;
            case TALK_SESSION_RESULT:
                set2Talk(data);
                break;
            default:
                mOpponentStatusText.setText("");
                mFollowersView.setNoneFollowersMode();
                break;
        }
    }

    @OnClick(R.id.fragment_event_ripple_layout)
    void onClick() {
        if(mBus!=null && mData.getTypeId() != START_FOLLOW) {
            mBus.post(new SessionInfoEvent(mData));
        }
    }

    private void set2Talk(Event data) {
        if (data == null) {
            return;
        }
        Category category;

        try {
            category = Category.getCategoryById(data.getCategory());

            Picasso.with(getContext())
                    .load(Utils.getCorrectCategoryIconSizeURL(getContext(), true, false) +
                            category.getIcon())
                    .placeholder(R.drawable.ic_default_user_avatar)
                    .into(mEventImageView);
        } catch (Exception e) {
            return;
        }

        mLeftNameText.setText(data.getOwner().getName());
        mOwnStatusText.setText(" ");
        String name = TextUtils.isEmpty(data.getName())? category.getName(): data.getName();
        mOpponentStatusText.setText(name);
        mLeftNameText.setText(data.getOwner().getName());
        mRightNameText.setText(" ");
        mLvlOpponentCountTextView.setText(" ");
        mLvlCountTextView.setText(" ");

        mFollowsTextView.setText(String.valueOf(data.getSpectators()));

        mLvlCountTextView.setText(getContext().getString(R.string.xp_format, data.getExp() >= 0 ? "+" : "-", Math.abs(data.getExp()), (new QuantityMapper().convertFrom(data.getLikes()))));

        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + data.getOwner().getAvatar())
                .placeholder(R.drawable.ic_default_user_big)
                .into(mCenterImage);

        mFollowersView.setFollowers(data.getPartners());
    }

    private void setStartFollowType(Event data) {
        mEventImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_new_follow_event));
        mOwnStatusText.setText(" ");
        mLvlOpponentCountTextView.setText(" ");
        mLvlCountTextView.setText(" ");
        mOpponentStatusText.setText(getResources().getString(R.string.start_follow));
        mLeftNameText.setText(data.getOwner().getName());
        mRightNameText.setText(data.getFollowing().getName());
        mFollowsTextView.setVisibility(GONE);

        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + data.getOwner().getAvatar())
                .placeholder(R.drawable.ic_default_user_big)
                .into(mCenterImage);
        mFollowersView.addFollower(data.getFollowing());
    }

    private void setGameSessionType(Event data) {
        mEventImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_event));
        mLeftNameText.setText(data.getOwner().getName());
        mRightNameText.setText(data.getOpponent().getName());
        mOwnStatusText.setVisibility(INVISIBLE);
        mOpponentStatusText.setText("");

        if (data.getOwner().getLeaver() == 1) {
            mOwnStatusText.setVisibility(VISIBLE);
            mOwnStatusText.setText(getResources().getString(R.string.leave_session));
        }

        if (data.getOwner().getLevelUp() == 1) {
            mOwnStatusText.setVisibility(VISIBLE);
            mOwnStatusText.setText(getResources().getString(R.string.level_up));
        }

        if (data.getOpponent().getLeaver() == 1) {
            mOpponentStatusText.setText(getResources().getString(R.string.leave_session));
        }

        if (data.getOpponent().getLevelUp() == 1) {
            mOpponentStatusText.setText(getResources().getString(R.string.level_up));
        }

        mLvlCountTextView.setText(getContext().getString(R.string.xp_format, data.getOwner().getExp() >= 0 ? "+" : "-",
                Math.abs(data.getOwner().getExp()), String.valueOf(new QuantityMapper().convertFrom(data.getOwner().getLikes()))));
        mLvlOpponentCountTextView.setText(getContext().getString(R.string.xp_format, data.getOpponent().getExp() >= 0 ? "+" : "-",
                Math.abs(data.getOpponent().getExp()), (new QuantityMapper().convertFrom(data.getOpponent().getLikes()))));

        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + data.getOwner().getAvatar())
                .placeholder(R.drawable.ic_default_user_big)
                .into(mCenterImage);

        mFollowersView.addFollower(data.getOpponent());
        mFollowersView.setVisibility(VISIBLE);
        mFollowsTextView.setText(String.valueOf(data.getSpectators()));
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_event, this);
        ButterKnife.bind(view);
    }
}
