package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.wezom.ulcv2.interfaces.OnItemSelectListener;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.presenter.ToTalkPresenter;
import com.wezom.ulcv2.mvp.view.ToTalkView;
import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.ui.adapter.ActiveTalksAdapter;
import com.wezom.ulcv2.ui.adapter.CategoriesAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created: Zorin A.
 * Date: 27.06.2016.
 */
public class ToTalkFragment extends ListLceFragment<ArrayList<Talk>>
        implements ToTalkView, SwipeRefreshLayout.OnRefreshListener, EndlessRecyclerViewAdapter.RequestToLoadMoreListener,
        OnItemSelectListener, RecyclerItemClickListener.OnItemClickListener {

    private static final String EXTRA_NAME = "fragment_to_talk";
    //region var
    @InjectPresenter
    ToTalkPresenter mPresenter;
    @Inject
    CategoriesAdapter mCategoriesAdapter;

    ActiveTalksAdapter mActiveTalksAdapter;
    EndlessRecyclerViewAdapter mWrappedAdapter;
    List<Category> mCategoriesList;
    int mSelectedPosition;
    //endregion

    //region views
    @BindView(R.id.fragment_2talk_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_2talk_active_sessions_recycler)
    RecyclerView mActiveTalksRecyclerView;
    @BindView(R.id.fragment_2Talk_categories)
    RecyclerView mCategoriesRecyclerView;
    @BindView(R.id.fragment_2talk_sessions_count_label)
    TextView mSessionsCountTextView;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;
    //endregion

    //region overrides
    public static ToTalkFragment getNewInstance(String name) {
        ToTalkFragment fragment = new ToTalkFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_2talk;
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
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }

    @Override
    public void loadData(@Constants.LoadingMode int loadMode) {
        mPresenter.getActiveTalks(loadMode);
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override
    public void setData(ArrayList<Talk> data) {
        super.setData(data);
        mActiveTalksAdapter.setData(data);
        checkDataRead(data);
        mSessionsCountTextView.setText(String.valueOf(mActiveTalksAdapter.getItemCount()));
    }

    @Override
    public void addData(ArrayList<Talk> data) {
        super.addData(data);
        mActiveTalksAdapter.addData(data);
        checkDataRead(data);
        mSessionsCountTextView.setText(String.valueOf(mActiveTalksAdapter.getItemCount()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        mPresenter.getCategories();
        loadData(false);
        initRecycler();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(false);
    }

    @Override
    public void onStop() {
        if (mSelectedPosition > 0)
            mCategoriesList.get(mSelectedPosition).setChecked(false);
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_watch, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // in future this thing will switch between all and following sessions
//        mPresenter.searchRandomGame();
        return true;
    }

    private void setCategoryErrorText(int textRes) {
//        mCategoryErrorView.setText(textRes);
    }

    @Override
    public void onRefresh() {
        mWrappedAdapter.restartAppending();
        loadData(true);
    }

    @Override
    public void onLoadMoreRequested() {
        loadData(Constants.LOADING_MODE_ENDLESS);
    }

    @Override
    public void setCategories(List<Category> categories) {
        mCategoriesList = categories;
        mCategoriesAdapter.setData(mCategoriesList);
        mCategoriesAdapter.setListener(this);
    }

    @Override
    public void showCategoryProgress(boolean isShow) {
//        mCategoryLoadingView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showCategoryError(boolean isShow) {
//        mCategoryErrorView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onSelected(int position) {
        if (mSelectedPosition != position) { //select item
            if (mSelectedPosition != -1) {
                mCategoriesList.get(mSelectedPosition).setChecked(false); //old uncheck
            }
            mSelectedPosition = position;
            mCategoriesList.get(mSelectedPosition).setChecked(true); //new check
        } else { //unselect item
            mCategoriesList.get(mSelectedPosition).setChecked(false);//old uncheck
            mSelectedPosition = -1; //uncheck at all
        }
        mCategoriesAdapter.notifyDataSetChanged();

        int categoryId;
        if (mSelectedPosition == -1) {
            categoryId = 0;
        } else {
            categoryId = mCategoriesList.get(mSelectedPosition).getCategoryId();
            Log.v("SEL", "category name = " + categoryId);
        }
        mPresenter.setCategoryId(categoryId);
        loadData(false);

        Log.v("SEL", "category id = " + categoryId);
    }

    @Override
    public void onItemClick(View view, int position) {
        Talk talk = mActiveTalksAdapter.getModel(position);
        mPresenter.showTalk(talk, position);
    }

    //endregion

    @OnClick(R.id.fragment_2talk_ripple)
    void on2TalkClick(View view) {
        mPresenter.onNewSessionClick();
    }

    private void initRecycler() {
        mActiveTalksAdapter = new ActiveTalksAdapter();
        mWrappedAdapter = new EndlessRecyclerViewAdapter(getContext(), mActiveTalksAdapter, this, R.layout.layout_loading_view, false);

        mActiveTalksRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        mActiveTalksRecyclerView.setAdapter(mWrappedAdapter);
        mActiveTalksRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));

        mCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mCategoriesRecyclerView.setHasFixedSize(true);
        mCategoriesRecyclerView.setAdapter(mCategoriesAdapter);
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        showDrawerToggleButton(mToolbar);
        mToolbar.setTitle(R.string.to_talk);
        mToolbar.setSubtitle(null);
//        setCategoryErrorText(R.string.category_error);
        contentView.setOnRefreshListener(this);
        setEmptyViewText(R.string.no_active_talks);
    }

    private void checkDataRead(ArrayList<Talk> data) {
        if (data.size() == 0) {
            mWrappedAdapter.onDataReady(false);
        } else {
            mWrappedAdapter.onDataReady(true);
        }
    }
}
