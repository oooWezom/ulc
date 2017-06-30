package com.wezom.ulcv2.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.FailureConnectionEvent;
import com.wezom.ulcv2.events.OnShowToastEvent;
import com.wezom.ulcv2.exception.WebsocketException;
import com.wezom.ulcv2.net.models.requests.websocket.FollowRequest;
import com.wezom.ulcv2.net.models.requests.websocket.IntroduceRequest;
import com.wezom.ulcv2.net.models.requests.websocket.MessageRequest;
import com.wezom.ulcv2.net.models.responses.websocket.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.RequestBody;
import okhttp3.ws.WebSocket;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 29.07.2016
 * Time: 18:21
 */

@Accessors(prefix = "m")
public abstract class ReconnectingChannelHandler extends WebSocketChannelHandler {

    private static final String CLOSED_TAG = "closed";
    @Getter
    @Setter
    protected String mToken;
    protected EventBus mBus;
    protected Gson mGson;
    private Subscription subscription;

    public ReconnectingChannelHandler(EventBus bus, Context context, String tag) {
        super(tag);
        mBus = bus;
        mGson = new GsonBuilder().create();
    }

    protected abstract String getConnectUrl();

    public void connect(String token, int sessionId) {
        super.connect(getConnectUrl() + sessionId);
        mToken = token;
    }

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {
        super.onOpen(webSocket, response);
        mToken = TextUtils.isEmpty(mToken) ? mToken = null : mToken;
        IntroduceRequest request = new IntroduceRequest(mToken, Constants.API_PLATFORM_ID, Constants.API_VERSION);
        request.setData(RequestType.INTRODUCE);
        try {
            sendMessage(buildRequestBody(request));
        } catch (WebsocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(IOException e, okhttp3.Response response) {
        Log.v("SOCKET_RECONNECT " + TAG, "onFailure " + "exception: " + e.getMessage());
        mBus.post(new OnShowToastEvent(R.string.lost_connection));
        try {
            super.onFailure(e, response);
        } catch (IllegalStateException e1) {
            Log.v("SOCKET_RECONNECT " + TAG, "RectonnectChannelEvent()" + e1.getMessage());

        }
        Log.v("SOCKET_RECONNECT " + TAG, "onFailure " + "FailureConnectionEvent()");
        mBus.post(new FailureConnectionEvent());
    }

    @Override
    public void connect(String token) {
        super.connect(getConnectUrl());
        mToken = token;
    }

    @Override
    public void disconnect() throws IllegalStateException {
        isConnected = false;
        try {
            super.disconnect();
        } catch (IllegalStateException e) {
            Log.v("SOCKET_RECONNECT " + TAG, "IllegalStateException " + e.getMessage());
            destroySocket();
        }
    }

    @Override
    protected void sendMessage(RequestBody requestBody) {
        try {
            String s = mGson.toJson(requestBody, RequestBody.class);
            Log.v("SOCKET_ " + TAG, "sendMessage() " + s);
            super.sendMessage(requestBody);
        } catch (IllegalStateException e) {
            Log.v("SOCKET_RECONNECT " + TAG, "sendMessage() IllegalStateException " + e.getMessage());
            isConnected = false;
            if (e.getMessage().equals(CLOSED_TAG)) {
                Log.v("SOCKET_RECONNECT " + TAG, "sendMessage() IllegalStateException()- closed.");
            }else {
                mBus.post(new FailureConnectionEvent());
            }
        }
    }

    @Override
    protected void sendPing() {
        try {
            super.sendPing();
        } catch (IllegalStateException e) {
            removePingObservable();
        }
    }

    public void follow(int personId) {
        FollowRequest request = new FollowRequest(personId);
        request.setData(RequestType.FOLLOW);
        sendMessage(buildRequestBody(request));
    }

    public void sendPrivateMessage(String text, int personId) {
        MessageRequest request = new MessageRequest(text, personId);
        request.setData(RequestType.SEND_MESSAGE);
        sendMessage(buildRequestBody(request));
    }

    RequestBody buildRequestBody(Object object) {
        return RequestBody.create(WebSocket.TEXT, mGson.toJson(object));
    }

    protected <T extends com.wezom.ulcv2.net.models.responses.websocket.Response> T getResponse(Class<T> clazz, com.wezom.ulcv2.net.models.responses.websocket.Response response) {
        return mGson.fromJson(response.getSelf(), clazz);
    }

    protected void createPingObservable(int timeSec) {
        subscription = Observable.interval(timeSec, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    sendPing();
                }, throwable -> {
                    Log.v("SOCKET_RECONNECT " + TAG, throwable.getMessage());
                    if (subscription != null && !subscription.isUnsubscribed()) {
                        subscription.unsubscribe();
                    }
                });
    }

    protected void removePingObservable() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }


}
