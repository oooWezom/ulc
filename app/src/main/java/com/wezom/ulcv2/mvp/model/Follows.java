package com.wezom.ulcv2.mvp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by usik.a on 08.02.2017.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class Follows {
    private int mTabMode;
    private int mId;
}
