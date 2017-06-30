package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.ShowNextTalkEvent;
import com.wezom.ulcv2.events.ShowTalkEvent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.view.ToTalkView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.net.models.requests.ActiveTalksRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Zorin A.
 * Date: 27.06.2016.
 */

@InjectViewState
public class ToTalkPresenter extends BasePresenter<ToTalkView> {
    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;
    @Inject
    @ApplicationContext
    Context mContext;
    @Inject
    Router mRouter;

    private List<Talk> mLoadedTalks = new ArrayList<>();
    private int mCount = 10;
    private int mOffset;
    private int mSelectedTalkPosition;
    private int mCategoryId;

    public ToTalkPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void onNewSessionClick() {
        mRouter.navigateTo(Screens.CREATE_2TALK_SCREEN);
    }

    public void getActiveTalks(@Constants.LoadingMode int loadMode) {
        if (loadMode == Constants.LOADING_MODE_INITIAL || loadMode == Constants.LOADING_MODE_REFRESH) {
            mOffset = 0;
        }
        getViewState().showLoading(loadMode);
        mApiManager.getActiveTalksById(new ActiveTalksRequest(mCategoryId, mOffset, mCount))
                .subscribe(activeTalksResponse -> {
                    ArrayList<Talk> talks = activeTalksResponse.getTalksList();
                    if (loadMode != Constants.LOADING_MODE_ENDLESS) {
                        mLoadedTalks = talks;
                        getViewState().setData(talks);
                    } else {
                        mLoadedTalks.addAll(talks);
                        getViewState().addData(talks);
                    }
                    getViewState().showContent();

                }, throwable -> {
                    getViewState().showError(throwable, false);
                });
        mOffset += mCount;
    }

    public void getCategories() {
        getViewState().showCategoryProgress(true);
        Observable.from(Category.getCategories())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .toList()
                .subscribe(categories -> {
                    getViewState().setCategories(categories);
                    getViewState().showCategoryProgress(false);
                    getViewState().showCategoryError(false);
                }, throwable -> {
                    getViewState().showCategoryProgress(false);
                    getViewState().showCategoryError(true);
                });

    }

    public void showTalk(Talk talk, int position) {
        mSelectedTalkPosition = position;
        mBus.post(new ShowTalkEvent(talk));
    }

    @Subscribe
    public void onShowNextTalk(ShowNextTalkEvent event) {
        int nextPosition = mSelectedTalkPosition + 1;
        if (mLoadedTalks.size() - 1 >= nextPosition) {
            Talk nextTalk = mLoadedTalks.get(nextPosition);
            showTalk(nextTalk, nextPosition);
        }
    }

    public void setCategoryId(int mCategoryId) {
        this.mCategoryId = mCategoryId;
    }
}