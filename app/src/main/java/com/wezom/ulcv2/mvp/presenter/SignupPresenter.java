package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.LanguageSelectedEvent;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.view.SignupView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.RegisterRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created by Sivolotskiy.v on 26.05.2016.
 */
@InjectViewState
public class SignupPresenter extends BasePresenter<SignupView> {

    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;
    @Inject
    Router mRouter;

    public SignupPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void register(RegisterRequest registerRequest) {

        getViewState().showLocalLoading();

        mApiManager.register(registerRequest).subscribe(registerResponse -> {
            getViewState().showEmailConfirmView();
            getViewState().hideLocalLoading();

        }, throwable -> {
            getViewState().hideLocalLoading();
            getViewState().showError(throwable.getMessage());
        }, () -> {
            getViewState().hideLocalLoading();
        });
    }

    public void callAuthRequest() {
        mBus.post(new BackEvents());
    }

    public void callTermsOfUse() {
        mRouter.navigateTo(Screens.TERMS_OF_USE_FRAGMENT);
    }

    public void clickChangeLanguage(List<Language> languages) {
        ArrayList<Language> currentList = null;

        if (languages != null) {
            currentList = new ArrayList<>();
            for (Language language : languages) {
                currentList.add(language);
            }
        }

        mRouter.navigateTo(Screens.LANGUAGES_SCREEN, currentList);
    }

    public void onUserSelectedLanguages(LanguageSelectedEvent event) {
        getViewState().setLanguages(event.getLanguageList());
    }

    public void onBackPressed() {
        mBus.post(new BackEvents());
    }
}
