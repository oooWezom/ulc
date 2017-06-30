package com.wezom.ulcv2.net.models.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 22.01.2016.
 */
@Data
@Accessors(prefix = "m")
@NoArgsConstructor
public class DialogsRequest {
    private int mDialogId;
}
