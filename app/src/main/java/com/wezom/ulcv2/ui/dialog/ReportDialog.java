package com.wezom.ulcv2.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;

import butterknife.BindView;

import static com.wezom.ulcv2.common.Constants.DialogResult.CANCEL;
import static com.wezom.ulcv2.common.Constants.DialogResult.OK;

/**
 * Created by Sivolotskiy.v on 29.06.2016.
 */
@FragmentWithArgs
public class ReportDialog extends BaseDialog {

    @BindView(R.id.dialog_invite_text_view)
    EditText mEditText;
    @BindView(R.id.dialog_report_image_view)
    ImageView mImageView;

//    @BindView(R.id.radial)
//    RadialProgressView mView;

    @Arg
    String avatar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = setControlButton();
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_report, null);
        builder.setView(view);

        bindView(view);

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + avatar)
                .placeholder(getResources().getDrawable(R.drawable.ic_default_user_avatar))
                .into(mImageView);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private AlertDialog.Builder setControlButton() {
        AlertDialog.Builder builder = getDialogBuilder();
        builder.setPositiveButton(R.string.send, this::onClickOk);
        builder.setNegativeButton(R.string.cancel, this::onClickCancel);

        return builder;
    }

    private void onClickOk(DialogInterface dialog, int which) {
        if (mOnDialogResult == null) {
            return;
        }

        mOnDialogResult.onDialogResult(OK, mEditText.getText().toString());
    }

    private void onClickCancel(DialogInterface dialog, int which) {
        if (mOnDialogResult == null) {
            return;
        }
        mOnDialogResult.onDialogResult(CANCEL);
    }
}
