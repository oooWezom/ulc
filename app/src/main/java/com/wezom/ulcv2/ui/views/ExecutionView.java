package com.wezom.ulcv2.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wezom.ulcv2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Android 3 on 18.04.2017.
 */

public class ExecutionView extends RelativeLayout {

    @BindView(R.id.view_execution_text_view)
    TextView textView;
    @BindView(R.id.view_execution_done_button)
    Button doneButton;
    OnDoneClickListener clickListener;

    public ExecutionView(Context context) {
        super(context);
        init(context);
    }

    public ExecutionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExecutionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_execution_layout, this);
        ButterKnife.bind(this);
    }


    public void looserLayout() {
        textView.setText(R.string.when_you_be_finish_push_the_button);
        doneButton.setVisibility(VISIBLE);

    }

    public void winnerLayout() {
        doneButton.setVisibility(GONE);
        textView.setText(R.string.you_are_winner);
    }

    public void setWatcherMode() {
        textView.setText(R.string.winner_makes_a_wish);
        doneButton.setVisibility(GONE);
    }

    public void setButtonVisibility(int visibility) {
        doneButton.setVisibility(visibility);
    }

    @OnClick(R.id.view_execution_done_button)
    void onDoneClick() {
        if (clickListener != null) {
            clickListener.onDoneClick();
        }
    }

    public void setClickListener(OnDoneClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setInitState() {
        textView.setText("");
        doneButton.setVisibility(GONE);
    }


    public interface OnDoneClickListener {
        void onDoneClick();
    }
}
