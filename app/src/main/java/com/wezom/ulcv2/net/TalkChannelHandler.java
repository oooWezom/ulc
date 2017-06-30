package com.wezom.ulcv2.net;

import android.content.Context;
import android.util.Log;

import com.wezom.ulcv2.events.CancelFindTalkPartnerEvent;
import com.wezom.ulcv2.events.CreateTalkEvent;
import com.wezom.ulcv2.events.DonateMessageEvent;
import com.wezom.ulcv2.events.FindTalkPartnerEvent;
import com.wezom.ulcv2.events.FollowEvent;
import com.wezom.ulcv2.events.InviteToTalkRequestEvent;
import com.wezom.ulcv2.events.InviteToTalkResponseEvent;
import com.wezom.ulcv2.events.InviteToTalkSentResultEvent;
import com.wezom.ulcv2.events.NewFollowerEvent;
import com.wezom.ulcv2.events.OnErrorEvent;
import com.wezom.ulcv2.events.OnFindTalkResponseEvent;
import com.wezom.ulcv2.events.SendDonateMessageEvent;
import com.wezom.ulcv2.events.StreamClosedByReportsEvent;
import com.wezom.ulcv2.events.SwitchTalkResponseEvent;
import com.wezom.ulcv2.events.TalkAddedEvent;
import com.wezom.ulcv2.events.TalkClosedEvent;
import com.wezom.ulcv2.events.TalkLikesLocalEvent;
import com.wezom.ulcv2.events.TalkLikesResponseEvent;
import com.wezom.ulcv2.events.TalkRemovedEvent;
import com.wezom.ulcv2.events.TalkSpectatorConnectedEvent;
import com.wezom.ulcv2.events.TalkSpectatorDisconnectedEvent;
import com.wezom.ulcv2.events.TalkStateEvent;
import com.wezom.ulcv2.events.TalkStreamerConnectedEvent;
import com.wezom.ulcv2.events.TalkStreamerDisconnectedEvent;
import com.wezom.ulcv2.events.TalkStreamerReconnectedEvent;
import com.wezom.ulcv2.events.UnFollowEvent;
import com.wezom.ulcv2.events.UpdateTalkDateEvent;
import com.wezom.ulcv2.events.game.NewMessageEvent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.NewMessage;
import com.wezom.ulcv2.net.models.User;
import com.wezom.ulcv2.net.models.requests.FindTalkPartnerRequest;
import com.wezom.ulcv2.net.models.requests.websocket.CancelFindTalkSession;
import com.wezom.ulcv2.net.models.requests.websocket.CreateTalkResponse;
import com.wezom.ulcv2.net.models.requests.websocket.DetachTalkRequest;
import com.wezom.ulcv2.net.models.requests.websocket.DonateMessageResponse;
import com.wezom.ulcv2.net.models.requests.websocket.FindTalkRequest;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalk;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkRequest;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkResponse;
import com.wezom.ulcv2.net.models.requests.websocket.ReportUserSocketRequest;
import com.wezom.ulcv2.net.models.requests.websocket.Request;
import com.wezom.ulcv2.net.models.requests.websocket.SendDonateMessageRequest;
import com.wezom.ulcv2.net.models.requests.websocket.SessionMessageRequest;
import com.wezom.ulcv2.net.models.requests.websocket.SwitchTalkRequest;
import com.wezom.ulcv2.net.models.requests.websocket.TalkLikesRequest;
import com.wezom.ulcv2.net.models.requests.websocket.TalkLikesResponse;
import com.wezom.ulcv2.net.models.requests.websocket.UpdateTalkDataRequest;
import com.wezom.ulcv2.net.models.responses.websocket.CancelFindTalkPartner;
import com.wezom.ulcv2.net.models.responses.websocket.ErrorResponse;
import com.wezom.ulcv2.net.models.responses.websocket.FindTalkPartnerResponse;
import com.wezom.ulcv2.net.models.responses.websocket.FindTalkResponse;
import com.wezom.ulcv2.net.models.responses.websocket.FollowEchoResponse;
import com.wezom.ulcv2.net.models.responses.websocket.NewFollowerResponse;
import com.wezom.ulcv2.net.models.responses.websocket.Response;
import com.wezom.ulcv2.net.models.responses.websocket.SendDonateMessageResponse;
import com.wezom.ulcv2.net.models.responses.websocket.SwitchTalkResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkAddedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkClosedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkMessageResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkRemovedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkSpectatorConnectedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkSpectatorDisconnectedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkStateResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkStreamerConnected;
import com.wezom.ulcv2.net.models.responses.websocket.TalkStreamerDisconnected;
import com.wezom.ulcv2.net.models.responses.websocket.TalkStreamerReconnected;
import com.wezom.ulcv2.net.models.responses.websocket.UnFollowEchoResponse;
import com.wezom.ulcv2.net.models.responses.websocket.UpdateTalkDataResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;

