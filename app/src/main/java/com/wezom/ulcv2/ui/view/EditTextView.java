package com.wezom.ulcv2.ui.view;

/**
 * Created by sivolotskiy.v on 30.05.2016.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.EditText;
import android.widget.ImageView;

import com.wezom.ulcv2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 10.12.2015.
 */
public class EditTextView extends TextInputLayout {

    public static final int DEFAULT_INPUT_TYPE = InputType.TYPE_TEXT_VARIATION_PASSWORD;
    public static final int DURATION_ANIMATION = 200;
    public static final float ALPHA_TRANSPARENT = 0f;
    public static final float ALPHA_OPAQUE = 1f;

    @Getter
    @Accessors(prefix = "m")
    EditText mEditText;
    ImageView mClearImageView;
    @BindView(R.id.view_edit_text_error_image_view)
    ImageView mErrorImageView;

    String mValue;

    private String mText;
    private String mHintText;
    private int mInputType;
    private boolean mIsErrorImage;
    private boolean mIsSingleLine;
    private boolean mHasClear;
    private int stateToSave;

    @IdRes
    private int mId = R.id.view_edit_text;
    @IdRes
    private int mImageId = R.id.view_edit_text_clear_image_view;

    @Setter
    @Accessors(prefix = "m")
    private TextWatcher mTextWatcher;
    @Setter
    private OnClickListener onClickListener;
    @Setter
    @Accessors(prefix = "m")
    private OnFocusChangeListener mFocusChangeListener;

    public EditTextView(Context context) {
        super(context);
        mInputType = DEFAULT_INPUT_TYPE;
        initViews(context);
    }

    public EditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.EditTextView, 0, 0);
        try {
            mText = mTypedArray.getString(R.styleable.EditTextView_text);
            mHintText = mTypedArray.getString(R.styleable.EditTextView_hintText);
            mInputType = mTypedArray.getInt(R.styleable.EditTextView_android_inputType, DEFAULT_INPUT_TYPE);
            mIsErrorImage = mTypedArray.getBoolean(R.styleable.EditTextView_errorImage, false);
            mIsSingleLine = mTypedArray.getBoolean(R.styleable.EditTextView_singleLine, false);
            mHasClear = mTypedArray.getBoolean(R.styleable.EditTextView_hasClear, true);
            mId = mTypedArray.getResourceId(R.styleable.EditTextView_inputId, 0);


        } finally {
            mTypedArray.recycle();
        }

        initViews(context);
    }

    public String getText() {
        return mEditText.getText().toString();
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void setInputType(int inputType) {
        mEditText.setInputType(InputType.TYPE_CLASS_TEXT | mInputType);
    }

    public void setHintText(String hintText) {
        mHintText = hintText;
        mEditText.setHint(mHintText);
    }

    public void setEnabled(boolean enabled) {
        mEditText.setEnabled(enabled);
    }

    public void setError(boolean show) {
        if (!(!show && mErrorImageView.getVisibility() != VISIBLE)) {
            animateVisibilityImage(show);
        }
    }

    public void setSingleLine(boolean single) {
        mEditText.setSingleLine(single);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_edit_text, this);
        ButterKnife.bind(this);

        mEditText = (EditText) findViewById(R.id.view_edit_text);
        mEditText.setId(mId);

        mClearImageView = (ImageView) findViewById(R.id.view_edit_text_clear_image_view);
        mClearImageView.setId(mImageId);

        setSingleLine(mIsSingleLine);
        mEditText.setText(mText);
        mEditText.setHint(mHintText);
        mClearImageView.setVisibility(GONE);
        mErrorImageView.setVisibility(mIsErrorImage ? INVISIBLE : GONE);
        if (mInputType != DEFAULT_INPUT_TYPE) {
            mEditText.setInputType(InputType.TYPE_CLASS_TEXT | mInputType);
        }

        mEditText.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
        });

        mClearImageView.setOnClickListener(view -> {
          mEditText.setText("");
        });

        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mFocusChangeListener != null) {
                        mFocusChangeListener.onFocusChange(mEditText, hasFocus);
                    }
                }
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                if (mHasClear) {
                    mClearImageView.setVisibility(!text.toString().isEmpty()
                            ? VISIBLE : GONE);
                }

                if (mTextWatcher != null) {
                    mTextWatcher.beforeTextChanged(text, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void animateVisibilityImage(boolean show) {
        AlphaAnimation alpha;
        AnimationSet animationSet = new AnimationSet(true);

        if (show) {
            alpha = new AlphaAnimation(ALPHA_TRANSPARENT, ALPHA_OPAQUE);
        } else {
            alpha = new AlphaAnimation(ALPHA_OPAQUE, ALPHA_TRANSPARENT);
        }

        animationSet.addAnimation(alpha);
        animationSet.setDuration(DURATION_ANIMATION);
        mErrorImageView.startAnimation(animationSet);
        mErrorImageView.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    public EditText getInput() {
        return mEditText;
    }

}

