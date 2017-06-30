package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Conversation;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 22.01.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class DialogsResponse extends Response {

    @SerializedName("response")
    private ArrayList<Conversation> mResponse;
}