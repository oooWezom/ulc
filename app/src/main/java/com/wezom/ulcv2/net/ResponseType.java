package com.wezom.ulcv2.net;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kartavtsev.s on 28.03.2016.
 */
public enum ResponseType {
    @SerializedName("2")
    ERROR(2, "error"),
    @SerializedName("11")
    LOOK_FOR_GAME_RESULT(11, "look_for_game_result"),
    @SerializedName("12")
    GAME_CREATED(12, "game_created"),
    @SerializedName("15")
    GAME_FOUND(15, "game_found"),
    @SerializedName("22")
    NEW_GAME_MESSAGE(22, "game_message"),
    @SerializedName("102")
    MESSAGE_ECHO(102, "message_echo"),
    @SerializedName("103")
    NEW_MESSAGE(103, "new_message"),
    @SerializedName("105")
    FOLLOW_ECHO(105, "follow_echo"),
    @SerializedName("106")
    UNFOLLOW_ECHO(106, "unfollow_echo"),
    @SerializedName("107")
    NEW_FOLLOWER(107, "new_follower"),
    @SerializedName("123")
    PLAYER_CONNECTED(123, "player_connected"),
    @SerializedName("124")
    PLAYER_RECONNECTED(124, "player_reconnected"),
    @SerializedName("125")
    GAME_SPECTATOR_CONNECTED(125, "spectator_connected_to_session"),
    @SerializedName("126")
    GAME_USER_DISCONNECTED(126, "player_disconnected"),
    @SerializedName("131")
    STREAM_CLOSED_BY_REPORTS(131, "stream_closed_by_reports"),
    @SerializedName("200")
    TALK_STATE(200, "talk_state"),
    @SerializedName("201")
    CREATE_TALK(201, "create_talk"),
    @SerializedName("203")
    TALK_MESSAGE(203, "talk_message"),
    @SerializedName("204")
    FIND_TALK_PARTNER(204, "find_talk_partner"),
    @SerializedName("205")
    CANCEL_FIND_TALK_PARTNER(205, "cancel_find_talk_partner"),
    @SerializedName("206")
    TALK_ADDED(206, "talk_added"),
    @SerializedName("207")
    TALK_REMOVED(207, "talk_removed"),
    @SerializedName("208")
    TALK_CLOSED(208, "talk_closed"),
    @SerializedName("210")
    SWITCH_TALK(210, "switch_talk"),
    @SerializedName("211")
    UPDATE_TALK_DATA(211, "update_talk_data"),
    @SerializedName("212")
    TALK_LIKES(212, "talk_likes"),
    @SerializedName("213")
    FIND_TALK(213, "find_talk"),
    @SerializedName("214")
    SEND_DONATE_MESSAGE(214, "talk_donate_message"),
    @SerializedName("215")
    DONATE_MESSAGE(215, "talk_donate_message"),
    @SerializedName("220")
    INVITE_TO_TALK(220, "invite_to_talk"),
    @SerializedName("221")
    INVITE_TO_TALK_REQUEST(221, "invite_to_talk_request"),
    @SerializedName("222")
    INVITE_TO_TALK_RESPONSE(222, "invite_to_talk_response"),
    @SerializedName("231")
    TALK_STREAMER_CONNECTED(231, "streamer_connected"),
    @SerializedName("232")
    TALK_STREAMER_RECONNECTED(232, "streamer_reconnected"),
    @SerializedName("233")
    STREAMER_DISCONNECTED(233, "streamer_disconnected"),
    @SerializedName("234")
    SPECTATOR_CONNECTED(234, "spectator_connected"),
    @SerializedName("235")
    SPECTATOR_DISCONNECTED(235, "spectator_disconnected"),
    @SerializedName("301")
    NOTIFY_GAME_STARTED(301, "notify_game_started"),
    @SerializedName("302")
    NOTIFY_TALK_STARTED(302, "notify_talk_started"),
    @SerializedName("500")
    SESSION_STATE(500, "session_state"),
    @SerializedName("501")
    BEGIN_EXECUTION(501, "begin_execution"),
    @SerializedName("502")
    POLL_START(502, "poll_start"),
    @SerializedName("503")
    GAME_RESULT(503, "game_result"),
    @SerializedName("1000")
    GAME_STATE(1000, "game_state"),
    @SerializedName("1001")
    PLAYER_READY(1001, "player_ready"),
    @SerializedName("1002")
    ROUND_START(1002, "round_start"),
    @SerializedName("1005")
    ROUND_RESULT(1005, "round_start");

    private int mType;
    private String mStringType;

    ResponseType(int type, String stringType) {
        mType = type;
        mStringType = stringType;
    }

    public int getType() {
        return mType;
    }

    public String getStringType() {
        return mStringType;
    }
}
