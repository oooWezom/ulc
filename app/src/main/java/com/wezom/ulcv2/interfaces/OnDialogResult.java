package com.wezom.ulcv2.interfaces;


import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.Constants.DialogResult;
import com.wezom.ulcv2.ui.dialog.BaseDialog;

/**
 * Created by Sivolotskiy.v on 11.12.2015.
 */
public interface OnDialogResult {
    void onDialogResult(DialogResult dialogResult, Object... result);
}
