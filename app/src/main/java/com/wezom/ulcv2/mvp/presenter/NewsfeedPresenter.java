package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.FilterOptionsRequestEvent;
import com.wezom.ulcv2.events.On2PlayClickEvent;
import com.wezom.ulcv2.events.On2TalkClickEvent;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.view.NewsfeedView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.EventsRequest;
import com.wezom.ulcv2.net.models.responses.EventsResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.terrakok.cicerone.Router;
import rx.Subscriber;

import static com.wezom.ulcv2.ui.fragment.NewsfeedFragment.TYPE_FOLLOWING;
import static com.wezom.ulcv2.ui.fragment.NewsfeedFragment.TYPE_GAMES;
import static com.wezom.ulcv2.ui.fragment.NewsfeedFragment.TYPE_SELF;
import static com.wezom.ulcv2.ui.fragment.NewsfeedFragment.TYPE_TALKS;

/**
 * Created by Sivolotskiy.v on 03.06.2016.
 */
@InjectViewState
public class NewsfeedPresenter extends BasePresenter<NewsfeedView> {

    private final int mLoadCount = 100;
    @Inject
    ApiManager mApiManager;
    @Inject
    @Getter
    @Accessors(prefix = "m")
    EventBus mBus;
    @Inject
    Router mRouter;

    @Setter
    @Accessors(prefix = "m")
    private int mOffset = 0;


    public NewsfeedPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void loadNewsFeed(@Constants.LoadingMode int refreshMode, int feedType, int profileId) {
        if (refreshMode == Constants.LOADING_MODE_REFRESH) {
            mOffset = 0;
        }
        getViewState().showLoading(refreshMode);
        switch (feedType) {
            case TYPE_SELF:
                mApiManager.getSelfEvents(new EventsRequest(profileId, mOffset, mLoadCount, 0))
                        .subscribe(new FeedSubscriber(refreshMode));
                break;
            case TYPE_FOLLOWING:
                mApiManager.getFollowingEvents(new EventsRequest(0, mOffset, mLoadCount, 0))
                        .subscribe(new FeedSubscriber(refreshMode));
                break;
            case TYPE_TALKS:
                mApiManager.getSelfEvents(new EventsRequest(profileId, mOffset, mLoadCount, feedType))
                        .subscribe(new FeedSubscriber(refreshMode));
                break;
            case TYPE_GAMES:
                mApiManager.getSelfEvents(new EventsRequest(profileId, mOffset, mLoadCount, feedType))
                        .subscribe(new FeedSubscriber(refreshMode));
                break;
        }
        mOffset += mLoadCount;
    }

    public void filterSettingsClick() {
        mRouter.navigateTo(Screens.FEED_FILTER_SCREEN);
    }

    public void onClick2Play() {
        mBus.post(new On2PlayClickEvent());
    }

    public void onClick2Talk() {
        mBus.post(new On2TalkClickEvent());
    }

    public ArrayList<Category> getCategories() {
        return Category.getCategories();
    }

    class FeedSubscriber extends Subscriber<EventsResponse> {

        private int mRefreshMode;

        FeedSubscriber(int refreshMode) {
            mRefreshMode = refreshMode;
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
                getViewState().showError(e, false);
        }

        @Override
        public void onNext(EventsResponse eventsResponse) {
                if (mRefreshMode != Constants.LOADING_MODE_ENDLESS) {
                    getViewState().setData(eventsResponse.getResponse());
                } else {
                    getViewState().addData(eventsResponse.getResponse());
                }
            //// TODO: 24.01.2017 Check this method
                getViewState().showContent();
        }
    }
}
