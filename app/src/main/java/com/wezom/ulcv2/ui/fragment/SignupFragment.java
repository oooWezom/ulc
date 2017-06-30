package com.wezom.ulcv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.events.LanguageSelectedEvent;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.presenter.SignupPresenter;
import com.wezom.ulcv2.mvp.view.SignupView;
import com.wezom.ulcv2.net.models.requests.RegisterRequest;
import com.wezom.ulcv2.ui.view.EditTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by Sivolotskiy.v on 26.05.2016.
 */
public class SignupFragment extends BaseFragment implements SignupView, DatePickerDialog.OnDateSetListener {

    private static final String EXTRA_NAME = "fragment_sign_up_name";

    private static final int MIN_LENGTH = 3;
    @InjectPresenter
    SignupPresenter mSignupPresenter;

    @Inject
    EventBus mBus;

    @BindView(R.id.fragment_signup_email_edit_text)
    EditTextView mEmailEdit;
    @BindView(R.id.fragment_signup_username_edit_text)
    EditTextView mUsernameEditText;
    @BindView(R.id.fragment_signup_password_edit_text)
    EditTextView mPasswordEdit;
    @BindView(R.id.fragment_signup_repeat_password_edit_text)
    EditTextView mPasswordRepeatEdit;
    @BindView(R.id.fragment_signup_birthday_edit_text)
    EditTextView mBirthDate;
    @BindView(R.id.fragment_signup_language_edit_text)
    EditTextView mLanguageEdit;
    @BindView(R.id.fragment_register_form_layout)
    LinearLayout mFormLayout;
    @BindView(R.id.fragment_register_confirm_layout)
    LinearLayout mConfirmLayout;
    @BindView(R.id.fragment_signup_signup_button)
    RelativeLayout mSignupButton;
    @BindView(R.id.fragment_signup_signup_button_progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.fragment_signup_sex_radio_group)
    RadioGroup mSexRadioGroup;
    @BindView(R.id.fragment_signup_error_text_view)
    TextView mErrorTextView;
    @BindView(R.id.fragment_signup_error_frame_layout)
    RelativeLayout mErrorLayout;
    @BindView(R.id.fragment_signup_toolbar)
    android.support.v7.widget.Toolbar mToolbar;
    @BindView(R.id.fragment_sign_up_terms_check_box)
    CheckBox mTermsCheckBox;
    @BindView(R.id.fragment_sign_up_terms_text)
    TextView mTermsTextView;

    private HashSet<Language> mLanguages = new HashSet<>();
    private List<Language> mLanguagesArray;


    public static SignupFragment getNewInstance(String name) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_signup;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSupportActionBar(mToolbar);
        Log.d("Log_ SignUp ", "onViewCreated");
        mToolbar.setTitle(getString(R.string.title_fragment_signup));
        mToolbar.setSubtitle(null);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(v -> mSignupPresenter.onBackPressed());

