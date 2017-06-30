package com.wezom.ulcv2.injection.component;

import com.wezom.ulcv2.injection.module.ActivityModule;
import com.wezom.ulcv2.injection.module.ApplicationModule;
import com.wezom.ulcv2.injection.scope.ApplicationScope;
import com.wezom.ulcv2.jobs.FetchDialogsJob;
import com.wezom.ulcv2.jobs.FetchLanguageJob;
import com.wezom.ulcv2.jobs.FetchMessagesJob;
import com.wezom.ulcv2.jobs.SendMessageJob;
import com.wezom.ulcv2.mvp.presenter.AvatarFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.BlackListPresenter;
import com.wezom.ulcv2.mvp.presenter.ChangeLoginPresenter;
import com.wezom.ulcv2.mvp.presenter.ChangePasswordPresenter;
import com.wezom.ulcv2.mvp.presenter.ChatFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.ChatPresenter;
import com.wezom.ulcv2.mvp.presenter.ConfirmRestorePassPresenter;
import com.wezom.ulcv2.mvp.presenter.ConversationPresenter;
import com.wezom.ulcv2.mvp.presenter.Create2TalkSessionPresenter;
import com.wezom.ulcv2.mvp.presenter.DialogFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.DialogsFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.FeedFilterPresenter;
import com.wezom.ulcv2.mvp.presenter.FollowsListPresenter;
import com.wezom.ulcv2.mvp.presenter.FollowsPresenter;
import com.wezom.ulcv2.mvp.presenter.GameActivityPresenter;
import com.wezom.ulcv2.mvp.presenter.GamesFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.HomeActivityPresenter;
import com.wezom.ulcv2.mvp.presenter.LanguagesFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.LoginFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.NavigationDrawerPresenter;
import com.wezom.ulcv2.mvp.presenter.NewsfeedPresenter;
import com.wezom.ulcv2.mvp.presenter.PasswordRecoveryPresenter;
import com.wezom.ulcv2.mvp.presenter.ProfileFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.SearchFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.SessionInfoPresenter;
import com.wezom.ulcv2.mvp.presenter.SettingsPresenter;
import com.wezom.ulcv2.mvp.presenter.SignupPresenter;
import com.wezom.ulcv2.mvp.presenter.TalkActivityPresenter;
import com.wezom.ulcv2.mvp.presenter.TalkUserSearchFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.TermsOfUsePresenter;
import com.wezom.ulcv2.mvp.presenter.ToPlayPresenter;
import com.wezom.ulcv2.mvp.presenter.ToTalkPresenter;
import com.wezom.ulcv2.mvp.presenter.VideoFragmentPresenter;
import com.wezom.ulcv2.mvp.presenter.Watch2PlaySessionsPresenter;
import com.wezom.ulcv2.mvp.presenter.Watch2TalkSessionsPresenter;
import com.wezom.ulcv2.net.ConnectionListener;

import dagger.Component;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */
@ApplicationScope
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    ActivityComponent providesActivityComponent(ActivityModule activityModule);

    void inject(FetchDialogsJob fetchDialogsJob);

    void inject(FetchLanguageJob fetchLanguageJob);

    void inject(FetchMessagesJob fetchMessagesJob);

    void inject(SendMessageJob sendMessageJob);

    void inject(ConnectionListener connectionListener);

    void inject(HomeActivityPresenter homeActivityPresenter);

    void inject(AvatarFragmentPresenter avatarFragmentPresenter);

    void inject(LoginFragmentPresenter loginFragmentPresenter);

    void inject(BlackListPresenter blackListPresenter);

    void inject(ChangeLoginPresenter changeLoginPresenter);

    void inject(ChangePasswordPresenter changePasswordPresenter);

    void inject(ChatFragmentPresenter chatFragmentPresenter);

    void inject(ChatPresenter chatPresenter);

    void inject(ConversationPresenter conversationPresenter);

    void inject(Create2TalkSessionPresenter create2TalkSessionPresenter);

    void inject(DialogFragmentPresenter dialogFragmentPresenter);

    void inject(DialogsFragmentPresenter dialogsFragmentPresenter);

    void inject(FeedFilterPresenter feedFilterPresenter);

    void inject(FollowsListPresenter followsListPresenter);

    void inject(FollowsPresenter followsPresenter);

    void inject(GameActivityPresenter gameActivityPresenter);

    void inject(GamesFragmentPresenter gamesFragmentPresenter);

    void inject(LanguagesFragmentPresenter languagesFragmentPresenter);

    void inject(NavigationDrawerPresenter navigationDrawerPresenter);

    void inject(NewsfeedPresenter newsfeedPresenter);

    void inject(PasswordRecoveryPresenter passwordRecoveryPresenter);

    void inject(ProfileFragmentPresenter profileFragmentPresenter);

    void inject(SearchFragmentPresenter searchFragmentPresenter);

    void inject(SessionInfoPresenter sessionInfoPresenter);

    void inject(SettingsPresenter settingsPresenter);

    void inject(SignupPresenter signupPresenter);

    void inject(TalkActivityPresenter talkActivityPresenter);

    void inject(TalkUserSearchFragmentPresenter talkUserSearchFragmentPresenter);

    void inject(ToPlayPresenter toPlayPresenter);

    void inject(ToTalkPresenter toTalkPresenter);

    void inject(VideoFragmentPresenter videoFragmentPresenter);

    void inject(Watch2TalkSessionsPresenter watch2TalkSessionsPresenter);

    void inject(Watch2PlaySessionsPresenter watch2PlaySessionsPresenter);

    void inject(TermsOfUsePresenter termsOfUsePresenter);

    void inject(ConfirmRestorePassPresenter confirmRestorePassPresenter);

}
