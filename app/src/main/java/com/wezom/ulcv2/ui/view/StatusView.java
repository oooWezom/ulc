package com.wezom.ulcv2.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.net.models.Status;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kartavtsev.s on 28.01.2016.
 */
public class StatusView extends RelativeLayout {

    @BindView(R.id.view_status_online_view)
    ImageView mIndicatorView;
    @BindView(R.id.view_status_text)
    TextView mStatusText;

    Status mStatus;

    public StatusView(Context context) {
        super(context);
        init();
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public StatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setTextColor(int color) {
        mStatusText.setTextColor(color);
    }

    private void init() {
        inflate(getContext(), R.layout.view_status, this);
        ButterKnife.bind(this);
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        if (status == null) {
            status = Status.OFFLINE;
        }
        switch (status) {
            case OFFLINE:
                mStatusText.setVisibility(GONE);
                mIndicatorView.setVisibility(GONE);
                break;
            case ONLINE:
                mStatusText.setText(R.string.online);
                mIndicatorView.setVisibility(VISIBLE);
                mIndicatorView.setImageResource(R.drawable.ic_status_online);
                break;
            case PLAYING:
                mStatusText.setText(R.string.playing);
                mIndicatorView.setVisibility(VISIBLE);
                mIndicatorView.setImageResource(R.drawable.ic_status_2play);
                break;
            case SEARCHING:
                mStatusText.setText(R.string.searching);
                mIndicatorView.setVisibility(VISIBLE);
                mIndicatorView.setImageResource(R.drawable.ic_status_online);
                break;
            case WATCHING:
                mStatusText.setText(R.string.watching);
                mIndicatorView.setVisibility(VISIBLE);
                mIndicatorView.setImageResource(R.drawable.ic_status_online);
                break;
            case TALKING:
                mStatusText.setText(R.string.talking);
                mIndicatorView.setVisibility(VISIBLE);
                mIndicatorView.setImageResource(R.drawable.ic_status_2talk);
                break;
        }
        mStatus = status;
    }
}
