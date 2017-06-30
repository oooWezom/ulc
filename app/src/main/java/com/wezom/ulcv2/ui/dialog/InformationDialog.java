package com.wezom.ulcv2.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;

import static com.wezom.ulcv2.common.Constants.DialogResult.CANCEL;
import static com.wezom.ulcv2.common.Constants.DialogResult.OK;
import static com.wezom.ulcv2.ui.dialog.InformationDialog.DialogType.CANCEL_BUTTON_MODE;
import static com.wezom.ulcv2.ui.dialog.InformationDialog.DialogType.INPUT_MODE;
import static com.wezom.ulcv2.ui.dialog.InformationDialog.DialogType.OK_BUTTON_MODE;
import static com.wezom.ulcv2.ui.dialog.InformationDialog.DialogType.REPORT_MODE;
import static com.wezom.ulcv2.ui.dialog.InformationDialog.DialogType.TWO_BUTTON_MODE;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 07.07.2016
 * Time: 13:43
 */
@FragmentWithArgs
public class InformationDialog extends BaseDialog {

    protected View mView;
    @Arg
    DialogType dialogType;
    @Arg(required = false)
    int viewRes;
    @Arg(required = false)
    int hint;
    private EditText mInputEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = getDialogBuilder();

        if (viewRes > 0) {
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            mView = layoutInflater.inflate(viewRes, null);
            builder.setView(mView);
        }

        if (dialogType == OK_BUTTON_MODE) {
            builder.setPositiveButton(R.string.ok, this::onClickOk);
        } else if (dialogType == CANCEL_BUTTON_MODE) {
            builder.setNegativeButton(R.string.cancel, this::onClickCancel);
        } else if (dialogType == TWO_BUTTON_MODE) {
            builder.setPositiveButton(R.string.ok, this::onClickOk);
            builder.setNegativeButton(R.string.cancel, this::onClickCancel);
        } else if (dialogType == INPUT_MODE) {
            mInputEditText = new EditText(getContext());
            mInputEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            mInputEditText.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            mInputEditText.setHint(hint != 0 ? hint : R.string.type_message);
            builder.setView(mInputEditText);
            builder.setPositiveButton(R.string.ok, this::onClickOk);
            builder.setNegativeButton(R.string.cancel, this::onClickCancel);
        } else if (dialogType == REPORT_MODE) {

            builder.setPositiveButton(R.string.send, this::onClickOk);
            builder.setNegativeButton(R.string.cancel, this::onClickCancel);
        }

        return builder.create();
    }

    private void onClickOk(DialogInterface dialog, int which) {
        if (mOnDialogResult == null) {
            return;
        }

        switch (dialogType) {
            case INPUT_MODE:
                mOnDialogResult.onDialogResult(OK, mInputEditText.getText().toString());
                return;
            case REPORT_MODE:
                mOnDialogResult.onDialogResult(OK, defineSelectedReportCase(mView.findViewById(R.id.report_radio_group)));
                return;
        }

        mOnDialogResult.onDialogResult(OK);
    }

    private int defineSelectedReportCase(View view) {
        RadioGroup rg = (RadioGroup) view;
        int id = rg.getCheckedRadioButtonId();
        int checked = 0;
        switch (id) {
            case R.id.report_nudity:
                checked = Constants.REPORT_NUDITY;
                break;
            case R.id.report_violence:
                checked = Constants.REPORT_VIOLENCE;
                break;
            case R.id.report_harassment:
                checked = Constants.REPORT_HARASSMENT;
                break;
        }
        return checked;
    }

    private void onClickCancel(DialogInterface dialog, int which) {
        if (mOnDialogResult == null) {
            return;
        }
        mOnDialogResult.onDialogResult(CANCEL);
    }

    public enum DialogType {
        OK_BUTTON_MODE,
        CANCEL_BUTTON_MODE,
        TWO_BUTTON_MODE,
        INPUT_MODE,
        REPORT_MODE
    }
}
