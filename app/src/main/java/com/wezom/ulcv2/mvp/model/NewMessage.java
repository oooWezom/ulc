package com.wezom.ulcv2.mvp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 29.07.2016
 * Time: 19:18
 */
@Data
@Accessors(prefix = "m")
public class NewMessage {
    private String mText;
    private User mUser;

    @Data
    @Accessors(prefix = "m")
    public static class User {
        private int mId;
        private String mName;
        private int mSex;
        private int mLevel;
    }
}
