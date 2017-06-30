package com.wezom.ulcv2.common;

import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Validator;

/**
 * Created by kartavtsev.s on 03.11.2015.
 */
public class TextInputLayoutDefaultValidateAction implements Validator.ViewValidatedAction {

    @Override
    public void onAllRulesPassed(View view) {
        if (view.getParent() != null && view.getParent() instanceof TextInputLayout) {
            ((TextInputLayout) view.getParent()).setError(null);
            ((TextInputLayout) view.getParent()).setErrorEnabled(false);
        } else {
            if (view instanceof TextView) {
                ((TextView) view).setError(null);
            }
        }
    }
}
