package com.wezom.ulcv2.net;

import android.content.Context;
import android.util.Log;

import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.FollowEvent;
import com.wezom.ulcv2.events.NewFollowerEvent;
import com.wezom.ulcv2.events.OnErrorEvent;
import com.wezom.ulcv2.events.StreamClosedByReportsEvent;
import com.wezom.ulcv2.events.TalkLikesLocalEvent;
import com.wezom.ulcv2.events.TalkSpectatorConnectedEvent;
import com.wezom.ulcv2.events.TalkSpectatorDisconnectedEvent;
import com.wezom.ulcv2.events.UnFollowEvent;
import com.wezom.ulcv2.events.game.BeginExecutionEvent;
import com.wezom.ulcv2.events.game.GameResultEvent;
import com.wezom.ulcv2.events.game.GameStateEvent;
import com.wezom.ulcv2.events.game.NewMessageEvent;
import com.wezom.ulcv2.events.game.PlayerConnectionEvent;
import com.wezom.ulcv2.events.game.PlayerReadyEvent;
import com.wezom.ulcv2.events.game.onRatingStartedEvent;
import com.wezom.ulcv2.events.game.ResentUnityMessageEvent;
import com.wezom.ulcv2.events.game.RoundResultEvent;
import com.wezom.ulcv2.events.game.RoundStartEvent;
import com.wezom.ulcv2.events.game.SessionStateEvent;
import com.wezom.ulcv2.events.game.SpectatorConnectionEvent;
import com.wezom.ulcv2.exception.WebsocketException;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.NewMessage;
import com.wezom.ulcv2.net.models.requests.websocket.DiskMoveRequest;
import com.wezom.ulcv2.net.models.requests.websocket.PerformanceDoneRequest;
import com.wezom.ulcv2.net.models.requests.websocket.ReportUserSocketRequest;
import com.wezom.ulcv2.net.models.requests.websocket.Request;
import com.wezom.ulcv2.net.models.requests.websocket.RockSpockMoveRequest;
import com.wezom.ulcv2.net.models.requests.websocket.SessionMessageRequest;
import com.wezom.ulcv2.net.models.requests.websocket.TalkLikesRequest;
import com.wezom.ulcv2.net.models.requests.websocket.TalkLikesResponse;
import com.wezom.ulcv2.net.models.requests.websocket.VoteRequest;
import com.wezom.ulcv2.net.models.responses.websocket.BeginExecutionResponse;
import com.wezom.ulcv2.net.models.responses.websocket.ChatMessageResponse;
import com.wezom.ulcv2.net.models.responses.websocket.ErrorResponse;
import com.wezom.ulcv2.net.models.responses.websocket.FollowEchoResponse;
import com.wezom.ulcv2.net.models.responses.websocket.GameResultResponse;
import com.wezom.ulcv2.net.models.responses.websocket.GameSpectatorConnectedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.GameStateResponse;
import com.wezom.ulcv2.net.models.responses.websocket.NewFollowerResponse;
import com.wezom.ulcv2.net.models.responses.websocket.PlayerConnectedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.PlayerReadyResponse;
import com.wezom.ulcv2.net.models.responses.websocket.PlayerReconnectedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.Response;
import com.wezom.ulcv2.net.models.responses.websocket.RoundResultResponse;
import com.wezom.ulcv2.net.models.responses.websocket.SessionStateResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkSpectatorConnectedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.TalkSpectatorDisconnectedResponse;
import com.wezom.ulcv2.net.models.responses.websocket.UnFollowEchoResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wezom.ulcv2.common.Constants.CONNECTION_MODE_CONNECT;
import static com.wezom.ulcv2.common.Constants.CONNECTION_MODE_DISCONNECT;
import static com.wezom.ulcv2.common.Constants.WEBSOCKET_SESSION_URL;

/**
 * Created by kartavtsev.s on 16.02.2016.
 */
