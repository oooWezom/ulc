package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.support.design.widget.RxTabLayout;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.mvp.presenter.SearchFragmentPresenter;
import com.wezom.ulcv2.mvp.view.SearchFragmentView;
import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.net.models.requests.SearchRequest;
import com.wezom.ulcv2.ui.adapter.SearchAdapter;
import com.wezom.ulcv2.ui.adapter.decorator.ItemListDividerDecorator;
import com.wezom.ulcv2.ui.view.RangeSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;

/**
 * Created by Zorin on 16.06.2016.
 */
public class SearchFragment extends ListLceFragment<ArrayList<Profile>>
        implements SearchFragmentView, EndlessRecyclerViewAdapter.RequestToLoadMoreListener {
    private static final String EXTRA_NAME = "fragment_search_name";
    //region injects
    @InjectPresenter
    SearchFragmentPresenter mPresenter;
    @Inject
    SearchAdapter mSearchAdapter;
    //endregion
    @BindDimen(R.dimen.fragment_search_item_decorator_padding_left)
    int mDecoratorPadding;
    @BindString(R.string.max)
    String mMaxValLabel;

    //region var
    Subscription mSubscription;
    EndlessRecyclerViewAdapter mWrappedAdapter;
    ItemListDividerDecorator mDecorator;
    int mMaxLevel;
    //endregion

    //region views
    @BindView(R.id.fragment_search_options_layout)
    LinearLayout mSearchOptionsLayout;
    @BindView(R.id.fragment_filter_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_search_toolbar_search_input)
    EditText mInputEditText;
    @BindView(R.id.fragment_search_parameters_indicator)
    ImageView mParametersIndicator;
    @BindView(R.id.fragment_search_sex_tabLayout)
    TabLayout mSexTabLayout;
    @BindView(R.id.fragment_search_region_spinner)
    Spinner mRegionSpinner;
    @BindView(R.id.fragment_search_status_spinner)
    Spinner mStatusSpinner;
    @BindView(R.id.fragment_search_level_range)
    RangeSeekBar<Integer> mLevelRangeSeekBar;
    @BindView(R.id.fragment_search_level_text)
    TextView mLevelTextView;
    @BindView(R.id.fragment_search_root)
    ViewGroup mRoot;
    @BindView(R.id.contentView)
    RecyclerView contentView;
    //endregion


    public static SearchFragment getNewInstance(String name) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    //region overrides
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_search;
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
    public void setData(ArrayList<Profile> response) {
        super.setData(response);
        mSearchAdapter.setData(response);
        checkDataRead(response);
    }

    @Override
    public void addData(ArrayList<Profile> response) {
        super.setData(response);
        mSearchAdapter.addData(response);
        checkDataRead(response);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.showKeyBoard();
    }

    @Override
    public void onStart() {
        super.onStart();
        prepareSearchObservable();
        mPresenter.getMaxLevel();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }

        mPresenter.hideKeyboard(mInputEditText);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prepareToolbar();
        prepareRecyclerView();
        mPresenter.loadLanguages();
        setEmptyViewText("");
        prepareSexTabLayout();
        mPresenter.getMaxLevel();
    }

    @Override
    public void onLoadMoreRequested() {
        loadData(Constants.LOADING_MODE_ENDLESS);
    }

    @Override
    public void setLanguages(ArrayList<String> languageNames) {
        ArrayList<String> adapterNames = new ArrayList<>();
        adapterNames.add(getString(R.string.any));
        adapterNames.addAll(languageNames);
        mRegionSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, adapterNames));
    }

    @Override
    public void prepareLevelRangeBar(int level) {
        mMaxLevel = level;
        mLevelRangeSeekBar.setRangeValues(0, level);
        mLevelRangeSeekBar.setSelectedMinValue(0);
        mLevelRangeSeekBar.setSelectedMaxValue(level);
        Observable<RangeSeekBar<Integer>.Range> var = mLevelRangeSeekBar.observableListenerWrapper();
        ConnectableObservable<RangeSeekBar<Integer>.Range> publishObservable = var.publish();

        publishObservable
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(range -> {
                    loadData(false);
                }, throwable -> {
                    Log.v("ERROR", throwable.getMessage());
                });

        publishObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(range -> {
                    mLevelTextView.setText(String.format("%s - %s", mLevelRangeSeekBar.getSelectedMinValue(), mLevelRangeSeekBar.getSelectedMaxValue() == mMaxLevel ? mMaxValLabel : mLevelRangeSeekBar.getSelectedMaxValue()));
                }, throwable -> {
                    Log.v("ERROR", throwable.getMessage());
                });

        publishObservable.connect();

        mLevelTextView.setText(String.format("%s - %s", mLevelRangeSeekBar.getSelectedMinValue(), mLevelRangeSeekBar.getSelectedMaxValue()));
    }

    @Override
    public void showKeyboard() {
        Utils.showKeyboard(mInputEditText);
    }

    //endregion

    //region clicks

    @OnClick(R.id.fragment_search_toolbar_clear)
    public void clearSearchInput() {
        mInputEditText.setText("");
    }

    @OnClick(R.id.fragment_search_parameters_show)
    public void onShowHideTextClick() {
        TransitionManager.beginDelayedTransition(mRoot);

        if (mSearchOptionsLayout.getVisibility() == View.VISIBLE) {
            mSearchOptionsLayout.setVisibility(View.GONE);
            mParametersIndicator.setImageResource(R.drawable.ic_arrow_down);

        } else {
            mSearchOptionsLayout.setVisibility(View.VISIBLE);
            mParametersIndicator.setImageResource(R.drawable.ic_arrow_up);
            mPresenter.hideKeyboard(mInputEditText);
        }
    }
    //endregion

    private void checkDataRead(ArrayList<Profile> data) {
        if (data.size() == 0) {
            mWrappedAdapter.onDataReady(false);
        } else {
            mWrappedAdapter.onDataReady(true);
        }
    }

    private void prepareToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(v -> mPresenter.onBackPressed());
    }

    private void prepareSearchObservable() {
        mSubscription = Observable.merge(
                RxTextView.textChanges(mInputEditText),
                RxAdapterView.itemSelections(mStatusSpinner),
                RxAdapterView.itemSelections(mRegionSpinner),
                RxTabLayout.selectionEvents(mSexTabLayout))
                .debounce(1, TimeUnit.SECONDS)
                .skip(1)
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(o -> {
                    loadData(false);
                });
    }

    public void loadData(@Constants.LoadingMode int loadMode) {
        SearchRequest.Request request = new SearchRequest.Request();
        if (!mInputEditText.getText().toString().isEmpty()) {
            request.setName(mInputEditText.getText().toString());
        }
        int min = mLevelRangeSeekBar.getSelectedMinValue();
        int max = mLevelRangeSeekBar.getSelectedMaxValue();
        request.setMinLevel(min);
        request.setMaxLevel(max);

        if (mStatusSpinner.getSelectedItemPosition() > 0) {
            int status = mStatusSpinner.getSelectedItemPosition();
            request.setStatus(status);
        } else {
            request.setStatus(null);
        }
        if (mRegionSpinner.getSelectedItemPosition() > 0) {
            request.setLanguages(Collections.singletonList(mRegionSpinner.getSelectedItemPosition()));
        } else {
            request.setLanguages(null);
        }

        int position = mSexTabLayout.getSelectedTabPosition();
        switch (position) {
            case Constants.GENDER_FEMALE:
                request.setSex(1);
                break;
            case Constants.GENDER_MALE:
                request.setSex(2);
                break;
        }

        mPresenter.search(request, loadMode);
    }

    private void prepareSexTabLayout() {
        mSexTabLayout.addTab(mSexTabLayout.newTab().setText(R.string.any));
        mSexTabLayout.addTab(mSexTabLayout.newTab().setText(R.string.male));
        mSexTabLayout.addTab(mSexTabLayout.newTab().setText(R.string.female));
    }

    private void prepareRecyclerView() {

        mWrappedAdapter = new EndlessRecyclerViewAdapter(getContext(), mSearchAdapter, this, R.layout.layout_loading_view, true);
        if (mDecorator == null) {
            mDecorator = new ItemListDividerDecorator(getActivity(), R.drawable.bg_line_divider_dialogs);
            mDecorator.setPaddingLeft(mDecoratorPadding);
            contentView.addItemDecoration(mDecorator);
        }
        contentView.setAdapter(mWrappedAdapter);
        contentView.setItemAnimator(new DefaultItemAnimator());
        contentView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
