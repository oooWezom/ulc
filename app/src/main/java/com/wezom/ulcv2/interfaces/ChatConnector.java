package com.wezom.ulcv2.interfaces;

import com.wezom.ulcv2.mvp.model.NewMessage;

/**
 * Created: Zorin A.
 * Date: 23.09.2016.
 */

public interface ChatConnector {
    void sendNewMessage(NewMessage newMessage);

    void clearChat();

    void setSpectators(int spectators);
}
