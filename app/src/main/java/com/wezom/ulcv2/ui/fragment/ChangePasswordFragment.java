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
import com.wezom.ulcv2.mvp.presenter.ChangePasswordPresenter;
import com.wezom.ulcv2.mvp.view.ChangePasswordView;
import com.wezom.ulcv2.ui.views.HeartParticleView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sivolotskiy.v on 09.06.2016.
 */
public class ChangePasswordFragment extends BaseFragment
        implements ChangePasswordView {

    private static final String EXTRA_NAME = "fragment_change_password";
    @InjectPresenter
    ChangePasswordPresenter mPresenter;
    @BindView(R.id.fragment_change_pass_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_change_pass_old_edit_text)
    EditText mOldPassEditText;
    @BindView(R.id.fragment_change_pass_edit_text)
    EditText mNewPassEditText;
    @BindView(R.id.fragment_change_pass_repeat_edit_text)
    EditText mRepeatPassEditText;

    public static ChangePasswordFragment getNewInstance(String name) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.accept_item).setVisible(true)
                .setOnMenuItemClickListener(item -> {
                    mPresenter.onPasswordChange(mOldPassEditText.getText().toString(), mNewPassEditText.getText().toString(),
                            mRepeatPassEditText.getText().toString());
                    return false;
                });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_change_password;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public int getScreenOrientation() {
        return 1;
    }

    /*@Override
    public List<Feature> requestFeature() {
        ArrayList<Feature> features = new ArrayList<>();
        Feature feature = ToolbarFeature.newBuilder()
                .setLayoutMode(Constants.LayoutMode.NOT_LOGGED_IN)
                .build();
        features.add(feature);
        return features;
    }
*/
    @Override
    public void showError(String error) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.error))
                .setMessage(error)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // continue with delete
                })
                .show();
    }

    @OnClick(R.id.fragment_change_pass_last_layout)
    void onClickOld() {
        mOldPassEditText.requestFocus();
    }

    @OnClick(R.id.fragment_change_login_pass_layout)
    void onClickNewPass() {
        mNewPassEditText.requestFocus();
    }

    @OnClick(R.id.fragment_change_pass_repeat_layout)
    void onClickReapetPass() {
        mRepeatPassEditText.requestFocus();
    }

    private void prepareToolbar() {
        mToolbar.setTitle(R.string.password);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(v -> mPresenter.onBackPressed());
    }
}
