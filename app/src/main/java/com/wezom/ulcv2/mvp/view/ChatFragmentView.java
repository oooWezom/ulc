package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.mvp.model.NewMessage;

/**
 * Created by Zorin.A on 23.09.2016.
 */
public interface ChatFragmentView extends BaseFragmentView {
    void onMessageSent(NewMessage newMessage);

    void onChatClear();

    void showCloseSessionDialog(int title, int message);
}
