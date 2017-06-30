package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.mvp.model.Game;
import com.wezom.ulcv2.net.models.Session;
import com.wezom.ulcv2.net.models.Talk;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 09.03.2017.
 */
@Data
@Accessors(prefix = "m")
public class ActiveSessionResponse extends Response {
    @SerializedName("talk")
    private Talk mTalk;
    @SerializedName("game")
    private Session mGame;

}
