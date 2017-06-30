package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.BuildConfig;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.events.ChangeAvatarRequestEvent;
import com.wezom.ulcv2.events.ChangeEvents;
import com.wezom.ulcv2.events.LanguagesSelectedWithDelay;
import com.wezom.ulcv2.events.ProfileImageUpdateEvent;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.presenter.SettingsPresenter;
import com.wezom.ulcv2.mvp.view.SettingsView;
import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.net.models.requests.ProfileUpdateRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sivolotskiy.v on 09.06.2016.
 */
public class SettingsFragment extends BaseFragment implements
        SettingsView, MenuItem.OnMenuItemClickListener {

    private static final String EXTRA_NAME = "fragment_settings_extra_name";

    @InjectPresenter
    SettingsPresenter mPreferenceFragmentPresenter;

    @Inject
    EventBus mEventBus;
    @Inject
    PreferenceManager preferenceManager;

    @BindView(R.id.fragment_preference_background_image)
    ImageView mBackgroundImage;
    @BindView(R.id.fragment_setting_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_preference_about_text)
    EditText mAboutText;
    @BindView(R.id.fragment_setting_languages_text_view)
    TextView mLanguagesTextView;
    @BindView(R.id.fragment_preference_about_button)
    LinearLayout mAboutButton;
    @BindView(R.id.fragment_preference_private_account_switch)
    SwitchCompat mPrivateAccountSwitch;
    @BindView(R.id.fragment_preference_push_notifications_switch)
    SwitchCompat mPushNotificationsSwitch;
    @BindView(R.id.fragment_setting_gender_tab_layout)
    TabLayout mTabs;
    @BindView(R.id.fragment_preference_avatar_image)
    ImageView mAvatarImage;
    @BindView(R.id.fragment_preference_login_text)
    TextView mLoginText;
    @BindView(R.id.fragment_preference_name_text)
    TextView mNameText;
    @BindView(R.id.fragment_settings_login_ripple_layout)
    MaterialRippleLayout mLayout;
    @BindView(R.id.fragment_preference_serv_switch)
    SwitchCompat serverSwitch;
    @BindView(R.id.fragment_preference_is_dev_container)
    LinearLayout isDevContainer;

    private HashSet<Language> mLanguages = new HashSet<>();
    private List<Language> mLanguagesList;


    public static SettingsFragment getNewInstance(String name) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_settings;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareToolbar();
        mToolbar.setVisibility(View.VISIBLE);
        mToolbar.setTitle(getString(R.string.setting));
        mToolbar.setSubtitle(null);
        prepareGenderBarLayout();
        showDrawerToggleButton(mToolbar);

        mPreferenceFragmentPresenter.loadData();

        if (mLanguages.isEmpty()) {
            mPreferenceFragmentPresenter.loadLanguages();
        }

        if (!BuildConfig.DEBUG)
            isDevContainer.setVisibility(View.GONE);

        //TODO: for testing purpose only
        //if checked or true - dev.ulc.tv serv, not checked - ulc.tv
        boolean isDevServ = preferenceManager.isDevServ();
        serverSwitch.setChecked(isDevServ);

        serverSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    preferenceManager.setIsDevServ(true);
                    setDevServer(true);
                } else {
                    preferenceManager.setIsDevServ(false);
                    setDevServer(false);
                }
            }
        });
    }

    private void setDevServer(boolean isDev) {
        Log.d("Log_ setDevServer ", String.valueOf(isDev));
        if (isDev) {
            Constants.API_URL = "http://dev.ulc.tv";
            Constants.SOCKET_BASE_URL = "ws://dev.ulc.tv";
            showMessageToast("set dev.ulc.tv");
            preferenceManager.setBaseUrl(getActivity(), "http://dev.ulc.tv", "ws://dev.ulc.tv");

        } else {
            Constants.API_URL = "https://ulc.tv";
            Constants.SOCKET_BASE_URL = "wss://ulc.tv";
            showMessageToast("set ulc.tv");
            preferenceManager.setBaseUrl(getActivity(), "https://ulc.tv", "wss://ulc.tv");
        }
        showMessageToast("Restart application");
        mPreferenceFragmentPresenter.logout(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);

        MenuItem submitButton = menu.findItem(R.id.accept_item);
        submitButton.setVisible(true);
        submitButton.setOnMenuItemClickListener(this);
    }

    @OnClick(R.id.fragment_preference_name_text)
    public void onUpdatableClick(View view) {
        showDataInputDialog(R.string.enter_new_name, (TextView) view);
    }

    @OnClick(R.id.fragment_preference_language_button)
    public void onLanguagesClick(View view) {
        mPreferenceFragmentPresenter.clickChangeLanguage(mLanguagesList);
    }

    @Override
    public void setData(Profile data, boolean pushEnabled) {
        if (data.getAbout().isEmpty()) {
            mAboutText.setText(R.string.click_to_change_info);
        } else {
            mAboutText.setText(data.getAbout());
        }
        mNameText.setText(data.getName());
        mLoginText.setText(data.getLogin());
        mPrivateAccountSwitch.setChecked(data.getAccountStatusId() != 1);
        mPushNotificationsSwitch.setChecked(pushEnabled);
        if (data.getLanguages() != null) {
            mPreferenceFragmentPresenter.getLanguagesFromBase(data.getLanguages());
        }

        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + data.getBackground())
                .into(mBackgroundImage);
        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + data.getAvatar())
                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                .into(mAvatarImage);
    }

    @Override
    public void setLogin(String login) {
        mLoginText.setText(login);
    }

    @Override
    public void setLanguages(List<Language> response) {
        mLanguages.clear();
        mLanguagesList = response;
        for (Language language : response) {
            if (language.isCheckStatus()) {
                mLanguages.add(language);
                setLanguagesText(language.getName());
            }
        }
    }

    @Override
    public void showAvatarPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(), mAvatarImage, Gravity.TOP);
        popupMenu.inflate(R.menu.menu_avatar_load_type);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_avatar_load_type_gallery:
                    mPreferenceFragmentPresenter.onClickAvatarChange(ChangeAvatarRequestEvent.GALLERY);
                    return true;
                case R.id.menu_avatar_load_type_photo:
                    mPreferenceFragmentPresenter.onClickAvatarChange(ChangeAvatarRequestEvent.PHOTO);
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void loadAvatarAndBackground(Profile profile) {

        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + profile.getBackground())
                .into(mBackgroundImage);

        Picasso.with(getContext()).load(Constants.CONTENT_DEV_URL + profile.getAvatar())
                .placeholder(R.drawable.ic_default_user_big)
                .into(mAvatarImage);
    }

    @OnClick(R.id.fragment_preference_black_list_button)
    public void onBlacklistClick() {
        mPreferenceFragmentPresenter.blacklistRequest();
    }

    @OnClick(R.id.fragment_preference_logout_button)
    public void onClickLogout() {
        AppCompatDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.are_you_sure_about_logout)
                .setPositiveButton(R.string.ok, (dialog1, which) -> {
                    mPreferenceFragmentPresenter.logout(false);
                })
                .setNegativeButton(R.string.no, null)
                .create();
        dialog.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_item:
                ProfileUpdateRequest profile = new ProfileUpdateRequest();
                profile.setUsername(mNameText.getText().toString());
                ArrayList<Integer> languageList = null;
                if (mLanguages.size() > 0) {
                    languageList = new ArrayList<>();
                    for (Language language : mLanguages) {
                        try {
                            languageList.add(language.getServerId());
                        } catch (Exception e) {

                        }
                    }
                }
                profile.setLanguages(languageList);
                profile.setAbout(mAboutText.getText().toString());
                profile.setPrivate(mPrivateAccountSwitch.isChecked());
                mPreferenceFragmentPresenter.updateEnablePush(mPushNotificationsSwitch.isChecked());
                mPreferenceFragmentPresenter.submitPreferences(profile);
                mPreferenceFragmentPresenter.refreshData();
                return true;
        }
        return false;
    }

    @OnClick(R.id.fragment_setting_background_change_button)
    public void onBackgroundChange() {
        mPreferenceFragmentPresenter.onBackgroundChangeClick();
    }

    @OnClick(R.id.fragment_preference_avatar_image)
    public void onAvatarClick() {
        showAvatarPopupMenu();
    }

    @OnClick(R.id.fragment_settings_login_ripple_layout)
    void onClickLogin() {
        mPreferenceFragmentPresenter.clickLogin();
    }


    @OnClick(R.id.fragment_settings_password_ripple_layout)
    void onClickPassword() {
        mPreferenceFragmentPresenter.clickPassword();
    }

    @OnClick(R.id.fragment_preference_terms_and_conditions)
    void onClickTerms() {
        mPreferenceFragmentPresenter.clickTerms();
    }

    //TODO:delete this shit
    @OnClick(R.id.fragment_preference_url_button)
    void mApiChange() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("API change dialog");
