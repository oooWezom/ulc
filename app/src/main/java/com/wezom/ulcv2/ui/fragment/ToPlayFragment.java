package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.RecyclerItemClickListener;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.mvp.presenter.ToPlayPresenter;
import com.wezom.ulcv2.mvp.view.ToPlayView;
import com.wezom.ulcv2.net.models.Session;
import com.wezom.ulcv2.ui.adapter.ActiveGamesAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created: Zorin A.
 * Date: 27.06.2016.
 */
public class ToPlayFragment extends ListLceFragment<ArrayList<Session>>
        implements ToPlayView, SwipeRefreshLayout.OnRefreshListener, EndlessRecyclerViewAdapter.RequestToLoadMoreListener,
        RecyclerItemClickListener.OnItemClickListener {

    private static final String EXTRA_NAME = "fragment_to_play";
    //region var
    @InjectPresenter
    ToPlayPresenter mPresenter;
    @Inject
    ActiveGamesAdapter mActiveGamesAdapter;
    //endregion

    //region views
    @BindView(R.id.fragment_2play_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_2play_watch_other_quanti_label)
    TextView mSessionsQuantityTextView;
    @BindView(R.id.fragment_2play_feed_recycler)
    RecyclerView mSessionsRecyclerView;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;
    //endregion

    //region overrides

    public static ToPlayFragment getNewInstance(String name) {
        ToPlayFragment fragment = new ToPlayFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_2play;
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
        setHasOptionsMenu(true);
    }

    @Override
    public void setData(ArrayList<Session> data) {
        super.setData(data);
        mActiveGamesAdapter.setData(data);
        mSessionsQuantityTextView.setText(String.valueOf(mActiveGamesAdapter.getItemCount()));
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_watch, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // in future this thing will switch between all and following sessions
        mPresenter.searchRandomGame();
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initRecycler();
    }

    @Override
    public void loadData(@Constants.LoadingMode int loadMode) {
        mPresenter.getActiveGames(loadMode);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(Constants.LOADING_MODE_INITIAL);
    }

    @Override
    public void onRefresh() {
        loadData(Constants.LOADING_MODE_REFRESH);
    }

    @Override
    public void onLoadMoreRequested() {

    }
    //endregion

    @OnClick(R.id.fragment_2play_ripple)
    void on2PlayClick(View view) {
        mPresenter.playSession();
    }

    private void initRecycler() {
        mSessionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSessionsRecyclerView.setAdapter(mActiveGamesAdapter);
        mSessionsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
    }

    private void initViews() {
        contentView.setOnRefreshListener(this);
        setEmptyViewText(R.string.no_active_games);
        setSupportActionBar(mToolbar);
        showDrawerToggleButton(mToolbar);
        mToolbar.setTitle(R.string.to_play);
    }

    @Override
    public void onItemClick(View view, int position) {
        Session session = mActiveGamesAdapter.getData(position);
        mPresenter.watchGame(session, position);
    }
}
