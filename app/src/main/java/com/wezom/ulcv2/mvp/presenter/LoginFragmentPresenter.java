package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.LocaleUtils;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.database.DatabaseManager;
import com.wezom.ulcv2.events.InitDrawerEvent;
import com.wezom.ulcv2.events.LoginEvent;
import com.wezom.ulcv2.events.SocialLoginRequestEvent;
import com.wezom.ulcv2.events.VideoPreviewEvent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.view.LoginFragmentView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.LoginRequest;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import lombok.Getter;
import ru.terrakok.cicerone.Router;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */

@InjectViewState
public class LoginFragmentPresenter extends BasePresenter<LoginFragmentView> {
    private static final String[] LANGUAGES = {"English"};
    @Inject
    ApiManager mApiManager;
    @Inject
    @Getter
    EventBus mBus;
    @Inject
    @ApplicationContext
    Context mContext;
    @Inject
    Router mRouter;
    @Inject
    DatabaseManager databaseManager;
    @Inject
    LocaleUtils localeUtils;

    String mToken;

    public LoginFragmentPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }


    public void doLogin(String email, String password) {
        getViewState().showLoading();
        mApiManager.login(new LoginRequest(email, password)).subscribe(loginResponse -> {
            mToken = loginResponse.getToken();
            getViewState().animateLoginSuccess();

        }, throwable -> {
            getViewState().showMessageDialog(R.string.error, throwable.getMessage());
            getViewState().hideLoading();
        }, () -> {
            getViewState().hideLoading();
        });
    }

    public void checkUpdate() {
        mApiManager.getApiVersion()
                .subscribe(response -> {
                            if (response.getVersion() > Constants.VERSION_API) {
                                getViewState().showUpdateDialog();
                            }
                        },
                        throwable -> {
                            getViewState().showMessageDialog(R.string.error, throwable.getMessage());
                            getViewState().hideLoading();
                        }
                );
    }

    public void onAnimationEnd(String login, String password) {
        Log.d("Log_ onAnimationEnd ", login + " pass: " + password);
        mBus.post(new LoginEvent(login, password, mToken));
    }

    public void doRegister() {
        mRouter.navigateTo(Screens.SIGNUP_SCREEN);
    }

    public void doPasswordRestore() {
        mRouter.navigateTo(Screens.PASSWORD_RECOVERY_SCREEN);
    }

    public void doLoginVk() {
        mBus.post(new SocialLoginRequestEvent(SocialLoginRequestEvent.VK));
    }

    public void doLoginFb() {
        mBus.post(new SocialLoginRequestEvent(SocialLoginRequestEvent.FACEBOOK));
    }

    public void clickChangeLanguage() {

        getViewState().showAppLanguagesDialog();
    }

    public void stopVideoPreview() {
        mBus.post(new VideoPreviewEvent(VideoPreviewEvent.STOP));
    }

    public void startVideoPreview() {
        mBus.post(new VideoPreviewEvent(VideoPreviewEvent.START));
    }

    public void hideKeyboard(View view) {
        Utils.hideKeyboard(view);
    }

    public void switchLanguage(int language) {
        switch (language) {
            case R.id.action_switch_english:
                localeUtils.setLocale(LocaleUtils.ENGLISH);
                Toast.makeText(mContext, "English language", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_switch_russian:
                localeUtils.setLocale(LocaleUtils.RUSSIAN);
                Toast.makeText(mContext, "Русский язык", Toast.LENGTH_SHORT).show();
                break;
        }
        getViewState().setUiValues();
        mBus.post(new InitDrawerEvent());
    }

    public void onWatchClick() {
        mRouter.navigateTo(Screens.SESSIONS_WATCH_FRAGMENT);
    }

    public void getCategoriesFromBackend() {
        mApiManager.getCategories()
                .subscribe(categoryResponse -> {
                    if (categoryResponse.getResponse() == null || categoryResponse.getResponse().size() < 1) {
                        getCategoriesFromBackend();
                    } else {
                        databaseManager.clearCategories();
                        Category.updateCategories(categoryResponse.getResponse());
                    }

                }, throwable -> {
                    //don't do anything
                });
    }
}
