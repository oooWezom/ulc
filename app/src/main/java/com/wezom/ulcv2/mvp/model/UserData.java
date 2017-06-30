package com.wezom.ulcv2.mvp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 13.07.2016
 * Time: 13:12
 */

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserData {
    private int id;
    private String name;
    private int level;
    private String avatar;
    private long likes;
}
