package com.wezom.ulcv2.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.injection.component.ActivityComponent;
import com.wezom.ulcv2.interfaces.OnDialogResult;
import com.wezom.ulcv2.ui.activity.BaseActivity;

import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.wezom.ulcv2.common.Constants.DialogResult.CANCEL;

/**
 * Created by Sivolotskiy.v on 29.06.2016.
 */
@Accessors(prefix = "m")
public class BaseDialog extends DialogFragment {

    private static final String TAG = BaseDialog.class.getSimpleName();

    @Getter
    @Setter
    static boolean mIsShown;
    @Setter
    OnDialogResult mOnDialogResult;

    @Arg(required = false)
    String dialogTitle;
    @Arg(required = false)
    int dialogTitleRes;
    @Arg(required = false)
    String dialogMessage;
    @Arg(required = false)
    int dialogMessageRes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
        injectDependencies();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mOnDialogResult != null) {
            mOnDialogResult.onDialogResult(CANCEL);
        }
        mIsShown = false;
        super.onDismiss(dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            ((AlertDialog) getDialog()).getButton(BUTTON_POSITIVE).setTextColor(getResources()
                    .getColor(R.color.colorPrimary));
            ((AlertDialog) getDialog()).getButton(BUTTON_NEGATIVE).setTextColor(getResources()
                    .getColor(R.color.colorPrimary));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setUpDialog();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected ActivityComponent getActivityComponent() {
        return getBaseActivity().getActivityComponent();
    }

    /**
     * This method will be called from {@link #onViewCreated(View, Bundle)} and this is the right place to
     * inject
     * dependencies (i.e. by using dagger)
     */
    protected void injectDependencies() {
    }

    protected void setUpDialog() {
        Dialog mDialog = getDialog();
        mDialog.setCancelable(false);
        mDialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
    }

    protected void bindView(View view) {
        if (view != null) {
            ButterKnife.bind(this, view);
        }
    }

    protected AlertDialog.Builder getDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (dialogTitleRes != 0) {
            builder.setTitle(dialogTitleRes);
        } else {
            builder.setTitle(dialogTitle);
        }
        if (dialogMessageRes != 0) {
            builder.setMessage(dialogMessageRes);
        } else {
            builder.setMessage(dialogMessage);
        }

        return builder;
    }
}
