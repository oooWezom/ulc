package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by Android 3 on 27.03.2017.
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class EventByIdResptnse extends Response{
    @SerializedName("response")
    private Event event;
}
