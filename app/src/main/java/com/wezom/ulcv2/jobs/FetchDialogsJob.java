package com.wezom.ulcv2.jobs;


import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.wezom.ulcv2.database.DatabaseManager;
import com.wezom.ulcv2.events.game.DialogsUpdatedEvent;
import com.wezom.ulcv2.injection.component.ApplicationComponent;
import com.wezom.ulcv2.mvp.model.Dialog;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.DialogsRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

/**
 * Created by kartavtsev.s on 08.02.2016.
 */
public class FetchDialogsJob extends BaseJob {

    private static AtomicInteger mJobCounter = new AtomicInteger(0);
    private final int mId;

    @Inject
    transient ApiManager mApiManager;

    @Inject
    transient DatabaseManager mDatabaseManager;

    @Inject
    transient EventBus mBus;

    public FetchDialogsJob() {
        super(new Params(UI_PRIORITY).groupBy("fetch-dialogs").requireNetwork());
        mId = mJobCounter.incrementAndGet();
    }

    @Override
    public void inject(ApplicationComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        if (mId != mJobCounter.get()) {
            //If we have new same job behind this,
            // let that job to be executed instead of this one
            return;
        }
        mApiManager.getDialogs(new DialogsRequest())
                .subscribe(dialogsResponse -> {
                    Dialog.updateDialogs(dialogsResponse.getResponse());
                    mBus.post(new DialogsUpdatedEvent());
                },Throwable::printStackTrace);
    }

    @Override
    protected void onCancel(int cancelReason) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
