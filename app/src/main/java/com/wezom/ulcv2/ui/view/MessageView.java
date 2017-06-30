package com.wezom.ulcv2.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.IntDef;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wezom.ulcv2.R;
import com.wezom.ulcv2.mvp.model.Message;
import com.wezom.ulcv2.mvp.view.ViewModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by kartavtsev.s on 25.01.2016.
 */
public class MessageView extends RelativeLayout implements ViewModel<Message> {

    public static final int MESSAGE_TYPE_MINE = 0;
    public static final int MESSAGE_TYPE_NOT_MINE = 1;
    public static final int MESSAGE_TYPE_TIME = 2;

    @BindView(R.id.view_message_left_message_text)
    TextView mRightMessageText;
    @BindView(R.id.view_message_my_status_image_view)
    ImageView mStatusImageView;
    @BindView(R.id.view_message_right_message_text)
    TextView mLeftMessageText;
    @BindView(R.id.view_message_left_time)
    TextView mLeftTimeText;
    @BindView(R.id.view_message_right_time)
    TextView mRightTimeText;
    @BindView(R.id.view_message_middle_time)
    TextView mMiddleTimeText;
    @BindView(R.id.view_message_left_layout)
    LinearLayout mLeftLayout;
    @BindView(R.id.view_message_right_layout)
    LinearLayout mRightLayout;
    @BindView(R.id.view_message_send_progress)
    MaterialProgressBar mSendingProgress;

    private int mType;

    public MessageView(Context context, @MessageType int type) {
        super(context);
        mType = type;
        init();
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public MessageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setData(Message data) {
        if (data.getText() != null) {
            mLeftMessageText.setText(data.getText().trim());
            mRightMessageText.setText(data.getText().trim());
        }

        TimeZone timeZone = TimeZone.getDefault();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        timeFormat.setTimeZone(timeZone);
        dateFormat.setTimeZone(timeZone);
        String time = timeFormat.format(new Date(data.getPostedTimestamp() * 1000L + timeZone.getRawOffset()));
        String date = dateFormat.format(new Date(data.getPostedTimestamp() * 1000L + timeZone.getRawOffset()));

        mLeftTimeText.setText(time);
        mRightTimeText.setText(time);

        if (data.isRead()) {
            mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_readed));
        } else if (data.isSent()) {
            mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_sended));
        } else {
            mStatusImageView.setVisibility(GONE);
        }

        if (DateUtils.isToday(data.getPostedTimestamp() * 1000L)) {
            mMiddleTimeText.setText(getResources().getString(R.string.today));
        } else {
            mMiddleTimeText.setText(date);
        }
        if (!data.isSent()) {
            mSendingProgress.setVisibility(VISIBLE);
        } else {
            mSendingProgress.setVisibility(GONE);
        }
    }

    public void setMessageType(@MessageType int type) {
        switch (type) {
            case MESSAGE_TYPE_MINE:
                mMiddleTimeText.setVisibility(GONE);
                mRightLayout.setVisibility(GONE);
                mLeftLayout.setVisibility(VISIBLE);
                mLeftMessageText.setBackgroundResource(R.drawable.bg_chat_cloud_my);
                break;

            case MESSAGE_TYPE_NOT_MINE:
                mMiddleTimeText.setVisibility(GONE);
                mRightLayout.setVisibility(VISIBLE);
                mLeftLayout.setVisibility(GONE);
                mRightMessageText.setBackgroundResource(R.drawable.bg_chat_cloud);
                break;

            case MESSAGE_TYPE_TIME:
                mRightLayout.setVisibility(GONE);
                mLeftLayout.setVisibility(GONE);
                mMiddleTimeText.setVisibility(VISIBLE);
                break;
        }
    }

    private void init() {
        inflate(getContext(), R.layout.view_message, this);
        ButterKnife.bind(this);
        setMessageType(mType);
    }

    @IntDef({MESSAGE_TYPE_MINE, MESSAGE_TYPE_NOT_MINE, MESSAGE_TYPE_TIME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageType {
    }
}
