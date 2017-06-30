package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.injection.component.ActivityComponent;
import com.wezom.ulcv2.injection.component.FragmentComponent;
import com.wezom.ulcv2.injection.module.FragmentModule;
import com.wezom.ulcv2.interfaces.ToolbarActions;
import com.wezom.ulcv2.mvp.view.BaseFragmentView;
import com.wezom.ulcv2.ui.activity.BaseActivity;

import butterknife.ButterKnife;
import lombok.Getter;
import lombok.experimental.Accessors;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
@Getter
@Accessors(prefix = "m")
abstract public class BaseLceFragment extends MvpAppCompatFragment implements BaseFragmentView {

    protected CompositeSubscription mSubscriptions;
    private FragmentComponent mFragmentComponent;

    abstract public int getLayoutRes();

    public void injectDependencies() {
    }

    public void injectViews(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mSubscriptions = new CompositeSubscription();
        mFragmentComponent = getActivityComponent()
                .providesFragmentComponent(new FragmentModule(this));
        injectDependencies();
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


    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return getContext().getString(R.string.retry);
    }

    protected ActivityComponent getActivityComponent() {
        return ((BaseActivity) getActivity()).getActivityComponent();
    }

    @Override
    public void showMessageDialog(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(R.string.ok, null);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.show();
    }

    @Override
    public void showMessageDialog(int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(R.string.ok, null);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void showMessageDialog(int title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(R.string.ok, null);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.show();
    }

    @Override
    public void showMessageDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(R.string.ok, null);
        builder.setMessage(message);
        builder.show();
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
    public void showUpdateDialog() {
        ((BaseActivity)getActivity()).showUpdateDialog();
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

    public void showDrawerToggleButton() {
        ((ToolbarActions) getActivity()).showDrawerToggleButton();
    }

    public void showDrawerToggleButton(Toolbar toolbar) {
        ((ToolbarActions) getActivity()).showDrawerToggleButton(toolbar);
    }

    public void setSupportActionBar(Toolbar toolbar) {
        ((ToolbarActions) getActivity()).setSupportToolBar(toolbar);
    }

    @Override
    public void onDestroy() {
        if (mSubscriptions != null && !mSubscriptions.isUnsubscribed()) {
            mSubscriptions.unsubscribe();
        }
        super.onDestroy();
    }
}
