package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 25.07.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(prefix = "m")
@EqualsAndHashCode(callSuper = true)
public class CancelFindTalkSession extends Request {
    @SerializedName("result")
    private int mResult;
}
