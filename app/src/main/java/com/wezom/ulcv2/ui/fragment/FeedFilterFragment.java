package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.presenter.FeedFilterPresenter;
import com.wezom.ulcv2.mvp.view.FeedFilterView;
import com.wezom.ulcv2.ui.activity.HomeActivity;

import butterknife.BindView;

/**
 * Created by Sivolotskiy.v on 07.06.2016.
 */
public class FeedFilterFragment extends BaseFragment
        implements FeedFilterView {

    private static final String EXTRA_NAME = "fragment_feed_filter_name";

    @InjectPresenter
    FeedFilterPresenter mPresenter;

    @BindView(R.id.fragment_news_feed_2play_switch)
    SwitchCompat m2PlaySwitch;
    @BindView(R.id.fragment_news_feed_filter_follows_switch)
    SwitchCompat mFollowsSwitch;
    @BindView(R.id.fragment_news_feed_2talk_switch)
    SwitchCompat m2TalkSwitch;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public static FeedFilterFragment getNewInstance(String name) {
        FeedFilterFragment fragment = new FeedFilterFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_feed_filter;
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.accept_item).setVisible(true)
                .setOnMenuItemClickListener(item -> {
                   mPresenter.switchPrefs(m2PlaySwitch.isChecked(), m2TalkSwitch.isChecked(), mFollowsSwitch.isChecked());
                   mPresenter.onBack();
                    return true;
                });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle(getString(R.string.title_filter));mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(v ->mPresenter.onBack());
       mPresenter.loadFilterSettings();
    }

    @Override
    public void setData(PreferenceManager.FilterPrefs prefs) {
        m2PlaySwitch.setChecked(prefs.isGames());
        mFollowsSwitch.setChecked(prefs.isFollows());
        m2TalkSwitch.setChecked(prefs.isTalkss());
    }
/*
    @Override
    public List<Feature> requestFeature() {
        ArrayList<Feature> features = new ArrayList<>();
        Feature feature = ToolbarFeature.newBuilder()
                .setLayoutMode(Constants.LayoutMode.NOT_LOGGED_IN)
                .build();
        features.add(feature);
        return features;
    }*/
}
