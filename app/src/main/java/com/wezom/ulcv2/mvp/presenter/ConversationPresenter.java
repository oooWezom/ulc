package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.birbit.android.jobqueue.JobManager;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.MessageEchoEvent;
import com.wezom.ulcv2.events.ProfileClickedEvent;
import com.wezom.ulcv2.events.UpdateDateRequestEvent;
import com.wezom.ulcv2.exception.WebsocketException;
import com.wezom.ulcv2.jobs.FetchMessagesJob;
import com.wezom.ulcv2.mvp.model.Dialog;
import com.wezom.ulcv2.mvp.model.Message;
import com.wezom.ulcv2.mvp.view.ConversationView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.ProfileChannelHandler;
import com.wezom.ulcv2.net.models.requests.DialogsRequest;
import com.wezom.ulcv2.net.models.requests.MessagesRequest;
import com.wezom.ulcv2.net.models.requests.MessagesSinceRequest;
import com.wezom.ulcv2.net.models.requests.ProfileRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

/**
 * Created by Sivolotskiy.v on 16.06.2016.
 */

@InjectViewState
public class ConversationPresenter extends BasePresenter<ConversationView> {


    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;
    @Inject
    JobManager mJobManager;
    @Inject
    ProfileChannelHandler mProfileChannelHandler;

