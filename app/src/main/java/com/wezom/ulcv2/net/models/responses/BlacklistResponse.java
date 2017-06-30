package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.BlacklistRecord;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 11.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class BlacklistResponse extends Response {

    @SerializedName("response")
    private ArrayList<BlacklistRecord> mResponse;
}
