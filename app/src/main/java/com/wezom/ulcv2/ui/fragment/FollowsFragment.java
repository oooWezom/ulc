package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.mvp.model.Follows;
import com.wezom.ulcv2.mvp.presenter.FollowsPresenter;
import com.wezom.ulcv2.mvp.view.FollowsView;
import com.wezom.ulcv2.ui.listeners.UlcActionButtonClickListener;
import com.wezom.ulcv2.ui.view.UlcActionButtonView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sivolotskiy.v on 30.05.2016.
 */
public class FollowsFragment extends BaseFragment
        implements FollowsView, UlcActionButtonClickListener {

    public static final int FOLLOWERS_TAB_MODE = 0;
    public static final int FOLLOWING_TAB_MODE = 1;

    private static final String KEY_ID = "key_id";
    private static final String KEY_MODE = "key_mode";
    private static final String EXTRA_NAME = "fragment_follows";

    @InjectPresenter
    FollowsPresenter mFollowsPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.fragment_follows_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.fragment_follows_action_button)
    UlcActionButtonView mActionButtonView;

    private int mId;
    private int mMode;

    public static FollowsFragment getNewInstance(String name, Follows follows) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, name);
        bundle.putInt(KEY_ID, follows.getId());
        bundle.putInt(KEY_MODE, follows.getTabMode());
        FollowsFragment followsFragment = new FollowsFragment();
        followsFragment.setArguments(bundle);
        return followsFragment;
    }

    /*  public void setInitData(int mode, int id) {
          mId = id;
          mMode = mode;
      }
  */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.settings_item).setVisible(false);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_follows;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.UNSPECIFIED;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getArguments().getInt(KEY_ID);
        mMode = getArguments().getInt(KEY_MODE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSupportActionBar(mToolbar);
        showDrawerToggleButton(mToolbar);
        mToolbar.setTitle("");
        setupViewPager();
        setHasOptionsMenu(true);
        mTabLayout.setupWithViewPager(mViewPager);
        mActionButtonView.setActionListener(this);
        mToolbar.setSubtitle(null);
    }

    /*@Override
    public List<Feature> requestFeature() {
        ArrayList<Feature> features = new ArrayList<>();
        Feature feature = ToolbarFeature.newBuilder()
                .setLayoutMode(Constants.LayoutMode.NOT_LOGGED_IN)
                .build();
        features.add(feature);
        return features;
    }*/

    @Override
    public void on2PlayClick(View view) {
        mFollowsPresenter.onClick2Play();
    }

    @Override
    public void on2TalkClick(View view) {
        mFollowsPresenter.onClick2Talk();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(FollowsListFragment.getInstance(FOLLOWERS_TAB_MODE, mId), getString(R.string.followers));
        adapter.addFragment(FollowsListFragment.getInstance(FOLLOWING_TAB_MODE, mId), getString(R.string.following));
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mMode);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}

