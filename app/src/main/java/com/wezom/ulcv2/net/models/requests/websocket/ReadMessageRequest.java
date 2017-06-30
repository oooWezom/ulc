package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 23.03.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class ReadMessageRequest extends Request {

    @SerializedName("messages")
    ArrayList<Integer> mMessages;
    @SerializedName("sender")
    private int mSender;
}
