package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.LanguageSelectedEvent;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.view.LanguagesFragmentView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created: Zorin A.
 * Date: 09.06.2016.
 */

@InjectViewState
public class LanguagesFragmentPresenter extends BasePresenter<LanguagesFragmentView> {

    @Inject
    EventBus mBus;
    @Inject

    public LanguagesFragmentPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void onBackPressed() {
        mBus.post(new BackEvents());
    }

    public void loadData() {
        Observable.from(Language.getLanguages()).toList().subscribe(languages -> {
            getViewState().setLanguageData(languages);
        }, Throwable::printStackTrace);
    }

    public void onUserSelectedLanguages(List<Language> languages) {
        mBus.postSticky(new LanguageSelectedEvent(languages)); //TODO: implevent final functional when it will be ready;
        onBackPressed();
    }
}
