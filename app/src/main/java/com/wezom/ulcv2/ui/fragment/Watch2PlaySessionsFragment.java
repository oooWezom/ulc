package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.RecyclerItemClickListener;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.mvp.presenter.Watch2PlaySessionsPresenter;
import com.wezom.ulcv2.mvp.view.Watch2PlayFragmentView;
import com.wezom.ulcv2.net.models.Session;
import com.wezom.ulcv2.ui.adapter.ActiveGamesAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created: Zorin A.
 * Date: 23.02.2017.
 */

public class Watch2PlaySessionsFragment extends ListLceFragment<ArrayList<Session>>
        implements Watch2PlayFragmentView,
        SwipeRefreshLayout.OnRefreshListener,
        EndlessRecyclerViewAdapter.RequestToLoadMoreListener,
        RecyclerItemClickListener.OnItemClickListener {

    @InjectPresenter
    Watch2PlaySessionsPresenter presenter;
    @Inject
    ActiveGamesAdapter mActiveGamesAdapter;

    //region views
    @BindView(R.id.fragment_watch_2play_feed_recycler)
    RecyclerView mSessionsRecyclerView;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;
    //endregion

    public static Watch2PlaySessionsFragment getNewInstance() {
        Watch2PlaySessionsFragment fragment = new Watch2PlaySessionsFragment();
        return fragment;
    }

    //region overrides
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_watch_2play;
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void setData(ArrayList<Session> data) {
        super.setData(data);
        mActiveGamesAdapter.setData(data);
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initRecycler();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(Constants.LOADING_MODE_INITIAL);
    }

    @Override
    public void loadData(@Constants.LoadingMode int loadMode) {
        presenter.getActiveGames(loadMode);
    }

    @Override
    public void onRefresh() {
        loadData(Constants.LOADING_MODE_REFRESH);
    }

    @Override
    public void onLoadMoreRequested() {

    }

    @Override
    public void onItemClick(View view, int position) {
        Session session = mActiveGamesAdapter.getData(position);
        presenter.watchGame(session, position);
    }
    //endregion

    private void initRecycler() {
        mSessionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSessionsRecyclerView.setAdapter(mActiveGamesAdapter);
        mSessionsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
    }

    private void initViews() {
        contentView.setOnRefreshListener(this);
        setEmptyViewText(R.string.no_active_games);
    }
}