//        builder.setView(R.layout.dialog_base_urls_change);
//        builder.setCancelable(true);
//        builder.setPositiveButton("OK", (dialog, which) -> {
//
//            EditText api = (EditText) ((AlertDialog) dialog).findViewById(R.id.api_url);
//            PreferenceManager.setTempApiURL(getActivity(), api.getText().toString());
//
//            EditText content = (EditText) ((AlertDialog) dialog).findViewById(R.id.api_url);
//            PreferenceManager.setTempContentDevUrl(getActivity(), content.getText().toString());
//
//            EditText wsProf = (EditText) ((AlertDialog) dialog).findViewById(R.id.ws_profile);
//            PreferenceManager.setWsProfileUrl(getActivity(), wsProf.getText().toString());
//
//            EditText wsSession = (EditText) ((AlertDialog) dialog).findViewById(R.id.ws_session);
//            PreferenceManager.setWsSessionUrl(getActivity(), wsSession.getText().toString());
//
//            dialog.dismiss();
//            ((HomeActivity) getActivity()).closeApplication();
//        });
//
//        AlertDialog d = builder.show();
//        EditText api = (EditText) d.findViewById(R.id.api_url);
//        String aHint = "" + PreferenceManager.getTempApiUrl(getActivity());
//        api.setHint(aHint);
//
//        EditText content = (EditText) d.findViewById(R.id.content_url);
//        String cHint = "" + PreferenceManager.getTempContentDevUrl(getActivity());
//        content.setHint(cHint);
//
//        EditText wsProf = (EditText) d.findViewById(R.id.ws_profile);
//        String pHint = "" + PreferenceManager.getWsProfileUrl(getActivity());
//        wsProf.setHint(pHint);
//
//        EditText wsSession = (EditText) d.findViewById(R.id.ws_session);
//        String sHint = "" + PreferenceManager.getWsSessionUrl(getActivity());
//        wsSession.setHint(sHint);
    }

    private void prepareToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setVisibility(View.VISIBLE);
        mToolbar.setTitle(R.string.setting);
    }

    private void prepareGenderBarLayout() {
        mTabs.addTab(mTabs.newTab().setText(getString(R.string.male)));
        mTabs.addTab(mTabs.newTab().setText(getString(R.string.female)));
    }

    private void setLanguagesText(String text) {
        String currentText = mLanguagesTextView.getText().toString();
        mLanguagesTextView.setText(currentText.trim().isEmpty() ? text :
                String.format("%s, %s", currentText, text));
    }

    private void showDataInputDialog(int titleRes, final TextView view) {
        AppCompatDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(titleRes)
                .setView(R.layout.dialog_data_input)
                .setPositiveButton(R.string.ok, (dialog1, which) -> {
                    EditText data = ButterKnife.findById(((AlertDialog) dialog1), R.id.dialog_data_input_data_edit);
                    view.setText(data.getText().toString());
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }

    @Subscribe
    public void onChangedLogin(ChangeEvents.ChangedLoginEvent event) {
        mPreferenceFragmentPresenter.onChangedLogin(event);
    }

    @Subscribe
    public void onUserSelectedLanguages(LanguagesSelectedWithDelay event) {
        mPreferenceFragmentPresenter.onUserSelectedLanguages(event);

    }

    @Subscribe
    public void onProfileImageUpdate(ProfileImageUpdateEvent event) {
        mPreferenceFragmentPresenter.onProfileImageUpdate(event);
    }

}
