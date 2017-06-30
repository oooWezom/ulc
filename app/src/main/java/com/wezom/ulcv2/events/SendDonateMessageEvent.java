package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.SendDonateMessageResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 25.07.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(prefix = "m")
public class SendDonateMessageEvent {
    public static final int RESPONSE_OK = 1;
    private SendDonateMessageResponse mResponse;
}
