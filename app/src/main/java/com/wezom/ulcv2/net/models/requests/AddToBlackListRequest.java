package com.wezom.ulcv2.net.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.02.2016.
 */
@AllArgsConstructor
@Accessors(prefix = "m")
@Data
public class AddToBlackListRequest {

    private int mId;
}
