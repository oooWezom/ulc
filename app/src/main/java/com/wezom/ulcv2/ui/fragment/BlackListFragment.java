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
import com.wezom.ulcv2.events.BlacklistRemoveRequestEvent;
import com.wezom.ulcv2.mvp.presenter.BlackListPresenter;
import com.wezom.ulcv2.mvp.view.BlackListView;
import com.wezom.ulcv2.net.models.BlacklistRecord;
import com.wezom.ulcv2.ui.adapter.BlacklistAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Sivolotskiy.v on 13.06.2016.
 */
public class BlackListFragment extends ListLceFragment<ArrayList<BlacklistRecord>> implements BlackListView,
        EndlessRecyclerViewAdapter.RequestToLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String EXTRA_NAME = "fragment_black_list_name";

    @InjectPresenter
    BlackListPresenter mPresenter;
    @Inject
    BlacklistAdapter mBlacklistAdapter;
    @Inject
    EventBus mBus;

    @BindView(R.id.fragment_blacklist_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_blacklist_toolbar)
    android.support.v7.widget.Toolbar mToolbar;
    @BindView(R.id.fragment_blacklist_users_text_view)
    TextView mUsersTextView;
    @BindView(R.id.fragment_blacklist_search_text_view)
    EditText mSearchEditText;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;

    private String mSearchQuery = "";
    private EndlessRecyclerViewAdapter mWrappedAdapter;


    public static BlackListFragment getNewInstance(String name) {
        BlackListFragment fragment = new BlackListFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getString(R.string.black_list));
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(v ->mPresenter.onBackPressed());

        setEmptyViewText(R.string.blacklist_empty);
        mWrappedAdapter = new EndlessRecyclerViewAdapter(getContext(), mBlacklistAdapter, this,
                R.layout.layout_loadingview_recycler, true);
        mRecyclerView.setAdapter(mWrappedAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentView.setOnRefreshListener(this);
        loadData(Constants.LOADING_MODE_INITIAL);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_blacklist;
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }

    @Override
    public void loadData(@Constants.LoadingMode int loadMode) {
        mPresenter.loadBlacklist(loadMode,mSearchQuery);
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onLoadMoreRequested() {
        loadData(Constants.LOADING_MODE_ENDLESS);
    }

    @Override
    public void addData(ArrayList<BlacklistRecord> data) {
        super.addData(data);
        mBlacklistAdapter.addData(data);
        checkDataRead(data);
    }

    @Override
    public void setData(ArrayList<BlacklistRecord> data) {
        super.setData(data);
        mUsersTextView.setText(getString(R.string.count_users, String.valueOf(data.size())));
        mBlacklistAdapter.setData(data);
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

    @Override
    public void onRefresh() {
        mWrappedAdapter.restartAppending();
        loadData(Constants.LOADING_MODE_REFRESH);
    }

    @Subscribe
    public void onRemove(BlacklistRemoveRequestEvent event) {
       mPresenter.onRemoveClick(event.getId(), mSearchQuery);
    }

    @OnTextChanged(R.id.fragment_blacklist_search_text_view)
    void onTextChanged(CharSequence s) {
        mSearchQuery = s.toString();
       mPresenter.setOffset(0);
        loadData(false);
    }
/*

    @Override
    public List<Feature> requestFeature() {
        ArrayList<Feature> features = new ArrayList<>();
        Feature feature = ToolbarFeature.newBuilder()
                .setLayoutMode(Constants.LayoutMode.NOT_LOGGED_IN)
//                .setTitleRes(R.string.title_fragment_blacklist)
                .build();
        features.add(feature);
        return features;
    }
*/

    private void checkDataRead(ArrayList<BlacklistRecord> data) {
        if (data.size() == 0) {
            mWrappedAdapter.onDataReady(false);
        } else {
            mWrappedAdapter.onDataReady(true);
        }
    }

    @OnClick(R.id.fragment_blacklist_clear_search_edit_view)
    void onClickClearSearch(){
        mSearchEditText.setText("");
    }
}
