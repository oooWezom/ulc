package com.wezom.ulcv2.injection.module;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.wezom.ulcv2.injection.qualifier.ChildFragmentManager;

import java.lang.ref.WeakReference;

import dagger.Module;
import dagger.Provides;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */

@Module
public class FragmentModule {

    private WeakReference<Fragment> mFragment;

    public FragmentModule(Fragment fragment) {
        mFragment = new WeakReference<>(fragment);
    }

    @Provides
    @ChildFragmentManager
    public FragmentManager provideFragmentManager() {
        return mFragment.get().getChildFragmentManager();
    }

}
