package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 07.10.2016.
 */
@Data
@Accessors(prefix = "m")
public class ReportUserSocketRequest extends Request {
    @SerializedName("session")
    private int mSessionId;
    @SerializedName("user")
    private int mUserId;
    @SerializedName("category")
    private int mCategoryId;
    @SerializedName("comment")
    private String mComment;
}
