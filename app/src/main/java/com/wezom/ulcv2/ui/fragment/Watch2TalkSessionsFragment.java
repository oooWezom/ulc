package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.RecyclerItemClickListener;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.interfaces.OnItemSelectListener;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.presenter.Watch2TalkSessionsPresenter;
import com.wezom.ulcv2.mvp.view.Watch2TalkFragmentView;
import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.ui.adapter.ActiveTalksAdapter;
import com.wezom.ulcv2.ui.adapter.CategoriesAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created: Zorin A.
 * Date: 23.02.2017.
 */

public class Watch2TalkSessionsFragment extends ListLceFragment<ArrayList<Talk>>
        implements Watch2TalkFragmentView, SwipeRefreshLayout.OnRefreshListener, EndlessRecyclerViewAdapter.RequestToLoadMoreListener,
        OnItemSelectListener, RecyclerItemClickListener.OnItemClickListener {

    //region var
    @InjectPresenter
    Watch2TalkSessionsPresenter mPresenter;
    @Inject
    CategoriesAdapter mCategoriesAdapter;

    ActiveTalksAdapter mActiveTalksAdapter;
    EndlessRecyclerViewAdapter mWrappedAdapter;
    List<Category> mCategoriesList;
    int mSelectedPosition = -1;
    //endregion

    //region views
    @BindView(R.id.fragment_watch_2talk_active_sessions_recycler)
    RecyclerView mActiveTalksRecyclerView;
    @BindView(R.id.fragment_watch_2talk_categories)
    RecyclerView mCategoriesRecyclerView;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;
    //endregion

    public static Watch2TalkSessionsFragment getNewInstance() {
        Watch2TalkSessionsFragment fragment = new Watch2TalkSessionsFragment();
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_watch_2talk;
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
    }

    @Override
    public void addData(ArrayList<Talk> data) {
        super.addData(data);
        mActiveTalksAdapter.addData(data);
        checkDataRead(data);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initRecycler();
        mPresenter.getCategories();
        loadData(false);
    }

    @Override
    public void onStop() {
        if (mSelectedPosition > -1)
            mCategoriesList.get(mSelectedPosition).setChecked(false);
        super.onStop();
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
    public void onItemClick(View view, int position) {
        Talk talk = mActiveTalksAdapter.getModel(position);
        mPresenter.showTalk(talk, position);
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

    private void checkDataRead(ArrayList<Talk> data) {
        if (data.size() == 0) {
            mWrappedAdapter.onDataReady(false);
        } else {
            mWrappedAdapter.onDataReady(true);
        }
    }

    private void initViews() {
        contentView.setOnRefreshListener(this);
        setEmptyViewText(R.string.no_active_talks);
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

    @Override
    public void setCategories(List<Category> categories) {
        mCategoriesList = categories;
        mCategoriesAdapter.setData(categories);
        mCategoriesAdapter.setListener(this);

        for (int i = 0; i < mCategoriesList.size(); i++) {
            mCategoriesList.get(i).setChecked(false); //uncheck
        }
    }

    @Override
    public void showCategoryProgress(boolean isShow) {
//        mCategoryLoadingView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showCategoryError(boolean isShow) {
//        mCategoryErrorView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }
}
