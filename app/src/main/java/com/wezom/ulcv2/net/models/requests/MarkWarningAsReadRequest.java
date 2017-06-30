package com.wezom.ulcv2.net.models.requests;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 18.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class MarkWarningAsReadRequest {
    private ArrayList<Long> mTimeStamps;

}
