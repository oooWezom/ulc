package com.wezom.ulcv2.interfaces;

import com.wezom.ulcv2.managers.UnityManager.OrientationMessage;

import static com.wezom.ulcv2.managers.UnityManager.InitMessage;
import static com.wezom.ulcv2.managers.UnityManager.UnityMessage;

/**
 * Created by zorin.a on 14.04.2017.
 */

public interface UnityActionsManager {

    void sendMessage(String message);

    void sendMessage(UnityMessage message);

    void sendSystemMessage(InitMessage message);

    void sendSystemMessage(OrientationMessage message);

    void onMessage(String message);

    void switchScene(int scene);
}