        initTextWatchers();
        //  mEmailEdit.setEditTextId(R.id.id_fragment_signup_email_edit_text);
        //  mUsernameEditText.setEditTextId(R.id.id_fragment_signup_username_edit_text);
        mPasswordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPasswordRepeatEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mBirthDate.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        mBirthDate.setFocusChangeListener(this::onBirthDateFocusChange);
        mLanguageEdit.setFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mSignupPresenter.clickChangeLanguage(mLanguagesArray);
                mLanguageEdit.clearFocus();
            }
        });
        checkFields();

        setupTermsOfUse();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mBus.isRegistered(this)) {
            mBus.register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mBus.isRegistered(this)) {
            mBus.unregister(this);
        }
    }

    @OnClick(R.id.fragment_signup_signup_button)
    public void onRegisterClick() {
        if (validate()) {
            HashSet<Integer> languageIds = new HashSet<>();
            for (Language language : mLanguages) {
                languageIds.add((int) (long) language.getId());
            }

            if (!mTermsCheckBox.isChecked()) {
                showError(getString(R.string.error_agree_terms_of_use));
                return;
            }

            mSignupPresenter.register(new RegisterRequest(mEmailEdit.getText(),
                    mUsernameEditText.getText(),
                    mPasswordEdit.getText(),
                    mBirthDate.getText(),
                    mSexRadioGroup.getCheckedRadioButtonId() == R.id.fragment_signup_female_radio_button ? 1 : 2, languageIds));
        }
    }

    @OnClick(R.id.fragment_signup_confirm_button)
    public void onClickConfirm() {
        mSignupPresenter.callAuthRequest();
    }

    @OnClick(R.id.fragment_sign_up_terms_text)
    public void onTermsClick() {
        mSignupPresenter.callTermsOfUse();
    }

    @Override
    public void showEmailConfirmView() {
        mFormLayout.setVisibility(INVISIBLE);
        mConfirmLayout.setVisibility(VISIBLE);
        mSignupButton.setVisibility(GONE);
        mErrorLayout.setVisibility(GONE);
    }

    @Override
    public void showLocalLoading() {
        mProgressBar.setVisibility(VISIBLE);
        mSignupButton.setEnabled(false);
    }

    @Override
    public void hideLocalLoading() {
        mProgressBar.setVisibility(INVISIBLE);
        mSignupButton.setEnabled(true);
    }

    @Override
    public void showError(String error) {
        mErrorTextView.setText(error);
        mErrorLayout.setVisibility(VISIBLE);
    }

    @Override
    public void setLanguages(List<Language> response) {
        mLanguageEdit.setText("");
        mLanguagesArray = response;
        for (Language language : response) {
            if (language.isCheckStatus()) {
                mLanguages.add(language);
                setLanguagesText(language.getName());
            }
        }
    }

    public void onBirthDateFocusChange(View view, boolean focused) {
        if (focused) {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
            datePickerDialog.setMaxDate(calendar);
            datePickerDialog.show(getActivity().getFragmentManager(), "");
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date = dateFormat.format(new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime());
        mBirthDate.setText(date);
        mBirthDate.clearFocus();
    }

    private void initTextWatchers() {
        mEmailEdit.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mEmailEdit.setError(!(!mEmailEdit.getText().isEmpty()
                        && Patterns.EMAIL_ADDRESS.matcher(mEmailEdit.getText()).matches()));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mUsernameEditText.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mUsernameEditText.setError(s.toString().length() < MIN_LENGTH);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupTermsOfUse() {
        String text = getString(R.string.fragment_sign_up_i_agree_to) + " ";
        int startItem = text.length();
        text += getString(R.string.fragment_sign_up_terms_of_use);

        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), startItem, content.length(), 0);
        mTermsTextView.setText(content);
    }

    private boolean validate() {
        if (mEmailEdit.getText().isEmpty()) {
            mEmailEdit.setError(true);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmailEdit.getText()).matches()) {
            mEmailEdit.setError(true);
            return false;
        } else if (mUsernameEditText.getText().length() < MIN_LENGTH) {
            mUsernameEditText.setError(true);
            return false;
        } else if (mPasswordEdit.getText().length() < MIN_LENGTH) {
            mPasswordEdit.setError(true);
            return false;
        } else if (mPasswordRepeatEdit.getText().length() < MIN_LENGTH) {
            mPasswordRepeatEdit.setError(true);
            return false;
        } else if (!mPasswordRepeatEdit.getText().equals(mPasswordEdit.getText())) {
            mPasswordRepeatEdit.setError(true);
            mPasswordEdit.setError(true);
            return false;
        } else if (mBirthDate.getText().isEmpty()) {
            mBirthDate.setError(true);
        } else if (mLanguageEdit.getText().isEmpty()) {
            mLanguageEdit.setError(true);
        }

        return true;
    }

    private void checkFields() {
        rx.Observable
                .combineLatest(
                        RxTextView.textChanges(mEmailEdit.getEditText()),
                        RxTextView.textChanges(mUsernameEditText.getEditText()),
                        RxTextView.textChanges(mPasswordEdit.getEditText()),
                        RxTextView.textChanges(mPasswordRepeatEdit.getEditText()),
                        RxTextView.textChanges(mBirthDate.getEditText()),
                        RxTextView.textChanges(mLanguageEdit.getEditText()), (email, username, password, passrepeat, birthday, language) ->
                                Patterns.EMAIL_ADDRESS.matcher(email).matches()
                                        && username.length() > 0 && password.length() > 0
                                        && passrepeat.length() > 0 && birthday.length() > 0
                                        && language.length() > 0
                ).subscribe(mSignupButton::setEnabled);
    }

    private void setLanguagesText(String text) {
        String currentText = mLanguageEdit.getText().toString();
        mLanguageEdit.setText(currentText.trim().isEmpty() ? text :
                String.format("%s, %s", currentText, text));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUserSelectedLanguages(LanguageSelectedEvent event) {
        mSignupPresenter.onUserSelectedLanguages(event);
    }
}
