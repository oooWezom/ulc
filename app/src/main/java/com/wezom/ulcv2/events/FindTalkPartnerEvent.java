package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.FindTalkPartnerResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 25.07.2016.
 */
@Accessors(prefix = "m")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindTalkPartnerEvent {
    public static final int OK = 1;
    public static final int WRONG_TALK_STATE = 2;
    public static final int MAX_PARTNERS = 3;
    public static final int UNKNOWN_ERROR = 4;

    private FindTalkPartnerResponse mResponse;
}
