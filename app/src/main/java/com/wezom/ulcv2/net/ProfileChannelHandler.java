package com.wezom.ulcv2.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.CreateTalkEvent;
import com.wezom.ulcv2.events.ErrorMessageEvent;
import com.wezom.ulcv2.events.FollowEvent;
import com.wezom.ulcv2.events.GameCreatedEvent;
import com.wezom.ulcv2.events.GameFoundEvent;
import com.wezom.ulcv2.events.InviteToTalkRequestEvent;
import com.wezom.ulcv2.events.InviteToTalkResponseEvent;
import com.wezom.ulcv2.events.InviteToTalkSentResultEvent;
import com.wezom.ulcv2.events.MessageEchoEvent;
import com.wezom.ulcv2.events.NewFollowerEvent;
import com.wezom.ulcv2.events.NewMessageEvent;
import com.wezom.ulcv2.events.NewNotificationEvent;
import com.wezom.ulcv2.events.SearchGameEvent;
import com.wezom.ulcv2.events.TalkAddedEvent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.net.models.Session;
import com.wezom.ulcv2.net.models.requests.websocket.CreateTalkRequest;
import com.wezom.ulcv2.net.models.requests.websocket.CreateTalkResponse;
import com.wezom.ulcv2.net.models.requests.websocket.FollowAcknowledgeRequest;
import com.wezom.ulcv2.net.models.requests.websocket.FollowRequest;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalk;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkRequest;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkResponse;
import com.wezom.ulcv2.net.models.requests.websocket.MessageRequest;
import com.wezom.ulcv2.net.models.requests.websocket.ReadMessageRequest;
import com.wezom.ulcv2.net.models.requests.websocket.Request;
import com.wezom.ulcv2.net.models.requests.websocket.SearchGameRequest;
import com.wezom.ulcv2.net.models.requests.websocket.SearchWatchGameRequest;
import com.wezom.ulcv2.net.models.requests.websocket.UnFollowRequest;
import com.wezom.ulcv2.net.models.responses.websocket.FollowEchoResponse;
import com.wezom.ulcv2.net.models.responses.websocket.GameCreatedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.GameFoundResponse;
import com.wezom.ulcv2.net.models.responses.websocket.MessageEchoResponse;
import com.wezom.ulcv2.net.models.responses.websocket.NewFollowerResponse;
import com.wezom.ulcv2.net.models.responses.websocket.NewMessageResponse;
import com.wezom.ulcv2.net.models.responses.websocket.NotificationResponse;
import com.wezom.ulcv2.net.models.responses.websocket.Response;
import com.wezom.ulcv2.net.models.responses.websocket.SearchGamesResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkAddedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.UnFollowEchoResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;

/**
 * Created by kartavtsev.s on 16.02.2016.
 */
public class ProfileChannelHandler extends ReconnectingChannelHandler {
    public static final String TAG = "PROFILE";
    private PreferenceManager preferenceManager;
    private Context context;

    public ProfileChannelHandler(EventBus bus, @ApplicationContext Context context, PreferenceManager preferenceManager) {
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
        Log.v("SOCKET_PROFILE", "onFailure() message:" + e.getMessage());
        removePingObservable();
    }

    @Override
    public void connect(String token) {
        Log.v("SOCKET_PROFILE", "connect() ");
        super.connect(token);
    }

    @Override
    public void disconnect() throws IllegalStateException {
        removePingObservable();
        Log.v("SOCKET_PROFILE", "disconnect()");
        super.disconnect();

    }

    @Override
    protected void sendMessage(RequestBody requestBody) {
        super.sendMessage(requestBody);
    }

    public void follow(int personId) {
        FollowRequest request = new FollowRequest(personId);
        request.setData(RequestType.FOLLOW);
        sendMessage(buildRequestBody(request));
    }

