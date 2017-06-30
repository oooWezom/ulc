package com.wezom.ulcv2.net;

import android.content.Context;
import android.util.Log;

import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.InviteToTalkRequestEvent;
import com.wezom.ulcv2.events.InviteToTalkResponseEvent;
import com.wezom.ulcv2.events.InviteToTalkSentResultEvent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkRequest;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkResponse;
import com.wezom.ulcv2.net.models.requests.websocket.MessageRequest;
import com.wezom.ulcv2.net.models.responses.websocket.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;


public class ProfileChannelHandlerInTalk extends ReconnectingChannelHandler {
    public static final String TAG = "SOCKET_PROFILE_IN_TALK";
    PreferenceManager preferenceManager;
    Context context;

    public ProfileChannelHandlerInTalk(EventBus bus, @ApplicationContext Context context, PreferenceManager preferenceManager) {
        super(bus, context, TAG);
        this.context = context;
        this.preferenceManager = preferenceManager;
    }

    @Override
    protected String getConnectUrl() {
        String url = preferenceManager.getBaseUrl(context).getSocketUrl();
        return url + Constants.WEBSOCKET_PROFILE_URL;
    }

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {
        super.onOpen(webSocket, response);
        createPingObservable(20);
    }

    @Override
    public void onFailure(IOException e, okhttp3.Response response) {
        super.onFailure(e, response);
        Log.v(TAG, "onFailure() message:" + e.getMessage());
        removePingObservable();
    }

    @Override
    public void connect(String token) {
        Log.v(TAG, "connect() ");
        super.connect(token);
    }

    @Override
    public void disconnect() throws IllegalStateException {
        removePingObservable();
        Log.v(TAG, "disconnect()");
        super.disconnect();

    }

    @Override
    protected void sendMessage(RequestBody requestBody) {
        Log.v(TAG,"sendMessage text = " + requestBody.toString());
        super.sendMessage(requestBody);
    }

    @Override
    public void onMessage(ResponseBody message) throws IOException {
        String jsonMessage = message.string();
        Response response = mGson.fromJson(jsonMessage, Response.class);
        response.setSelf(jsonMessage);
        Log.v(TAG, jsonMessage);
        if (response.getType() != null) {
            switch (response.getType()) {
                case INVITE_TO_TALK:
                    Log.v(TAG, "INVITE_TO_TALK");
                    com.wezom.ulcv2.net.models.responses.websocket.InviteToTalk inviteToTalk = getResponse(com.wezom.ulcv2.net.models.responses.websocket.InviteToTalk.class, response);
                    mBus.post(new InviteToTalkSentResultEvent(inviteToTalk));
                    break;
                case INVITE_TO_TALK_REQUEST:
                    Log.v(TAG, "INVITE_TO_TALK_REQUEST");
                    InviteToTalkRequest inviteToTalkRequest = mGson.fromJson(response.getSelf(),
                            InviteToTalkRequest.class);
                    mBus.post(new InviteToTalkRequestEvent(inviteToTalkRequest));
                    Log.v(TAG, "hash = "+ this.hashCode());
                    break;
                case INVITE_TO_TALK_RESPONSE:
                    Log.v(TAG, "INVITE_TO_TALK_RESPONSE");
                    com.wezom.ulcv2.net.models.responses.websocket.InviteToTalkResponse inviteToTalkResponse = getResponse(com.wezom.ulcv2.net.models.responses.websocket.InviteToTalkResponse.class, response);
                    mBus.post(new InviteToTalkResponseEvent(inviteToTalkResponse));
                    break;
            }
        }
    }

    public void sendMessage(String text, int personId) {
        MessageRequest request = new MessageRequest(text, personId);
        request.setData(RequestType.SEND_MESSAGE);
        sendMessage(buildRequestBody(request));
    }

    public void sendInvite2TalkResponse(int sender, int result, int delay) {
        InviteToTalkResponse inviteToTalkResponse = new InviteToTalkResponse();
        inviteToTalkResponse.setData(RequestType.INVITE_TO_TALK_RESPONSE);
        inviteToTalkResponse.setResult(result);
        inviteToTalkResponse.setSenderId(sender);

        if (result == InviteToTalkResponseEvent.DO_NOT_DISTURB) {
            inviteToTalkResponse.setTimeDoNotDisturb(delay * 60); // seconds have to be minutes
        }
        sendMessage(buildRequestBody(inviteToTalkResponse));
    }
}
