package com.wezom.ulcv2.interfaces;

/**
 * Created: Zorin A.
 * Date: 24.10.2016.
 */

public interface OnNotificationClickListener {
    void onNotificationClick(int itemPosition, int sessionType);

    void onCloseNotification(int itemPosition);
}
