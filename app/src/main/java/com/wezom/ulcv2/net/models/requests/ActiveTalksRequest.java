package com.wezom.ulcv2.net.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 14.07.2016.
 */
@AllArgsConstructor
@Data
@Accessors(prefix = "m")
public class ActiveTalksRequest {
    private int mCategoryId;
    private int mOffset;
    private int mCount;
}
