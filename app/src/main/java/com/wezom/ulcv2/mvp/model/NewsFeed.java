package com.wezom.ulcv2.mvp.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by usik.a on 31.01.2017.
 */

@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class NewsFeed implements Serializable {
    int mFeedType;
    int mProfileId;
    boolean mShowTolbar;

}
