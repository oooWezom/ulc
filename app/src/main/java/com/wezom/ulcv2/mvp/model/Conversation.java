package com.wezom.ulcv2.mvp.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by usik.a on 07.02.2017.
 */

@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class Conversation implements Serializable {

    private String mName;
    private String mAvatar;
    private int mDialogId;

}
