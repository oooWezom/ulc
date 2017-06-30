package com.wezom.ulcv2.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.interfaces.MessageBoardActions;

import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by zorin.a on 12.04.2017.
 */

public class GameMessageBoard extends RelativeLayout implements MessageBoardActions {

    //region views
    @BindView(R.id.game_board_left_button_container)
    ViewGroup leftButtonContainer;
    @BindView(R.id.game_board_right_button_container)
    ViewGroup rightButtonContainer;
    @BindView(R.id.game_board_left_button)
    Button leftButton;
    @BindView(R.id.game_board_right_button)
    Button rightButton;
    @BindView(R.id.game_board_message)
    TextView messageTextView;
    @BindView(R.id.game_board_root)
    ViewGroup root;
    //endregion

    //region strings
    @BindString(R.string.waiting_for_players)
    String waitingPlayersMessageString;
    @BindString(R.string.this_game_is_not_available_for_now)
    String gameNotImplementedString;
    @BindString(R.string.let_the_execution_begin)
    String executionBeginMessage;
    @BindString(R.string.rate_them)
    String pollMessage;
    @BindString(R.string.you_may_leave_now)
    String leaveMessage;
    @BindString(R.string.wait_for_execution)
    String avaitExecution;

    //endregion

    //region colors
    @BindColor(R.color.color_game_ready_button_idle)
    int readyButtonIdleColor;
    @BindColor(R.color.color_game_ready_button_pressed)
    int readyButtonPressedColor;
    //endregion

    OnReadyToPlayClickListener clickListener;

    public GameMessageBoard(Context context) {
        super(context);
        init(context);
    }

    public GameMessageBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setLeftButtonReady() {
        leftButton.setBackgroundColor(readyButtonPressedColor);
    }

    public void setRightButtonReady() {
        rightButton.setBackgroundColor(readyButtonPressedColor);
    }

    public void switchBoardToInitMode() {
        resetButtons();
        messageTextView.setText(waitingPlayersMessageString);
        setButtonsVisible(true);
        showMessageBoard();
    }

    public void switchBoardToExecutionMode() {
        setButtonsVisible(false);
        messageTextView.setText(executionBeginMessage);
        showMessageBoard();
    }

    public void switchToRatingMode() {
        setButtonsVisible(false);
        messageTextView.setText(pollMessage);
        showMessageBoard();
    }

    public void switchBoardToEndMode() {
        messageTextView.setText(leaveMessage);
        setButtonsVisible(false);
        showMessageBoard();
    }

    public void switchBoardAwaitingExecution(){
        messageTextView.setText(avaitExecution);
        setButtonsVisible(false);
        showMessageBoard();
    }

    public void switchBoardToGameNotSupportedMode() {
        messageTextView.setText(gameNotImplementedString);
        setButtonsVisible(false);
        showMessageBoard();
    }

    public void setButtonsVisible(boolean isVisible) {
        leftButtonContainer.setVisibility(isVisible ? VISIBLE : GONE);
        rightButtonContainer.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void hideMessageBoard() {
        root.setVisibility(GONE);
    }
    public void hideMessageBoardDelayed() {
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    root.setVisibility(GONE);
                });

    }

    public void showMessageBoard() {
        root.setVisibility(VISIBLE);
    }

    private void resetButtons() {
        leftButton.setBackgroundColor(readyButtonIdleColor);
        rightButton.setBackgroundColor(readyButtonIdleColor);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_game_message_board, this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.game_board_left_button)
    void onReadyClick() { //left button clicked
        if (clickListener != null) {
            clickListener.onReadyClick();
        }
    }

    public void setClickListener(OnReadyToPlayClickListener clickListener) {
        this.clickListener = clickListener;
    }



    public interface OnReadyToPlayClickListener {
        void onReadyClick(); //only for left button
    }
}
