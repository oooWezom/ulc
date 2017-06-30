package com.wezom.ulcv2.interfaces;

/**
 * Created by zorin.a on 08.05.2017.
 */

public interface MessageBoardActions {
    void switchBoardToInitMode();

    void switchBoardToEndMode();

    void switchBoardToExecutionMode();

    void switchToRatingMode();

    void hideMessageBoard();

    void showMessageBoard();

    void switchBoardToGameNotSupportedMode();

}
