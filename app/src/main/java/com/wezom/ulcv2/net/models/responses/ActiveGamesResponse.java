package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Session;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 16.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Accessors(prefix = "m")
@Data
public class ActiveGamesResponse extends Response {

    @SerializedName("response")
    private ArrayList<Session> mSession;
}
