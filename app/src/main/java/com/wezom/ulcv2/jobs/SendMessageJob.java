package com.wezom.ulcv2.jobs;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.wezom.ulcv2.events.MessagesUpdatedEvent;
import com.wezom.ulcv2.injection.component.ApplicationComponent;
import com.wezom.ulcv2.mvp.model.Message;
import com.wezom.ulcv2.net.ProfileChannelHandler;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by sivolotskiy.v on 11/08/2016.
 */

public class SendMessageJob extends BaseJob {

    @Inject
    transient ProfileChannelHandler mWebSocketManager;

    @Inject
    transient EventBus mBus;

    private int mPersonId;
    private String mMessageText;
    private long mMessageId;

    public SendMessageJob(String messageText, int personId) {
        super(new Params(BACKGROUND_PRIORITY).setGroupId("send-message").requireNetwork().persist());
        mPersonId = personId;
        mMessageText = messageText;
        saveMessage();
    }

    private void saveMessage() {
        Message message = Message.createMessageInDialogId(mPersonId);
        message.setText(mMessageText);
        message.setSent(false);
        message.setIsOut(1);
        message.setPostedTimestamp(System.currentTimeMillis() / 1000);
        message.save();
        mMessageId = message.getId();
    }

    @Override
    public void inject(ApplicationComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public void onAdded() {
        mBus.post(new MessagesUpdatedEvent());
    }

    @Override
    public void onRun() throws Throwable {
        mWebSocketManager.sendMessage(mMessageText, mPersonId);
        Message message = Message.load(Message.class, mMessageId);
        message.setSent(true);
        message.save();
        mBus.post(new MessagesUpdatedEvent());
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(20, 1000);
    }

    @Override
    protected void onCancel(int i) {
        Message.delete(Message.class, mMessageId);
        mBus.post(new MessagesUpdatedEvent());
    }
}
