package com.wezom.ulcv2.net.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.02.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class RemoveFromBlackListRequest {

    private int mId;
}