    public ConversationPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }


    @Override
    public void attachView(ConversationView view) {
        super.attachView(view);
        mBus.register(this);
    }

    @Override
    public void detachView(ConversationView view) {
        super.detachView(view);
        mBus.unregister(this);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public void loadMessages(int dialogId) {
        getViewState().showLoading(false);

        mApiManager.getMessages(new MessagesRequest(dialogId, 0, 500)).subscribe(messagesResponse -> {
            transformMessages(dialogId, messagesResponse.getMessages());
        }, throwable -> {
            ArrayList<Message> finalMessages = loadAndTransformMessages(dialogId);

            ArrayList<Integer> messageIds = new ArrayList<>();

            for (Message message : finalMessages) {
                if (!message.isRead() && message.getServerId() > 0 && message.getIsOut() == 0) {
                    messageIds.add(message.getServerId());
                }
            }

            if (messageIds.size() > 0) {
                try {
                    mProfileChannelHandler.readMessages(dialogId, messageIds);
                    Message.readMessages(messageIds);
                } catch (WebsocketException e) {
                    Message.unreadMessages(messageIds);
                }
            }

            if (!finalMessages.isEmpty()) {

                getViewState().setData(finalMessages);
                getViewState().showContent();
            }
        });
    }

    public void loadConversationName(int dialogId) {
        String partnerName = Dialog.getByServerId(dialogId).getPartner().getName();
        getViewState().setDialogName(partnerName);
    }

    public void sendMessage(String message, int personId) {
        try {
            mProfileChannelHandler.sendMessage(message, personId);
        } catch (WebsocketException e) {
            getViewState().showError(e, false);
        }
    }

    public void fetchMessages(int dialogId) {
        mJobManager.addJobInBackground(new FetchMessagesJob(dialogId));
    }

    public void onBackPressed() {
        onRefresh();
        mBus.post(new BackEvents());
    }

    public void readMessage(int dialogId, int messageId) {
        ArrayList<Integer> messagesIds = new ArrayList<>();

        messagesIds.add(messageId);

        mProfileChannelHandler.readMessages(dialogId, messagesIds);
        Message.readMessages(messagesIds);
    }

    public void onRefresh() {
        mBus.post(new UpdateDateRequestEvent());
    }

    public void onAvatarPressed(int userId) {
        mBus.post(new ProfileClickedEvent(userId));
    }

    public void fetchPartnerAvatar(int partnerId) {
        DialogsRequest request = new DialogsRequest();
        request.setDialogId(partnerId);
        mApiManager.getProfile(new ProfileRequest(partnerId)).subscribe(profileResponse -> {
            getViewState().setPartnerAvatar(profileResponse.getProfile().getAvatar());
        }, throwable -> {
            /*nothing to do, bad luck*/
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEchoEvents(MessageEchoEvent event) {
        loadMessages(event.getEchoResponse().getRecipientId());
    }

    private ArrayList<Message> loadAndTransformMessages(int dialogId) {
        TreeMap<Long, ArrayList<Message>> groupedMessages = new TreeMap<>();
        ArrayList<Message> finalMessages = new ArrayList<>();

        if (Message.getMessages(dialogId).isEmpty()) {
            mApiManager.getMessagesSince(new MessagesSinceRequest(dialogId, 0, 100, 0)).subscribe(messagesResponse -> {
                transformMessages(dialogId, messagesResponse.getMessages());
            }, throwable -> {
                throwable.getLocalizedMessage();
            });
        }

        for (Message message : Message.getMessages(dialogId)) {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String formatedDate = formatter.format(new Date(message.getPostedTimestamp() * 1000L));
            Date parse;
            try {
                parse = formatter.parse(formatedDate);
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse date");
            }

            if (groupedMessages.get(parse.getTime()) != null) {
                groupedMessages.get(parse.getTime()).add(message);
            } else {
                ArrayList<Message> messages = new ArrayList<>();
                messages.add(message);
                groupedMessages.put(parse.getTime(), messages);
            }
        }


        for (Long dateTime : groupedMessages.descendingKeySet()) {
            Message message = new Message();
            message.setPostedTimestamp(dateTime / 1000L);
            message.setIsOut(2); //magic! 2 = date
            List<Message> messages = groupedMessages.get(dateTime);

            finalMessages.addAll(messages);
            finalMessages.add(message);
        }

        Collections.reverse(finalMessages);

        onRefresh();
        return finalMessages;
    }

    private void transformMessages(int dialogId, ArrayList<com.wezom.ulcv2.net.models.Message> messages) {
        TreeMap<Long, ArrayList<Message>> groupedMessages = new TreeMap<>();
        ArrayList<Message> finalMessages = new ArrayList<>();


        for (com.wezom.ulcv2.net.models.Message message : messages) {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String formatedDate = formatter.format(new Date(message.getPostedTimestamp() * 1000L));
            Date parse;
            try {
                parse = formatter.parse(formatedDate);
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse date");
            }

            if (groupedMessages.get(parse.getTime()) != null) {
                Dialog dialog = new Dialog();
                dialog.setSenderId(dialogId);
                groupedMessages.get(parse.getTime()).add(new Message(message.getId(), dialog, message.getMessage(),
                        message.getPostedTimestamp(), message.getPostedDate(), message.getDeliveredTimestamp() != 0, message.getOut(), true));
            } else {
                ArrayList<Message> messagess = new ArrayList<>();
                Dialog dialog = new Dialog();
                dialog.setSenderId(dialogId);
                messagess.add(new Message(message.getId(), dialog, message.getMessage(),
                        message.getPostedTimestamp(), message.getPostedDate(), message.getDeliveredTimestamp() != 0, message.getOut(), true));
                groupedMessages.put(parse.getTime(), messagess);
            }
        }


        for (Long dateTime : groupedMessages.descendingKeySet()) {
            Message message = new Message();
            message.setPostedTimestamp(dateTime / 1000L);
            message.setIsOut(2); //magic! 2 = date
            List<Message> messages2 = groupedMessages.get(dateTime);
//            Collections.reverse(messages2);
            finalMessages.addAll(messages2);
            finalMessages.add(message);
        }

        Collections.reverse(finalMessages);

        onRefresh();
        ArrayList<Integer> messageIds = new ArrayList<>();

        for (Message message : finalMessages) {
            if (!message.isRead() && message.getServerId() > 0 && message.getIsOut() == 0) {
                messageIds.add(message.getServerId());
            }
        }

        if (messageIds.size() > 0) {
            try {
                mProfileChannelHandler.readMessages(dialogId, messageIds);
                Message.readMessages(messageIds);
            } catch (WebsocketException e) {
                Message.unreadMessages(messageIds);
            }
        }
        getViewState().setData(finalMessages);
        getViewState().showContent();
    }
}