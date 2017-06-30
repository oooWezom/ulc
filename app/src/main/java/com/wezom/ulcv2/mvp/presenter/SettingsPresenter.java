package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.ChangeAvatarRequestEvent;
import com.wezom.ulcv2.events.ChangeBackgroundRequestEvent;
import com.wezom.ulcv2.events.ChangeEvents;
import com.wezom.ulcv2.events.LanguagesSelectedWithDelay;
import com.wezom.ulcv2.events.LogoutRequestEvent;
import com.wezom.ulcv2.events.ProfileImageUpdateEvent;
import com.wezom.ulcv2.events.ProfileUpdateEvent;
import com.wezom.ulcv2.events.UpdateDateRequestEvent;
import com.wezom.ulcv2.exception.ApiException;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.view.SettingsView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.net.models.requests.ProfileRequest;
import com.wezom.ulcv2.net.models.requests.ProfileUpdateRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created by Sivolotskiy.v on 09.06.2016.
 */

@InjectViewState
public class SettingsPresenter extends BasePresenter<SettingsView> {

    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;
    @Inject
    PreferenceManager mPreferenceManager;
    @Inject
    Router mRouter;
    private Profile mProfile;

    public SettingsPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void loadData() {
        mProfile = mPreferenceManager.getProfileData();
        getViewState().setData(mProfile, mPreferenceManager.getPushEnabled());
    }

    public void loadLanguages() {
        mApiManager.getProfile(new ProfileRequest(0)).subscribe(profileResponse -> {
            getLanguagesFromBase(profileResponse.getProfile().getLanguages());
        }, throwable -> {

        });
    }

    public void logout(boolean restart) {
        mBus.post(new LogoutRequestEvent(restart));
    }

    public void blacklistRequest() {
        mRouter.navigateTo(Screens.BLACK_LIST_SCREEN);
    }

    public void submitPreferences(ProfileUpdateRequest request) {
        mApiManager.updateProfile(request)
                .subscribe(response -> {
                    refreshData();
                }, throwable -> {
                    getViewState().showMessageDialog(R.string.network_error, R.string.error_check_your_net_and_retry);

                }, () -> {
                    getViewState().showMessageDialog(R.string.success, R.string.data_successfully_updated);
                    mPreferenceManager.setPrivateProfile(request.getPrivate() ? 2 : 1);
                    mBus.post(new ProfileUpdateEvent(request));
                });
    }

    public void updateEnablePush(boolean isPush) {
        mPreferenceManager.setPushEnabled(isPush);
    }

    public void refreshData() {
        mBus.post(new UpdateDateRequestEvent());
    }

    public void clickChangeLanguage(List<Language> languages) {
        ArrayList<Language> currentList = null;

        if (languages != null) {
            currentList = new ArrayList<>();
            for (Language language : languages) {
                currentList.add(language);
            }
        }

        //bus.post(new LanguagesDisplayEvent(currentList, Constants.SWITCH_TYPE_ADD));
        mRouter.navigateTo(Screens.LANGUAGES_SCREEN, currentList);
    }

    public void clickLogin() {
    }

    public void clickPassword() {
      //  bus.post(new ChangeEvents.ChangingPassword());
    mRouter.navigateTo(Screens.CHANGE_PASS_SCREEN);
    }

    public void clickTerms() {
        //  bus.post(new ChangeEvents.ChangingPassword());
        mRouter.navigateTo(Screens.TERMS_OF_USE_FRAGMENT);
    }

    public void onChangedLogin(ChangeEvents.ChangedLoginEvent event) {
        getViewState().setLogin(event.getLogin());
    }


    public void onUserSelectedLanguages(LanguagesSelectedWithDelay event) {
        getViewState().setLanguages(event.getLanguageList());

    }


    public void onProfileImageUpdate(ProfileImageUpdateEvent event) {
        loadProfile();
    }


    public void onClickAvatarChange(int type) {
        //0 - gallery
        //1 - make photo
        mBus.post(new ChangeAvatarRequestEvent(type));
    }

    public void onBackgroundChangeClick() {
        mBus.post(new ChangeBackgroundRequestEvent());
    }

    public void loadProfile() {

        mApiManager.getProfile(new ProfileRequest(0))
                .subscribe(profileResponse -> {

                    getViewState().loadAvatarAndBackground(profileResponse.getProfile());

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
                        }
                    }
                    getViewState().showMessageDialog(errorMessageResId);

                });
    }

    public void getLanguagesFromBase(ArrayList<Integer> selectedLang) {
        ArrayList<Language> languages = Language.getLanguages();
        for (Language language : languages) {
            for (Integer idLang : selectedLang) {
                if (language.getServerId() == idLang) {
                    language.setCheckStatus(true);
                }
            }
        }

        getViewState().setLanguages(languages);
    }
}