import static com.wezom.ulcv2.common.Constants.WEBSOCKET_TALK_URL;

/**
 * Created by zorin.a on 25.07.2016.
 */
public class TalkChannelHandler extends ReconnectingChannelHandler implements SessionChannel {

    public static final String TAG = "TALK";
    PreferenceManager preferenceManager;
    Context context;

    public TalkChannelHandler(EventBus bus, @ApplicationContext Context context, PreferenceManager preferenceManager) {
        super(bus, context, TAG);
        this.context = context;
        this.preferenceManager = preferenceManager;
    }

    @Override
    protected String getConnectUrl() {
        String baseUrl = preferenceManager.getBaseUrl(context).getSocketUrl();
        return baseUrl + WEBSOCKET_TALK_URL;
    }

    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {
        super.onOpen(webSocket, response);
        createPingObservable(20);
    }

    @Override
    public void onFailure(IOException e, okhttp3.Response response) {
        super.onFailure(e, response);
        removePingObservable();
    }

    @Override
    public void disconnect() {
        removePingObservable();
        super.disconnect();
    }

    @Override
    public void onClose(int code, String reason) {
        super.onClose(code, reason);

    }

    @Override
    public void onMessage(ResponseBody message) throws IOException {
        String jsonMessage = message.string();
        Log.e("SOCKET_TALK", jsonMessage);
        Response response = mGson.fromJson(jsonMessage, Response.class);
        response.setSelf(jsonMessage);
        if (response.getType() != null) {
            switch (response.getType()) {
                case ERROR:
                    Log.v("SOCKET_TALK", "ON_ERROR");
                    ErrorResponse errorResponse = getResponse(ErrorResponse.class, response);
                    mBus.post(new OnErrorEvent(errorResponse));
                    break;

                case TALK_STATE:
                    Log.v("SOCKET_TALK", "TALK_STATE");
                    TalkStateResponse talkStateResponse = getResponse(TalkStateResponse.class, response);
                    mBus.post(new TalkStateEvent(talkStateResponse.getTalk()));
                    break;
                case TALK_ADDED:
                    Log.v("SOCKET_TALK", "TALK_ADDED");
                    TalkAddedResponse talkAddedResponse = getResponse(TalkAddedResponse.class, response);
                    mBus.post(new TalkAddedEvent(talkAddedResponse));
                    break;
                case TALK_REMOVED:
                    Log.v("SOCKET_TALK", "TALK_REMOVED");
                    TalkRemovedResponse talkRemovedResponse = getResponse(TalkRemovedResponse.class, response);
                    mBus.post(new TalkRemovedEvent(talkRemovedResponse));
                    break;
                case TALK_CLOSED:
                    Log.v("SOCKET_TALK", "TALK_CLOSED");
                    TalkClosedResponse talkClosedResponse = getResponse(TalkClosedResponse.class, response);
                    mBus.post(new TalkClosedEvent(talkClosedResponse));
                    break;
                case FIND_TALK:
                    Log.v("SOCKET_TALK", "FIND_TALK");
                    FindTalkResponse findTalkResponse = getResponse(FindTalkResponse.class, response);
                    mBus.post(new OnFindTalkResponseEvent(findTalkResponse));
                    break;
                case SWITCH_TALK:
                    Log.v("SOCKET_TALK", "SWITCH_TALK");
                    SwitchTalkResponse switchTalkResponse = getResponse(SwitchTalkResponse.class, response);
                    mBus.post(new SwitchTalkResponseEvent(switchTalkResponse));
                    break;
                case UPDATE_TALK_DATA:
                    Log.v("SOCKET_TALK", "UPDATE_TALK_DATA");
                    UpdateTalkDataResponse updateTalkDataResponse = getResponse(UpdateTalkDataResponse.class, response);
                    mBus.post(new UpdateTalkDateEvent(updateTalkDataResponse));
                    break;
                case FIND_TALK_PARTNER:
                    Log.v("SOCKET_TALK", "FIND_TALK_PARTNER");
                    FindTalkPartnerResponse findTalkPartnerResponse = getResponse(FindTalkPartnerResponse.class, response);
                    mBus.post(new FindTalkPartnerEvent(findTalkPartnerResponse));
                    break;
                case CANCEL_FIND_TALK_PARTNER:
                    Log.v("SOCKET_TALK", "CANCEL_FIND_TALK_PARTNER");
                    CancelFindTalkPartner cancelFindTalkPartner = getResponse(CancelFindTalkPartner.class, response);
                    mBus.post(new CancelFindTalkPartnerEvent(cancelFindTalkPartner));
                    break;
                case TALK_MESSAGE:
                    Log.v("SOCKET_TALK", "TALK_MESSAGE");
                    TalkMessageResponse talkMessageResponse = getResponse(TalkMessageResponse.class, response);

                    NewMessage.User user = new NewMessage.User();
                    user.setId(talkMessageResponse.getUser().getId());
                    user.setName(talkMessageResponse.getUser().getName());

                    NewMessage newMessage = new NewMessage();
                    newMessage.setText(talkMessageResponse.getText());
                    newMessage.setUser(user);

                    mBus.post(new NewMessageEvent(newMessage));
                    break;
                case SEND_DONATE_MESSAGE:
                    Log.v("SOCKET_TALK", "SEND_DONATE_MESSAGE");
                    SendDonateMessageResponse sendDonateMessageResponse = getResponse(SendDonateMessageResponse.class, response);
                    mBus.post(new SendDonateMessageEvent(sendDonateMessageResponse));
                    break;
                case DONATE_MESSAGE:
                    Log.v("SOCKET_TALK", "DONATE_MESSAGE");
                    DonateMessageResponse donateMessageResponse = getResponse(DonateMessageResponse.class, response);
                    mBus.post(new DonateMessageEvent(donateMessageResponse));
                    break;
                case TALK_LIKES:
                    Log.v("SOCKET_TALK", "TALK_LIKES");
                    TalkLikesResponse talkLikesResponse = getResponse(TalkLikesResponse.class, response);
                    mBus.post(new TalkLikesResponseEvent(talkLikesResponse));
                    break;
                case TALK_STREAMER_CONNECTED:
                    Log.v("SOCKET_TALK", "TALK_STREAMER_CONNECTED");
                    TalkStreamerConnected talkStreamerConnected = getResponse(TalkStreamerConnected.class, response);
                    mBus.post(new TalkStreamerConnectedEvent(talkStreamerConnected));
                    break;
                case TALK_STREAMER_RECONNECTED:
                    Log.v("SOCKET_TALK", "TALK_STREAMER_RECONNECTED");
                    TalkStreamerReconnected talkStreamerReconnected = getResponse(TalkStreamerReconnected.class, response);
                    mBus.post(new TalkStreamerReconnectedEvent(talkStreamerReconnected));
                    break;
                case STREAMER_DISCONNECTED:
                    Log.v("SOCKET_TALK", "TALK_STREAMER_DISCONNECTED");
                    TalkStreamerDisconnected talkStreamerDisconnected = getResponse(TalkStreamerDisconnected.class, response);
                    mBus.post(new TalkStreamerDisconnectedEvent(talkStreamerDisconnected));
                    break;
                case SPECTATOR_CONNECTED:
                    Log.v("SOCKET_TALK", "SPECTATOR_CONNECTED");
                    TalkSpectatorConnectedResponse talkSpectatorConnectedResponse = getResponse(TalkSpectatorConnectedResponse.class, response);
                    mBus.post(new TalkSpectatorConnectedEvent(talkSpectatorConnectedResponse));
                    break;
                case INVITE_TO_TALK:
                    Log.v("SOCKET_TALK", "INVITE_TO_TALK");
                    com.wezom.ulcv2.net.models.responses.websocket.InviteToTalk inviteToTalk = getResponse(com.wezom.ulcv2.net.models.responses.websocket.InviteToTalk.class, response);
                    mBus.post(new InviteToTalkSentResultEvent(inviteToTalk));
                    break;
                case INVITE_TO_TALK_REQUEST:
                    Log.v("SOCKET_TALK", "INVITE_TO_TALK_REQUEST");
                    InviteToTalkRequest inviteToTalkRequest = mGson.fromJson(response.getSelf(),
                            InviteToTalkRequest.class);
                    mBus.post(new InviteToTalkRequestEvent(inviteToTalkRequest));
                    break;
                case INVITE_TO_TALK_RESPONSE:
                    Log.v("SOCKET_TALK", "INVITE_TO_TALK_RESPONSE");
                    com.wezom.ulcv2.net.models.responses.websocket.InviteToTalkResponse inviteToTalkResponse = getResponse(com.wezom.ulcv2.net.models.responses.websocket.InviteToTalkResponse.class, response);
                    mBus.post(new InviteToTalkResponseEvent(inviteToTalkResponse));
                    break;
                case SPECTATOR_DISCONNECTED:
                    Log.v("SOCKET_TALK", "SPECTATOR_DISCONNECTED");
                    TalkSpectatorDisconnectedResponse talkSpectatorDisconnectedResponse = getResponse(TalkSpectatorDisconnectedResponse.class, response);
                    mBus.post(new TalkSpectatorDisconnectedEvent(talkSpectatorDisconnectedResponse));
                    break;
                case FOLLOW_ECHO:
                    Log.v("SOCKET_TALK", "FOLLOW_ECHO");
                    FollowEchoResponse echoResponse = mGson.fromJson(response.getSelf(),
                            FollowEchoResponse.class);
                    mBus.post(new FollowEvent(echoResponse.isResult(), echoResponse.getFollowId(), echoResponse.getUser()));
                    break;
                case UNFOLLOW_ECHO:
                    Log.v("SOCKET_TALK", "UNFOLLOW_ECHO");
                    UnFollowEchoResponse unFollowEchoResponse = mGson.fromJson(response.getSelf(),
                            UnFollowEchoResponse.class);
                    mBus.post(new UnFollowEvent(unFollowEchoResponse.isResult(),
                            unFollowEchoResponse.getUnFollowId()));
                    break;
                case NEW_FOLLOWER:
                    Log.v("SOCKET_TALK", "NEW_FOLLOWER");
                    NewFollowerResponse followerResponse = mGson.fromJson(response.getSelf(),
                            NewFollowerResponse.class);
                    mBus.post(new NewFollowerEvent(followerResponse.getUser()));
                    break;

                case STREAM_CLOSED_BY_REPORTS:
                    Log.v("SOCKET_TALK", "STREAM_CLOSED_BY_REPORTS");
                    mBus.post(new StreamClosedByReportsEvent());
                    break;

                case CREATE_TALK:
                    Log.v("SOCKET_PROFILE", "CREATE_TALK");
                    CreateTalkResponse createTalkResponse = getResponse(CreateTalkResponse.class, response);
                    mBus.post(new CreateTalkEvent(createTalkResponse.getTalk(), createTalkResponse.getMessage()));
                    break;
            }
        }
    }

