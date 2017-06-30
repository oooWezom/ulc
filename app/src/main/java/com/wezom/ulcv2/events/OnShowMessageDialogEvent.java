package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created: Zorin A.
 * Date: 28.11.2016.
 */
@AllArgsConstructor
@Data
public class OnShowMessageDialogEvent {
    private int messageId;
}