public class SessionChannelHandler extends ReconnectingChannelHandler implements SessionChannel {
    public static final String TAG = "PLAY";
    private static int lastAngle = -1;
    private static int lastTouchedDisk = -1;
    PreferenceManager preferenceManager;
    Context context;

    public SessionChannelHandler(EventBus bus, @ApplicationContext Context context, PreferenceManager preferenceManager) {
        super(bus, context, TAG);
        this.context = context;
        this.preferenceManager = preferenceManager;
    }

    @Override
    protected String getConnectUrl() {
        String baseUrl = preferenceManager.getBaseUrl(context).getSocketUrl();
        return baseUrl + WEBSOCKET_SESSION_URL;
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
    public void onMessage(ResponseBody message) throws IOException {
        String jsonMessage = message.string();
        Response response = mGson.fromJson(jsonMessage, Response.class);
        response.setSelf(jsonMessage);
        Log.v("SOCKET", jsonMessage);
        if (response.getType() != null) {
            switch (response.getType()) {

                case ERROR:
                    Log.v("SOCKET", "ERROR");
                    ErrorResponse errorResponse = mGson.fromJson(response.getSelf(), ErrorResponse.class);
                    mBus.post(new OnErrorEvent(errorResponse));
                    break;

                case NEW_GAME_MESSAGE:
                    Log.v("SOCKET", "NEW_GAME_MESSAGE");
                    ChatMessageResponse chatMessageResponse = mGson.fromJson(response.getSelf(), ChatMessageResponse.class);

                    NewMessage.User user = new NewMessage.User();
                    user.setId(chatMessageResponse.getUser().getId());
                    user.setName(chatMessageResponse.getUser().getName());

                    NewMessage newMessage = new NewMessage();
                    newMessage.setText(chatMessageResponse.getText());
                    newMessage.setUser(user);

                    mBus.post(new NewMessageEvent(newMessage));
                    break;
                case PLAYER_CONNECTED:
                    Log.v("SOCKET", "PLAYER_CONNECTED");
                    PlayerConnectedResponse playerConnectedResponse = mGson.fromJson(response.getSelf(), PlayerConnectedResponse.class);
                    mBus.post(new PlayerConnectionEvent(CONNECTION_MODE_CONNECT, playerConnectedResponse.getPlayer().getId()));
                    break;
                case PLAYER_RECONNECTED:
                    Log.v("SOCKET", "PLAYER_RECONNECTED");
                    PlayerReconnectedResponse playerReconnectedResponse = mGson.fromJson(response.getSelf(), PlayerReconnectedResponse.class);
                    mBus.post(new PlayerConnectionEvent(Constants.CONNECTION_MODE_RECONNECT, playerReconnectedResponse.getPlayer().getId()));
                    break;
                case GAME_SPECTATOR_CONNECTED:
                    Log.v("SOCKET", "GAME_SPECTATOR_CONNECTED");
                    //  GameSpectatorConnectedResponse spectatorConnectedResponse = mGson.fromJson(response.getSelf(), GameSpectatorConnectedResponse.class);
                    mBus.post(new SpectatorConnectionEvent(CONNECTION_MODE_CONNECT, 0));
                    break;
                case GAME_USER_DISCONNECTED:
                    Log.v("SOCKET", "GAME_USER_DISCONNECTED"); //can be player or watcher
                    GameSpectatorConnectedResponse spectatorDisconnectedResponse = mGson.fromJson(response.getSelf(), GameSpectatorConnectedResponse.class);
                    mBus.post(new SpectatorConnectionEvent(CONNECTION_MODE_DISCONNECT, spectatorDisconnectedResponse.getUser().getId()));
                    break;
                case SESSION_STATE:
                    Log.v("SOCKET", "SESSION_STATE");
                    SessionStateResponse sessionStateResponse = mGson.fromJson(response.getSelf(), SessionStateResponse.class);
                    SessionStateResponse.SessionState sessionState = sessionStateResponse.getSessionState();
                    mBus.post(new SessionStateEvent(
                            sessionState.getId(),
                            sessionState.getGame(),
                            sessionState.getState(),
                            sessionState.getSpectators(),
                            sessionState.getPlayers(),
                            sessionState.getViewers(),
                            sessionState.getLoserId()
                    ));
                    break;
                case GAME_STATE:
                    Log.v("SOCKET", "GAME_STATE");
                    GameStateResponse gameStateResponse = mGson.fromJson(response.getSelf(), GameStateResponse.class);
                    GameStateResponse.GameState gameState = gameStateResponse.getGameState();
                    mBus.post(new GameStateEvent(gameState, jsonMessage));
                    break;
                case BEGIN_EXECUTION:
                    Log.v("SOCKET", "BEGIN_EXECUTION");
                    BeginExecutionResponse beginExecutionResponse = mGson.fromJson(response.getSelf(), BeginExecutionResponse.class);
                    mBus.post(new BeginExecutionEvent(beginExecutionResponse.getSessionState().getLoserId()));
                    break;
                case POLL_START:
                    Log.v("SOCKET", "POLL_START");
                    mBus.post(new onRatingStartedEvent());
                    break;
                case GAME_RESULT:
                    Log.v("SOCKET", "GAME_RESULT");
                    GameResultResponse gameResultResponse = mGson.fromJson(response.getSelf(), GameResultResponse.class);
                    if (gameResultResponse == null || gameResultResponse.getSessionState() == null) {
                        break;
                    }
                    mBus.post(new GameResultEvent(gameResultResponse));
                    break;
                case PLAYER_READY:
                    Log.v("SOCKET", "PLAYER_READY");
                    PlayerReadyResponse readyResponse = mGson.fromJson(response.getSelf(), PlayerReadyResponse.class);
                    mBus.post(new PlayerReadyEvent(readyResponse.getId()));
                    break;
                case ROUND_START:
                    Log.v("SOCKET", "ROUND_START");
                    mBus.post(new RoundStartEvent());
                    break;
                case ROUND_RESULT:
                    Log.v("SOCKET", "ROUND_RESULT");
                    // call invokes function inside Unity and callback from it triggers onRoundResultEvent
                    break;
                case FOLLOW_ECHO:
                    Log.v("SOCKET", "FOLLOW_ECHO");
                    FollowEchoResponse echoResponse = mGson.fromJson(response.getSelf(),
                            FollowEchoResponse.class);
                    mBus.post(new FollowEvent(echoResponse.isResult(), echoResponse.getFollowId(), echoResponse.getUser()));
                    break;
                case UNFOLLOW_ECHO:
                    Log.v("SOCKET", "UNFOLLOW_ECHO");
                    UnFollowEchoResponse unFollowEchoResponse = mGson.fromJson(response.getSelf(),
                            UnFollowEchoResponse.class);
                    mBus.post(new UnFollowEvent(unFollowEchoResponse.isResult(),
                            unFollowEchoResponse.getUnFollowId()));
                    break;
                case NEW_FOLLOWER:
                    Log.v("SOCKET", "NEW_FOLLOWER");
                    NewFollowerResponse followerResponse = mGson.fromJson(response.getSelf(),
                            NewFollowerResponse.class);
                    mBus.post(new NewFollowerEvent(followerResponse.getUser()));
                    break;
                case STREAM_CLOSED_BY_REPORTS:
                    Log.v("SOCKET", "STREAM_CLOSED_BY_REPORTS");
                    mBus.post(new StreamClosedByReportsEvent());
                case SPECTATOR_CONNECTED:
                    Log.v("SOCKET", "SPECTATOR_CONNECTED");
                    TalkSpectatorConnectedResponse talkSpectatorConnectedResponse = getResponse(TalkSpectatorConnectedResponse.class, response);
                    mBus.post(new TalkSpectatorConnectedEvent(talkSpectatorConnectedResponse));
                    break;
                case SPECTATOR_DISCONNECTED:
                    Log.v("SOCKET", "SPECTATOR_DISCONNECTED");
                    TalkSpectatorDisconnectedResponse talkSpectatorDisconnectedResponse = getResponse(TalkSpectatorDisconnectedResponse.class, response);
                    mBus.post(new TalkSpectatorDisconnectedEvent(talkSpectatorDisconnectedResponse));
                    break;
            }
        }
        Observable.timer(150, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(t -> {
                    mBus.post(new ResentUnityMessageEvent(response.getSelf()));
                });

    }

    public void ready() {
        Request request = new Request();
        request.setData(RequestType.READY);
        sendMessage(buildRequestBody(request));
    }

    public void moveDisk(int angle, int disk) {
        if (disk != lastTouchedDisk) {
            sendMoveRequest(angle, disk);
            lastTouchedDisk = disk;
            lastAngle = angle;
        } else {
            if (angle != lastAngle) {
                sendMoveRequest(angle, disk);
                lastAngle = angle;
            }
        }
    }

    public void setRockSpockMove(int moveId) {
        sendRockSpockMove(moveId);
    }

    private void sendMoveRequest(int angle, int disk) {
        DiskMoveRequest diskMoveRequest = new DiskMoveRequest(disk, angle);
        diskMoveRequest.setData(RequestType.MOVE);
        try {
            Log.v("SOCKET", "moveDisk(angle:" + angle + ", disk:" + disk + ")");
            sendMessage(buildRequestBody(diskMoveRequest));
        } catch (WebsocketException e) {
            e.printStackTrace();
        }
    }

    private void sendRockSpockMove(int moveId) {
        RockSpockMoveRequest rockSpockMoveRequest = new RockSpockMoveRequest(moveId);
        rockSpockMoveRequest.setData(RequestType.MOVE);
        try {
            Log.v("SOCKET", "move winner(" + moveId + ")");
            sendMessage(buildRequestBody(rockSpockMoveRequest));
        } catch (WebsocketException e) {
            e.printStackTrace();
        }
    }

    public void executionDone() {
        PerformanceDoneRequest doneRequest = new PerformanceDoneRequest();
        doneRequest.setData(RequestType.PERFORMANCE_DONE);
        sendMessage(buildRequestBody(doneRequest));
    }

    public void performanceDone() {
        PerformanceDoneRequest doneRequest = new PerformanceDoneRequest();
        doneRequest.setData(RequestType.PERFORMANCE_DONE);
        sendMessage(buildRequestBody(doneRequest));
    }

    public void votePlus(int playerVoteId) {
        VoteRequest voteRequest = new VoteRequest(playerVoteId);
        voteRequest.setData(RequestType.VOTE_PLUS);
        sendMessage(buildRequestBody(voteRequest));
    }

    public void voteMinus(int playerVoteId) {
        VoteRequest voteRequest = new VoteRequest(playerVoteId);
        voteRequest.setData(RequestType.VOTE_MINUS);
        sendMessage(buildRequestBody(voteRequest));
    }

    public void leave() {
        Request request = new Request();
        request.setData(RequestType.GAME_LEAVE);
        sendMessage(buildRequestBody(request));
    }

    public void sendMessage(String message) {
        SessionMessageRequest sessionMessageRequest = new SessionMessageRequest(message);
        sessionMessageRequest.setData(RequestType.GAME_SEND_MESSAGE);
        sendMessage(buildRequestBody(sessionMessageRequest));
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

    public void reportUser(int talkId, int reportCategory) {
        ReportUserSocketRequest request = new ReportUserSocketRequest();
        request.setData(RequestType.REPORT_USER_BEHAVIOR);
        request.setSessionId(talkId);
        request.setCategoryId(reportCategory);
        sendMessage(buildRequestBody(request));
    }
}