    @Override
    public void onMessage(ResponseBody message) throws IOException {
        String jsonMessage = message.string();
        Response response = mGson.fromJson(jsonMessage, Response.class);
        response.setSelf(jsonMessage);
        Log.v("SOCKET_PROFILE", jsonMessage);
        if (response.getType() != null) {
            switch (response.getType()) {
                case ERROR:
                    mBus.post(new ErrorMessageEvent(R.string.error));
                    break;
                case LOOK_FOR_GAME_RESULT: //Looking for game result
                    SearchGamesResponse searchResponse = mGson.fromJson(response.getSelf(),
                            SearchGamesResponse.class);
                    switch (searchResponse.getResult()) {
                        case ADDED_TO_QUEUE:
                            Log.v("SOCKET_PROFILE", "ADDED_TO_QUEUE");
                            mBus.post(new SearchGameEvent(searchResponse));
                            break;
                        case PLAYER_DISCONNECTED:
                            Log.v("SOCKET_PROFILE", "PLAYER_DISCONNECTED");
                            GameCreatedResponse gameCreatedResponse = mGson.fromJson(response.getSelf(),
                                    GameCreatedResponse.class);
                            Session session = gameCreatedResponse.getSession();
                            mBus.post(new GameCreatedEvent(session));
                            break;
                        case WARNING_BAN:
                            mBus.post(new ErrorMessageEvent(R.string.banned));
                            break;
                        case ALREADY_IN_QUEUE:
                            mBus.post(new ErrorMessageEvent(R.string.already_in_queue));
                            break;
                        case ALREADY_IN_GAME:
                            mBus.post(new ErrorMessageEvent(R.string.already_in_game));
                            break;
                        case GAME_NOT_SUPPORTED:
                            mBus.post(new ErrorMessageEvent(R.string.this_game_is_not_supported));
                            break;
                        case UNKNOWN_ERROR:
                            mBus.post(new ErrorMessageEvent(R.string.unknown_error));
                            break;
                    }
                    break;
                case GAME_CREATED:
                    Log.v("SOCKET_PROFILE", "GAME_CREATED");
                    GameCreatedResponse gameCreatedResponse = mGson.fromJson(response.getSelf(),
                            GameCreatedResponse.class);
                    Session session = gameCreatedResponse.getSession();
                    mBus.post(new GameCreatedEvent(session));
                    break;
                case GAME_FOUND:
                    Log.v("SOCKET_PROFILE", "GAME_FOUND");
                    GameFoundResponse gameFoundResponse = mGson.fromJson(response.getSelf(),
                            GameFoundResponse.class);
                    Session gameSession = gameFoundResponse.getSession();
                    if (gameSession == null)
                        return;
                    mBus.post(new GameFoundEvent(gameSession));
                    break;
                case NEW_GAME_MESSAGE:
                    break;
                case MESSAGE_ECHO:
                    MessageEchoResponse messageEchoResponse = mGson.fromJson(response.getSelf(),
                            MessageEchoResponse.class);
                    mBus.post(new MessageEchoEvent(messageEchoResponse));
                    break;
                case NOTIFY_GAME_STARTED:
                    Log.v("SOCKET_PROFILE", "NOTIFY_GAME_STARTED");
                    NotificationResponse gameEchoResponse = mGson.fromJson(response.getSelf(),
                            NotificationResponse.class);
                    mBus.post(new NewNotificationEvent(gameEchoResponse));
                    break;
                case NOTIFY_TALK_STARTED:
                    Log.v("SOCKET_PROFILE", "NOTIFY_TALK_STARTED");
                    NotificationResponse talkEchoResponse = mGson.fromJson(response.getSelf(),
                            NotificationResponse.class);
                    mBus.post(new NewNotificationEvent(talkEchoResponse));
                    break;
                case NEW_MESSAGE:
                    NewMessageResponse newMessage = mGson.fromJson(response.getSelf(),
                            NewMessageResponse.class);
                    mBus.post(new NewMessageEvent(newMessage));
                    break;
                case FOLLOW_ECHO: //TODO subscribe no realized
                    FollowEchoResponse echoResponse = mGson.fromJson(response.getSelf(),
                            FollowEchoResponse.class);
                    mBus.post(new FollowEvent(echoResponse.isResult(), echoResponse.getFollowId(), null));
                    break;
                case UNFOLLOW_ECHO: //TODO subscribe no realized
                    UnFollowEchoResponse unFollowEchoResponse = mGson.fromJson(response.getSelf(),
                            UnFollowEchoResponse.class);
                    mBus.post(new FollowEvent(unFollowEchoResponse.isResult(),
                            unFollowEchoResponse.getUnFollowId(), null));
                    break;
                case NEW_FOLLOWER: //TODO subscribe no realized
                    NewFollowerResponse followerResponse = mGson.fromJson(response.getSelf(),
                            NewFollowerResponse.class);
                    mBus.post(new NewFollowerEvent(followerResponse.getUser()));
                    break;
                case INVITE_TO_TALK:
                    Log.v("SOCKET_PROFILE", "INVITE_TO_TALK");
                    com.wezom.ulcv2.net.models.responses.websocket.InviteToTalk inviteToTalk = getResponse(com.wezom.ulcv2.net.models.responses.websocket.InviteToTalk.class, response);
                    mBus.post(new InviteToTalkSentResultEvent(inviteToTalk));
                    break;
                case INVITE_TO_TALK_REQUEST:
                    Log.v("SOCKET_PROFILE", "INVITE_TO_TALK_REQUEST");
                    InviteToTalkRequest inviteToTalkRequest = mGson.fromJson(response.getSelf(),
                            InviteToTalkRequest.class);
                    mBus.post(new InviteToTalkRequestEvent(inviteToTalkRequest));
                    break;
                case INVITE_TO_TALK_RESPONSE:
                    Log.v("SOCKET_PROFILE", "INVITE_TO_TALK_RESPONSE");
                    com.wezom.ulcv2.net.models.responses.websocket.InviteToTalkResponse inviteToTalkResponse = getResponse(com.wezom.ulcv2.net.models.responses.websocket.InviteToTalkResponse.class, response);
                    mBus.post(new InviteToTalkResponseEvent(inviteToTalkResponse));
                    break;
                case CREATE_TALK:
                    Log.v("SOCKET_PROFILE", "CREATE_TALK");
                    CreateTalkResponse createTalkResponse = getResponse(CreateTalkResponse.class, response);
                    mBus.post(new CreateTalkEvent(createTalkResponse.getTalk(), createTalkResponse.getMessage()));
                    break;
                case TALK_ADDED:
                    Log.v("SOCKET_PROFILE", "TALK_ADDED");
                    TalkAddedResponse talkAddedResponse = getResponse(TalkAddedResponse.class, response);
                    mBus.postSticky(new TalkAddedEvent(talkAddedResponse));
                    break;
            }
        }
    }

