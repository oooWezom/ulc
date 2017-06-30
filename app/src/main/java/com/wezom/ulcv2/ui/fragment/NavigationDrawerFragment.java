package com.wezom.ulcv2.ui.fragment;

import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.QuantityMapper;
import com.wezom.ulcv2.events.ChangeAvatarRequestEvent;
import com.wezom.ulcv2.events.CountersUpdateEvent;
import com.wezom.ulcv2.events.ProfileImageUpdateEvent;
import com.wezom.ulcv2.events.ProfileUpdatedEvent;
import com.wezom.ulcv2.mvp.presenter.NavigationDrawerPresenter;
import com.wezom.ulcv2.mvp.view.NavigationDrawerView;
import com.wezom.ulcv2.net.models.Profile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static java.lang.String.format;

/**
 * Created by Sivolotskiy.v on 25.05.2016.
 */
public class NavigationDrawerFragment extends BaseFragment
        implements NavigationDrawerView {

    @Inject
    EventBus mBus;

    @InjectPresenter
    NavigationDrawerPresenter mPresenter;

    @BindView(R.id.fragment_navigation_drawer_follows_count_text_view)
    TextView mFollowsCountText;
    @BindView(R.id.fragment_navigation_drawer_messages_count_text_view)
    TextView mMessagesCountText;
    @BindView(R.id.fragment_navigation_drawer_username_text_view)
    TextView mNameTextView;
    @BindView(R.id.fragment_navigation_drawer_user_image_view)
    ImageView mPhotoImageView;
    @BindView(R.id.fragment_navigation_drawer_create_photo_image_view)
    ImageView mAvatarImageView;

    @Subscribe
    public void onRefreshInfoEvent(CountersUpdateEvent event) {
        countersUpdate(event.getWarningsCount(), event.getFollowsCount(), event.getMessagesCount());
    }

    @Subscribe
    public void onProfileUpdatedEvent(ProfileUpdatedEvent event) {
        profileUpdate(event.getProfile().getName(), event.getProfile().getAvatar());
    }

    @Subscribe
    public void onProfileImageUpdate(ProfileImageUpdateEvent event) {
        mPresenter.loadProfile();
    }

    public void onClickAvatarChange(int type) {
        //0 - gallery
        //1 - make photo
        mBus.post(new ChangeAvatarRequestEvent(type));
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_navigation_drawer;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public int getScreenOrientation() {
        return 0;
    }

    @Override
    public void countersUpdate(int warning, int follows, int messages) {
        if (follows != 0) {
            mFollowsCountText.setText(format("%s", String.valueOf(new QuantityMapper().convertFrom(follows))));
            mFollowsCountText.setVisibility(View.VISIBLE);
        } else {
            mFollowsCountText.setVisibility(View.INVISIBLE);
        }

        if (messages != 0) {
            mMessagesCountText.setText(format("%s", String.valueOf(new QuantityMapper().convertFrom(messages))));
            mMessagesCountText.setVisibility(View.VISIBLE);
        } else {
            mMessagesCountText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void profileUpdate(String username, String photo) {
        mNameTextView.setText(username);
        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + photo)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .into(mPhotoImageView);
    }

    @Override
    public void loadAvatarAndBackground(Profile profile) {
        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + profile.getAvatar())
                .placeholder(R.drawable.ic_default_user_big)
                .into(mPhotoImageView);
    }

    @Override
    public void showAvatarPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(), mAvatarImageView, Gravity.TOP);
        popupMenu.inflate(R.menu.menu_avatar_load_type);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_avatar_load_type_gallery:
                    mPresenter.onClickAvatarChange(ChangeAvatarRequestEvent.GALLERY);
                    return true;
                case R.id.menu_avatar_load_type_photo:
                    mPresenter.onClickAvatarChange(ChangeAvatarRequestEvent.PHOTO);
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBus.isRegistered(this)) {
            mBus.register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBus.isRegistered(this)) {
            mBus.unregister(this);
        }
    }

    @OnClick(R.id.fragment_navigation_drawer_follows_ripple_layout)
    void onClickFollows() {
        mPresenter.callFollows();
    }

    @OnClick(R.id.fragment_navigation_drawer_newsfeed_ripple_layout)
    void onClickNews() {
        mPresenter.callNews();
    }

    @OnClick(R.id.fragment_navigation_drawer_messages_ripple_layout)
    void onClickMessages() {
        mPresenter.callMessages();
    }

    @OnClick(R.id.fragment_navigation_drawer_search_people_view)
    void onClickSearch() {
        mPresenter.callSearch();
    }

    @OnClick(R.id.fragment_navigation_drawer_user_ripple_layout)
    void onClickProfile() {
        mPresenter.callProfile();
    }

    @OnClick(R.id.fragment_navigation_drawer_setting_ripple_layout)
    void onClickSettings() {
        mPresenter.callSettings();
    }

    @OnClick(R.id.fragment_navigation_drawer_create_photo_image_view)
    void onClickChangeAvatar() {

        showAvatarPopupMenu();
    }
}
