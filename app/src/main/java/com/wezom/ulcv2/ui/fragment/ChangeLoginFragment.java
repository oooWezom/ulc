package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.mvp.presenter.ChangeLoginPresenter;
import com.wezom.ulcv2.mvp.view.ChangeLoginView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sivolotskiy.v on 09.06.2016.
 */
public class ChangeLoginFragment extends BaseFragment
        implements ChangeLoginView {

    private static final String EXTRA_NAME = "fragment_change_login_name";

    @InjectPresenter
    ChangeLoginPresenter mChangeLoginPresenter;
    @BindView(R.id.fragment_change_login_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_change_login_new_edit_text)
    EditText mNewLoginEditText;
    @BindView(R.id.fragment_change_login_pass_edit_text)
    EditText mPassEditText;


    public static ChangeLoginFragment getNewInstance(String name) {
        ChangeLoginFragment fragment = new ChangeLoginFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       setSupportActionBar(mToolbar);
        prepareToolbar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    private void prepareToolbar() {
        mToolbar.setTitle(R.string.login);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(v -> mChangeLoginPresenter.onBackPressed());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.accept_item).setVisible(true)
                .setOnMenuItemClickListener(item -> {
                    mChangeLoginPresenter.saveLogin(mNewLoginEditText.getText().toString());
                    return false;
                });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_change_login;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public int getScreenOrientation() {
        return 1;
    }
/*
    @Override
    public List<Feature> requestFeature() {
        ArrayList<Feature> features = new ArrayList<>();
        Feature feature = ToolbarFeature.newBuilder()
                .setLayoutMode(Constants.LayoutMode.NOT_LOGGED_IN)
                .build();
        features.add(feature);
        return features;
    }*/

    @OnClick(R.id.fragment_change_login_new_login_layout)
    void onClickNewLogin() {
        mNewLoginEditText.requestFocus();
    }

    @OnClick(R.id.fragment_change_login_pass_layout)
    void onClickPass() {
        mPassEditText.requestFocus();
    }

    @Override
    public void showError(String error){
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.error))
                .setMessage(error)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // continue with delete
                })
                .show();
    }
}
