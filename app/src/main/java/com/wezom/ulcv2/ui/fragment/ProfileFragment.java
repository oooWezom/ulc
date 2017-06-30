package com.wezom.ulcv2.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.ImageCircle;
import com.wezom.ulcv2.common.QuantityMapper;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.events.ChangeAvatarRequestEvent;
import com.wezom.ulcv2.events.InviteToTalkResponseEvent;
import com.wezom.ulcv2.events.InviteToTalkSentResultEvent;
import com.wezom.ulcv2.events.ProfileImageUpdateEvent;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.model.NewsFeed;
import com.wezom.ulcv2.mvp.presenter.ProfileFragmentPresenter;
import com.wezom.ulcv2.mvp.view.ProfileFragmentView;
import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.net.models.Status;
import com.wezom.ulcv2.net.models.Warning;
import com.wezom.ulcv2.ui.adapter.ArrayAdapterWithIcon;
import com.wezom.ulcv2.ui.dialog.InviteDialog;
import com.wezom.ulcv2.ui.dialog.InviteDialogBuilder;
import com.wezom.ulcv2.ui.dialog.ReportDialog;
import com.wezom.ulcv2.ui.dialog.ReportDialogBuilder;
import com.wezom.ulcv2.ui.listeners.UlcActionButtonClickListener;
import com.wezom.ulcv2.ui.view.ProfileToggleButtonView;
import com.wezom.ulcv2.ui.view.StatusView;
import com.wezom.ulcv2.ui.view.UlcActionButtonView;
import com.wezom.ulcv2.ui.views.AvatarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.wezom.ulcv2.common.Constants.DialogResult.OK;
import static com.wezom.ulcv2.common.Constants.InviteDialogType.WAITING_MODE;


/**
 * Created: Zorin A.
 * Date: 31.05.2016.
 */
