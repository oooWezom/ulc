package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.NotificationResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 19.07.2016.
 */
@AllArgsConstructor
@Accessors(prefix = "m")
@Data
public class NewNotificationEvent {
    private NotificationResponse mResponse;
}
