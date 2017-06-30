package com.wezom.ulcv2.net.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 25.01.2016.
 */
@AllArgsConstructor
@Data
@Accessors(prefix = "m")
public class MessagesRequest {

    private int mConversationId;
    private int mOffset;
    private int mCount;
}
