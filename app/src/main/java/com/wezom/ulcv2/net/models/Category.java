package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 05.07.2016.
 */
@Data
@Accessors(prefix = "m")
public class Category {

    @SerializedName("id")
    private int mId;
    @SerializedName("created_timestamp")
    private long mCreatedTimestamp;
    @SerializedName("created_date")
    private String mCreatedDate;
    @SerializedName("enabled")
    private boolean mEnabled;
    @SerializedName("icon")
    private String mIcon;
    @SerializedName("name")
    private String mName;
    @SerializedName("name_ru")
    private String mNameRu;
}
