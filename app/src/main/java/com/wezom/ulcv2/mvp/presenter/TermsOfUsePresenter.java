package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.mvp.view.TermsOfUseView;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created by usik.a on 01.03.2017.
 */

@InjectViewState
public class TermsOfUsePresenter extends BasePresenter<TermsOfUseView> {

    @Inject
    Router mRouter;
    public TermsOfUsePresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void onBackPressed() {
        mRouter.exit();
    }
}
