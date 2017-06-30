package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.birbit.android.jobqueue.JobManager;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.exception.WebsocketException;
import com.wezom.ulcv2.jobs.FetchMessagesJob;
import com.wezom.ulcv2.mvp.model.Dialog;
import com.wezom.ulcv2.mvp.model.Message;
import com.wezom.ulcv2.mvp.view.DialogFragmentView;
import com.wezom.ulcv2.net.ProfileChannelHandler;

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
 * Created by kartavtsev.s on 25.01.2016.
 */

@InjectViewState
public class DialogFragmentPresenter extends BasePresenter<DialogFragmentView> {

    @Inject
    JobManager mJobManager;
    @Inject
    ProfileChannelHandler mProfileChannelHandler;

    public DialogFragmentPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void loadMessages(int dialogId) {
        getViewState().showLoading(false);

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

        getViewState().setData(finalMessages);
        getViewState().showContent();
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

    private ArrayList<Message> loadAndTransformMessages(int dialogId) {
        TreeMap<Long, ArrayList<Message>> groupedMessages = new TreeMap<>();
        ArrayList<Message> finalMessages = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        for (Message message : Message.getMessages(dialogId)) {
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

        return finalMessages;
    }
}
