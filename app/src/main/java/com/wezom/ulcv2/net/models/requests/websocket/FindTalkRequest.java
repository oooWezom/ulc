package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 25.07.2016.
 */
@NoArgsConstructor
@Data
@Accessors(prefix = "m")
@EqualsAndHashCode(callSuper = true)
public class FindTalkRequest extends Request {
    @SerializedName("category")
    private int mCategory;
    @SerializedName("except")
    private int mExcept;
}
