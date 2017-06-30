package com.wezom.ulcv2.mvp.presenter;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.ChangeAvatarRequestEvent;
import com.wezom.ulcv2.events.ChangeBackgroundRequestEvent;
import com.wezom.ulcv2.events.DialogSelectedEvent;
import com.wezom.ulcv2.events.FollowClickEvent;
import com.wezom.ulcv2.events.FollowsRequestEvent;
import com.wezom.ulcv2.events.GameCreatedEvent;
import com.wezom.ulcv2.events.HideDrawerEvent;
import com.wezom.ulcv2.events.InviteToTalkResponseEvent;
import com.wezom.ulcv2.events.InviteToTalkSentResultEvent;
import com.wezom.ulcv2.events.On2PlayClickEvent;
import com.wezom.ulcv2.events.On2TalkClickEvent;
import com.wezom.ulcv2.events.PreferenceClickedEvent;
import com.wezom.ulcv2.events.ShowTalkEvent;
import com.wezom.ulcv2.events.WatchGameEvent;
import com.wezom.ulcv2.exception.ApiException;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.model.Follows;
import com.wezom.ulcv2.mvp.model.NewsFeed;
import com.wezom.ulcv2.mvp.view.ProfileFragmentView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.ProfileChannelHandler;
import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.net.models.Session;
import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.net.models.Warning;
import com.wezom.ulcv2.net.models.requests.AddToBlackListRequest;
import com.wezom.ulcv2.net.models.requests.MarkWarningAsReadRequest;
import com.wezom.ulcv2.net.models.requests.ProfileRequest;
import com.wezom.ulcv2.net.models.requests.RemoveFromBlackListRequest;
import com.wezom.ulcv2.net.models.requests.ReportUserRequest;
import com.wezom.ulcv2.ui.fragment.FollowsFragment;
import com.wezom.ulcv2.ui.fragment.NewsfeedFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import lombok.Getter;
import ru.terrakok.cicerone.Router;

/**
 * Created: Zorin A.
 * Date: 31.05.2016.
 */

@InjectViewState
public class ProfileFragmentPresenter extends BasePresenter<ProfileFragmentView> {

    @Inject
    ApiManager mApiManager;
    @Inject
    ProfileChannelHandler mProfileChannelHandler;
    @Inject
    @Getter
    EventBus mBus;
    @Inject
    PreferenceManager mPreferenceManager;
    @Inject
    Router mRouter;

    private Profile mProfileData;
    private int mUserId;

    public ProfileFragmentPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void onClickAvatarChange(int type) {
        //0 - gallery
        //1 - make photo
        mBus.post(new ChangeAvatarRequestEvent(type));
    }

    public void clickWatch(Session session) {
        mBus.post(new WatchGameEvent(session));
    }

    public void init(int profileId) {
        mUserId = profileId;
        if (!isMyProfile()) {
            getViewState().enableForeignControls();
        }
    }

    public void loadProfile() {
        if (isMyProfile()) {
            boolean isCachePresent = mPreferenceManager.getProfileData() != null;
            if (isCachePresent) {
                mProfileData = mPreferenceManager.getProfileData();
                getViewState().setProfileData(mPreferenceManager.getProfileData());
            } else {
                getViewState().showLoading();
            }
        } else {
            getViewState().showLoading();
            getViewState().switchToForeignProfile();
        }
        mApiManager.getProfile(new ProfileRequest(mUserId))
                .subscribe(profileResponse -> {
                    mProfileData = profileResponse.getProfile();
                    getViewState().hideLoading();
                    getViewState().setProfileData(profileResponse.getProfile());

                    Log.d("Log_ reconnect prof", profileResponse.getProfile().getStatus().getId() + "");


                }, throwable -> {
                    int errorMessageResId = R.string.error_check_your_net_and_retry;
                    if (throwable instanceof ApiException) {
                        switch (((ApiException) throwable).getCode()) {
                            case 10:
                                errorMessageResId = R.string.error_user_not_found;
                                break;
                            case 11:
                                errorMessageResId = R.string.error_account_disabled;
                                break;
                            case 12:
                                errorMessageResId = R.string.error_account_private;
                                break;
                            case 13:
                                errorMessageResId = R.string.error_blacklisted;
                                break;
                        }
                    }
                    getViewState().hideLoading();
                    if (!isMyProfile()) {
                        getViewState().showErrorMessage(errorMessageResId);
                    }
                });
    }

    @DebugLog
    public void onBlacklistClicked(int profileId, boolean isBlock) {
        if (isBlock) {
            addUserToBlsckList(profileId);
        } else {
            removeUserFromBlackList(profileId);
        }
    }

    private void addUserToBlsckList(int profileId) {
        getViewState().showGlobalLoading(true);
        mApiManager.addToBlackList(new AddToBlackListRequest(profileId))
                .subscribe(response -> {
                    getViewState().showGlobalLoading(false);
                    getViewState().showMessageDialog(R.string.added, R.string.successfully_added_to_blacklist);
                    getViewState().updateBlockButtonStatus(true);
                }, throwable -> {
                    getViewState().showGlobalLoading(false);
                    getViewState().showMessageDialog(R.string.failed, R.string.add_to_blacklist_failed);
                });
    }

