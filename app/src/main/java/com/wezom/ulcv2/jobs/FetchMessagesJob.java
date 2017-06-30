package com.wezom.ulcv2.jobs;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.wezom.ulcv2.database.DatabaseManager;
import com.wezom.ulcv2.events.MessagesUpdatedEvent;
import com.wezom.ulcv2.injection.component.ApplicationComponent;
import com.wezom.ulcv2.mvp.model.Message;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.MessagesSinceRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by kartavtsev.s on 09.02.2016.
 */
public class FetchMessagesJob extends BaseJob {

    @Inject
    transient ApiManager mApiManager;

    @Inject
    transient DatabaseManager mDatabaseManager;

    @Inject
    transient EventBus mBus;

    private int mDialogId;

    public FetchMessagesJob(int dialogId) {
        super(new Params(UI_PRIORITY)
                .groupBy(String.format("fetch-messages-%d", dialogId))
                .requireNetwork());
        mDialogId = dialogId;
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
        int lastMessageServerId = Message.getLastMessageServerId(mDialogId);
        int count = 100;

        Observable.range(0, Integer.MAX_VALUE - 1)
                .concatMap(integer -> mApiManager.getMessagesSince(new MessagesSinceRequest(mDialogId, integer * count, count, lastMessageServerId)))
                .takeWhile(messagesResponse -> !messagesResponse.getMessages().isEmpty())
                .collect((Func0<ArrayList<com.wezom.ulcv2.net.models.Message>>) ArrayList::new,
                        (messages, messagesResponse) -> messages.addAll(messagesResponse.getMessages()))
                .subscribe(messages -> {
                    if (!messages.isEmpty()) {
                        Message.updateMessages(mDialogId, messages);
                        mBus.post(new MessagesUpdatedEvent());
                    }
                });

    }

    @Override
    protected void onCancel(int cancelReason) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

}
