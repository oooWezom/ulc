package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by zorin.a on 16.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class SearchGameRequest extends Request {

    @SerializedName("games")
    private List<Integer> games;
}
