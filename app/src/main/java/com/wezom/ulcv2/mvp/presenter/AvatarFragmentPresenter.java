package com.wezom.ulcv2.mvp.presenter;

import android.graphics.Bitmap;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.AvatarCropEvent;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.BackgroundCropEvent;
import com.wezom.ulcv2.events.OnShowDrawerEvent;
import com.wezom.ulcv2.mvp.view.AvatarFragmentView;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created: Zorin A.
 * Date: 24.05.2016.
 */

@InjectViewState
public class AvatarFragmentPresenter extends BasePresenter<AvatarFragmentView> {
    @Inject
    EventBus mBus;


    public AvatarFragmentPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }


    public void onCrop(Bitmap imageBitmap, boolean isAvatar) {
        if (isAvatar) {
            mBus.post(new AvatarCropEvent(imageBitmap));
        } else {
            mBus.post(new BackgroundCropEvent(imageBitmap));
        }
    }

    public void onBackPressed() {
        mBus.post(new BackEvents());
    }

    public void setDrawerVisible(boolean isVisible) {
        mBus.post(new OnShowDrawerEvent(isVisible));
    }
}
