package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 12.07.2016.
 */

@Getter
@Accessors(prefix = "m")
public enum TalkState {
    @SerializedName("1")
    TALK_NEW(1),
    @SerializedName("2")
    TALK_TALKING(2),
    @SerializedName("3")
    TALK_SEARCHING(3),
    @SerializedName("4")
    TALK_DISCONNECTED(4),
    @SerializedName("5")
    TALK_INVITATION_SEND(5),
    @SerializedName("6")
    TALK_CLOSING(6),
    @SerializedName("7")
    TALK_CLOSED(7);


    private int mId;

    TalkState(int id) {
        mId = id;
    }
}