    @Override
    public void leave() {
        Request request = new Request();
        request.setData(RequestType.LEAVE_TALK);
        sendMessage(buildRequestBody(request));
    }

    @Override
    public void sendMessage(String message) {
        SessionMessageRequest sessionMessageRequest = new SessionMessageRequest(message);
        sessionMessageRequest.setData(RequestType.TALK_MESSAGE);
        sendMessage(buildRequestBody(sessionMessageRequest));
    }

    public void inviteToTalkRequest(User user, int category, int timeToExpire) {
        InviteToTalkRequest request = new InviteToTalkRequest(user, category, timeToExpire);
        request.setData(RequestType.INVITE_TO_TALK_REQUEST);
        sendMessage(buildRequestBody(request));
    }

    public void findTalk(int category, int except) { //parameters optional. if not needed - send -1 as argument.
        FindTalkRequest findTalkRequest = new FindTalkRequest();
        if (category > -1) {
            findTalkRequest.setCategory(category);
        }
        if (except > -1) {
            findTalkRequest.setExcept(except);
        }
        findTalkRequest.setData(RequestType.FIND_TALK);
        sendMessage(buildRequestBody(findTalkRequest));
    }

    public void findTalkPartner() {
        FindTalkPartnerRequest request = new FindTalkPartnerRequest();
        request.setData(RequestType.FIND_TALK_PARTNER);
        sendMessage(buildRequestBody(request));
    }

