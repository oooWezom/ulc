package com.wezom.ulcv2.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.mvp.presenter.PasswordRecoveryPresenter;
import com.wezom.ulcv2.mvp.view.PasswordRecoveryView;
import com.wezom.ulcv2.ui.view.EditTextView;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.wezom.ulcv2.R.drawable.ic_action_back;
import static com.wezom.ulcv2.R.string.recovery_password_title;
import static com.wezom.ulcv2.R.string.reset_password_success;

/**
 * Created by Sivolotskiy.v on 27.05.2016.
 */
public class PasswordRecoveryFragment extends BaseFragment
        implements PasswordRecoveryView {

    private static final String EXTRA_NAME = "fragment_pass_recovery_name";

    @InjectPresenter
    PasswordRecoveryPresenter mPresenter;
    @BindView(R.id.fragment_recovery_success_text)
    TextView mInfoText;
    @BindView(R.id.fragment_recovery_search_button)
    RelativeLayout mSearchButton;
    @BindView(R.id.fragment_recovery_toolbar)
    android.support.v7.widget.Toolbar mToolbar;
    @BindView(R.id.fragment_recovery_search_button_progressbar)
    ProgressBar mSearchProgressBar;
    @BindView(R.id.fragment_recovery_username_text_view)
    EditTextView mUsernameEditText;


    public static PasswordRecoveryFragment getNewInstance(String name) {
        PasswordRecoveryFragment fragment = new PasswordRecoveryFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_password_recovery;
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
    public void showLocalLoading() {
        mSearchProgressBar.setVisibility(VISIBLE);
        mSearchButton.setEnabled(false);
    }

    @Override
    public void hideLocalLoading() {
        mSearchProgressBar.setVisibility(INVISIBLE);
        mSearchButton.setEnabled(true);
    }

    @Override
    public void showSuccessRestoreText() {
        mInfoText.setText(getString(reset_password_success));
        mInfoText.setVisibility(View.VISIBLE);
        mSearchButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorText(int error) {
        mInfoText.setText(getString(error));
        mUsernameEditText.setError(true);
        mInfoText.setVisibility(VISIBLE);
    }


  /*  @Override
    public List<Feature> requestFeature() {
        ArrayList<Feature> features = new ArrayList<>();
        Feature feature = ToolbarFeature.newBuilder()
                .setLayoutMode(Constants.LayoutMode.NOT_LOGGED_IN)
                .build();
        features.add(feature);
        return features;
    }*/

    public void hideKeyboard() {
        // Check if no view has focus:
        View cview = getActivity().getCurrentFocus();
        if (cview != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(cview.getWindowToken(), 0);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getString(recovery_password_title));
        mToolbar.setNavigationIcon(ic_action_back);
        mToolbar.setNavigationOnClickListener(v -> mPresenter.onBackPressed());

        mSearchButton.setEnabled(false);
        mUsernameEditText.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mSearchButton.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.fragment_recovery_search_button)
    void onClickSearch() {
        mPresenter.restorePassword(mUsernameEditText.getText());
        mInfoText.setVisibility(View.GONE);
        mUsernameEditText.setError(false);
        hideKeyboard();
    }
}
