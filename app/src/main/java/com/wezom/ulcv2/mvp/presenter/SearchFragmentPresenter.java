package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;
import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.OnDrawerOpenEvent;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.view.SearchFragmentView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.SearchRequest;
import com.wezom.ulcv2.net.models.responses.ProfileSearchResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zorin.a on 16.06.2016.
 */

@InjectViewState
public class SearchFragmentPresenter extends BasePresenter<SearchFragmentView> {

    @Inject
    ApiManager mApiManager;
    @Inject
    EventBus mBus;
    @Inject
    PreferenceManager mPreferenceManager;

    private int mOffset = 0;
    private int mLoadCount = 20;
    private ArrayList<Language> mLanguages;


    public SearchFragmentPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void onBackPressed() {
        mBus.post(new BackEvents());
//        bus.post(new OnDrawerOpenEvent(true));
    }

    public void search(SearchRequest.Request request, int refreshMode) {
        if (refreshMode == Constants.LOADING_MODE_REFRESH || refreshMode == Constants.LOADING_MODE_INITIAL) {
            mOffset = 0;
        }

        ArrayList<Integer> transformedIds = new ArrayList<>();

        if (request.getLanguages() != null) {
            for (Integer languageId : request.getLanguages()) {
                transformedIds.add(mLanguages.get(languageId - 1).getServerId());
            }
        }
        if (request.getLanguages() != null) {
            request.setLanguages(transformedIds);
        }

        mApiManager.search(new SearchRequest(request, mOffset, mLoadCount))
                .subscribe(new Observer<ProfileSearchResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getViewState().showError(throwable, false);
                    }

                    @Override
                    public void onNext(ProfileSearchResponse profileSearchResponse) {
                        if (!(refreshMode == Constants.LOADING_MODE_ENDLESS)) {
                            getViewState().setData(profileSearchResponse.getResponse());
                        } else {
                            getViewState().addData(profileSearchResponse.getResponse());
                        }
                        getViewState().showContent();
                        getViewState().showLoading(false);
                    }
                });
        mOffset += mLoadCount;
    }

    public void loadLanguages() {
        ArrayList<Language> languages = Language.getLanguages();
        mLanguages = languages;
        ArrayList<String> languageNames = new ArrayList<>();
        for (Language language : languages) {
            languageNames.add(language.getName());
        }
        getViewState().setLanguages(languageNames);
    }

    public void getMaxLevel() {
        int level = mPreferenceManager.getMaxUserLevel();
        getViewState().prepareLevelRangeBar(level);
    }

    public void showKeyBoard() {
        rx.Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    getViewState().showKeyboard();
                });
    }

    public void hideKeyboard(View view) {
        Utils.hideKeyboard(view);
    }
}
