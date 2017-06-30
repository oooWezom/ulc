package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.03.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class BlacklistRecord extends User {

    @SerializedName("added_timestamp")
    private long mAddedTimestamp;
}
