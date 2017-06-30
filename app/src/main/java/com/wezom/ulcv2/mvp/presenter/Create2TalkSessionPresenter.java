package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.CreateTalkClickEvent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.view.Create2TalkSessionView;
import com.wezom.ulcv2.net.ApiManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created: Zorin A.
 * Date: 16.07.2016.
 */

@InjectViewState
public class Create2TalkSessionPresenter extends BasePresenter<Create2TalkSessionView> {
    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;
    @Inject
    @ApplicationContext
    Context mContext;
    @Inject
    PreferenceManager mPreferenceManager;

    public Create2TalkSessionPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }


    public void onBackPressed() {
        mBus.post(new BackEvents());
    }

    public void start2TalkSession(int categoryId, String categoryName) {
        mBus.post(new CreateTalkClickEvent(categoryId, categoryName));
    }

    public void getData() {
        rx.Observable.from(Category.getCategories())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .toList()
                .subscribe(categories -> {
                    getViewState().setData(categories);
                }, throwable -> {
                    getViewState().showMessageDialog(R.string.no_categories);
                });
    }

    public void notifyUserDataNotSet(int message) {
        getViewState().showMessageDialog(R.string.error, message);
    }
}