    public void cancelFindTalkPartner() {
        CancelFindTalkSession cancelFindTalkSession = new CancelFindTalkSession();
        cancelFindTalkSession.setData(RequestType.CANCEL_FIND_TALK_PARTNER);
        sendMessage(buildRequestBody(cancelFindTalkSession));
    }

    public void sendDonateMessage(String text) {
        SendDonateMessageRequest sendDonateMessageRequest = new SendDonateMessageRequest(text);
        sendDonateMessageRequest.setData(RequestType.SEND_DONATE_MESSAGE);
        sendMessage(buildRequestBody(sendDonateMessageRequest));
    }

    public void detachTalk() {
        DetachTalkRequest detachTalkRequest = new DetachTalkRequest();
        detachTalkRequest.setData(RequestType.DETACH_TALK);
        sendMessage(buildRequestBody(detachTalkRequest));
    }

    public void switchTalk(int talkId) {
        SwitchTalkRequest switchTalkRequest = new SwitchTalkRequest(talkId);
        switchTalkRequest.setData(RequestType.SWITCH_TALK);
        sendMessage(buildRequestBody(switchTalkRequest));
    }

    public void updateTalkData(String name) {
        UpdateTalkDataRequest updateTalkDataRequest = new UpdateTalkDataRequest();
        updateTalkDataRequest.setData(RequestType.UPDATE_TALK_DATA);
        updateTalkDataRequest.setName(name);
        sendMessage(buildRequestBody(updateTalkDataRequest));
    }

