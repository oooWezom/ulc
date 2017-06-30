package com.wezom.ulcv2.net.models.requests.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 29.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class PerformanceDoneRequest extends Request {
}
