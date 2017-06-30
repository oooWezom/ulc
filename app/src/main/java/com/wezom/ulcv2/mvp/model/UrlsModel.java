package com.wezom.ulcv2.mvp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Android 3 on 07.04.2017.
 */

@Data
@AllArgsConstructor
public class UrlsModel {
    private String restUrl;
    private String socketUrl;
}
