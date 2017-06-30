package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.mvp.model.NewsFeed;
import com.wezom.ulcv2.mvp.presenter.NewsfeedPresenter;
import com.wezom.ulcv2.mvp.view.NewsfeedView;
import com.wezom.ulcv2.net.models.Event;
import com.wezom.ulcv2.ui.adapter.EventsAdapter;
import com.wezom.ulcv2.ui.adapter.decorator.ItemListDividerDecorator;
import com.wezom.ulcv2.ui.listeners.UlcActionButtonClickListener;
import com.wezom.ulcv2.ui.view.UlcActionButtonView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Sivolotskiy.v on 03.06.2016.
 */
public class NewsfeedFragment extends ListLceFragment<ArrayList<Event>> implements
        NewsfeedView,
        SwipeRefreshLayout.OnRefreshListener,
        EndlessRecyclerViewAdapter.RequestToLoadMoreListener,
        UlcActionButtonClickListener {

    public final static int TYPE_FOLLOWING = 0;
    public final static int TYPE_SELF = 1;
    public final static int TYPE_GAMES = 2;
    public final static int TYPE_TALKS = 4;
    private final static String KEY_PROFILE_ID = "key_profile";
    private final static String KEY_FEED_TYPE = "key_feed_type";
    private final static String KEY_TOOLBAR_TYPE = "key_toolbar_type";

    private static final String EXTRA_NAME = "fragment_news_feed_extra_name";

    @InjectPresenter
    NewsfeedPresenter mNewsFeedFragmentPresenter;
    @BindView(R.id.fragment_news_feed_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_news_action_button)
    UlcActionButtonView mActionButtonView;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;

    private EventsAdapter mEventsAdapter;
    private int mProfileId;
    private int mFeedType;
    private boolean isShowToolbar;
    private EndlessRecyclerViewAdapter mWrappedAdapter;

    public static NewsfeedFragment getNewInstance(String name, NewsFeed newsFeed) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, name);
        bundle.putInt(KEY_FEED_TYPE, newsFeed.getFeedType());
        bundle.putInt(KEY_PROFILE_ID, newsFeed.getProfileId());
        bundle.putBoolean(KEY_TOOLBAR_TYPE, newsFeed.isShowTolbar());
        NewsfeedFragment newsFeedFragment = new NewsfeedFragment();
        newsFeedFragment.setArguments(bundle);
        return newsFeedFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_news_feed;
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }

    @Override
    public void injectDependencies() {
        super.injectDependencies();
        getFragmentComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProfileId = getArguments().getInt(KEY_PROFILE_ID, 0);
        mFeedType = getArguments().getInt(KEY_FEED_TYPE, 0);
        isShowToolbar = getArguments().getBoolean(KEY_TOOLBAR_TYPE, true);

        setHasOptionsMenu(true);
    }

    @Override
    public void setData(ArrayList<Event> data) {
        super.setData(data);
        mEventsAdapter.setData(data);
        checkDataRead(data);
    }

    @Override
    public void addData(ArrayList<Event> data) {
        super.addData(data);
        mEventsAdapter.addData(data);
        checkDataRead(data);
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        contentView.setRefreshing(false);
        mWrappedAdapter.onDataReady(false);
    }

    public void hideToolbar() {
        mToolbar.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSupportActionBar(mToolbar);

        if (!isShowToolbar) {
            hideToolbar();
            hideActionButton();
        } else {
            mToolbar.setTitle(getString(R.string.newsfeed));
            showDrawerToggleButton(mToolbar);
            mActionButtonView.setTintEnabled(true);//enable tint in action button if its fragment has toolbar (on full screen)
            mActionButtonView.setActionListener(this);
        }

        mEventsAdapter = new EventsAdapter(getContext(), mNewsFeedFragmentPresenter.getBus(), mNewsFeedFragmentPresenter.getCategories());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new ItemListDividerDecorator(getContext(), R.drawable.bg_line_divider));
        mWrappedAdapter = new EndlessRecyclerViewAdapter(getContext(), mEventsAdapter, this, R.layout.layout_loadingview_recycler, true);
        mRecyclerView.setAdapter(mWrappedAdapter);
        setEmptyViewText(R.string.empty_news_feed_text);
        contentView.setOnRefreshListener(this);
        mNewsFeedFragmentPresenter.setOffset(0);//TODO REWRITE!!!
        loadData(false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.settings_item).setVisible(true)
                .setOnMenuItemClickListener(item -> {
                    mNewsFeedFragmentPresenter.filterSettingsClick();
                    return true;
                });
    }

    @Override
    public void onRefresh() {
        mWrappedAdapter.restartAppending();
        loadData(true);
    }

    @Override
    public void loadData(@Constants.LoadingMode int loadMode) {
        mNewsFeedFragmentPresenter.loadNewsFeed(loadMode, mFeedType, mProfileId);
    }

    @Override
    public void onLoadMoreRequested() {
        loadData(Constants.LOADING_MODE_ENDLESS);
    }

 /*   @Override
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
        mNewsFeedFragmentPresenter.onClick2Play();
    }

    @Override
    public void on2TalkClick(View view) {
        mNewsFeedFragmentPresenter.onClick2Talk();
    }

    private void hideActionButton() {
        mActionButtonView.setVisibility(View.GONE);
    }

    private void checkDataRead(ArrayList<Event> data) {
        if (data.size() == 0) {
            mWrappedAdapter.onDataReady(false);
        } else {
            mWrappedAdapter.onDataReady(true);
        }
    }

    @IntDef({TYPE_FOLLOWING, TYPE_SELF, TYPE_GAMES, TYPE_TALKS})
    public @interface FeedType {
    }
}