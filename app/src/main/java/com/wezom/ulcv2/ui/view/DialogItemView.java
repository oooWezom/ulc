package com.wezom.ulcv2.ui.view;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.DialogSelectedEvent;
import com.wezom.ulcv2.mvp.model.Dialog;
import com.wezom.ulcv2.mvp.view.ViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kartavtsev.s on 22.01.2016.
 */
public class DialogItemView extends RelativeLayout implements ViewModel<Dialog> {

    private final EventBus mBus;
    @BindView(R.id.view_dialog_avatar)
    ImageView mAvatarImage;
    @BindView(R.id.view_dialog_content_layout)
    RelativeLayout mLayout;
    @BindView(R.id.view_dialog_status_image_view)
    ImageView mStatusImageView;
    @BindView(R.id.view_dialog_small_avatar)
    ImageView mSmallImageView;
    @BindView(R.id.view_dialog_name_text)
    TextView mNameText;
    @BindView(R.id.view_dialog_message_text)
    TextView mMessageText;
    @BindView(R.id.view_dialog_time_text)
    TextView mTimeText;
    @BindView(R.id.view_dialog_item_message_layout)
    LinearLayout mMessageLayout;
    private Dialog mData;
    private String mUserAvatar;

    public DialogItemView(Context context, EventBus bus, String userAvatar) {
        super(context);
        mBus = bus;
        mUserAvatar = userAvatar;
        init();
    }

    @Override
    public void setData(Dialog data) {
        mData = data;
        mStatusImageView.setVisibility(VISIBLE);
        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + data.getPartner().getAvatar())
                .placeholder(R.drawable.ic_default_user_big)
                .into(mAvatarImage);
        mNameText.setText(data.getPartner().getName());
        mMessageText.setText(data.getLastMessageText());
        mTimeText.setText(getFormattedDate(data.getLastMessageTimeStamp() * 1000));
        if (data.getPartner() != null) {
            switch (data.getPartner().getStatus()) {
                case 1:
                    mStatusImageView.setVisibility(GONE);
                    break;
                case 2:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_online));
                    break;
                case 5:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_2play));
                    break;
                case 6:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_2talk));
                    break;
                default:
                    mStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_online));
                    break;
            }
        }
        if (data.getUnreadCount() < 0) {
            data.setUnreadCount(0);
        }

        if (data.isMy() && !data.isLastMessageRead()) {
            mMessageLayout.setBackgroundColor(getResources().getColor(R.color.view_dialog_unread_background_color));
            mSmallImageView.setVisibility(VISIBLE);
        } else {
            mMessageLayout.setBackgroundColor(getResources().getColor(R.color.color_white));
        }

        if (data.getUnreadCount() == 0) {
            mLayout.setBackgroundColor(getResources().getColor(R.color.color_white));
        } else {
            mLayout.setBackgroundColor(getResources().getColor(R.color.view_dialog_unread_background_color));
            mMessageLayout.setBackgroundColor(getResources().getColor(R.color.view_dialog_unread_background_color));
        }


        mSmallImageView.setVisibility(data.isMy()
                ? VISIBLE : GONE);

        Picasso.with(getContext())
                .load(Constants.CONTENT_DEV_URL + mUserAvatar)
                .placeholder(R.drawable.ic_default_user_big)
                .into(mSmallImageView);
    }

    @OnClick
    public void onClick() {
        mBus.post(new DialogSelectedEvent(mData.getPartner().getServerId(), mData.getPartner().getName(),
                mData.getPartner().getAvatar()));
    }

    @OnClick(R.id.view_dialog_layout)
    public void onAvatarClick() {
        mBus.post(new DialogSelectedEvent(mData.getPartner().getServerId(),
                mData.getPartner().getName(), mData.getPartner().getAvatar()));
    }

    public String getFormattedDate(long smsTimeInMilis) {
        TimeZone timeZone = TimeZone.getDefault();
        long timeZoneOffset = timeZone.getRawOffset();
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis + timeZoneOffset);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) &&
                now.get(Calendar.MONTH) == smsTime.get(Calendar.MONTH)) {
            return String.valueOf(DateFormat.format(timeFormatString, smsTime));
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else if (now.get(Calendar.WEEK_OF_YEAR) == smsTime.get(Calendar.WEEK_OF_YEAR)) {
            return DateFormat.format("EEEE", smsTime).toString();
        } else
            return DateFormat.format("MMMM d", smsTime).toString();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_dialog, this);
        ButterKnife.bind(view);
    }
}
