package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Event;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 19.01.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class EventsResponse extends Response {

    @SerializedName("response")
    private ArrayList<Event> mResponse;
}
