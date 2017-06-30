package com.wezom.ulcv2.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.interfaces.TalkUserSearchFragmentConnector;
import com.wezom.ulcv2.mvp.presenter.TalkUserSearchFragmentPresenter;
import com.wezom.ulcv2.mvp.view.TalkUserSearchFragmentView;
import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.net.models.requests.SearchRequest;
import com.wezom.ulcv2.ui.adapter.TalkUserSearchAdapter;
import com.wezom.ulcv2.ui.adapter.decorator.ItemListDividerDecorator;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created: Zorin A.
 * Date: 19.09.2016.
 */
public class TalkUserSearchFragment extends ListLceFragment<ArrayList<Profile>>
        implements TalkUserSearchFragmentView, EndlessRecyclerViewAdapter.RequestToLoadMoreListener, TalkUserSearchFragmentConnector {

    public static final String TAG = TalkUserSearchFragment.class.getSimpleName();

    //region views
    @BindView(R.id.talk_activity_search_input)
    EditText mInputEditText;
    @BindView(R.id.talk_activity_random_user_button_label)
    TextView mRandomButtonLabel;
    @BindView(R.id.talk_activity_search_hint_layout)
    ViewGroup mHintLayout;
    @BindView(R.id.fragment_talk_user_search_root)
    ViewGroup mRoot;
    @BindView(R.id.talk_activity_random_search_progress)
    View mRandomSearchProgress;
    @BindView(R.id.talk_activity_search_big_icon)
    View mSearchBigIcon;
    @BindView(R.id.talk_activity_random_user_button)
    ViewGroup mRandomSearchButton;
    @BindView(R.id.contentView)
    RecyclerView contentView;

    //endregion

    @Inject
    TalkUserSearchAdapter mSearchAdapter;
    @InjectPresenter
    TalkUserSearchFragmentPresenter mPresenter;

    @BindDimen(R.dimen.fragment_search_item_decorator_padding_left)
    int mDecoratorPadding;

    //region var
    Subscription mSubscription;
    EndlessRecyclerViewAdapter mWrappedAdapter;
    ItemListDividerDecorator mDecorator;
    boolean mIsInSearchMode;
    //endregion

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_talk_user_search;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareRecyclerView();
        setEmptyViewText("");
    }

    private void checkDataRead(ArrayList<Profile> data) {
        if (data.size() == 0) {
            mWrappedAdapter.onDataReady(false);
            showHintLayer(true);
        } else {
            mWrappedAdapter.onDataReady(true);
            showHintLayer(false);
        }
    }

    public void showHintLayer(boolean isShow) {
        TransitionManager.beginDelayedTransition(mRoot);
        mHintLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void loadData(@Constants.LoadingMode int loadMode) {
        SearchRequest.Request request = new SearchRequest.Request();
        //request.setStatus(2, 6); //online
        request.setName(mInputEditText.getText().toString());
        mPresenter.searchUser(request, loadMode);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                mSearchBigIcon.setVisibility(View.GONE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                mSearchBigIcon.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareSearchObservable();
    }

    @Override
    public void onLoadMoreRequested() {

    }

    @Override
    public void setSearchState(boolean isInSearchMode) {
        mIsInSearchMode = isInSearchMode;
        if (mIsInSearchMode) {
            setSearchButtonText(R.string.cancel);
            showRandomSearchProgress(true);
        } else {
            setSearchButtonText(R.string.random);
            showRandomSearchProgress(false);
        }
    }

    @OnClick(R.id.talk_activity_random_user_button)
    void onSearchRandomUserClick() {
        if (!mIsInSearchMode) {
            mPresenter.searchRandomUser();
        } else {
            mPresenter.cancelUserSearch();
        }
    }

    @OnClick(R.id.talk_activity_search_close_button)
    void onClosePanelClick() {
        mPresenter.closeSearchPanel();
    }

    private void prepareSearchObservable() {
        mSubscriptions.add(
                RxTextView
                        .textChanges(mInputEditText)
                        .debounce(1, TimeUnit.SECONDS)
                        .filter(name -> !TextUtils.isEmpty(name) && name.length() >= 2)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(name -> {
                            loadData(false);
                        })
        );
    }

    private void prepareRecyclerView() {
        mWrappedAdapter = new EndlessRecyclerViewAdapter(getContext(), mSearchAdapter, this, R.layout.layout_loading_view, true);
        if (mDecorator == null) {
            mDecorator = new ItemListDividerDecorator(getActivity(), R.drawable.bg_line_divider_dialogs);
            mDecorator.setPaddingLeft(mDecoratorPadding);
            contentView.addItemDecoration(mDecorator);
        }
        contentView.setAdapter(mWrappedAdapter);
        contentView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onRandomSearchStateActive(boolean isSearching) {
        mPresenter.setSearchState(isSearching);
    }


    private void setSearchButtonText(int searchState) {
        mRandomButtonLabel.setText(searchState);
    }


    private void showRandomSearchProgress(boolean isProgress) {
        mRandomSearchProgress.setVisibility(isProgress ? View.VISIBLE : View.INVISIBLE);
//        mRandomSearchButton.setVisibility(isProgress ? View.INVISIBLE : View.VISIBLE);
    }
}
