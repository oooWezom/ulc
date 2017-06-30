package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.mvp.view.SessionsWatchView;
import com.wezom.ulcv2.ui.adapter.SessionsPagerAdapter;

import butterknife.BindView;

/**
 * Created: Zorin A.
 * Date: 23.02.2017.
 */

public class WatchSessionsFragment extends BaseFragment implements SessionsWatchView {
    SessionsPagerAdapter pagerAdapter;

    @BindView(R.id.fragment_sessions_tab_layout)
    TabLayout tablLayout;
    @BindView(R.id.sessions_watch_viewpager)
    ViewPager viewPager;


    public static WatchSessionsFragment getNewInstance() {
        WatchSessionsFragment fragment = new WatchSessionsFragment();
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_sessions_watch;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        pagerAdapter = new SessionsPagerAdapter(getActivity().getSupportFragmentManager());
        pagerAdapter.addFragment(Watch2PlaySessionsFragment.getNewInstance(), getString(R.string.to_play));
        pagerAdapter.addFragment(Watch2TalkSessionsFragment.getNewInstance(), getString(R.string.to_talk));
        viewPager.setAdapter(pagerAdapter);
        tablLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablLayout));
        tablLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
