package com.wezom.ulcv2.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.mvp.presenter.ConfirmRestorePassPresenter;
import com.wezom.ulcv2.mvp.view.ConfirmRecoveryPassView;
import com.wezom.ulcv2.ui.view.EditTextView;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by Android 3 on 24.03.2017.
 */

@FragmentWithArgs
public class ConfirmRestorePassFragment extends BaseFragment
        implements ConfirmRecoveryPassView {

    @InjectPresenter
    ConfirmRestorePassPresenter presenter;
    @BindView(R.id.fragment_confirm_restore_success_text)
    TextView infoText;
    @BindView(R.id.fragment_confirm_restore_search_button)
    RelativeLayout nextButton;
    @BindView(R.id.fragment_confirm_restore_toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.fragment_confirm_restore_search_button_progressbar)
    ProgressBar progressBar;
    @BindView(R.id.fragment_confirm_restore_pass_text_view)
    EditTextView password;
    @BindView(R.id.fragment_confirm_restore_pass_repeat_text_view)
    EditTextView passwordRepeat;

    @Arg
    String key;

    @Override
    public int getLayoutRes() {
        return R.layout.fragmen_confirm_restore_password;
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
        progressBar.setVisibility(VISIBLE);
        nextButton.setEnabled(false);
    }

    @Override
    public void hideLocalLoading() {
        progressBar.setVisibility(INVISIBLE);
        nextButton.setEnabled(true);
    }

    @Override
    public void showErrorText(int error) {
        infoText.setText(getString(error));
        password.setError(true);
        passwordRepeat.setError(true);
        infoText.setVisibility(VISIBLE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSupportActionBar(toolbar);
        initToolbar();

        nextButton.setEnabled(false);
        passwordRepeat.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (TextUtils.equals(password.getText(), passwordRepeat.getText())) {
                    nextButton.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.cahnage_password_title_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> presenter.onBackPressed());
    }

    public void hideKeyboard() {
        View cview = getActivity().getCurrentFocus();
        if (cview != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(cview.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.fragment_confirm_restore_search_button)
    void onClickNext() {
        presenter.restorePassword(key, password.getText());
        infoText.setVisibility(View.GONE);
        password.setError(false);
        passwordRepeat.setError(false);
    }
}
