package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.mvp.model.Message;

import java.util.ArrayList;

/**
 * Created by kartavtsev.s on 25.01.2016.
 */
public interface DialogFragmentView extends ListLceView<ArrayList<Message>> {
    void setNewMessages(ArrayList<Message> messages);

    void setDialogName(String partnerName);

}
