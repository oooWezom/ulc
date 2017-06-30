package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.InviteToTalk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 19.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class InviteToTalkSentResultEvent {
    public static final int OK = 1;
    public static final int WRONG_TALK_STATE = 2;
    public static final int MAX_PARTNERS = 3;
    public static final int USER_NOT_READY = 4;
    public static final int INVITATION_EXISTS = 5;
    public static final int DO_NOT_DISTURB = 6;


    private InviteToTalk mInviteToTalk;

}
