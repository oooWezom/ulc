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

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.injection.component.ActivityComponent;
import com.wezom.ulcv2.injection.component.FragmentComponent;
import com.wezom.ulcv2.injection.module.FragmentModule;
import com.wezom.ulcv2.interfaces.ToolbarActions;
import com.wezom.ulcv2.mvp.view.BaseFragmentView;
import com.wezom.ulcv2.ui.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import lombok.Getter;
import lombok.experimental.Accessors;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static com.wezom.ulcv2.common.Constants.CONTENT_DEV_URL;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */
@Getter
@Accessors(prefix = "m")
public abstract class BaseFragment extends MvpAppCompatFragment
        implements BaseFragmentView {

    @Inject
    Picasso mPicasso;
    @Inject
    EventBus mBus;

    private FragmentComponent mFragmentComponent;

    public abstract int getLayoutRes();

    public abstract void injectDependencies();

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

    public void setScreenOrientation(int screenOrientation) {
        ((BaseActivity) getActivity()).setScreenOrientation(screenOrientation);
    }

    @Override
    public void showMessageDialog(int title, int message) {
        ((BaseActivity) getActivity()).showMessageDialog(title, message);
    }

    @Override
    public void showMessageDialog(int title, String message) {
        ((BaseActivity) getActivity()).showMessageDialog(title, message);
    }

    @Override
    public void showMessageDialog(int message) {
        ((BaseActivity) getActivity()).showMessageDialog(message);
    }

    @Override
    public void showMessageDialog(String message) {
        ((BaseActivity) getActivity()).showMessageDialog(message);
    }

    @Override
    public void showUpdateDialog() {
        ((BaseActivity) getActivity()).showUpdateDialog();
    }


    @Override
    public void showMessageToast(int message) {
        ((BaseActivity) getActivity()).showToast(message);
    }

    @Override
    public void showMessageToast(String message) {
        ((BaseActivity) getActivity()).showToast(message);
    }

    @Override
    public void showGlobalLoading(boolean show) {
        ((BaseActivity) getActivity()).showLoading(show);
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {

    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    protected ActivityComponent getActivityComponent() {
        return ((BaseActivity) getActivity()).getActivityComponent();
    }

    public Toolbar getToolBar() {
        return ((ToolbarActions) getActivity()).getToolBar();
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
