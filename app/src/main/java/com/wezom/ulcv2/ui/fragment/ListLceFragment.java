package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.injection.component.ActivityComponent;
import com.wezom.ulcv2.injection.component.FragmentComponent;
import com.wezom.ulcv2.injection.module.FragmentModule;
import com.wezom.ulcv2.interfaces.ToolbarActions;
import com.wezom.ulcv2.mvp.view.ListLceView;
import com.wezom.ulcv2.ui.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.experimental.Accessors;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static com.wezom.ulcv2.common.Constants.CONTENT_DEV_URL;

/**
 * Created by kartavtsev.s on 08.02.2016.
 */
@Getter
@Accessors(prefix = "m")
abstract public class ListLceFragment<M extends List> extends BaseLceFragment implements ListLceView<M> {

    @Inject
    Picasso mPicasso;
    @Inject
    EventBus mBus;

    @BindView(R.id.emptyView)
    @Nullable
    TextView mEmptyView;

    private int mContentSize = 0;
    private FragmentComponent mFragmentComponent;

    abstract public int getLayoutRes();

    public void injectDependencies() {
    }

    public void injectViews(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getScreenOrientation() != SCREEN_ORIENTATION_UNSPECIFIED) {
            ((BaseActivity) getActivity()).setScreenOrientation(getScreenOrientation());
        }

        mFragmentComponent = getActivityComponent()
                .providesFragmentComponent(new FragmentModule(this));
        injectDependencies();
        FragmentArgs.inject(this);

        try {
            mBus.register(this); //TODO unwrap from try catch
        } catch (Exception e) {
        }

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getLayoutRes(), container, false);
        injectViews(view);
        return view;
    }

    @Override
    public void onDestroy() {
        if (mBus != null) {
            mBus.unregister(this);
        }
        super.onDestroy();
    }

    public int getScreenOrientation() {
        return SCREEN_ORIENTATION_UNSPECIFIED;
    }

    @Override
    public void loadData(@Constants.LoadingMode int loadMode) {

    }

    @Override
    public void loadData(boolean pullToRefresh) {
        loadData(pullToRefresh ? Constants.LOADING_MODE_REFRESH : Constants.LOADING_MODE_INITIAL);
    }

    @Override
    public void showLoading(@Constants.LoadingMode int loadMode) {
        switch (loadMode) {
            case Constants.LOADING_MODE_ENDLESS:
                break;
            case Constants.LOADING_MODE_INITIAL:
                super.showLoading(false);
                break;
            case Constants.LOADING_MODE_REFRESH:
                super.showLoading(true);
                break;
        }
    }

    @Override
    public void showContent() {
        super.showContent();
        if (mContentSize == 0) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
    }

    @Override
    public void setData(M data) {
        mContentSize = data.size();
    }

    @Override
    public void addData(M data) {
        mContentSize += data.size();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        hideEmptyView();
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        hideEmptyView();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return getContext().getString(R.string.retry);
    }

    protected ActivityComponent getActivityComponent() {
        return ((BaseActivity) getActivity()).getActivityComponent();
    }

    protected void setEmptyViewText(int textResId) {
        try {
            if (mEmptyView != null) {
                mEmptyView.setText(textResId);
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Empty view is null!");
        }
    }

    protected void setEmptyViewText(String text) {
        try {
            if (mEmptyView != null) {
                mEmptyView.setText(text);
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Empty view is null!");
        }
    }

    private void hideEmptyView() {
        try {
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Empty view is null!");
        }
    }

    private void showEmptyView() {
        try {
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Empty view is null!");
        }
    }

    public void showDrawerToggleButton() {
        ((ToolbarActions) getActivity()).showDrawerToggleButton();
    }

    public void showDrawerToggleButton(Toolbar toolbar) {
        ((ToolbarActions) getActivity()).showDrawerToggleButton(toolbar);
    }

    public void setSupportActionBar(Toolbar toolbar) {
        ((ToolbarActions) getActivity()).setSupportToolBar(toolbar);
    }

    protected void loadImage(ImageView imageView, Callback callback, String url, @DrawableRes int errorDrawableRes) {
        final String fullUrl = !TextUtils.isEmpty(url) ? CONTENT_DEV_URL + url : null;

        mPicasso
                .load(!TextUtils.isEmpty(fullUrl) ? fullUrl : "http://")
                .placeholder(errorDrawableRes)
                .error(errorDrawableRes)
                .into(imageView, callback);
    }

    protected void loadImage(ImageView imageView, String url, @DrawableRes int errorDrawableRes) {
        final String fullUrl = !TextUtils.isEmpty(url) ? CONTENT_DEV_URL + url : null;

        mPicasso
                .load(!TextUtils.isEmpty(fullUrl) ? fullUrl : "http://")
                .placeholder(errorDrawableRes)
                .error(errorDrawableRes)
                .into(imageView);
    }

    protected void loadImage(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!url.contains("http")) {
            url = CONTENT_DEV_URL + url;
        }

        mPicasso
                .load(url)
                .into(imageView);
    }
}