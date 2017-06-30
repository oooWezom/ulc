package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.FollowersShowEvent;
import com.wezom.ulcv2.events.UpdateDateRequestEvent;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.view.FollowsListView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.Follower;
import com.wezom.ulcv2.net.models.requests.FollowsRequest;
import com.wezom.ulcv2.net.models.responses.FollowsResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Subscriber;

/**
 * Created by Sivolotskiy.v on 31.05.2016.
 */

@InjectViewState
public class FollowsListPresenter extends BasePresenter<FollowsListView> {

    @Inject
    ApiManager mApiManager;
    @Inject
    @Getter
    EventBus mBus;

    private int mCount = 20;
    @Setter
    @Accessors(prefix = "m")
    private int mOffset = 0;

    public FollowsListPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    
    public void loadFollowing(int loadingMode, String query, int id) {
        if (loadingMode == Constants.LOADING_MODE_REFRESH) {
            mOffset = 0;
        }
        getViewState().showLoading(loadingMode);

        mApiManager.getFollowing(new FollowsRequest(id, mOffset, mCount,new FollowsRequest.Request(query)))
                .doOnNext(followsResponse -> {
                    for (Follower follow : followsResponse.getResponse()) {
                        follow.setLink(1); // API returns status relative to current user
                    }
                })
                .subscribe(new FollowsSubscriber(loadingMode));
        mOffset += mCount;
    }

    public void loadFollowers(int loadingMode, String query, int id) {
        if (loadingMode == Constants.LOADING_MODE_REFRESH) {
            mOffset = 0;
        }
        getViewState().showLoading(loadingMode);

        mApiManager.getFollowers(new FollowsRequest(id, mOffset, mCount,new FollowsRequest.Request(query)))
                .doOnNext(followsResponse -> {
                    ArrayList<Integer> followerIds = new ArrayList<Integer>();
                    for (Follower follower : followsResponse.getResponse()) {
                        followerIds.add(follower.getId());
                    }
                    mBus.post(new FollowersShowEvent(followerIds));//
                    mBus.post(new UpdateDateRequestEvent());
                })
                .subscribe(new FollowsSubscriber(loadingMode));
        mOffset += mCount;
    }

    private void askFollowShowed(ArrayList<Follower> followers) {
        ArrayList<Integer> idFollows = new ArrayList<>();
        for (Follower follower : followers) {
            idFollows.add(follower.getId());
        }

        mBus.post(new FollowersShowEvent(idFollows));
    }

    private class FollowsSubscriber extends Subscriber<FollowsResponse> {

        private int mRefreshType;

        public FollowsSubscriber(int refreshType) {
            mRefreshType = refreshType;
        }

        @Override
        public void onError(Throwable e) {
                mBus.post(new UpdateDateRequestEvent());
                getViewState().showError(e, false);
        }

        @Override
        public void onNext(FollowsResponse followsResponse) {
                if (mRefreshType != Constants.LOADING_MODE_ENDLESS) {
                    getViewState().setData(followsResponse.getResponse());
                } else {
                    getViewState().addData(followsResponse.getResponse());
                }
                askFollowShowed((followsResponse.getResponse()));
                mBus.post(new UpdateDateRequestEvent());
                getViewState().showContent();
        }

        @Override
        public void onCompleted() {

        }
    }
}
