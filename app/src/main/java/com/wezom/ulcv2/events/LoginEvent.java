package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.05.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class LoginEvent {
    private String mLogin;
    private String mPassword;
    private String mToken;
}
