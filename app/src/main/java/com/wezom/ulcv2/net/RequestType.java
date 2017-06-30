package com.wezom.ulcv2.net;

public enum RequestType {
    INTRODUCE(1, "introduce"),
    PLAY(10, "lets_play"),
    CANCEL_SEARCH(13, "search_cancel"),
    WATCH(14, "lets_watch"),
    GAME_SEND_MESSAGE(22, "message"),
    GAME_LEAVE(29, "leave"),
    SEND_MESSAGE(101, "message"),
    READ_MESSAGE(104, "read_messages"),
    FOLLOW(105, "follow"),
    FOLLOWER_ACK(109, "follower_ack"),
    REPORT_USER_BEHAVIOR(130, "report_user_behavior"),
    CREATE_TALK(201, "create_talk"),
    LEAVE_TALK(202, "leave_talk"),
    TALK_MESSAGE(203, "talk_message"),
    FIND_TALK_PARTNER(204, "find_partner"),
    CANCEL_FIND_TALK_PARTNER(205, "cancel_find_talk_partner"),
    DETACH_TALK(209, "detach_talk"),
    SWITCH_TALK(210, "switch_talk"),
    UPDATE_TALK_DATA(211,"update_talk_data"),
    TALK_LIKES(212, "talk_likes"),
    FIND_TALK(213, "find_talk"),
    SEND_DONATE_MESSAGE(214, "talk_donate_message"),
    INVITE_TO_TALK(220, "invite_to_talk"),
    INVITE_TO_TALK_REQUEST(221, "invite_to_talk_request"),
    INVITE_TO_TALK_RESPONSE(222, "invite_to_talk_response"),
    PERFORMANCE_DONE(502, "performance_done"),
    VOTE_PLUS(504, "vote_plus"),
    VOTE_MINUS(505, "vote_minus"),
    UNFOLLOW(106, "follow"),
    READY(1001, "ready"),
    MOVE(1003, "move");

    private int mType;
    private String mStringType;

    RequestType(int type, String stringType) {
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