    public void searchGameForWatch() {
        SearchWatchGameRequest request = new SearchWatchGameRequest(new ArrayList<>());
        request.setData(RequestType.WATCH);
        sendMessage(buildRequestBody(request));
    }

    public void acknowledgeFollowers(ArrayList<Integer> follows) {
        FollowAcknowledgeRequest request = new FollowAcknowledgeRequest(follows);
        request.setData(RequestType.FOLLOWER_ACK);
        sendMessage(buildRequestBody(request));
    }

    public void cancelSearch() {
        Request request = new Request();
        request.setData(RequestType.CANCEL_SEARCH);
        sendMessage(buildRequestBody(request));
    }

    public void sendMessage(String text, int personId) {
        MessageRequest request = new MessageRequest(text, personId);
        request.setData(RequestType.SEND_MESSAGE);
        sendMessage(buildRequestBody(request));
    }

    public void unFollow(int personId) {
        UnFollowRequest request = new UnFollowRequest(personId);
        request.setData(RequestType.UNFOLLOW);
        sendMessage(buildRequestBody(request));
    }

    public void searchGame(List<Integer> games) {
        SearchGameRequest request = new SearchGameRequest(games);
        request.setData(RequestType.PLAY);
        sendMessage(buildRequestBody(request));
    }

    public void readMessages(int sender, ArrayList<Integer> messages) {
        ReadMessageRequest request = new ReadMessageRequest(messages, sender);
        request.setData(RequestType.READ_MESSAGE);
        sendMessage(buildRequestBody(request));
    }

    public void createTalk(int categoryId, String name) {
        CreateTalkRequest request = new CreateTalkRequest();
        request.setCategory(categoryId);
        if (!TextUtils.isEmpty(name)) {
            request.setTitle(name);
        }
        request.setData(RequestType.CREATE_TALK);
        sendMessage(buildRequestBody(request));
    }

    public void inviteToTalk(int participantId, int categoryId) {
        InviteToTalk request = new InviteToTalk();
        request.setRecipientId(participantId);
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
}
