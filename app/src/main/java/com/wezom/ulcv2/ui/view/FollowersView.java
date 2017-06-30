package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.net.models.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sivolotskiy.v on 20.06.2016.
 */
public class FollowersView extends RelativeLayout {

    public static final int NONE_FOLLOWERS = 0;
    public static final int ONE_FOLLOWER = 1;
    public static final int TWO_FOLLOWERS = 2;

    @BindView(R.id.view_followers_first_avatar)
    ImageView mFirstImageView;
    @BindView(R.id.view_followers_second_avatar)
    ImageView mSecondImageView;
    @BindView(R.id.view_followers_third_avatar)
    ImageView mThirdImageView;
    private ArrayList<User> mFollowers;

    public FollowersView(Context context) {
        super(context);
        initViews(context);
    }

    public FollowersView(Context context, User user) {
        super(context);
        initViews(context);
        addFollower(user);
    }

    public FollowersView(Context context, ArrayList<User> followers) {
        super(context);
        initViews(context);
        setFollowers(followers);
    }

    public FollowersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.EditTextView, 0, 0);
        try {

        } finally {
            mTypedArray.recycle();
        }

        initViews(context);
    }

    public void setNoneFollowersMode() {
        displayFollowers();
    }

    public void addFollower(User follower) {
        if (mFollowers == null) {
            mFollowers = new ArrayList<>();
        } else {
            mFollowers.clear();
        }
        mFollowers.add(follower);

        displayFollowers();
    }

    public void setFollowers(ArrayList<User> followers) {
        mFollowers = followers;

        displayFollowers();
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_followers, this);
        ButterKnife.bind(this);
    }

    private void displayFollowers() {
        if (mFollowers == null) {
            return;
        }

        switch (mFollowers.size()) {
            case NONE_FOLLOWERS:
                mFirstImageView.setVisibility(INVISIBLE);
                mSecondImageView.setVisibility(GONE);
                mThirdImageView.setVisibility(GONE);
                break;
            case ONE_FOLLOWER:
                displayFollow(mFollowers.get(0),mFirstImageView);
                mSecondImageView.setVisibility(GONE);
                mThirdImageView.setVisibility(GONE);
                break;
            case TWO_FOLLOWERS:
                displayFollow(mFollowers.get(0), mFirstImageView);
                displayFollow(mFollowers.get(1), mSecondImageView);
                mThirdImageView.setVisibility(GONE);
                break;
            default:
                displayFollow(mFollowers.get(0), mFirstImageView);
                displayFollow(mFollowers.get(1), mSecondImageView);
                displayFollow(mFollowers.get(2), mThirdImageView);
                break;
        }
    }

    private void displayFollow(User follower, ImageView imageView) {
        imageView.setVisibility(VISIBLE);
        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + follower.getAvatar())
                .placeholder(R.drawable.ic_default_user_avatar)
                .into(imageView);
    }
}

