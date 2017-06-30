package com.wezom.ulcv2.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.interfaces.OnTimeIsOverListener;
import com.wezom.ulcv2.ui.view.RadialProgressView;

import butterknife.BindView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.wezom.ulcv2.common.Constants.DialogResult.CANCEL;
import static com.wezom.ulcv2.common.Constants.DialogResult.DELAY;
import static com.wezom.ulcv2.common.Constants.DialogResult.OK;
import static com.wezom.ulcv2.common.Constants.DialogResult.TIME_IS_OVER;
import static com.wezom.ulcv2.common.Constants.InviteDialogType;

/**
 * Created by Sivolotskiy.v on 30.06.2016.
 */
@FragmentWithArgs
public class InviteDialog extends BaseDialog implements OnTimeIsOverListener {

    @Nullable
    @BindView(R.id.dialog_info_text_view)
    TextView mInfoTextView;
    @Nullable
    @BindView(R.id.dialog_invite_image_view)
    ImageView mUserImageView;
    @Nullable
    @BindView(R.id.dialog_waiting_info_text_view)
    TextView mWaitingInfoText;
    @Nullable
    @BindView(R.id.dialog_waiting_radial_progress_view)
    RadialProgressView mRadialProgressView;
    @Nullable
    @BindView(R.id.dialog_with_delay_category_image)
    ImageView mCategoryImageView;
    @Nullable
    @BindView(R.id.dialog_with_delay_progress_view)
    RadialProgressView mDelayRadialProgressView;
    @Nullable
    @BindView(R.id.dialog_with_delay_text_view)
    TextView mDelayTextView;
    @Nullable
    @BindView(R.id.dialog_with_delay_input)
    EditText mInputEditText;

    @Arg
    InviteDialogType dialogType;
    @Arg(required = false)
    String dialogUserUrl;
    @Arg(required = false)
    String dialogMessage2;
    @Arg(required = false)
    int dialogTime;
    @Arg(required = false)
    String dialogCategoryUrl;
    @Arg(required = false)
    String userName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = setControlButton();
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = null;

        switch (dialogType) {
            case INFO_MODE:
                view = layoutInflater.inflate(R.layout.dialog_info, null);
                break;
            case WAITING_MODE:
                view = layoutInflater.inflate(R.layout.dialog_waiting, null);
                break;
            case DELAY_MODE:
                view = layoutInflater.inflate(R.layout.dialog_invite_with_delay, null);
                break;
        }

        if (view != null) {
            bindView(view);
            builder.setView(view);
        }
        onViewCreated(view, savedInstanceState);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Dialog mDialog = getDialog();
        mDialog.setCancelable(false);
        mDialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle(dialogTitle != null ? dialogTitle : null);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int userNameSpanSize;
        int spanStart;
        int spanEnd;
        StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);

        switch (dialogType) {
            case INFO_MODE:
                assert mInfoTextView != null;
                loadImage(mUserImageView, dialogUserUrl);
                mInfoTextView.setText(dialogMessage);
                break;
            case WAITING_MODE:

                spanStart = dialogMessage.length();
                spanEnd = spanStart + userName.length();


                SpannableString spanTitle = new SpannableString(dialogMessage + userName + dialogMessage2);
                spanTitle.setSpan(boldStyle, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                assert mRadialProgressView != null;
                assert mWaitingInfoText != null;
                if (dialogUserUrl != null) {
                    mRadialProgressView.setImageView(dialogUserUrl);
                }
                mRadialProgressView.setInitData(this, dialogTime);
                mRadialProgressView.start();
                mWaitingInfoText.setText(spanTitle);
                break;
            case DELAY_MODE:
                assert mDelayTextView != null;
                assert mDelayRadialProgressView != null;
                Picasso.with(getContext())
                        .load(dialogCategoryUrl)
                        .placeholder(R.drawable.ic_default_user_avatar)
                        .into(mCategoryImageView);

                spanStart = dialogMessage.length();
                spanEnd = spanStart + dialogMessage2.length();
                userNameSpanSize = userName.length();

                SpannableString spannableString = new SpannableString(userName + dialogMessage + dialogMessage2);

                spannableString.setSpan(boldStyle, 0, userNameSpanSize, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(boldStyle, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (dialogMessage2 != null) {
                    mDelayTextView.setText(spannableString);
                } else {
                    mDelayTextView.setText(dialogMessage);
                }

                mDelayRadialProgressView.setInitData(this, dialogTime);
                mDelayRadialProgressView.start();

                if (dialogUserUrl != null) {
                    mDelayRadialProgressView.setImageView(dialogUserUrl);
                }

                mDelayTextView.setOnClickListener(v -> {
                    mInputEditText.setVisibility(mInputEditText.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                });
                break;
        }
    }

    @Override
    public void onTimeIsOverListener() {
        if (mOnDialogResult != null) {
            mOnDialogResult.onDialogResult(TIME_IS_OVER);
        }
        mOnDialogResult = null;
        if (isVisible()) {
            dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mDelayRadialProgressView != null) {
            mDelayRadialProgressView.finish();
        }
        super.onDismiss(dialog);
    }

    public void loadImage(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            url = Constants.CONTENT_DEV_URL + url;
        }

        Picasso.with(getContext())
                .load(!TextUtils.isEmpty(url) ? url : "http://")
                .error(R.drawable.ic_default_user_avatar)
                .into(imageView);
    }

    private AlertDialog.Builder setControlButton() {
        AlertDialog.Builder builder = getDialogBuilder();
        int positiveButtonTextRes = R.string.ok;

        builder.setPositiveButton(positiveButtonTextRes, this::onClickOk);

        if (dialogType == InviteDialogType.DELAY_MODE) {
            builder.setNegativeButton(R.string.cancel, this::onClickCancel);
        }

        return builder;
    }

    private void onClickOk(DialogInterface dialog, int which) {
        if (mOnDialogResult == null) {
            return;
        }
        if (mInputEditText != null && !TextUtils.isEmpty(mInputEditText.getText())) {
            mOnDialogResult.onDialogResult(DELAY, Integer.decode(mInputEditText.getText().toString()));
        } else {
            mOnDialogResult.onDialogResult(OK);
        }
        dismiss();
        mOnDialogResult = null;
    }

    private void onClickCancel(DialogInterface dialog, int which) {
        if (mOnDialogResult != null) {
            mOnDialogResult.onDialogResult(CANCEL);
        }
        dismiss();
    }
}