public class ProfileFragment extends BaseFragment
        implements ProfileFragmentView, UlcActionButtonClickListener {

    public final static String KEY_USER_ID = "key_user_id";
    private static final String EXTRA_NAME = "fragment_profile_name";
    private static final int TWO_DIGITS = 2;

    //region injects
    @InjectPresenter
    ProfileFragmentPresenter mPresenter;
    @Inject
    EventBus bus;
    //endregion

    //region views
    @BindView(R.id.fragment_profile_action_button)
    UlcActionButtonView mActionButtonView;
    @BindView(R.id.fragment_profile_background_image)
    ImageView mBackgroundImage;
    @BindView(R.id.fragment_profile_avatar_view)
    AvatarView mAvatarView;
    @BindView(R.id.fragment_profile_games_played_text)
    TextView mGamesPlayedText;
    @BindView(R.id.fragment_profile_followers_text)
    TextView mFollowersText;
    @BindView(R.id.fragment_profile_following_text)
    TextView mFollowingText;
    @BindView(R.id.fragment_profile_streams_played_text)
    TextView mStreamsPlayed; //TODO: set streams data
    @BindView(R.id.fragment_profile_about_text)
    TextView mAboutText;
    @BindView(R.id.fragment_profile_status_view)
    StatusView mStatusView;
    @BindView(R.id.fragment_profile_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.fragment_profile_appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.fragment_profile_background_change_button)
    ImageView mChangeBackgroundButton;
    @BindView(R.id.fragment_profile_loading_view)
    FrameLayout mLoadingView;
    @BindView(R.id.fragment_profile_error_view)
    FrameLayout mErrorView;
    @BindView(R.id.fragment_profile_error_text)
    TextView mErrorTextView;
    @BindView(R.id.fragment_profile_user_background)
    ImageView mBackGroundImageView;
    @BindView(R.id.fragment_profile_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.fragment_profile_user_up_bar)
    View mUpperBarView;
    @BindView(R.id.fragment_profile_actions_panel)
    View mActionsPanelView;
    @BindView(R.id.fragment_profile_follow_user)
    ProfileToggleButtonView mFollowToggleButton;
    @BindView(R.id.fragment_profile_block_user)
    ProfileToggleButtonView mBlockToggleButton;
    @BindView(R.id.fragment_profile_warning_layout)
    View mWarningLayout;
    @BindView(R.id.fragment_profile_warning_text)
    TextView mWarningText;
    //endregion

    private int mProfileId;
    private String mProfileAvatar;
    private String mProfileName;
    private String mUserName;
    private String mAvatar;
    private ArrayList<Warning> mWarnings;
    private InviteDialog mInviteDialog;
    private AlertDialog mReconnectDialog;
    private Profile mProfile;


    public static ProfileFragment getNewInstance(String name, int profileId) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, name);
        bundle.putInt(KEY_USER_ID, profileId);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);
        return profileFragment;
    }

    //region overrides
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_profile;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProfileId = getArguments().getInt(KEY_USER_ID, 0);
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.init(mProfileId);

        mFollowToggleButton.setOnClickListener(v -> {
            mPresenter.onFollowClicked(mProfileId, mFollowToggleButton.getState());
        });

        mBlockToggleButton.setOnClickListener(v -> {
            mPresenter.onBlacklistClicked(mProfileId, mBlockToggleButton.getState());
        });
        mWarningLayout.setOnClickListener(v -> mPresenter.setWarningAsRead(mWarnings));

        mStatusView.setOnClickListener(v -> {
            if (mStatusView.getStatus() == Status.PLAYING) {
                mPresenter.watchToPlaySession(mProfile.getGame());
            }
            if (mStatusView.getStatus() == Status.TALKING) {
                mPresenter.watchToTalkSession(mProfile.getTalk());
            }
        });

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_profile_feed_placeholder,
                        NewsfeedFragment.getNewInstance(Screens.NEWS_FEED_SCREEN, new NewsFeed(NewsfeedFragment.TYPE_SELF, mProfileId, false)))
                .disallowAddToBackStack()
                .commit();
        initViews();

        mPresenter.checkUpdate();
    }

    private void initViews() {
        setSupportActionBar(mToolBar);
        showDrawerToggleButton(mToolBar);
        mToolBar.setSubtitle(null);

        mActionButtonView.setTintEnabled(true);
        mActionButtonView.setActionListener(this);

        //prepare follow-unfollow button
        mFollowToggleButton.setPositiveTitle(getString(R.string.follow));
        mFollowToggleButton.setNegativeTitle(getString(R.string.unfollow));
        mFollowToggleButton.setPositiveIcon(getResources().getDrawable(R.drawable.ic_profile_follow));
        mFollowToggleButton.setNegativeIcon(getResources().getDrawable(R.drawable.ic_profile_unfollow));
        //prepare block-unblock button
        mBlockToggleButton.setPositiveTitle(getString(R.string.block));
        mBlockToggleButton.setNegativeTitle(getString(R.string.unblock));
        mBlockToggleButton.setPositiveIcon(getResources().getDrawable(R.drawable.ic_profile_block));
        mBlockToggleButton.setNegativeIcon(getResources().getDrawable(R.drawable.ic_profile_block));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) {
            bus.register(this);
        }
        mPresenter.loadProfile();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bus.isRegistered(this)) {
            bus.unregister(this);
        }
        hideLoading();
        if (mInviteDialog != null) {
            mInviteDialog.dismiss();
        }
    }

    @Override
    public void setProfileData(Profile profile) {
        mProfile = profile;
        mUserName = profile.getName();
        mAvatar = profile.getAvatar();

        getToolBar().setTitle(profile.getName());

        mProfileAvatar = profile.getAvatar();
        mProfileName = profile.getName();

        mStreamsPlayed.setText(new QuantityMapper().convertFrom(profile.getTalks()));
        mGamesPlayedText.setText(new QuantityMapper().convertFrom(profile.getGamesTotal()));
        mFollowersText.setText(new QuantityMapper().convertFrom(profile.getFollowers()));
        mFollowingText.setText(new QuantityMapper().convertFrom(profile.getFollowing()));
        setupAvatarView(profile);
        if (profile.getStatus() != null) {
            mStatusView.setTextColor(getResources().getColor(R.color.color_white));
            mStatusView.setStatus(profile.getStatus());
        }
        if (profile.getAbout() == null || profile.getAbout().isEmpty()) {
            mAboutText.setVisibility(View.GONE);
        } else {
            mAboutText.setVisibility(View.VISIBLE);
        }
        if (profile.getWarnings() != null && !profile.getWarnings().isEmpty()) {
            mWarningText.setVisibility(View.VISIBLE);
            if (mProfileId != 0) {
                mWarningText.setText(R.string.this_user_has_two_warnings);
            } else {
                mWarningText.setText(R.string.you_have_two_warnings);
            }
        }
        mAboutText.setText(profile.getAbout());
        mCollapsingToolbar.setTitle(profile.getName());
        mCollapsingToolbar.setExpandedTitleColor(Color.argb(0, 0, 0, 0));
        mCollapsingToolbar.setCollapsedTitleTextColor(Color.rgb(255, 255, 255));

        //setCachedData follow button state
        mFollowToggleButton.setState(profile.getLink() != 0);

        //setCachedData block button state
        mBlockToggleButton.setState(profile.getBlock() != 0);

        //show warning messages
        mWarnings = profile.getWarnings();
        if (mWarnings != null) {
            switch (mWarnings.size()) {
                case 1:
                    if (mWarnings.get(0).isUnread()) {
                        setWarningVisible(true);
                        mWarningText.setText(getActivity().getString(R.string.first_warning));
                    }
                    break;
                case 2:
                    if (mWarnings.get(1).isUnread()) {
                        setWarningVisible(true);
                        mWarningText.setText(String.format(getActivity().getString(R.string.second_warning), mWarnings.get(1).getCreatedDate()));
                    }
                    break;
            }
        }
        loadAvatarAndBackground(profile);
    }

    @Override
    public void switchToForeignProfile() {
        mChangeBackgroundButton.setVisibility(View.GONE);

        mStatusView.setVisibility(View.VISIBLE);

        if (mProfileId == 0) {
            mStatusView.setVisibility(View.GONE);
            mUpperBarView.setVisibility(View.VISIBLE);
            mActionsPanelView.setVisibility(View.GONE);
        } else {
            mStatusView.setVisibility(View.VISIBLE);
            mUpperBarView.setVisibility(View.GONE);
            mActionsPanelView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showAvatarPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(), mAvatarView, Gravity.TOP);
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
    public void enableForeignControls() {
        mStatusView.setVisibility(View.VISIBLE);
        mUpperBarView.setVisibility(View.GONE);
        mActionsPanelView.setVisibility(View.VISIBLE);
    }

    @Override
    public void disableOptions() {
    }

    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void showErrorMessage(int errorMessageResId) {
        mErrorView.setVisibility(View.VISIBLE);
        mErrorTextView.setText(errorMessageResId);
    }

    @Override
    public void updateBlockButtonStatus(boolean isUserBlocked) {
        mBlockToggleButton.setState(isUserBlocked);
    }

    @Override
    public void showMessageReadToast() {
        Toast.makeText(getActivity(), "Warning read", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInviteDialog(int expires) {
        mInviteDialog = new InviteDialogBuilder(WAITING_MODE)
                .dialogMessage(getString(R.string.waiting_for_username_part_1) + " ")
                .dialogMessage2(" " + getString(R.string.waiting_for_username_part_2))
                .userName(mUserName)
                .dialogTime(expires)
                .dialogUserUrl(mAvatar)
                .build();
        mInviteDialog.show(getFragmentManager(), "");
    }

    @Override
    public void hideReconnectDialog() {
        if (mReconnectDialog != null && mReconnectDialog.isShowing()) {
            mReconnectDialog.dismiss();
        }
    }


    @Override
    public void dismissDialog() {
        if (mInviteDialog != null) {
            mInviteDialog.dismiss();
        }
    }

    private void setWarningVisible(boolean isVisible) {
        mWarningLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    //endregion

    //region clicks
    @OnClick(R.id.fragment_profile_background_change_button)
    public void onBackgroundChange() {
        mPresenter.onBackgroundChangeClick();
    }

    @OnClick(R.id.fragment_profile_avatar_view)
    public void onAvatarClick() {
        mPresenter.onAvatarClick();
    }

    @OnClick(R.id.fragment_profile_chat)
    public void onChatClick() {
        mPresenter.onStartChat(mProfileId, mProfileName, mAvatar);
    }

    @OnClick(R.id.fragment_profile_invite)
    public void onInviteClick() {
        ArrayList<Category> categories =
                mPresenter.getCategories();

        List<String> names = new ArrayList<>();
        for (Category category : categories) {
            names.add(category.getName());
        }

        ListAdapter adapter = new ArrayAdapterWithIcon(getActivity(), names, categories);

        new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.choose_category))
                .setAdapter(adapter, (dialog, item) -> {
                    mPresenter.onInviteUser(mProfileId, categories.get(item).getCategoryId());
                }).show();
    }

    @OnClick(R.id.fragment_profile_report)
    public void onReportClick() {
        ReportDialog dialog = new ReportDialogBuilder(mProfileAvatar).build();
        dialog.setOnDialogResult((dialogResult, result) -> {
            if (dialogResult == OK) {
                //(String) result[0]
                mPresenter.onReportUser(mProfileId);
            }
        });
        dialog.show(getFragmentManager(), "");
    }

    @Override
    public void on2PlayClick(View view) {
        mPresenter.start2Play();
    }

    @Override
    public void on2TalkClick(View view) {
        mPresenter.onClick2Talk();
    }

    @OnClick(R.id.fragment_profile_followers_button)
    void onFollowUserClick() {
        mPresenter.showFollowers();
    }

    @OnClick(R.id.fragment_profile_followings_button)
    void onFollowingsClick() {
        mPresenter.showFollowings();
    }

    @OnClick(R.id.fragment_profile_games_button)
    void onGamesCLick() {
        mPresenter.onUserGamesClick(mProfileId);
    }

    @OnClick(R.id.fragment_profile_streams_button)
    void onStreamsClick() {
        mPresenter.onUserStreamsClick(mProfileId);
    }

    //endregion


    private void loadAvatarAndBackground(Profile profile) {

        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + profile.getBackground())
                .into(mBackgroundImage);

        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + profile.getAvatar())
                .placeholder(R.drawable.ic_default_user_big)
                .resize(200, 200)
                .transform(new ImageCircle())
                .into(mAvatarView);
    }

    private void setupAvatarView(Profile profile) {
        String level = String.valueOf(profile.getLevel());
        if (level.length() <= TWO_DIGITS) {
            mAvatarView.showCircleLevelBg(true);
        }
        int levelPercent = (int) ((profile.getExp() * 100.0f) / profile.getExpMax());
        mAvatarView.setProgressPercent(levelPercent);
        mAvatarView.setLevel(new QuantityMapper().convertFrom(profile.getLevel()));
        mAvatarView.setLikes(new QuantityMapper().convertFrom(profile.getLikes()));
    }

    private boolean checkIsUserIn2Talk() {
        return mProfile.getTalk() != null;
    }

    private boolean checkIsUserIn2Play() {
        return mProfile.getGame() != null;
    }

    @Subscribe
    public void onProfileImageUpdate(ProfileImageUpdateEvent event) {
        mPresenter.loadProfile();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendInviteToTalkResult(InviteToTalkSentResultEvent event) {
        mPresenter.onSendInviteToTalkResult(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveResponseToTalk(InviteToTalkResponseEvent event) {
        mPresenter.onReceiveResponseToTalk(event);
    }

}