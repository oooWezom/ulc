package com.wezom.ulcv2.injection.component;

import com.wezom.ulcv2.injection.module.ActivityModule;
import com.wezom.ulcv2.injection.module.FragmentModule;
import com.wezom.ulcv2.injection.scope.ActivityScope;
import com.wezom.ulcv2.ui.activity.GameActivity;
import com.wezom.ulcv2.ui.activity.HomeActivity;
import com.wezom.ulcv2.ui.activity.TalkActivity;
import com.wezom.ulcv2.ui.dialog.CountDownDialog;
import com.wezom.ulcv2.ui.dialog.InviteDialog;
import com.wezom.ulcv2.ui.dialog.VoteRateDialog;

import dagger.Subcomponent;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */
@ActivityScope
@Subcomponent(modules =  ActivityModule.class)
public interface ActivityComponent {

    FragmentComponent providesFragmentComponent(FragmentModule fragmentModule);

    void inject(HomeActivity homeActivity);

    void inject(GameActivity gameActivity);

    void inject(TalkActivity talkActivity);

    void inject(InviteDialog inviteDialog);

    void inject(CountDownDialog countDownDialog);

    void inject(VoteRateDialog voteRateDialog);
}
