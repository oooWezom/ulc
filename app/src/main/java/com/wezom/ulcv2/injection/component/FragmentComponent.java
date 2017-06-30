package com.wezom.ulcv2.injection.component;

import com.wezom.ulcv2.injection.module.FragmentModule;
import com.wezom.ulcv2.injection.scope.FragmentScope;
import com.wezom.ulcv2.ui.fragment.AvatarFragment;
import com.wezom.ulcv2.ui.fragment.BlackListFragment;
import com.wezom.ulcv2.ui.fragment.ChangeLoginFragment;
import com.wezom.ulcv2.ui.fragment.ChangePasswordFragment;
import com.wezom.ulcv2.ui.fragment.ChatFragment;
import com.wezom.ulcv2.ui.fragment.ConfirmRestorePassFragment;
import com.wezom.ulcv2.ui.fragment.ConversationFragment;
import com.wezom.ulcv2.ui.fragment.Create2TalkSessionFragment;
import com.wezom.ulcv2.ui.fragment.DialogFragment;
import com.wezom.ulcv2.ui.fragment.DialogsFragment;
import com.wezom.ulcv2.ui.fragment.FeedFilterFragment;
import com.wezom.ulcv2.ui.fragment.FollowsFragment;
import com.wezom.ulcv2.ui.fragment.FollowsListFragment;
import com.wezom.ulcv2.ui.fragment.GamesFragment;
import com.wezom.ulcv2.ui.fragment.LanguagesFragment;
import com.wezom.ulcv2.ui.fragment.LoginFragment;
import com.wezom.ulcv2.ui.fragment.NavigationDrawerFragment;
import com.wezom.ulcv2.ui.fragment.NewsfeedFragment;
import com.wezom.ulcv2.ui.fragment.PasswordRecoveryFragment;
import com.wezom.ulcv2.ui.fragment.PlayVideoFragment;
import com.wezom.ulcv2.ui.fragment.ProfileFragment;
import com.wezom.ulcv2.ui.fragment.SessionInfoFragment;
import com.wezom.ulcv2.ui.fragment.SearchFragment;
import com.wezom.ulcv2.ui.fragment.TermsOfUseFragment;
import com.wezom.ulcv2.ui.fragment.WatchSessionsFragment;
import com.wezom.ulcv2.ui.fragment.SettingsFragment;
import com.wezom.ulcv2.ui.fragment.SignupFragment;
import com.wezom.ulcv2.ui.fragment.TalkUserSearchFragment;
import com.wezom.ulcv2.ui.fragment.ToPlayFragment;
import com.wezom.ulcv2.ui.fragment.ToTalkFragment;
import com.wezom.ulcv2.ui.fragment.VideoFragment;
import com.wezom.ulcv2.ui.fragment.Watch2PlaySessionsFragment;
import com.wezom.ulcv2.ui.fragment.Watch2TalkSessionsFragment;

import dagger.Subcomponent;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */
@FragmentScope
@Subcomponent(modules = {FragmentModule.class})
public interface FragmentComponent {

    void inject(LoginFragment loginFragment);

    void inject(AvatarFragment avatarFragment);

    void inject(NavigationDrawerFragment navigationDrawerFragment);

    void inject(SignupFragment signupFragment);

    void inject(PasswordRecoveryFragment passwordRecoveryFragment);

    void inject(FollowsFragment followsFragment);

    void inject(FollowsListFragment followingFragment);

    void inject(ProfileFragment profileFragment);

    void inject(SearchFragment filterFragment);

    void inject(NewsfeedFragment newsfeedFragment);

    void inject(FeedFilterFragment feedFilterFragment);

    void inject(SettingsFragment settingsFragment);

    void inject(ChangeLoginFragment changeLoginFragment);

    void inject(ChangePasswordFragment changePasswordFragment);

    void inject(LanguagesFragment languagesFragment);

    void inject(BlackListFragment blackListFragment);

    void inject(DialogsFragment dialogsFragment);

    void inject(DialogFragment dialogFragment);

    void inject(ConversationFragment conversationFragment);

    void inject(ToPlayFragment toPlayFragment);

    void inject(GamesFragment gamesFragment);

    void inject(SessionInfoFragment sessionInfoFragment);

    void inject(ToTalkFragment toTalkFragment);

    void inject(Create2TalkSessionFragment create2TalkSessionFragment);

    void inject(TalkUserSearchFragment talkUserSearchFragment);

    void inject(ChatFragment chatFragment);

    void inject(VideoFragment videoFragment);

    void inject(PlayVideoFragment playVideoFragment);

    void inject(WatchSessionsFragment watchSessionsFragment);

    void inject(Watch2PlaySessionsFragment watch2PlaySessionsFragment);

    void inject(Watch2TalkSessionsFragment watch2TalkSessionsFragment);

    void inject(TermsOfUseFragment termsOfUseFragment);

    void inject(ConfirmRestorePassFragment confirmRestorePassFragment);
}
