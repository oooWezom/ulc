package com.wezom.ulcv2.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class ApiException extends RuntimeException {

    private String mMessage;
    private int mCode;
}