    public void sendLocalLike(int talkId, int like) {
        TalkLikesResponse talkLikesResponse = new TalkLikesResponse();
        talkLikesResponse.setTalkId(talkId);
        talkLikesResponse.setLikes(like);
        talkLikesResponse.setType(ResponseType.TALK_LIKES);
        mBus.post(new TalkLikesLocalEvent(talkLikesResponse));
    }

    public void sendNetworkLike(int talkId, int likeCount) {
        TalkLikesRequest talkLikesRequest = new TalkLikesRequest();
        talkLikesRequest.setTalkId(talkId);
        talkLikesRequest.setLikes(likeCount);
        talkLikesRequest.setData(RequestType.TALK_LIKES);
        sendMessage(buildRequestBody(talkLikesRequest));
    }

    public void inviteToTalk(int profileId, int categoryId) {
        InviteToTalk request = new InviteToTalk();
        request.setRecipientId(profileId);
        request.setCategoryId(categoryId);
        request.setData(RequestType.INVITE_TO_TALK);
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

    public void reportUser(int talkId, int reportCategory) {
        ReportUserSocketRequest request = new ReportUserSocketRequest();
        request.setData(RequestType.REPORT_USER_BEHAVIOR);
        request.setSessionId(talkId);
        request.setCategoryId(reportCategory);
        sendMessage(buildRequestBody(request));
    }
}
