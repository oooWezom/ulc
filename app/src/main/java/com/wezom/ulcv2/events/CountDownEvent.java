package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 12.07.2016
 * Time: 10:20
 */

@Data
@AllArgsConstructor
public class CountDownEvent {

    private int count;
}
