package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.mvp.presenter.LoginFragmentPresenter;
import com.wezom.ulcv2.mvp.view.LoginFragmentView;
import com.wezom.ulcv2.ui.view.EditTextView;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginFragment extends BaseFragment
        implements LoginFragmentView {

    private static final String EXTRA_NAME = "fragment_login_extra_name";

    //region injects
    @InjectPresenter
    public LoginFragmentPresenter mPresenter;

    //endregion

    //region views
    @BindView(R.id.fragment_login_button)
    RelativeLayout mLoginButton;

    @BindView(R.id.fragment_watch_button)
    RelativeLayout mWatchButton;

    @BindView(R.id.fragment_login_reveal_view)
    View mRevealView;

    @BindView(R.id.fragment_login_login_edit)
    EditTextView mLoginEditText;

    @BindView(R.id.fragment_login_password_edit)
    EditTextView mPasswordEditText;

    @BindView(R.id.fragment_login_button_progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.fragment_login_logo_image)
    ImageView mLogoImage;

    @BindView(R.id.fragment_login_signup_button)
    TextView mLoginSignupButton;

    @BindView(R.id.fragment_login_forgot_password_button)
    TextView mRestorePasswordButton;

    @BindView(R.id.fragment_login_change_language_button)
    TextView mChangeLanguageButton;

    @BindView(R.id.fragment_login_selected_language_text)
    TextView mSelectedLanguageText;
    @BindView(R.id.fragment_login_login_text)
    TextView signInTextView;
    @BindView(R.id.fragment_watch_login_text)
    TextView watchLabelTextView;


//    @BindView(R.id.fragment_login_vk_button)
//    ImageView mVkLoginButton;
//
//    @BindView(R.id.fragment_login_facebook_button)
//    ImageView mFbLoginButton;

    EditText mLoginInput;
    EditText mPasswordInput;

    //endregion

    String mLoginSavedString;
    String mPassSavedString;

    //region overrides

    public static LoginFragment getNewInstance(String name) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolBar().setVisibility(View.GONE);
        mLoginButton.setOnClickListener(v ->
                mPresenter.doLogin(mLoginEditText.getText().toString(), mPasswordEditText.getText().toString())
        );
        mLoginSignupButton.setOnClickListener(v -> mPresenter.doRegister());
        mRestorePasswordButton.setOnClickListener(v -> mPresenter.doPasswordRestore());
        mChangeLanguageButton.setOnClickListener(v -> mPresenter.clickChangeLanguage());
        mWatchButton.setOnClickListener(v -> mPresenter.onWatchClick());

        mPresenter.startVideoPreview();
        handleInputFieldsActions();
        mPresenter.checkUpdate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoginEditText.setText(mLoginSavedString == null ? "" : mLoginSavedString);
        mPasswordEditText.setText(mPassSavedString == null ? "" : mPassSavedString);
    }

    @Override
    public void onStop() {
        super.onStop();
        mLoginSavedString = mLoginEditText.getText();
        mPassSavedString = mPasswordEditText.getText();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_login;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.getCategoriesFromBackend();
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.PORTRAIT;
    }

    public void animateLoginSuccess() {
        mRevealView.setVisibility(View.VISIBLE);
        mRevealView.setTop(mLogoImage.getTop());
        mRevealView.setLeft(mLogoImage.getLeft());
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 50f, 1f, 50f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1500);
        scaleAnimation.setFillAfter(true);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LoginFragment.this.onAnimationEnd(mLoginEditText.getText().toString(),
                        mPasswordEditText.getText().toString());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLogoImage.startAnimation(alphaAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRevealView.startAnimation(scaleAnimation);
    }

    @Override
    public void showLoading() {
        mLoginButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mLoginButton.setEnabled(true);
    }

    @Override
    public void showAppLanguagesDialog() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), mChangeLanguageButton, Gravity.TOP);
        popupMenu.inflate(R.menu.language_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            mPresenter.switchLanguage(item.getItemId());
            return true;
        });
        popupMenu.show();
    }

    @Override
    public void setUiValues() {
        watchLabelTextView.setText(getString(R.string.watch));
        mSelectedLanguageText.setText(getString(R.string.app_language));
        mChangeLanguageButton.setText(R.string.change);
        signInTextView.setText(getString(R.string.sign_in));
        mLoginEditText.setHintText(getString(R.string.email));
        mPasswordEditText.setHintText(getString(R.string.password));
        mLoginSignupButton.setText(getString(R.string.sign_up));
        mRestorePasswordButton.setText(getString(R.string.forgot_password));
    }


    //endregion

    private void onAnimationEnd(String login, String password) {
        mPresenter.onAnimationEnd(login, password);
    }

    private void handleInputFieldsActions() {
        mLoginInput = mLoginEditText.getInput();
        mPasswordInput = mPasswordEditText.getInput();

        mLoginInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                mPasswordInput.requestFocus();
                mPasswordInput.setSelection(mPasswordInput.getText().length());
            }
            return true;
        });

        mPasswordInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mPresenter.doLogin(mLoginEditText.getText().toString(), mPasswordEditText.getText().toString());
                Utils.hideKeyboard(mPasswordEditText);
            }

            return true;
        });
    }


    @OnClick(R.id.fragment_login_root)
    public void rootClick(View view) {
        mPresenter.hideKeyboard(view);
    }
    //endregion


}

