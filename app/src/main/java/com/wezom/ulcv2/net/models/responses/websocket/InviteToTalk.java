package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */

@Data
@Accessors(prefix = "m")
@EqualsAndHashCode(callSuper = true)
public class InviteToTalk extends Response {
    @SerializedName("result")
    private int mResult;
    @SerializedName("expires")
    private int mExpires;//время (секунды), за в течение которого необходимо ожидать ответ от получателя приглашения
    @SerializedName("time")
    private int mTime;//время (секунды), в течение которого получателя нельзя беспокоить
}
