package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.responses.websocket.Response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 19.07.2016.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(prefix = "m")
public class InviteToTalk extends Request {
    @SerializedName("recipient")
    private int mRecipientId;
    @SerializedName("category")
    private int mCategoryId;
}
