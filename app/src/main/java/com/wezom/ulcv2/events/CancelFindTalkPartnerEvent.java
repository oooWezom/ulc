package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.CancelFindTalkPartner;
import com.wezom.ulcv2.net.models.responses.websocket.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 25.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class CancelFindTalkPartnerEvent {
    public static final int OK = 1;
    public static final int NOT_IN_QUEUE = 2;
    private CancelFindTalkPartner mCancelFindTalkPartner;
}