    private void removeUserFromBlackList(int profileId) {
        getViewState().showGlobalLoading(true);
        mApiManager.removeFromBlacklist(new RemoveFromBlackListRequest(profileId))
                .subscribe(response -> {
                    getViewState().showGlobalLoading(false);
                    getViewState().showMessageDialog(R.string.removed, R.string.user_has_been_removed_from_black_list);
                    getViewState().updateBlockButtonStatus(false);
                }, throwable -> {
                    getViewState().showGlobalLoading(false);
                    getViewState().showMessageDialog(R.string.failed, R.string.remove_from_black_list_failed);
                });
    }

    public void onAvatarClick() {
        if (isMyProfile()) {
            getViewState().showAvatarPopupMenu();
        }
    }

    private void clickPreferences() {
        mBus.post(new PreferenceClickedEvent());
    }

    public void onBackgroundChangeClick() {
        mBus.post(new ChangeBackgroundRequestEvent());
    }

    private boolean isMyProfile() {
        return mUserId == 0 || mUserId == mPreferenceManager.getUserId();
    }

    public void onStartChat(int profileId, String profileName, String avatar) {
        mBus.post(new DialogSelectedEvent(profileId, profileName, avatar));
    }

    public void onInviteUser(int profileId, int categoryId) {
        mProfileChannelHandler.inviteToTalk(profileId, categoryId);
    }

    public void onReportUser(int profileId) {
        getViewState().showGlobalLoading(true);
        mApiManager.reportUser(new ReportUserRequest(profileId)).subscribe(response -> {
            getViewState().showGlobalLoading(false);
            getViewState().showMessageDialog(R.string.reported, R.string.user_has_been_reported);

        }, throwable -> {
            getViewState().showGlobalLoading(false);
            getViewState().showMessageDialog(R.string.failed, R.string.fail_to_report_user);
        });
    }

    public void onFollowClicked(int profileId, boolean state) {
        mBus.post(new FollowClickEvent(profileId, state));
    }

    public void start2Play() {
        mBus.post(new On2PlayClickEvent());
    }

    public void onClick2Talk() {
        mBus.post(new On2TalkClickEvent());
    }

    public ArrayList<Category> getCategories() {
        return Category.getCategories();
    }

    public void setWarningAsRead(ArrayList<Warning> warnings) {
        ArrayList<Long> list = new ArrayList<>();
        list.add(warnings.get(0).getCreatedTimestamp());
        list.add(warnings.get(1).getCreatedTimestamp());
        mApiManager.markWarningAsRead(new MarkWarningAsReadRequest(list))
                .subscribe(response -> {
                    if (response.getResult().equals("ok")) {
                        getViewState().showMessageReadToast();
                    }
                }, throwable -> {
                    getViewState().showMessageDialog(R.string.error, R.string.network_error);
                });
    }

    public void checkUpdate() {
        mApiManager.getApiVersion()
                .subscribe(response -> {
                    if (response.getVersion() > Constants.VERSION_API) {
                        getViewState().showUpdateDialog();
                    }
                }, throwable -> {
                    getViewState().showMessageDialog(R.string.error, R.string.network_error);
                });
    }



    public void showFollowers() {
        mBus.post(new FollowsRequestEvent(mProfileData.getId(), false));
    }

    public void showFollowings() {
        mBus.post(new HideDrawerEvent());
        mRouter.navigateTo(Screens.FOLLOWS_SCREEN, new Follows(FollowsFragment.FOLLOWING_TAB_MODE, mProfileData.getId()));
    }

    public void onUserGamesClick(int profileId) {
        mRouter.navigateTo(Screens.NEWS_FEED_SCREEN, new NewsFeed(NewsfeedFragment.TYPE_GAMES, profileId, true));
    }

    public void onUserStreamsClick(int profileId) {
        mRouter.navigateTo(Screens.NEWS_FEED_SCREEN, new NewsFeed(NewsfeedFragment.TYPE_TALKS, profileId, true));
    }

    public void onSendInviteToTalkResult(InviteToTalkSentResultEvent event) { //we receive result is our request sent successfully
        int result = event.getInviteToTalk().getResult();
        if (result == InviteToTalkSentResultEvent.OK) {
            getViewState().showInviteDialog(event.getInviteToTalk().getExpires());
        }
    }

    public void onReceiveResponseToTalk(InviteToTalkResponseEvent event) {
        getViewState().dismissDialog();
    }

    public void watchToPlaySession(Session game) {
        mBus.post(new WatchGameEvent(game));
    }

    public void watchToTalkSession(Talk talk) {
        mBus.post(new ShowTalkEvent(talk));
    }

    public void open2PlaySession(Session game) {
        mBus.post(new GameCreatedEvent(game));
    }

    public void open2TalkSession(Talk talk) {
        //mRouter.navigateTo(Screens.TO_TALK_SCREEN);
        mBus.post(new ShowTalkEvent(talk));
    }
}
