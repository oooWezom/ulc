package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.mvp.presenter.FollowsListPresenter;
import com.wezom.ulcv2.mvp.view.FollowsListView;
import com.wezom.ulcv2.net.models.Follower;
import com.wezom.ulcv2.ui.adapter.FollowsAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Sivolotskiy.v on 31.05.2016.
 */
public class FollowsListFragment extends ListLceFragment<ArrayList<Follower>>
        implements FollowsListView, SwipeRefreshLayout.OnRefreshListener, EndlessRecyclerViewAdapter.RequestToLoadMoreListener {

    private final static String KEY_MODE = "key_mode";
    private final static String KEY_ID = "key_id";

    @InjectPresenter
    FollowsListPresenter mFollowingPresenter;
    @BindView(R.id.fragment_following_list_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_follows_list_search_text_view)
    EditText mSearchEditText;
    @BindView(R.id.fragment_follows_list_count_text_view)
    TextView mCountTextView;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;
    FollowsAdapter mFollowsAdapter;
    private int mMode;
    private int mId;
    private String mSearchQuery = "";
    private EndlessRecyclerViewAdapter mWrappedAdapter;

    public static FollowsListFragment getInstance(int mode, int id) {
        FollowsListFragment fragment = new FollowsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MODE, mode);
        bundle.putInt(KEY_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_follows_list;
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }

    @Override
    public void loadData(@Constants.LoadingMode int loadMode) {
        switch (mMode) {
            case 0:
                mFollowingPresenter.loadFollowers(loadMode, mSearchQuery, mId);
                break;
            case 1:
                mFollowingPresenter.loadFollowing(loadMode, mSearchQuery, mId);
                break;
        }
    }

    @Override
    public void injectDependencies() {
        mMode = getArguments().getInt(KEY_MODE);
        mId = getArguments().getInt(KEY_ID);
        getFragmentComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFollowsAdapter = new FollowsAdapter(getContext(), mFollowingPresenter.getMBus());
        mWrappedAdapter = new EndlessRecyclerViewAdapter(getContext(), mFollowsAdapter, this, R.layout.layout_loadingview_recycler, true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mWrappedAdapter);
        contentView.setOnRefreshListener(this);
        loadData(false);
    }

    @Override
    public void setData(ArrayList<Follower> response) {
        super.setData(response);
        mFollowsAdapter.setData(response);
        checkDataReady(response);
        setTextCount(response.size());
    }

    @Override
    public void addData(ArrayList<Follower> data) {
        super.addData(data);
        mFollowsAdapter.addData(data);
        checkDataReady(data);
    }

    @Override
    public void onRefresh() {
        loadData(true);
        mWrappedAdapter.restartAppending();
    }

    @Override
    public void onLoadMoreRequested() {
        loadData(Constants.LOADING_MODE_ENDLESS);
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
        if (mSearchQuery.isEmpty()) {
            switch (mMode) {
                case 0:
                    setEmptyViewText(R.string.you_dont_have_followers);
                    break;
                case 1:
                    setEmptyViewText(R.string.not_following);
                    break;
            }
        } else {
            setEmptyViewText(R.string.search_no_results);
        }
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        contentView.setRefreshing(false);
    }

    @OnTextChanged(R.id.fragment_follows_list_search_text_view)
    void onTextChanged(CharSequence s) {
        mSearchQuery = s.toString();
        mFollowingPresenter.setOffset(0);
        loadData(false);
    }

    @OnClick(R.id.fragment_follows_list_clear_search_image_view)
    void onClickClearSearch() {
        mSearchEditText.setText("");
    }

    private void setTextCount(int count) {
        switch (mMode) {
            case 0:
                mCountTextView.setText(getString(R.string.you_have_followers, count));
                break;
            case 1:
                mCountTextView.setText(getString(R.string.your_following, count));
                break;
        }
    }

    private void checkDataReady(ArrayList<Follower> data) {
        if (data.size() == 0) {
            mWrappedAdapter.onDataReady(false);
        } else {
            mWrappedAdapter.onDataReady(true);
        }
    }
}
