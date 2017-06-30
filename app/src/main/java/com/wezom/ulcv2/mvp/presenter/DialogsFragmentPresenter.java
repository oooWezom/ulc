package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.birbit.android.jobqueue.JobManager;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.database.DatabaseManager;
import com.wezom.ulcv2.events.On2PlayClickEvent;
import com.wezom.ulcv2.events.On2TalkClickEvent;
import com.wezom.ulcv2.jobs.FetchDialogsJob;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Dialog;
import com.wezom.ulcv2.mvp.view.DialogsFragmentView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import lombok.Getter;

/**
 * Created by kartavtsev.s on 22.01.2016.
 */

@InjectViewState
public class DialogsFragmentPresenter extends BasePresenter<DialogsFragmentView> {

    @Inject
    @Getter
    EventBus mBus;
    @Inject
    @Getter
    PreferenceManager mPreferenceManager;
    @Inject
    DatabaseManager mDatabaseManager;
    @Inject
    JobManager mJobManager;


    public DialogsFragmentPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void loadDialogs(boolean pullToRefresh) {
        getViewState().showLoading(pullToRefresh);
        ArrayList<Dialog> dialogs = Dialog.getDialogs();
        getViewState().setData(dialogs);
        getViewState().showContent();
    }

    public void updateDialogs() {
        ArrayList<Dialog> dialogs = Dialog.getDialogs();
        getViewState().setData(dialogs);
        getViewState().showContent();
    }

    public void fetchNewDialogs() {
        mJobManager.addJobInBackground(new FetchDialogsJob());
    }

    public void onClick2Play() {
        mBus.post(new On2PlayClickEvent());
    }

    public void onClick2Talk() {
        mBus.post(new On2TalkClickEvent());
    }
}
