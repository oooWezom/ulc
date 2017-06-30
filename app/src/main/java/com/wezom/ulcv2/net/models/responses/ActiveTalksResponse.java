package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Talk;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 14.07.2016.
 */
@AllArgsConstructor
@Accessors(prefix = "m")
@Data
public class ActiveTalksResponse extends Response {
    @SerializedName("response")
    ArrayList<Talk> mTalksList;
}
