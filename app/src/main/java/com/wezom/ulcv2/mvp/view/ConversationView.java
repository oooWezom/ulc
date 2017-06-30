package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.mvp.model.Message;

import java.util.ArrayList;

/**
 * Created by Sivolotskiy.v on 16.06.2016.
 */
public interface ConversationView  extends ListLceView<ArrayList<Message>> {
    void setNewMessages(ArrayList<Message> messages);

    void setDialogName(String partnerName);

    void setPartnerAvatar(String avatar);
}