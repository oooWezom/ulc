package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.events.FollowClickEvent;
import com.wezom.ulcv2.events.OnDetachTalkClickEvent;
import com.wezom.ulcv2.events.OnLocalLikeClickEvent;
import com.wezom.ulcv2.events.OnNetworkLikeClickEvent;
import com.wezom.ulcv2.events.OnVideoClickEvent;
import com.wezom.ulcv2.events.OnVideoStreamDisconnectedEvent;
import com.wezom.ulcv2.events.ReportUserClickEvent;
import com.wezom.ulcv2.events.onSwitchTalkClickEvent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.mvp.view.VideoFragmentView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.Talk;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created: Zorin A.
 * Date: 26.09.2016.
 */

@InjectViewState
public class VideoFragmentPresenter extends BasePresenter<VideoFragmentView> {
    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;
    @Inject
    @ApplicationContext
    Context mContext;

    public VideoFragmentPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void onVideoClick() {
        mBus.post(new OnVideoClickEvent());
    }

    public void onReportUser(int talkId) {
        if (Utils.isUserLoggedIn(mContext)) {
            mBus.post(new ReportUserClickEvent(talkId));
        } else {
            getViewState().showCloseSessionDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void onFollowUser(int userId) {
        if (Utils.isUserLoggedIn(mContext)) {
            mBus.post(new FollowClickEvent(userId, false));
        } else {
            getViewState().showCloseSessionDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void detachTalk() {
        mBus.post(new OnDetachTalkClickEvent());
    }

    public void switchTalk(Talk talk) {
        if (Utils.isUserLoggedIn(mContext)) {
            mBus.post(new onSwitchTalkClickEvent(talk));
        } else {
            getViewState().showCloseSessionDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void localLikeClick(int talkId, int count) {
        mBus.post(new OnLocalLikeClickEvent(talkId, count));
    }

    public void networkLikeClick(int talkId, int likePack) {
        mBus.post(new OnNetworkLikeClickEvent(talkId, likePack));
    }

    public void onStreamDisconnected(int talkId) {
        mBus.post(new OnVideoStreamDisconnectedEvent(talkId));
    }
}
