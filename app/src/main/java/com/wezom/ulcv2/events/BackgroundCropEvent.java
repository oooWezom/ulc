package com.wezom.ulcv2.events;

import android.graphics.Bitmap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.03.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class BackgroundCropEvent {

    private Bitmap mImage;
}
