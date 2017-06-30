package com.wezom.ulcv2.injection.module;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.wezom.ulcv2.injection.qualifier.ActivityContext;
import com.wezom.ulcv2.ui.activity.BaseActivity;

import java.lang.ref.WeakReference;

import dagger.Module;
import dagger.Provides;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */
@Module
public class ActivityModule {
    private WeakReference<BaseActivity> mActivity;

    public ActivityModule(BaseActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Provides
    @com.wezom.ulcv2.injection.qualifier.FragmentManager
    public FragmentManager provideFragmentManager() {
        return mActivity.get().getSupportFragmentManager();
    }


    @Provides
    @ActivityContext
    Context providesContext() {
        return mActivity.get();
    }

    @Provides
    @ActivityContext
    Activity provideActivity() {
        return mActivity.get();
    }
}
