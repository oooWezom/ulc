package com.wezom.ulcv2.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.events.CountDownEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import static com.wezom.ulcv2.common.Constants.GAME_COUNTDOWN_NUMBER;
import static com.wezom.ulcv2.ui.dialog.InformationDialog.DialogType.CANCEL_BUTTON_MODE;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 12.07.2016
 * Time: 10:16
 */
@FragmentWithArgs
public class CountDownDialog extends InformationDialog {

    @Inject
    EventBus mBus;

    private static CountDownDialog sInstance;

    public static CountDownDialog getInstance() {
        return sInstance;
    }

    public static boolean show(FragmentManager fragmentManager) {
        if (sInstance == null) {
            sInstance = new CountDownDialogBuilder(CANCEL_BUTTON_MODE)
                    .viewRes(R.layout.layout_dialog_message_view)
                    .build();
            sInstance.show(fragmentManager, null);

            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setCount(GAME_COUNTDOWN_NUMBER);
        mBus.register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void injectDependencies() {
        getActivityComponent().inject(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCountDownEvent(CountDownEvent event) {
        setCount(event.getCount());

        if (event.getCount() == 0) {
            sInstance = null;
            dismiss();
        }
    }

    private void setCount(int count) {
        if (mView != null) {
            ((TextView) mView).setText(getString(R.string.game_will_start, count));
        }
    }

    @Override
    public void onDestroy() {
        mBus.unregister(this);
        super.onDestroy();
    }
}
