package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 10.06.2016.
 */
public class ChangeEvents {

    public static class ChangingLoginEvent {}

    @Data
    @AllArgsConstructor
    @Accessors(prefix = "m")
    public static class ChangedLoginEvent{
        private String mLogin;
    }

    public static class ChangingPassword{
    }
}
