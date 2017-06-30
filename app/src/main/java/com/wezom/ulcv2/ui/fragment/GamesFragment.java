package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.interfaces.OnCheckListener;
import com.wezom.ulcv2.mvp.model.Game;
import com.wezom.ulcv2.mvp.presenter.GamesFragmentPresenter;
import com.wezom.ulcv2.mvp.view.GamesFragmentView;
import com.wezom.ulcv2.ui.adapter.GamesAdapter;
import com.wezom.ulcv2.ui.adapter.decorator.ItemListDividerDecorator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created: Zorin A.
 * Date: 27.06.2016.
 */
public class GamesFragment extends BaseFragment implements GamesFragmentView, OnCheckListener {

    private static final String EXTRA_NAME = "fragment_games_screen";
    @InjectPresenter
    GamesFragmentPresenter mPresenter;
    @Inject
    GamesAdapter mGamesAdapter;

    @BindView(R.id.fragment_games_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_games_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_games_empty)
    TextView mEmptyView;

    private List<Game> mAllGameList = new ArrayList<>();
    private SearchView mSearchView;
    private Subscription mSubscription;

    public static GamesFragment getNewInstance(String name) {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_games;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }

    private void setupRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new ItemListDividerDecorator(getActivity(), R.drawable.bg_line_divider_dialogs));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mGamesAdapter);
        mGamesAdapter.setOnCheckChangeListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyViewText(R.string.empty_games);
        setupRecyclerView();
        initToolbar();
        mPresenter.getNewGamesList();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_games, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSubscription = RxSearchView.queryTextChanges(mSearchView)
                .observeOn(AndroidSchedulers.mainThread())
                .skip(1)
                .subscribe(charSequence -> {
                    mPresenter.filterGames(charSequence, mAllGameList);
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_confirm:
                onGamesSelected();
                break;
        }
        return true;
    }

    private void setEmptyViewText(int res) {
        mEmptyView.setText(res);
    }

    private void onGamesSelected() {
        List<Integer> gamesIds = Observable.from(mAllGameList)
                .filter(Game::isChecked)
                .map(Game::getGameId)
                .toList().toBlocking().first();
        mPresenter.searchGame(gamesIds);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.choose_game);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(v -> mPresenter.onBackPressed());
    }

    @Override
    public void setFilteredGames(List<Game> games) {
        mGamesAdapter.setData(games);
    }

    @Override
    public void setAllGames(List<Game> games) {
        mAllGameList = games;
        mGamesAdapter.setData(mAllGameList);
    }

    @Override
    public void showEmptyView(boolean isShow) {
        mEmptyView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemChecked(Game game) {
        int index = mAllGameList.indexOf(game);
        mAllGameList.get(index).setChecked(game.isChecked());

    }
}
