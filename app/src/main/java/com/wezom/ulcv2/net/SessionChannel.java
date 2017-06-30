package com.wezom.ulcv2.net;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 29.07.2016
 * Time: 17:08
 */

public interface SessionChannel {

    void connect(String token, int sessionId);

    void leave();

    void disconnect();

    void follow(int userId);

    void sendPrivateMessage(String message, int userId);

    void sendMessage(String message);
}
